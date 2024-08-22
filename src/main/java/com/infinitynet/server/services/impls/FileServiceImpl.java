package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.FileMetadata;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostMedia;
import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.repositories.FileMetadataRepository;
import com.infinitynet.server.repositories.PostMediaRepository;
import com.infinitynet.server.services.FileService;
import com.infinitynet.server.services.LocalStorageService;
import com.infinitynet.server.services.MinioClientService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.infinitynet.server.Constants.KAFKA_TOPIC_HANDLE_MEDIA;
import static com.infinitynet.server.enums.FileHandleAction.*;
import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.CAN_NOT_STORE_FILE;
import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.COULD_NOT_READ_FILE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileServiceImpl<O, F> implements FileService<O, F> {

    PostMediaRepository postMediaRepository;

    FileMetadataRepository fileMetadataRepository;

    LocalStorageService localStorageService;

    MinioClientService minioClientService;

    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public F findById(String id) {
        return (F) fileMetadataRepository.findById(id).orElseThrow(() ->
                new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST));
    }

    @Override
    public String getObjectUrl(F file) {
        String objectKey = ((FileMetadata) file).getObjectKey();
        try {
            return minioClientService.getObjectUrl(objectKey);

        } catch (Exception e) {
            throw new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void uploadFiles(O owner, List<MultipartFile> files) {
        List<F> results = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileId = UUID.randomUUID().toString();

            String backupPath = localStorageService.storeFile(file, getPrefixPath(owner) + "/" + fileId);

            kafkaTemplate.send(KAFKA_TOPIC_HANDLE_MEDIA, UPLOAD
                    + ":" + backupPath + ":" + file.getSize() + ":" + file.getContentType());

            switch (owner.getClass().getSimpleName()) {
                case "Post" -> results.add((F) PostMedia.builder()
                        .post((Post) owner)
                        .objectKey(backupPath)
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .build());
            }
        }

        switch (owner.getClass().getSimpleName()) {
            case "Post" -> postMediaRepository.saveAll((List<PostMedia>) results);
        }
    }

    @Override
    public void deleteFile(String fileId) {
        String objectKey = ((FileMetadata) findById(fileId)).getObjectKey();
        localStorageService.deleteFile(objectKey);
        fileMetadataRepository.deleteById(fileId);
        kafkaTemplate.send(KAFKA_TOPIC_HANDLE_MEDIA, DELETE_OBJECT + ":" + objectKey);
    }

    @Override
    public void deleteFolder(O owner) {
        localStorageService.deleteFolder(getPrefixPath(owner));
        switch (owner.getClass().getSimpleName()) {
            case "Post" -> postMediaRepository.deleteAll(((Post) owner).getPostMedias());
        }
        kafkaTemplate.send(KAFKA_TOPIC_HANDLE_MEDIA, DELETE_PARENT_OBJECT + ":" + getPrefixPath(owner));
    }

    private String getPrefixPath(O owner) {
        return switch (owner.getClass().getSimpleName()) {
            case "Post" -> "posts/" + ((Post) owner).getId();
            default -> throw new FileStorageException(CAN_NOT_STORE_FILE, BAD_REQUEST);
        };
    }

}
