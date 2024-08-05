package com.infinitynet.server;

import com.nimbusds.jose.JWSAlgorithm;

import static com.nimbusds.jose.JWSAlgorithm.HS384;
import static com.nimbusds.jose.JWSAlgorithm.HS512;

public class Constants {

    public static final JWSAlgorithm ACCESS_TOKEN_SIGNATURE_ALGORITHM = HS512;

    public static final JWSAlgorithm REFRESH_TOKEN_SIGNATURE_ALGORITHM = HS384;

    public static final String KAFKA_TOPIC_SEND_MAIL = "SEND_MAIL";

    public static final String DEFAULT_MAIL_HEADERS_MAILIN_CUSTOM =
            "custom_header_1:custom_value_1|custom_header_2:custom_value_2|custom_header_3:custom_value_3";

    public static final String DEFAULT_MAIL_HEADERS_CHARSET = "iso-8859-1";

    public static final int SEND_MAIL_TO_NEW_USER = 1;

    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static final String REDIS_REVOKED_ACCESS_TOKEN_KEY = "revoked_access_tokens";

    public static final String REDIS_REVOKED_REFRESH_TOKEN_KEY = "revoked_refresh_tokens";

    public static final String REDIS_BLACKLISTED_ACCESS_TOKEN_KEY = "blacklisted_access_tokens";

    public static final String REDIS_BLACKLISTED_REFRESH_TOKEN_KEY = "blacklisted_refresh_tokens";

}
