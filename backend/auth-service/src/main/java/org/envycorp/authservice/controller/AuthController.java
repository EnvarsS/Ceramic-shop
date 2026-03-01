package org.envycorp.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.authservice.model.dto.UserAuthRequestDto;
import org.envycorp.authservice.model.entity.UserAuth;
import org.envycorp.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(@RequestBody UserAuthRequestDto user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody UserAuthRequestDto user) {
        return authService.login(user);
    }

    @GetMapping("/test1")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<UserAuth> test1() {
        return authService.getAllUsers();
    }

    @GetMapping("/test2")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserAuth> test2() {
        return authService.getAllUsers();
    }
}
