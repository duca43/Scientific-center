package org.scientificcenter.service;

import org.scientificcenter.security.AuthenticationRequest;
import org.scientificcenter.security.UserState;

public interface AuthenticationService {

    UserState createAuthenticationToken(AuthenticationRequest authenticationRequest);
}
