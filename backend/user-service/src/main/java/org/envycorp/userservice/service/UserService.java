package org.envycorp.userservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.exception.user.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exception.user.InvalidUserLoginDataException;
import org.envycorp.userservice.exception.user.UserNotFoundException;
import org.envycorp.userservice.exception.user.UsernameIsAlreadyTakenException;
import org.envycorp.userservice.model.dto.request.AuthUserRequestDto;
import org.envycorp.userservice.model.dto.request.CreateUserRequestDto;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.envycorp.userservice.model.dto.response.UserResponseDto;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto createUser) {
        isEmailTaken(createUser.getEmail());
        isUsernameTaken(createUser.getUsername());

        User newUser = modelMapper.map(createUser, User.class);

        return modelMapper.map(userRepository.save(newUser), UserResponseDto.class);
    }

    public UserResponseDto login(AuthUserRequestDto reqUser) {
        User user = userRepository.findByUsername(reqUser.getUsername());

        return modelMapper.map(user, UserResponseDto.class);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
    }

    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public UserResponseDto patchUser(Long id, PatchUserRequestDto user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            isEmailTaken(user.getEmail());
            existingUser.setEmail(user.getEmail());
        }

        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            isUsernameTaken(user.getUsername());
            existingUser.setUsername(user.getUsername());
        }

        return modelMapper.map(userRepository.save(existingUser), UserResponseDto.class);
    }

    @Transactional
    public UserResponseDto delete(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userRepository.delete(userToDelete);
        return modelMapper.map(userToDelete, UserResponseDto.class);
    }

    @Transactional
    public UserResponseDto changeUserRole(Long id, Long roleId) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return modelMapper.map(userRepository.save(user), UserResponseDto.class);
    }

    private void isEmailTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailIsAlreadyTakenException("Email is already taken");
        }
    }

    private void isUsernameTaken(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameIsAlreadyTakenException("Username is already taken");
        }
    }


}
