package com.infinitynet.server.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AppException extends RuntimeException {

    public AppException(String Message, HttpStatus httpStatus) {
        super(Message);
        this.httpStatus = httpStatus;
    }

    Object[] moreInfo;
    final HttpStatus httpStatus;

    public void setMoreInfo(Object[] moreInfo) {
        this.moreInfo = moreInfo;
    }

}
