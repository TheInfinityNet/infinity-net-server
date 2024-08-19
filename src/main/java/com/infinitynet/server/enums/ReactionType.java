package com.infinitynet.server.enums;

public enum ReactionType {

    LIKE("1f44d"),

    LOVE("2764-fe0f"),

    HAHA("1f603"),

    WOW("1f622"),

    SAD("1f622"),

    FOLDED_HANDS("1f64f"),

    DISLIKE("1f44e"),

    ANGRY("1f621");

    String value;

    ReactionType(String value) {
        this.value = value;
    }

}
