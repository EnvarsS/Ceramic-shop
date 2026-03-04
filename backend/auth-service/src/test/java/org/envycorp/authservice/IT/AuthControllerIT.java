package org.envycorp.authservice.IT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envycorp.authservice.config.TestKafkaConfig;
import org.envycorp.authservice.model.dto.UserLoginRequestDto;
import org.envycorp.authservice.model.dto.UserPatchRequestDto;
import org.envycorp.authservice.model.dto.UserRegisterRequestDto;
import org.envycorp.authservice.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
public class AuthControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(kafkaProducerService);
    }

    @Test
    void register_shouldReturn200_andToken_whenValidData() throws Exception {
        UserRegisterRequestDto dto =
                new UserRegisterRequestDto("brandnew", "brandnew@gmail.com", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));

        verify(kafkaProducerService).publishUserRegistered(any());
    }

    @Test
    void register_shouldReturn400_whenUsernameAlreadyTaken() throws Exception {
        UserRegisterRequestDto dto =
                new UserRegisterRequestDto("user1", "unique@gmail.com", "pass");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void register_shouldReturn400_whenEmailAlreadyTaken() throws Exception {
        UserRegisterRequestDto dto =
                new UserRegisterRequestDto("uniqueusername", "user1@gmail.com", "pass");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void login_shouldReturn201_andToken_withSeedUser() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("user1", "u1password1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    void login_shouldReturn201_andToken_withSeedAdmin() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("admin1", "a1password1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    void login_shouldReturn400_whenPasswordWrong() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("user1", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void login_shouldReturn400_whenUsernameNotFound() throws Exception {
        UserLoginRequestDto dto = new UserLoginRequestDto("nonexistent", "anypassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deleteUser_shouldReturn204_whenDeletingSelf() throws Exception {
        // Use user 2 so other tests that rely on user 1 are not affected
        mockMvc.perform(delete("/auth/users/2")
                        .header("X-User-Id", "2")
                        .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isNoContent());

        verify(kafkaProducerService).publishUserDeleted(any());
    }

    @Test
    void deleteUser_shouldReturn400_whenNotOwnerAndNotAdmin() throws Exception {
        mockMvc.perform(delete("/auth/users/4")
                        .header("X-User-Id", "3")
                        .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deleteUser_shouldReturn204_whenAdmin() throws Exception {
        mockMvc.perform(delete("/auth/users/5")
                        .header("X-User-Id", "9")
                        .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isNoContent());

        verify(kafkaProducerService).publishUserDeleted(any());
    }


    @Test
    void updateUser_shouldReturn200_whenOwnerUpdatesOwnUsername() throws Exception {
        UserPatchRequestDto dto = new UserPatchRequestDto("user6updated", null, null);

        mockMvc.perform(patch("/auth/users/6")
                        .header("X-User-Id", "6")
                        .header("X-User-Role", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(kafkaProducerService).publishUserUpdated(any());
    }

    @Test
    void updateUser_shouldReturn200_whenAdminUpdatesAnyUser() throws Exception {
        UserPatchRequestDto dto = new UserPatchRequestDto("user7updated", null, null);

        mockMvc.perform(patch("/auth/users/7")
                        .header("X-User-Id", "9")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(kafkaProducerService).publishUserUpdated(any());
    }

    @Test
    void updateUser_shouldReturn400_whenNotOwnerAndNotAdmin() throws Exception {
        UserPatchRequestDto dto = new UserPatchRequestDto("hacked", null, null);

        mockMvc.perform(patch("/auth/users/8")
                        .header("X-User-Id", "3")
                        .header("X-User-Role", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateUser_shouldReturn400_whenNewUsernameAlreadyTaken() throws Exception {
        UserPatchRequestDto dto = new UserPatchRequestDto("user1", null, null);

        mockMvc.perform(patch("/auth/users/8")
                        .header("X-User-Id", "8")
                        .header("X-User-Role", "ROLE_USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}









