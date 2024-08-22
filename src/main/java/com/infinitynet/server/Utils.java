package com.infinitynet.server;

import org.springframework.context.NoSuchMessageException;
import org.springframework.web.multipart.MultipartFile;

import static com.infinitynet.server.Constants.ALLOWED_MEDIA_TYPES;
import static com.infinitynet.server.components.Translator.getLocalizedMessage;

public class Utils {

    public static boolean isMedia(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_MEDIA_TYPES.contains(contentType);
    }

    public static String getMessageForValidationException(String messageKey, Object... args) {
        try {
            return getLocalizedMessage(messageKey, args);

        } catch (NoSuchMessageException exception) {
            return exception.getMessage();
        }
    }

}
