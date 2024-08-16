package com.infinitynet.server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public interface LocalStorageService {

    String storeFile(MultipartFile file, String storageFolder, String fileName);

    byte[] readFile(String filePath);

    Stream<Path> loadAllFromFolder(String folderPath);

    void deleteFile(String filePath);

    void deleteFolder(String folderPath);

}
