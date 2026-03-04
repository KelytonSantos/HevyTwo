package com.hevy.demo.models.enums;

import java.util.Arrays;

public enum Series {
    DROP_SET("drop_set"),
    WARM_UP_SET("warm_up_set"),
    NORMAL_SET("normal_set"),
    FAILURE_SET("failure_set");

    private final String dbValue;

    Series(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Series fromDbValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.dbValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown series value: " + value));
    }
}
