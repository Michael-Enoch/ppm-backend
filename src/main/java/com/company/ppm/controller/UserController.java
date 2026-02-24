package com.company.ppm.controller;

import com.company.ppm.dto.UserCreateRequest;
import com.company.ppm.dto.UserResponse;
import com.company.ppm.dto.UserUpdateRequest;
import com.company.ppm.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public List<UserResponse> listUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}
