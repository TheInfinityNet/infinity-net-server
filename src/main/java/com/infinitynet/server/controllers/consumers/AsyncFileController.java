package com.infinitynet.server.controllers.consumers;

import com.infinitynet.server.enums.FileHandleAction;
import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.services.CloudStorageService;
import com.infinitynet.server.services.LocalStorageService;
import com.infinitynet.server.services.MinioClientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.InputStream;

import static com.infinitynet.server.Constants.KAFKA_TOPIC_HANDLE_MEDIA;
import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.*;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AsyncFileController {

    LocalStorageService localStorageService;

    CloudStorageService cloudStorageService;

    MinioClientService minioClientService;

    @KafkaListener(
            topics = KAFKA_TOPIC_HANDLE_MEDIA,
            groupId = "${spring.kafka.file-consumer.group-id}",
            errorHandler = "kafkaListenerErrorHandler")
    public void listenFileHandleAction(String message) {
        String action = message.split(":")[0];
        String key = message.split(":")[1];

        log.info("Message received: {}", message);

        switch (FileHandleAction.valueOf(action)) {
            case UPLOAD -> {
                long size = Long.parseLong(message.split(":")[2]);
                String contentType = message.split(":")[3];
                InputStream inputStream = localStorageService.readFile(key);

                try {
                    minioClientService.storeObject(inputStream, size, contentType, key);
                    cloudStorageService.storeFile(inputStream, contentType, key);
                } catch (Exception e) {
                    throw new FileStorageException(CAN_NOT_STORE_FILE, UNPROCESSABLE_ENTITY);
                }
            }
            case DELETE_OBJECT -> {
                try {
                    minioClientService.deleteObject(key);
                    cloudStorageService.deleteFile(key);
                } catch (Exception e) {
                    throw new FileStorageException(CAN_NOT_DELETE_FILE, UNPROCESSABLE_ENTITY);
                }
            }
            case DELETE_PARENT_OBJECT -> {
                try {
                    minioClientService.deleteParentObject(key);
                    cloudStorageService.deleteFolder(key);
                } catch (Exception e) {
                    throw new FileStorageException(CAN_NOT_DELETE_FOLDER, UNPROCESSABLE_ENTITY);
                }
            }

        }
    }

}
