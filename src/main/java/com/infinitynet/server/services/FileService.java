package com.infinitynet.server.services;

import com.infinitynet.server.enums.MediaOwnerType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService<P, M> {

    void uploadFiles(P owner, MediaOwnerType type, List<MultipartFile> files);

    void deleteFile(String fileId);

    void deleteFiles(String ownerId, MediaOwnerType type);

}
