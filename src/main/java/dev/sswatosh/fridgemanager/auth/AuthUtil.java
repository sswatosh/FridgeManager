package dev.sswatosh.fridgemanager.auth;

import org.pac4j.core.authorization.authorizer.RequireAllPermissionsAuthorizer;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.DirectBearerAuthClient;
import ratpack.handling.Handler;
import ratpack.pac4j.RatpackPac4j;

public class AuthUtil {
    public static Handler requirePermissions(String... permissions) {
        return RatpackPac4j.requireAuth(DirectBearerAuthClient.class, new RequireAllPermissionsAuthorizer<CommonProfile>(permissions));
    }
}
