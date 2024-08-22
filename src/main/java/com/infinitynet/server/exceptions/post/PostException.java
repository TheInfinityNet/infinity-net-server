package com.infinitynet.server.exceptions.post;

import com.infinitynet.server.exceptions.AppException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PostException extends AppException {

    public PostException(PostErrorCode postErrorCode, HttpStatus httpStatus) {
        super(postErrorCode.getMessage(), httpStatus);
        this.postErrorCode = postErrorCode;
    }

    private final PostErrorCode postErrorCode;

}