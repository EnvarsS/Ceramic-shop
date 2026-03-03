package org.envycorp.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.envycorp.userservice.model.dto.response.UserResponseDto;
import org.envycorp.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getMe(@RequestHeader("X-User-Id") Long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto patchUser(@PathVariable Long id,
                                     @RequestBody PatchUserRequestDto user,
                                     @RequestHeader("X-User-Id") Long requesterId,
                                     @RequestHeader("X-User-Role") String role) {
        return userService.patchUser(id, requesterId, role, user);
    }
}
