package org.envycorp.userservice.unit;

import org.envycorp.userservice.exception.user.NoPermissionException;
import org.envycorp.userservice.exception.user.UserNotFoundException;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.envycorp.userservice.model.dto.response.UserResponseDto;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.repository.UserRepository;
import org.envycorp.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserService userService;

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_shouldReturnMappedList_whenUsersExist() {
        User user1 = buildUser(1L, "user1", "user1@gmail.com");
        User user2 = buildUser(2L, "user2", "user2@gmail.com");
        UserResponseDto dto1 = buildResponseDto(1L, "Berlin");
        UserResponseDto dto2 = buildResponseDto(2L, "Munich");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(modelMapper.map(user1, UserResponseDto.class)).thenReturn(dto1);
        when(modelMapper.map(user2, UserResponseDto.class)).thenReturn(dto2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getUserById_shouldReturnDto_whenUserExists() {
        User user = buildUser(1L, "user1", "user1@gmail.com");
        UserResponseDto dto = buildResponseDto(1L, "Berlin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        UserResponseDto result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Berlin", result.getAddress());
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("User not found with id: 999", ex.getMessage());
    }

    @Test
    void patchUser_shouldThrowNoPermissionException_whenNotOwnerAndNotAdmin() {
        NoPermissionException ex = assertThrows(
                NoPermissionException.class,
                () -> userService.patchUser(2L, 1L, "ROLE_USER", new PatchUserRequestDto())
        );

        assertEquals("You don't have permission to update this user", ex.getMessage());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void patchUser_shouldThrowUserNotFoundException_whenOwnerButUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.patchUser(1L, 1L, "ROLE_USER", new PatchUserRequestDto())
        );
    }

    @Test
    void patchUser_shouldUpdateAddress_whenOwner() {
        User user = buildUser(1L, "user1", "user1@gmail.com");
        UserResponseDto dto = buildResponseDto(1L, "New Address");
        PatchUserRequestDto request = new PatchUserRequestDto("New Address", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        UserResponseDto result = userService.patchUser(1L, 1L, "ROLE_USER", request);

        assertEquals("New Address", result.getAddress());
        assertEquals("New Address", user.getAddress());
        verify(userRepository).save(user);
    }

    @Test
    void patchUser_shouldUpdateBirthDate_whenOwner() {
        User user = buildUser(1L, "user1", "user1@gmail.com");
        UserResponseDto dto = buildResponseDto(1L, null);
        dto.setBirthDate(LocalDate.of(2000, 1, 1));
        PatchUserRequestDto request = new PatchUserRequestDto(null, "2000-01-01");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        UserResponseDto result = userService.patchUser(1L, 1L, "ROLE_USER", request);

        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthDate());
        verify(userRepository).save(user);
    }

    @Test
    void patchUser_shouldUpdateBothFields_whenOwner() {
        User user = buildUser(1L, "user1", "user1@gmail.com");
        UserResponseDto dto = buildResponseDto(1L, "Hamburg");
        dto.setBirthDate(LocalDate.of(1995, 6, 15));
        PatchUserRequestDto request = new PatchUserRequestDto("Hamburg", "1995-06-15");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        UserResponseDto result = userService.patchUser(1L, 1L, "ROLE_USER", request);

        assertEquals("Hamburg", user.getAddress());
        assertEquals(LocalDate.of(1995, 6, 15), user.getBirthDate());
        verify(userRepository).save(user);
    }

    @Test
    void patchUser_shouldNotUpdate_whenAllFieldsNull() {
        User user = buildUser(1L, "user1", "user1@gmail.com");
        user.setAddress("Original Address");
        UserResponseDto dto = buildResponseDto(1L, "Original Address");
        PatchUserRequestDto request = new PatchUserRequestDto(null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        userService.patchUser(1L, 1L, "ROLE_USER", request);

        assertEquals("Original Address", user.getAddress());
        verify(userRepository).save(user);
    }

    @Test
    void patchUser_shouldAllow_whenAdmin() {
        User user = buildUser(2L, "user2", "user2@gmail.com");
        UserResponseDto dto = buildResponseDto(2L, "Cologne");
        PatchUserRequestDto request = new PatchUserRequestDto("Cologne", null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        // admin (id=9) patching user 2
        UserResponseDto result = userService.patchUser(2L, 9L, "ROLE_ADMIN", request);

        assertEquals("Cologne", result.getAddress());
        verify(userRepository).save(user);
    }

    private User buildUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setAddress(null);
        user.setBirthDate(null);
        return user;
    }

    private UserResponseDto buildResponseDto(Long id, String address) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(id);
        dto.setAddress(address);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }
}