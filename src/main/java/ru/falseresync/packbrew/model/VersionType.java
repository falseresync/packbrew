package ru.falseresync.packbrew.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VersionType {
    RELEASE("release"),
    SNAPSHOT("snapshot"),
    OLD_BETA("old_beta"),
    OLD_ALPHA("old_alpha");

    @JsonValue
    public final String json;

    VersionType(String json) {
        this.json = json;
    }
}
