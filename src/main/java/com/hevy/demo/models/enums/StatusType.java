package com.hevy.demo.models.enums;

import java.util.Arrays;

public enum StatusType {
    PENDING("pending"),
    CANCELED("canceled"),
    COMPLETED("completed");

    private final String dbValue;

    StatusType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static StatusType fromDbValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.dbValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status value: " + value));
    }
}
