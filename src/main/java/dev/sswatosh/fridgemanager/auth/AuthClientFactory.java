package dev.sswatosh.fridgemanager.auth;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.DirectBearerAuthClient;

import javax.inject.Inject;

public class AuthClientFactory {

    private final TokenStore tokenStore;

    @Inject
    public AuthClientFactory(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public BaseClient<TokenCredentials, CommonProfile> getTokenAuthClient() {
        Authenticator<TokenCredentials> tokenAuthenticator = (credentials, context) -> {
            User user = tokenStore.getUser(credentials.getToken());

            if (user == null) {
                throw new CredentialsException("Failed to authenticate token");
            }

            CommonProfile profile = new CommonProfile();
            profile.setId(user.getId());
            profile.setPermissions(user.getPermissions());
            credentials.setUserProfile(profile);
        };

        return new DirectBearerAuthClient(tokenAuthenticator);
    }
}
