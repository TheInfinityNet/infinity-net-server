package com.infinitynet.server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CloudStorageService {

    void storeFile(MultipartFile file, String fileName);

    String getFileUrl(String filePath);

    List<String> loadAllFromFolder(String folderPath);

    void deleteFile(String filePath);

    void deleteFolder(String folderPath);

}
