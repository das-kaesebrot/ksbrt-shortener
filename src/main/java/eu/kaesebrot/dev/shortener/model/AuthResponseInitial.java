package eu.kaesebrot.dev.shortener.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseInitial extends AuthResponseBase {
    @JsonProperty("refresh_token")
    private String refreshToken;

    public AuthResponseInitial() {
        super();
    }

    public AuthResponseInitial(AuthResponseBase base, String refreshToken) {
        super(base.getJwt(), base.getPrincipalName(), base.getExpiresAt());
        this.refreshToken = refreshToken;
    }

    public AuthResponseInitial(String jwt, String principalName, Long expiresAt, String refreshToken) {
        super(jwt, principalName, expiresAt);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
