package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.FileMetadata;
import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostMedia;
import com.infinitynet.server.enums.MediaOwnerType;
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
import static com.infinitynet.server.enums.FileHandleAction.UPLOAD;
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
    public void uploadFiles(O owner, MediaOwnerType type, List<MultipartFile> files) {
        String prefixPath = switch (type) {
            case POST -> "posts/";
            case PROFILE -> "users/";
            case COMMENT -> "comments/";
            case GROUP -> "groups/";
            case MESSAGE -> "messages/";

        };

        List<F> results = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileId = UUID.randomUUID().toString();

            String backupPath = localStorageService.storeFile(file,
                    prefixPath + ((Post) owner).getId() + "/" + fileId);

            kafkaTemplate.send(KAFKA_TOPIC_HANDLE_MEDIA, UPLOAD
                    + ":" + backupPath + ":" + file.getSize() + ":" + file.getContentType());

            switch (type) {
                case POST -> results.add((F) PostMedia.builder()
                        .post((Post) owner)
                        .objectKey(backupPath)
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .build());
            }
        }

        switch (type) {
            case POST -> postMediaRepository.saveAll((List<PostMedia>) results);
        }
    }

    @Override
    public void deleteFile(String fileId) {

    }

    @Override
    public void deleteFiles(String ownerId, MediaOwnerType type) {

    }

}
