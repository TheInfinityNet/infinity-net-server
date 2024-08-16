package com.infinitynet.server.exceptions.file_storage;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileStorageException extends RuntimeException {

    public FileStorageException(FileStorageErrorCode fileStorageErrorCode, HttpStatus httpStatus) {
        super(fileStorageErrorCode.getMessage());
        this.fileStorageErrorCode = fileStorageErrorCode;
        this.httpStatus = httpStatus;
    }

    private final FileStorageErrorCode fileStorageErrorCode;
    private Object[] moreInfo;
    private final HttpStatus httpStatus;

    public void setMoreInfo(Object[] moreInfo) {
        this.moreInfo = moreInfo;
    }

}