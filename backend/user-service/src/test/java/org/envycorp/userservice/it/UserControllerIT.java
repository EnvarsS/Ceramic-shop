package org.envycorp.userservice.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envycorp.userservice.controller.ServiceExceptionHandler;
import org.envycorp.userservice.model.dto.request.AuthUserRequestDto;
import org.envycorp.userservice.model.dto.request.CreateUserRequestDto;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.envycorp.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ServiceExceptionHandler.class)
@Transactional
@DisplayName("UserController Integration Tests")
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // POST /users/registration
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("POST /users/registration")
    class Registration {

        @Test
        @DisplayName("201 - user is created and stored in DB")
        void register_201() throws Exception {
            CreateUserRequestDto req =
                    new CreateUserRequestDto("testuser", "test@gmail.com", "password123");

            mockMvc.perform(post("/users/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@gmail.com"));

            assert userRepository.existsByEmail("test@gmail.com");
        }

        @Test
        @DisplayName("400 - duplicate email")
        void register_duplicateEmail_400() throws Exception {
            register_201();

            CreateUserRequestDto req =
                    new CreateUserRequestDto("another", "test@gmail.com", "password123");

            mockMvc.perform(post("/users/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    // -------------------------------------------------------------------------
    // POST /users/login
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("POST /users/login")
    class Login {

        @Test
        @DisplayName("200 - login success")
        void login_200() throws Exception {
            registerUser();

            AuthUserRequestDto req =
                    new AuthUserRequestDto("testuser", "password123");

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("400 - invalid password")
        void login_invalid_400() throws Exception {
            registerUser();

            AuthUserRequestDto req =
                    new AuthUserRequestDto("testuser", "wrong");

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    // -------------------------------------------------------------------------
    // GET /users
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GET /users")
    class GetAllUsers {

        @Test
        @DisplayName("200 - returns users from DB")
        void getAll_200() throws Exception {
            registerUser();

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("testuser"));
        }
    }

    // -------------------------------------------------------------------------
    // PATCH /users/{id}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("PATCH /users/{id}")
    class PatchUser {

        @Test
        @DisplayName("200 - user updated")
        void patch_200() throws Exception {
            Long userId = registerUser();

            PatchUserRequestDto req =
                    new PatchUserRequestDto("newname", null, null);

            mockMvc.perform(patch("/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("newname"));
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /users/{id}
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("DELETE /users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("204 - user deleted")
        void delete_204() throws Exception {
            Long userId = registerUser();

            mockMvc.perform(delete("/users/{id}", userId))
                    .andExpect(status().isNoContent());

            assert userRepository.findById(userId).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private Long registerUser() throws Exception {
        CreateUserRequestDto req =
                new CreateUserRequestDto("testuser", "test@gmail.com", "password123");

        String response =
                mockMvc.perform(post("/users/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}
