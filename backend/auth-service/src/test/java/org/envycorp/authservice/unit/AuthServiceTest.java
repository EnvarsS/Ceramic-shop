package org.envycorp.authservice.unit;

import org.envycorp.authservice.config.TestKafkaConfig;
import org.envycorp.authservice.exception.EmailIsAlreadyTaken;
import org.envycorp.authservice.exception.IncorrectPasswordException;
import org.envycorp.authservice.exception.NoPermissionException;
import org.envycorp.authservice.exception.UsernameIsAlreadyTaken;
import org.envycorp.authservice.model.dto.UserLoginRequestDto;
import org.envycorp.authservice.model.dto.UserPatchRequestDto;
import org.envycorp.authservice.model.dto.UserRegisterRequestDto;
import org.envycorp.authservice.model.entity.Role;
import org.envycorp.authservice.model.entity.UserAuth;
import org.envycorp.authservice.model.event.AuthEvent;
import org.envycorp.authservice.repository.AuthRepository;
import org.envycorp.authservice.repository.RoleRepository;
import org.envycorp.authservice.service.AuthService;
import org.envycorp.authservice.service.JwtService;
import org.envycorp.authservice.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
public class AuthServiceTest {

    @Mock
    AuthRepository authRepository;
    @Mock
    JwtService jwtService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    RoleRepository roleRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    KafkaProducerService kafkaProducerService;

    @InjectMocks
    AuthService authService;

    @Test
    void register_shouldThrow_whenUsernameAlreadyTaken() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("user1", "user1@gmail.com", "pass");
        when(authRepository.existsUserAuthByUsername("user1")).thenReturn(true);

        assertThrows(UsernameIsAlreadyTaken.class, () -> authService.register(dto));
        verify(authRepository, never()).save(any());
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyTaken() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("newuser", "existing@gmail.com", "pass");
        when(authRepository.existsUserAuthByUsername("newuser")).thenReturn(false);
        when(authRepository.existsUserAuthByEmail("existing@gmail.com")).thenReturn(true);

        assertThrows(EmailIsAlreadyTaken.class, () -> authService.register(dto));
    }

    @Test
    void register_shouldReturnToken_whenValid() {
        UserRegisterRequestDto dto = new UserRegisterRequestDto("newuser", "new@gmail.com", "pass");
        Role role = new Role(1L, "ROLE_USER");
        UserAuth userAuth = new UserAuth(1L, "newuser", "hashedPass", "new@gmail.com", role);

        when(authRepository.existsUserAuthByUsername(any())).thenReturn(false);
        when(authRepository.existsUserAuthByEmail(any())).thenReturn(false);
        when(modelMapper.map(dto, UserAuth.class)).thenReturn(userAuth);
        when(bCryptPasswordEncoder.encode("pass")).thenReturn("hashedPass");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(authRepository.save(any())).thenReturn(userAuth);
        when(modelMapper.map(userAuth, AuthEvent.class)).thenReturn(new AuthEvent(1L, "newuser", "new@gmail.com"));
        when(jwtService.generateToken(userAuth)).thenReturn("jwt-token");

        String token = authService.register(dto);

        assertEquals("jwt-token", token);
        verify(kafkaProducerService).publishUserRegistered(any());
    }

    @Test
    void login_shouldThrow_whenPasswordWrong() {
        UserLoginRequestDto dto = new UserLoginRequestDto("user1", "wrongpass");
        UserAuth userAuth = new UserAuth(1L, "user1", "hashedPass", "u@g.com", new Role(1L, "ROLE_USER"));

        when(authRepository.existsUserAuthByUsername("user1")).thenReturn(true);
        when(authRepository.findByUsername("user1")).thenReturn(userAuth);
        when(bCryptPasswordEncoder.matches("wrongpass", "hashedPass")).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> authService.login(dto));
    }

    @Test
    void deleteUser_shouldThrow_whenNoPermission() {
        assertThrows(NoPermissionException.class,
                () -> authService.deleteUser(1L, "ROLE_USER", 2L));
    }

    @Test
    void deleteUser_shouldDelete_whenAdmin() {
        authService.deleteUser(1L, "ROLE_ADMIN", 99L);

        verify(authRepository).deleteById(99L);
        verify(kafkaProducerService).publishUserDeleted(any());
    }

    @Test
    void updateUser_shouldThrow_whenNoPermission() {
        UserPatchRequestDto dto = new UserPatchRequestDto("newname", null, null);

        assertThrows(NoPermissionException.class,
                () -> authService.updateUser(1L, "ROLE_USER", 2L, dto));
    }
}
