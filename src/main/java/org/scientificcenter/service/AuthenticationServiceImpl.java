package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.scientificcenter.exception.UserNotFoundException;
import org.scientificcenter.security.AuthenticationRequest;
import org.scientificcenter.security.TokenUtils;
import org.scientificcenter.security.UserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImpl(final TokenUtils tokenUtils, final AuthenticationManager authenticationManager) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserState createAuthenticationToken(final AuthenticationRequest authenticationRequest) throws AuthenticationException {
        Assert.notNull(authenticationRequest, "Authentication request object can't be null!");
        Assert.noNullElements(Stream.of(authenticationRequest.getUsername(), authenticationRequest.getPassword()).toArray(),
                "Both username and password must be set!");

        AuthenticationServiceImpl.log.info("Begin authentication for username '{}'", authenticationRequest.getUsername());

        final UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(authenticationToken);
        } catch (final AuthenticationException e) {
            throw new UserNotFoundException(authenticationRequest.getUsername());
        }

        AuthenticationServiceImpl.log.info("User with username '{}' is authenticated successfully", authenticationRequest.getUsername());

        final String token = this.tokenUtils.generateToken(authenticationRequest.getUsername());


        AuthenticationServiceImpl.log.info("Completed authentication for username '{}'", authenticationRequest.getUsername());

        return UserState.builder()
                .username(authenticationRequest.getUsername())
                .token(token)
                .roles(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
    }
}