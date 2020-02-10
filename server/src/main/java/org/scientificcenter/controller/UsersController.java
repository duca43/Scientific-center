package org.scientificcenter.controller;

import org.scientificcenter.dto.UserDto;
import org.scientificcenter.model.User;
import org.scientificcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(this.userService.findAllNonAdminUsers());
    }

    @PostMapping(value = "/editor", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<User> addEditor(@RequestBody final User editor) {
        return ResponseEntity.ok(this.userService.addEditor(editor));
    }

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getByUsername(@PathVariable final String username) {
        return ResponseEntity.ok(this.userService.findUserByUsername(username));
    }
}