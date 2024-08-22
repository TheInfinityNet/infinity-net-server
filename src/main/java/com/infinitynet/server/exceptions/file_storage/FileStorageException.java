package com.infinitynet.server.exceptions.file_storage;

import com.infinitynet.server.exceptions.AppException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileStorageException extends AppException {

    public FileStorageException(FileStorageErrorCode fileStorageErrorCode, HttpStatus httpStatus) {
        super(fileStorageErrorCode.getMessage(), httpStatus);
        this.fileStorageErrorCode = fileStorageErrorCode;
    }

    private final FileStorageErrorCode fileStorageErrorCode;

}