package org.envycorp.userservice.unit;

import org.envycorp.userservice.exception.role.RoleIsAlreadyExisted;
import org.envycorp.userservice.exception.role.RoleNotFoundException;
import org.envycorp.userservice.model.dto.request.CreateRoleRequestDto;
import org.envycorp.userservice.model.dto.response.RoleResponseDto;
import org.envycorp.userservice.model.entity.Role;
import org.envycorp.userservice.repository.RoleRepository;
import org.envycorp.userservice.service.RoleService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Unit Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleService roleService;

    private Role userRole;
    private Role adminRole;
    private RoleResponseDto userRoleDto;
    private RoleResponseDto adminRoleDto;

    @BeforeEach
    void setUp() {
        userRole  = new Role(1L, "ROLE_USER");
        adminRole = new Role(2L, "ROLE_ADMIN");
        userRoleDto  = new RoleResponseDto(1L, "ROLE_USER");
        adminRoleDto = new RoleResponseDto(2L, "ROLE_ADMIN");
    }

    // -------------------------------------------------------------------------
    // getAllRoles
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("getAllRoles()")
    class GetAllRoles {

        @Test
        @DisplayName("should return all roles")
        void getAllRoles_returnsList() {
            when(roleRepository.findAll()).thenReturn(List.of(userRole, adminRole));
            when(modelMapper.map(userRole,  RoleResponseDto.class)).thenReturn(userRoleDto);
            when(modelMapper.map(adminRole, RoleResponseDto.class)).thenReturn(adminRoleDto);

            List<RoleResponseDto> result = roleService.getAllRoles();

            assertThat(result).hasSize(2).containsExactlyInAnyOrder(userRoleDto, adminRoleDto);
        }

        @Test
        @DisplayName("should return empty list when no roles exist")
        void getAllRoles_empty() {
            when(roleRepository.findAll()).thenReturn(List.of());
            assertThat(roleService.getAllRoles()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // getRoleById
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("getRoleById()")
    class GetRoleById {

        @Test
        @DisplayName("should return role when found")
        void getRoleById_found() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));
            when(modelMapper.map(userRole, RoleResponseDto.class)).thenReturn(userRoleDto);

            assertThat(roleService.getRoleById(1L)).isEqualTo(userRoleDto);
        }

        @Test
        @DisplayName("should throw RoleNotFoundException when not found")
        void getRoleById_notFound_throws() {
            when(roleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> roleService.getRoleById(99L))
                    .isInstanceOf(RoleNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // -------------------------------------------------------------------------
    // createRole
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("createRole()")
    class CreateRole {

        @Test
        @DisplayName("should create role successfully")
        void createRole_success() {
            CreateRoleRequestDto dto = new CreateRoleRequestDto("ROLE_MODERATOR");
            Role newRole = new Role(3L, "ROLE_MODERATOR");
            RoleResponseDto newRoleDto = new RoleResponseDto(3L, "ROLE_MODERATOR");

            when(roleRepository.existsByName("ROLE_MODERATOR")).thenReturn(false);
            when(modelMapper.map(dto, Role.class)).thenReturn(newRole);
            when(roleRepository.save(newRole)).thenReturn(newRole);
            when(modelMapper.map(newRole, RoleResponseDto.class)).thenReturn(newRoleDto);

            RoleResponseDto result = roleService.createRole(dto);

            assertThat(result).isEqualTo(newRoleDto);
        }

        @Test
        @DisplayName("should throw RoleNotFoundException when role name already exists")
        void createRole_alreadyExists_throws() {
            CreateRoleRequestDto dto = new CreateRoleRequestDto("ROLE_USER");
            when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

            assertThatThrownBy(() -> roleService.createRole(dto))
                    .isInstanceOf(RoleIsAlreadyExisted.class)
                    .hasMessageContaining("ROLE_USER");

            verify(roleRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteRole
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("deleteRole()")
    class DeleteRole {

        @Test
        @DisplayName("should delete role when found")
        void deleteRole_success() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));

            roleService.deleteRole(1L);

            verify(roleRepository).delete(userRole);
        }

        @Test
        @DisplayName("should throw RoleNotFoundException when not found")
        void deleteRole_notFound_throws() {
            when(roleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> roleService.deleteRole(99L))
                    .isInstanceOf(RoleNotFoundException.class)
                    .hasMessageContaining("99");

            verify(roleRepository, never()).delete(any());
        }
    }

    // -------------------------------------------------------------------------
    // internal helpers (package-private, tested via reflection or indirectly)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("getUserRole() [internal]")
    class GetUserRole {

        @Test
        @DisplayName("should return ROLE_USER entity")
        void getUserRole_returnsUserRole() {
            when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
            assertThat(roleService.getUserRole()).isEqualTo(userRole);
        }
    }

    @Nested
    @DisplayName("findRoleById() [internal]")
    class FindRoleById {

        @Test
        @DisplayName("should return role entity when found")
        void findRoleById_found() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));
            assertThat(roleService.findRoleById(1L)).isEqualTo(userRole);
        }

        @Test
        @DisplayName("should throw RoleNotFoundException when not found")
        void findRoleById_notFound_throws() {
            when(roleRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> roleService.findRoleById(99L))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }
}