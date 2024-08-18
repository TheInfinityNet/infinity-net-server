package com.infinitynet.server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public interface CloudStorageService {

    void storeFile(MultipartFile file, String filePath);

    void storeFile(InputStream file, String contentType, String filePath);

    String getFileUrl(String filePath);

    List<String> loadAllFromFolder(String folderPath);

    void deleteFile(String filePath);

    void deleteFolder(String folderPath);

}
