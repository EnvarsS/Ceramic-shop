package org.envycorp.userservice.it;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.envycorp.userservice.config.TestKafkaConfig;
import org.envycorp.userservice.model.dto.request.PatchUserRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private MockHttpServletRequestBuilder asUser(MockHttpServletRequestBuilder request, Long userId) {
        return request
                .header("X-User-Id", userId)
                .header("X-User-Role", "ROLE_USER")
                .header("X-User-Username", "user" + userId);
    }

    private MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return request
                .header("X-User-Id", 9L)
                .header("X-User-Role", "ROLE_ADMIN")
                .header("X-User-Username", "admin1");
    }

    @Test
    void getAllUsers_shouldReturn200_andList_whenAdmin() throws Exception {
        mockMvc.perform(asAdmin(get("/users")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(10))); // 10 users from seed data
    }

    @Test
    void getAllUsers_shouldReturn403_whenRegularUser() throws Exception {
        mockMvc.perform(asUser(get("/users"), 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_shouldReturn200_whenAdminAndUserExists() throws Exception {
        mockMvc.perform(asAdmin(get("/users/1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserById_shouldReturn400_whenAdminAndUserNotFound() throws Exception {
        mockMvc.perform(asAdmin(get("/users/9999")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_shouldReturn403_whenRegularUser() throws Exception {
        mockMvc.perform(asUser(get("/users/1"), 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMe_shouldReturn200_andOwnData_whenAuthenticated() throws Exception {
        mockMvc.perform(asUser(get("/users/me"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getMe_shouldReturn200_whenAdmin() throws Exception {
        mockMvc.perform(asAdmin(get("/users/me")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9L));
    }

    @Test
    void getMe_shouldReturn400_whenUserNotFoundInUserService() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("X-User-Id", 999L)
                        .header("X-User-Role", "ROLE_USER")
                        .header("X-User-Username", "ghost"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchUser_shouldReturn200_whenOwnerUpdatesAddress() throws Exception {
        PatchUserRequestDto dto = new PatchUserRequestDto("New Address Berlin", null);

        mockMvc.perform(asUser(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("New Address Berlin"));
    }

    @Test
    void patchUser_shouldReturn200_whenOwnerUpdatesBirthDate() throws Exception {
        PatchUserRequestDto dto = new PatchUserRequestDto(null, "1995-05-20");

        mockMvc.perform(asUser(patch("/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)), 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthDate").value("1995-05-20"));
    }

    @Test
    void patchUser_shouldReturn200_whenOwnerUpdatesBothFields() throws Exception {
        PatchUserRequestDto dto = new PatchUserRequestDto("Hamburg Hafen", "1990-03-10");

        mockMvc.perform(asUser(patch("/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)), 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Hamburg Hafen"))
                .andExpect(jsonPath("$.birthDate").value("1990-03-10"));
    }

    @Test
    void patchUser_shouldReturn200_whenAdminUpdatesAnyUser() throws Exception {
        PatchUserRequestDto dto = new PatchUserRequestDto("Munich Center", null);

        mockMvc.perform(asAdmin(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("Munich Center"));
    }

    @Test
    void patchUser_shouldReturn400_whenNotOwnerAndNotAdmin() throws Exception {
        PatchUserRequestDto dto = new PatchUserRequestDto("Hacked", null);

        mockMvc.perform(asUser(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)), 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchUser_shouldReturn400_whenUserNotFound() throws Exception {
        PatchUserRequestDto dto = new PatchUserRequestDto("Anywhere", null);

        mockMvc.perform(asAdmin(patch("/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }
}
