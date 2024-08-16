package com.infinitynet.server.controllers;

import com.infinitynet.server.dtos.requests.FileUploadRequest;
import com.infinitynet.server.dtos.responses.FileResponse;
import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.services.CloudStorageService;
import com.infinitynet.server.services.LocalStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

import static com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode.COULD_NOT_READ_FILE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.prefix}/media")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Media APIs")
public class MediaController {

    LocalStorageService localStorageService;

    CloudStorageService cloudStorageService;

    @Operation(summary = "test", description = "test")
    @GetMapping("/test")
    ResponseEntity<?> test() {
        FileStorageException fileStorageException = new FileStorageException(COULD_NOT_READ_FILE, BAD_REQUEST);
        fileStorageException.setMoreInfo(new Object[]{ "test path" });
        throw fileStorageException;
    }

    @Operation(summary = "Upload file", description = "Upload file")
    @PostMapping("/upload")
    ResponseEntity<?> upload(@RequestPart(name = "file") MultipartFile file,
                                    @RequestPart(name = "request") @Valid FileUploadRequest request) {
        String backupPath = localStorageService.storeFile(file, "tests", request.ownerId());

        String url = cloudStorageService.storeFile(file, "tests/" + request.ownerId());

        FileResponse response = FileResponse.builder()
                .uuid(UUID.randomUUID().toString())
                .url(url)
                .backupPath(backupPath)
                .type(file.getContentType())
                .size(file.getSize())
                .createdAt(new Date())
                .build();

        return ResponseEntity.status(CREATED).body(response);
    }

    @Operation(summary = "Get folder", description = "Load all files of folder")
    @GetMapping("/get-folder")
    ResponseEntity<?> getFolder(@RequestParam String path) {
        return ResponseEntity.ok().body(cloudStorageService.loadAllFromFolder(path));
    }

    @Operation(summary = "Delete file", description = "Delete file")
    @DeleteMapping("/delete-file")
    @Transactional(rollbackFor = FileStorageException.class)
    void deleteFile(@RequestParam String fileName) {
        localStorageService.deleteFile(fileName); //action 1
        cloudStorageService.deleteFile(fileName); //action 2
    }

    @Operation(summary = "Delete folder", description = "Delete folder")
    @DeleteMapping("/delete-folder")
    void deleteFolder(@RequestParam String folderName) {
        localStorageService.deleteFolder(folderName);
        cloudStorageService.deleteFolder(folderName);
    }

}
