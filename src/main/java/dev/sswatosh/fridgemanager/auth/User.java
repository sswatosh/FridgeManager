package dev.sswatosh.fridgemanager.auth;

import java.util.Set;

public class User {
    private final String id;
    private final Set<String> permissions;

    public User(String id, Set<String> permissions) {
        this.id = id;
        this.permissions = permissions;
    }

    public String getId() {
        return id;
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}
