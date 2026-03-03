package org.envycorp.userservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.exception.user.NoPermissionException;
import org.envycorp.userservice.exception.user.UserNotFoundException;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.envycorp.userservice.model.dto.response.UserResponseDto;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

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

    public UserResponseDto patchUser(Long id, Long requesterId, String role, PatchUserRequestDto dto) {
        if (!requesterId.equals(id) && !role.equals("ROLE_ADMIN")) {
            throw new NoPermissionException("You don't have permission to update this user");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (dto.getAddress() != null) {
            existingUser.setAddress(dto.getAddress());
        }

        if (dto.getBirthDate() != null) {
            existingUser.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        }

        return modelMapper.map(userRepository.save(existingUser), UserResponseDto.class);
    }

}
