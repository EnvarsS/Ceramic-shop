package org.envycorp.authservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.envycorp.authservice.exception.IncorrectPasswordException;
import org.envycorp.authservice.exception.UsernameIsAlreadyTaken;
import org.envycorp.authservice.model.dto.UserAuthRequestDto;
import org.envycorp.authservice.model.entity.UserAuth;
import org.envycorp.authservice.repository.AuthRepository;
import org.envycorp.authservice.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void changePasswords() {
        List<UserAuth> users = authRepository.findAll();

        for (UserAuth user : users) {
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPasswordHash());
            user.setPasswordHash(hashedPassword);
            authRepository.save(user);
        }
    }

    public String register(UserAuthRequestDto user) {
        Long userRoleId = 1L;
        if(authRepository.existsUserAuthByUsername(user.getUsername())){
            throw new UsernameIsAlreadyTaken(user.getUsername() + " is already taken");
        }
        UserAuth userAuth = modelMapper.map(user, UserAuth.class);
        userAuth.setPasswordHash(bCryptPasswordEncoder.encode(user.getPassword()));
        userAuth.setRole(roleRepository.findById(userRoleId).orElseThrow());

        UserAuth savedUserAuth = authRepository.save(userAuth);

        return jwtService.generateToken(savedUserAuth);
    }

    public String login(UserAuthRequestDto user) {
        if(!authRepository.existsUserAuthByUsername(user.getUsername())){
            throw new UsernameIsAlreadyTaken(user.getUsername() + " is not found");
        }
        UserAuth userAuth = authRepository.findByUsername(user.getUsername());
        if(!bCryptPasswordEncoder.matches(user.getPassword(), userAuth.getPasswordHash())){
            throw new IncorrectPasswordException("Incorrect password");
        }

        return jwtService.generateToken(userAuth);
    }

}
