package org.envycorp.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.authservice.model.dto.UserLoginRequestDto;
import org.envycorp.authservice.model.dto.UserPatchRequestDto;
import org.envycorp.authservice.model.dto.UserRegisterRequestDto;
import org.envycorp.authservice.model.entity.UserAuth;
import org.envycorp.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String register(@RequestBody UserRegisterRequestDto user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public String login(@RequestBody UserLoginRequestDto user) {
        return authService.login(user);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserAuth> getAllUsers() {
        return authService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestHeader("X-User-Id") Long userId, @RequestHeader("X-User-Role") String role, @PathVariable("id") Long deleteId) {
        authService.deleteUser(userId, role, deleteId);
    }

    @PatchMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestHeader("X-User-Id") Long userId,
                           @RequestHeader("X-User-Role") String role,
                            @PathVariable("id") Long updateId,
                           @RequestBody UserPatchRequestDto user) {
        authService.updateUser(userId, role, updateId, user);
    }
}
