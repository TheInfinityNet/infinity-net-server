package com.infinitynet.server;

import org.springframework.web.multipart.MultipartFile;

import static com.infinitynet.server.Constants.ALLOWED_MEDIA_TYPES;

public class Utils {

    public static boolean isMedia(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_MEDIA_TYPES.contains(contentType);
    }

}
