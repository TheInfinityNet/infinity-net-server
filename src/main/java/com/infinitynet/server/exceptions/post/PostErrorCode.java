package com.infinitynet.server.exceptions.post;

import lombok.Getter;

@Getter
public enum PostErrorCode {
    POST_NOT_FOUND("post/post-not-found", "post_not_found"),
    ;

    PostErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}