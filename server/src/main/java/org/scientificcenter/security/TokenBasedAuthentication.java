package org.scientificcenter.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenBasedAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 9187761132564527658L;
    private final String token;
    private final UserDetails user;

    public TokenBasedAuthentication(final String token, final UserDetails user) {
        super(user.getAuthorities());
        this.token = token;
        this.user = user;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public UserDetails getPrincipal() {
        return this.user;
    }
}