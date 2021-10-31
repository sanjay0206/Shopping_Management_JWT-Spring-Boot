package com.example.security.security;

public enum ApplicationUserPermission {
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    PRODUCT_CREATE("product:create"),
    PRODUCT_DELETE("product:delete"),
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
