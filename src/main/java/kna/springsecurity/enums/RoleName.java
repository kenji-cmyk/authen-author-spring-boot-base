package kna.springsecurity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum RoleName {
    USER,
    ADMIN;

    @JsonCreator
    public static RoleName fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role value must not be blank");
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }

        return RoleName.valueOf(normalized);
    }

    @JsonValue
    public String toValue() {
        return name();
    }

    public String toDatabaseRoleName() {
        return "ROLE_" + name();
    }
}
