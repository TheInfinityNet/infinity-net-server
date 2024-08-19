package com.infinitynet.server.exceptions.post;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PostException extends RuntimeException {

    public PostException(PostErrorCode postErrorCode, HttpStatus httpStatus) {
        super(postErrorCode.getMessage());
        this.postErrorCode = postErrorCode;
        this.httpStatus = httpStatus;
    }

    private final PostErrorCode postErrorCode;
    private Object[] moreInfo;
    private final HttpStatus httpStatus;

    public void setMoreInfo(Object[] moreInfo) {
        this.moreInfo = moreInfo;
    }

}