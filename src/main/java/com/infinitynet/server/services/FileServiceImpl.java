package com.infinitynet.server.services;

import com.infinitynet.server.entities.Post;
import com.infinitynet.server.entities.PostMedia;
import com.infinitynet.server.enums.MediaOwnerType;
import com.infinitynet.server.repositories.PostMediaRepository;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileServiceImpl<O, M> implements FileService<O, M> {

    PostMediaRepository postMediaRepository;

    LocalStorageService localStorageService;

    KafkaTemplate<String, String> kafkaTemplate;

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

        List<M> results = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileId = UUID.randomUUID().toString();

            String backupPath = localStorageService.storeFile(file,
                    prefixPath + ((Post) owner).getId() + "/" + fileId);

            kafkaTemplate.send(KAFKA_TOPIC_HANDLE_MEDIA, UPLOAD
                    + ":" + backupPath + ":" + file.getSize() + ":" + file.getContentType());

            results.add((M) new PostMedia(fileId, backupPath, file.getContentType(), file.getSize(), (Post) owner));
        }

        postMediaRepository.saveAll((List<PostMedia>) results);
    }

    @Override
    public void deleteFile(String fileId) {

    }

    @Override
    public void deleteFiles(String ownerId, MediaOwnerType type) {

    }

}
