package org.envycorp.authservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.envycorp.authservice.exception.*;
import org.envycorp.authservice.model.dto.UserLoginRequestDto;
import org.envycorp.authservice.model.dto.UserPatchRequestDto;
import org.envycorp.authservice.model.dto.UserRegisterRequestDto;
import org.envycorp.authservice.model.entity.UserAuth;
import org.envycorp.authservice.model.event.AuthEvent;
import org.envycorp.authservice.repository.AuthRepository;
import org.envycorp.authservice.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KafkaProducerService kafkaProducerService;

    @PostConstruct
    public void changePasswords() {
        List<UserAuth> users = authRepository.findAll();

        for (UserAuth user : users) {
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPasswordHash());
            user.setPasswordHash(hashedPassword);
            authRepository.save(user);
        }
    }

    @Transactional
    public String register(UserRegisterRequestDto user) {
        Long userRoleId = 1L;
        if (authRepository.existsUserAuthByUsername(user.getUsername())) {
            throw new UsernameIsAlreadyTaken(user.getUsername() + " is already taken");
        }
        if (authRepository.existsUserAuthByEmail(user.getEmail())) {
            throw new EmailIsAlreadyTaken(user.getEmail() + " is already taken");
        }

        UserAuth userAuth = modelMapper.map(user, UserAuth.class);
        userAuth.setPasswordHash(bCryptPasswordEncoder.encode(user.getPassword()));
        userAuth.setRole(roleRepository.findById(userRoleId).orElseThrow());

        UserAuth savedUserAuth = authRepository.save(userAuth);

        AuthEvent authEvent = modelMapper.map(savedUserAuth, AuthEvent.class);

        kafkaProducerService.publishUserRegistered(authEvent);

        return jwtService.generateToken(savedUserAuth);
    }

    @Transactional
    public String login(UserLoginRequestDto user) {
        if (!authRepository.existsUserAuthByUsername(user.getUsername())) {
            throw new UsernameIsAlreadyTaken(user.getUsername() + " is not found");
        }
        UserAuth userAuth = authRepository.findByUsername(user.getUsername());
        if (!bCryptPasswordEncoder.matches(user.getPassword(), userAuth.getPasswordHash())) {
            throw new IncorrectPasswordException("Incorrect password");
        }

        return jwtService.generateToken(userAuth);
    }

    public List<UserAuth> getAllUsers() {
        return authRepository.findAll();
    }

    public void deleteUser(Long id, String role, Long deleteId) {
        if (id.equals(deleteId) || role.equals("ROLE_ADMIN")) {
            authRepository.deleteById(deleteId);
            AuthEvent authEvent = new AuthEvent();
            authEvent.setId(deleteId);
            kafkaProducerService.publishUserDeleted(authEvent);
        } else {
            throw new NoPermissionError("You don't have permission to delete this user");
        }
    }

    @Transactional
    public void updateUser(Long userId, String role, Long updateId, UserPatchRequestDto dto) {
        if (!userId.equals(updateId) && !role.equals("ROLE_ADMIN")) {
            throw new NoPermissionError("You don't have permission to update this user");
        }

        UserAuth existingUser = authRepository.findById(updateId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + updateId));

        if (dto.getUsername() != null && !dto.getUsername().equals(existingUser.getUsername())) {
            if (authRepository.existsUserAuthByUsername(dto.getUsername())) {
                throw new UsernameIsAlreadyTaken(dto.getUsername() + " is already taken");
            }
            existingUser.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(existingUser.getEmail())) {
            if (authRepository.existsByEmail(dto.getEmail())) {
                throw new EmailIsAlreadyTaken(dto.getEmail() + " is already taken");
            }
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            existingUser.setPasswordHash(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        UserAuth updatedUser = authRepository.save(existingUser);

        if (dto.getUsername() != null || dto.getEmail() != null) {
            AuthEvent authEvent = modelMapper.map(updatedUser, AuthEvent.class);
            kafkaProducerService.publishUserUpdated(authEvent);
        }
    }
}
