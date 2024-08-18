package com.infinitynet.server.services.impls;

import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.services.MinioClientService;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.minio.messages.Item;

import static com.infinitynet.server.Utils.isMedia;
import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class MinioClientServiceImpl implements MinioClientService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioClientServiceImpl(@Value("${minio.endpoint}") String endpoint,
                                  @Value("${minio.access-key}") String accessKey,
                                  @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }

    @Override
    public void storeObject(MultipartFile file, String objectKey) throws Exception {
        if (file.isEmpty()) throw new FileStorageException(EMPTY_FILE, BAD_REQUEST);

        if (!isMedia(file)) throw new FileStorageException(INVALID_FILE_TYPE, BAD_REQUEST);

        float fileSizeInMegabytes = (float) file.getSize() / 1_000_000;

        if (fileSizeInMegabytes > 10.0f) throw new FileStorageException(FILE_TOO_LARGE, BAD_REQUEST);

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey + "." + fileExtension)
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        } catch (MinioException e) {
            throw new FileStorageException(CAN_NOT_STORE_FILE, BAD_REQUEST);
        }
    }

    @Override
    public void storeObject(InputStream file, long size, String contentType, String objectKey) throws Exception {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(file, size, -1)
                    .contentType(contentType)
                    .build());

        } catch (MinioException e) {
            throw new FileStorageException(CAN_NOT_STORE_FILE, BAD_REQUEST);
        }
    }

    @Override
    public String getObjectUrl(String objectKey) throws Exception {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());

        } catch (MinioException e) {
            throw new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST);
        }
    }

    @Override
    public List<String> loadAllFromParent(String parentKey) throws Exception {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(parentKey)
                            .recursive(true)
                            .build()
            );

            List<String> objectKeys = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                objectKeys.add(getObjectUrl(item.objectName()));
            }

            return objectKeys;
        } catch (MinioException e) {
            throw new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST);
        }
    }

    @Override
    public void deleteObject(String objectKey) throws Exception {
        GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .build());
        if (response == null) throw new FileStorageException(FILE_NOT_FOUND, BAD_REQUEST);

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .build());

        } catch (MinioException e) {
            throw new FileStorageException(CAN_NOT_DELETE_FILE, BAD_REQUEST);
        }
    }

    @Override
    public void deleteParentObject(String parentKey) throws Exception {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(parentKey)
                    .recursive(true)
                    .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(item.objectName())
                    .build());
            }

        } catch (MinioException e) {
            throw new FileStorageException(CAN_NOT_DELETE_FOLDER, BAD_REQUEST);
        }
    }

}
