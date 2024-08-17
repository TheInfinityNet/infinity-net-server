package com.infinitynet.server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface MinioClientService {

    void storeObject(MultipartFile file, String objectKey) throws Exception;

    String getObjectUrl(String objectKey) throws Exception;

    void deleteObject(String objectKey) throws Exception;

    void deleteParentObject(String parentKey) throws Exception;

}
