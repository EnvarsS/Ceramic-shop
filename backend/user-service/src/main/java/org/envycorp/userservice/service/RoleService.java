package org.envycorp.userservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.exception.role.RoleIsAlreadyExisted;
import org.envycorp.userservice.exception.role.RoleNotFoundException;
import org.envycorp.userservice.model.dto.request.CreateRoleRequestDto;
import org.envycorp.userservice.model.dto.response.RoleResponseDto;
import org.envycorp.userservice.model.entity.Role;
import org.envycorp.userservice.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER");
    }

    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
    }

    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> modelMapper.map(role, RoleResponseDto.class))
                .toList();
    }

    public RoleResponseDto getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(role -> modelMapper.map(role, RoleResponseDto.class))
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
    }

    @Transactional
    public RoleResponseDto createRole(CreateRoleRequestDto createRole) {
        if(roleRepository.existsByName(createRole.getName())) {
            throw new RoleIsAlreadyExisted("Role with name " + createRole.getName() + " already exists");
        }

        Role newRole = modelMapper.map(createRole, Role.class);
        return modelMapper.map(roleRepository.save(newRole), RoleResponseDto.class);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));

        roleRepository.delete(role);
    }
}
