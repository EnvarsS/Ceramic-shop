package org.envycorp.userservice.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envycorp.userservice.controller.ServiceExceptionHandler;
import org.envycorp.userservice.model.dto.request.CreateRoleRequestDto;
import org.envycorp.userservice.model.entity.Role;
import org.envycorp.userservice.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ServiceExceptionHandler.class)
@Transactional
@DisplayName("RoleController Integration Tests")
class RoleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // GET /roles
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /roles")
    class GetAllRoles {

        @Test
        @DisplayName("200 - returns all roles from DB")
        void getAll_200() throws Exception {
            roleRepository.save(new Role(null, "ROLE_USER"));
            roleRepository.save(new Role(null, "ROLE_ADMIN"));

            mockMvc.perform(get("/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"))
                    .andExpect(jsonPath("$[1].name").value("ROLE_USER"));
        }

        @Test
        @DisplayName("200 - returns empty list")
        void getAll_empty_200() throws Exception {
            mockMvc.perform(get("/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // -------------------------------------------------------------------------
    // GET /roles/{id}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /roles/{id}")
    class GetRoleById {

        @Test
        @DisplayName("200 - role found")
        void getById_200() throws Exception {
            Role role = roleRepository.save(new Role(null, "ROLE_USER"));

            mockMvc.perform(get("/roles/{id}", role.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(role.getId()))
                    .andExpect(jsonPath("$.name").value("ROLE_USER"));
        }

        @Test
        @DisplayName("400 - role not found")
        void getById_notFound_400() throws Exception {
            mockMvc.perform(get("/roles/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Role not found with id: 999"));
        }
    }

    // -------------------------------------------------------------------------
    // POST /roles
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("POST /roles")
    class CreateRole {

        @Test
        @DisplayName("201 - role created")
        void create_201() throws Exception {
            CreateRoleRequestDto req =
                    new CreateRoleRequestDto("ROLE_MODERATOR");

            mockMvc.perform(post("/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("ROLE_MODERATOR"));

            assert roleRepository.existsByName("ROLE_MODERATOR");
        }

        @Test
        @DisplayName("400 - role already exists")
        void create_duplicate_400() throws Exception {
            roleRepository.save(new Role(null, "ROLE_USER"));

            CreateRoleRequestDto req =
                    new CreateRoleRequestDto("ROLE_USER");

            mockMvc.perform(post("/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Role with name ROLE_USER already exists"));
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /roles
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("DELETE /roles")
    class DeleteRole {

        @Test
        @DisplayName("204 - role deleted")
        void delete_204() throws Exception {
            Role role = roleRepository.save(new Role(null, "ROLE_USER"));

            mockMvc.perform(delete("/roles")
                            .param("id", role.getId().toString()))
                    .andExpect(status().isNoContent());

            assert roleRepository.findById(role.getId()).isEmpty();
        }

        @Test
        @DisplayName("400 - role not found")
        void delete_notFound_400() throws Exception {
            mockMvc.perform(delete("/roles")
                            .param("id", "999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Role not found with id: 999"));
        }
    }
}