package com.infinitynet.server.enums;

public enum ReactionType {

    LIKE("like"),

    LOVE("love"),

    HAHA("haha"),

    WOW("wow"),

    SAD("sad"),

    ANGRY("angry");

    String value;

    ReactionType(String value) {
        this.value = value;
    }

}
