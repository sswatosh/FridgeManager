package dev.sswatosh.fridgemanager.auth;

import java.util.Map;
import java.util.Set;

// probably actually provide a way to log in
public class TokenStore {
    public static final String ADULT_TOKEN = "ADULT";
    public static final String CHILD_TOKEN = "CHILD";

    private Map<String, User> tokenToUserMap = Map.of(
        ADULT_TOKEN, new User("aRealAdult", Set.of(
            Permissions.VIEW_FRIDGES,
            Permissions.ADD_EDIT_FRIDGES,
            Permissions.VIEW_ITEMS,
            Permissions.ADD_EDIT_ITEMS,
            Permissions.DELETE_ITEMS
        )),
        CHILD_TOKEN, new User("justAChild", Set.of(
            Permissions.VIEW_FRIDGES,
            Permissions.VIEW_ITEMS,
            Permissions.DELETE_ITEMS
        ))
    );

    public User getUser(String token) {
        return tokenToUserMap.get(token);
    }
}
