package com.infinitynet.server.exceptions.post;

import lombok.Getter;

@Getter
public enum PostErrorCode {
    POST_NOT_FOUND("post/post-not-found", "post_not_found"),
    POST_REACTION_NOT_FOUND("post/post-reaction-not-found", "post_reaction_not_found"),
    COMMENT_NOT_FOUND("comment/comment-not-found", "comment_not_found") //key
    ;

    PostErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}