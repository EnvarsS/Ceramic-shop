package org.envycorp.userservice.unit;

import org.envycorp.userservice.exception.user.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exception.user.InvalidUserLoginDataException;
import org.envycorp.userservice.exception.user.UserNotFoundException;
import org.envycorp.userservice.exception.user.UsernameIsAlreadyTakenException;
import org.envycorp.userservice.model.dto.request.AuthUserRequestDto;
import org.envycorp.userservice.model.dto.request.CreateUserRequestDto;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.envycorp.userservice.model.dto.response.RoleResponseDto;
import org.envycorp.userservice.model.dto.response.UserResponseDto;
import org.envycorp.userservice.model.entity.Role;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.repository.UserRepository;
import org.envycorp.userservice.service.RoleService;
import org.envycorp.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private Role defaultRole;
    private User testUser;
    private UserResponseDto testUserResponseDto;

    @BeforeEach
    void setUp() {
        defaultRole = new Role(1L, "ROLE_USER");
        testUser = new User(1L, "test@gmail.com", "testuser", "password123", defaultRole);
        testUserResponseDto = new UserResponseDto(1L, "test@gmail.com", "testuser",
                new RoleResponseDto(1L, "ROLE_USER"));
    }

    // -------------------------------------------------------------------------
    // createUser
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("createUser()")
    class CreateUser {

        @Test
        @DisplayName("should create user successfully when email and username are available")
        void createUser_success() {
            CreateUserRequestDto dto = new CreateUserRequestDto("testuser", "test@gmail.com", "password123");

            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
            when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
            when(modelMapper.map(dto, User.class)).thenReturn(testUser);
            when(roleService.getUserRole()).thenReturn(defaultRole);
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            UserResponseDto result = userService.createUser(dto);

            assertThat(result).isEqualTo(testUserResponseDto);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should throw EmailIsAlreadyTakenException when email is taken")
        void createUser_emailTaken_throwsException() {
            CreateUserRequestDto dto = new CreateUserRequestDto("testuser", "test@gmail.com", "password123");
            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(dto))
                    .isInstanceOf(EmailIsAlreadyTakenException.class)
                    .hasMessageContaining("Email is already taken");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw UsernameIsAlreadyTakenException when username is taken")
        void createUser_usernameTaken_throwsException() {
            CreateUserRequestDto dto = new CreateUserRequestDto("testuser", "test@gmail.com", "password123");
            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
            when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(dto))
                    .isInstanceOf(UsernameIsAlreadyTakenException.class)
                    .hasMessageContaining("Username is already taken");

            verify(userRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // login
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("should return user on valid credentials")
        void login_validCredentials_returnsUser() {
            AuthUserRequestDto dto = new AuthUserRequestDto("testuser", "password123");

            when(userRepository.findByUsername("testuser")).thenReturn(testUser);
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            UserResponseDto result = userService.login(dto);

            assertThat(result).isEqualTo(testUserResponseDto);
        }

        @Test
        @DisplayName("should throw InvalidUserLoginDataException when user not found")
        void login_userNotFound_throwsException() {
            AuthUserRequestDto dto = new AuthUserRequestDto("unknown", "password123");
            when(userRepository.findByUsername("unknown")).thenReturn(null);

            assertThatThrownBy(() -> userService.login(dto))
                    .isInstanceOf(InvalidUserLoginDataException.class)
                    .hasMessageContaining("Invalid username or password");
        }

        @Test
        @DisplayName("should throw InvalidUserLoginDataException when password is wrong")
        void login_wrongPassword_throwsException() {
            AuthUserRequestDto dto = new AuthUserRequestDto("testuser", "wrongpassword");
            when(userRepository.findByUsername("testuser")).thenReturn(testUser);

            assertThatThrownBy(() -> userService.login(dto))
                    .isInstanceOf(InvalidUserLoginDataException.class)
                    .hasMessageContaining("Invalid username or password");
        }
    }

    // -------------------------------------------------------------------------
    // getAllUsers
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsers {

        @Test
        @DisplayName("should return list of all users")
        void getAllUsers_returnsUserList() {
            User user2 = new User(2L, "user2@gmail.com", "user2", "pass", defaultRole);
            UserResponseDto dto2 = new UserResponseDto(2L, "user2@gmail.com", "user2",
                    new RoleResponseDto(1L, "ROLE_USER"));

            when(userRepository.findAll()).thenReturn(List.of(testUser, user2));
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);
            when(modelMapper.map(user2, UserResponseDto.class)).thenReturn(dto2);

            List<UserResponseDto> result = userService.getAllUsers();

            assertThat(result).hasSize(2).containsExactlyInAnyOrder(testUserResponseDto, dto2);
        }

        @Test
        @DisplayName("should return empty list when no users exist")
        void getAllUsers_emptyList() {
            when(userRepository.findAll()).thenReturn(List.of());

            assertThat(userService.getAllUsers()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // getUserById
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("getUserById()")
    class GetUserById {

        @Test
        @DisplayName("should return user when found")
        void getUserById_found_returnsUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            assertThat(userService.getUserById(1L)).isEqualTo(testUserResponseDto);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when not found")
        void getUserById_notFound_throwsException() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // -------------------------------------------------------------------------
    // patchUser
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("patchUser()")
    class PatchUser {

        @Test
        @DisplayName("should update all fields when provided and available")
        void patchUser_allFields_updatesSuccessfully() {
            PatchUserRequestDto dto = new PatchUserRequestDto("newuser", "newpass", "new@gmail.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            UserResponseDto result = userService.patchUser(1L, dto);

            assertThat(result).isEqualTo(testUserResponseDto);
            assertThat(testUser.getPassword()).isEqualTo("newpass");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user to patch does not exist")
        void patchUser_userNotFound_throwsException() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.patchUser(99L, new PatchUserRequestDto()))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("should throw EmailIsAlreadyTakenException when new email is taken")
        void patchUser_newEmailTaken_throwsException() {
            PatchUserRequestDto dto = new PatchUserRequestDto(null, null, "taken@gmail.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("taken@gmail.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.patchUser(1L, dto))
                    .isInstanceOf(EmailIsAlreadyTakenException.class);
        }

        @Test
        @DisplayName("should not update email if same as existing")
        void patchUser_sameEmail_noUpdate() {
            PatchUserRequestDto dto = new PatchUserRequestDto(null, null, "test@gmail.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            userService.patchUser(1L, dto);

            verify(userRepository, never()).existsByEmail(anyString());
        }
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete user and return dto")
        void delete_success() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            UserResponseDto result = userService.delete(1L);

            assertThat(result).isEqualTo(testUserResponseDto);
            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user not found")
        void delete_notFound_throwsException() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(99L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // changeUserRole
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("changeUserRole()")
    class ChangeUserRole {

        @Test
        @DisplayName("should change user role successfully")
        void changeUserRole_success() {
            Role adminRole = new Role(2L, "ROLE_ADMIN");

            when(roleService.findRoleById(2L)).thenReturn(adminRole);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(modelMapper.map(testUser, UserResponseDto.class)).thenReturn(testUserResponseDto);

            userService.changeUserRole(1L, 2L);

            assertThat(testUser.getRole()).isEqualTo(adminRole);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user not found")
        void changeUserRole_userNotFound_throwsException() {
            Role adminRole = new Role(2L, "ROLE_ADMIN");
            when(roleService.findRoleById(2L)).thenReturn(adminRole);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.changeUserRole(99L, 2L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}