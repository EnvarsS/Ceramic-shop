package org.envycorp.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.authservice.model.dto.UserAuthRequestDto;
import org.envycorp.authservice.model.entity.UserAuth;
import org.envycorp.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/change-passwords")
    @ResponseStatus(HttpStatus.OK)
    public void changePasswords(){
        authService.changePasswords();
    }

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
}
