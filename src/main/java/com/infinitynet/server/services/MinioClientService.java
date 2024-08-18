package com.infinitynet.server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public interface MinioClientService {

    void storeObject(MultipartFile file, String objectKey) throws Exception;

    void storeObject(InputStream file, long size, String contentType, String objectKey) throws Exception;

    String getObjectUrl(String objectKey) throws Exception;

    List<String> loadAllFromParent(String parentKey) throws Exception;

    void deleteObject(String objectKey) throws Exception;

    void deleteParentObject(String parentKey) throws Exception;

}
