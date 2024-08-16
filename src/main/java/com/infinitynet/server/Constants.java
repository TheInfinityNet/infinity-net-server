package com.infinitynet.server;

import com.nimbusds.jose.JWSAlgorithm;

import java.util.Arrays;
import java.util.List;

import static com.nimbusds.jose.JWSAlgorithm.HS384;
import static com.nimbusds.jose.JWSAlgorithm.HS512;

public class Constants {

    public static final JWSAlgorithm ACCESS_TOKEN_SIGNATURE_ALGORITHM = HS512;

    public static final JWSAlgorithm REFRESH_TOKEN_SIGNATURE_ALGORITHM = HS384;

    public static final String KAFKA_TOPIC_SEND_MAIL = "SEND_MAIL";

    public static final String DEFAULT_MAIL_HEADERS_MAILIN_CUSTOM =
            "custom_header_1:custom_value_1|custom_header_2:custom_value_2|custom_header_3:custom_value_3";

    public static final String DEFAULT_MAIL_HEADERS_CHARSET = "iso-8859-1";

    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static final List<String> ALLOWED_MEDIA_TYPES =
            Arrays.asList(
                    "image/jpeg",
                    "image/png",
                    "video/mp4"
            );

    public static final String LOCAL_STORAGE_ROOT_FOLDER = "data_backup";

    public static final String FIREBASE_ADMIN_JSON_PATH = "firebase-admin.json";

}
