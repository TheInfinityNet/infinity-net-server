package com.infinitynet.server.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService<O, F> {

    F findById(String id);

    String getObjectUrl(F file);

    void uploadFiles(O owner, List<MultipartFile> files);

    void deleteFile(String fileId);

    void deleteFolder(O owner);

}
