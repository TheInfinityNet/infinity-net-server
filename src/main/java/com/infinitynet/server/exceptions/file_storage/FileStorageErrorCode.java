package com.infinitynet.server.exceptions.file_storage;

import lombok.Getter;

@Getter
public enum FileStorageErrorCode {
    NO_FILE_PROVIDED("media/no-file-provided", "no_file_provided"),
    INVALID_FILE_PROVIDED("media/invalid-file-provided", "invalid_file_provided"),
    INVALID_FILE_TYPE("media/invalid-file-type", "invalid_file_type"),

    EMPTY_FILE("media/empty-file", "empty_file"),
    FILE_TOO_LARGE("media/file-too-large", "file_too_large"),
    CAN_NOT_STORE_FILE("media/can-not-store-file", "can_not_store_file"),
    COULD_NOT_READ_FILE("media/could-not-read-file", "could_not_read_file"),
    CAN_NOT_INIT_BACKUP_FOLDER("media/can-not-init-backup-folder", "can_not_init_backup_folder"),
    FILE_NOT_FOUND("media/file-not-found", "file_not_found"),
    FOLDER_NOT_FOUND("media/folder-not-found", "folder_not_found"),
    CAN_NOT_DELETE_FILE("media/can-not-delete-file", "can_not_delete_file"),
    CAN_NOT_DELETE_FOLDER("media/can-not-delete-folder", "can_not_delete_folder")
    ;

    FileStorageErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}