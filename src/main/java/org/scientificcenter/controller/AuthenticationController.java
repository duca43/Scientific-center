package org.scientificcenter.controller;

import org.scientificcenter.security.AuthenticationRequest;
import org.scientificcenter.security.UserState;
import org.scientificcenter.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserState> createAuthenticationToken(@RequestBody final AuthenticationRequest authenticationRequest)
            throws AuthenticationException {
        return ResponseEntity.ok(this.authenticationService.createAuthenticationToken(authenticationRequest));
    }
}