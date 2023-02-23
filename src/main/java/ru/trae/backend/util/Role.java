package ru.trae.backend.util;

public enum Role {
    ROLE_ADMINISTRATOR("Administrator"),
    ROLE_EMPLOYEE("Employee"),
    ROLE_DEVELOPER("Developer"),
    ROLE_MANAGER("Manager"),
    ROLE_USER("User");

    public final String value;

    Role(String value) {
        this.value = value;
    }
}
