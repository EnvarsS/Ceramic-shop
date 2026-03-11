package org.envycorp.productservice.IT;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.envycorp.productservice.model.dto.request.CreateProductRequestDto;
import org.envycorp.productservice.model.dto.request.PatchProductRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return request
                .header("X-User-Id", 9L)
                .header("X-User-Role", "ROLE_ADMIN")
                .header("X-User-Username", "admin1");
    }

    private MockHttpServletRequestBuilder asUser(MockHttpServletRequestBuilder request) {
        return request
                .header("X-User-Id", 1L)
                .header("X-User-Role", "ROLE_USER")
                .header("X-User-Username", "user1");
    }

    @Test
    void getProducts_shouldReturn200_andPage_whenNoAuth() throws Exception {
        mockMvc.perform(get("/products/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(10))) // PAGE_SIZE = 10
                .andExpect(jsonPath("$.totalElements").value(15));  // 15 seed products
    }

    @Test
    void getProducts_shouldReturn200_andSecondPage() throws Exception {
        mockMvc.perform(get("/products/public").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5))) // remaining 5
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void getProducts_withBlankSearch_shouldReturnFullPage() throws Exception {
        mockMvc.perform(get("/products/public").param("name", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    void createProduct_shouldReturn201_whenAdmin() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("Unique Test Bowl", "A test bowl", new BigDecimal("12.99"), 5);

        mockMvc.perform(asAdmin(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isCreated());
    }

    @Test
    void createProduct_shouldReturn403_whenRegularUser() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("User Bowl", "A bowl", new BigDecimal("12.99"), 5);

        mockMvc.perform(asUser(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_shouldReturn401_whenNoAuth() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("Anon Bowl", "A bowl", new BigDecimal("12.99"), 5);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProduct_shouldReturn400_whenNameAlreadyExists() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("Handmade Ceramic Mug", "A mug", new BigDecimal("12.99"), 5);

        mockMvc.perform(asAdmin(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_shouldReturn400_whenNameIsBlank() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("", "A desc", new BigDecimal("12.99"), 5);

        mockMvc.perform(asAdmin(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_shouldReturn400_whenPriceIsNegative() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("Valid Name XYZ", "A desc", new BigDecimal("-1"), 5);

        mockMvc.perform(asAdmin(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_shouldReturn400_whenQuantityIsNegative() throws Exception {
        CreateProductRequestDto dto = buildCreateDto("Valid Name ABC", "A desc", new BigDecimal("10.00"), -1);

        mockMvc.perform(asAdmin(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_shouldReturn400_whenPriceIsNull() throws Exception {
        CreateProductRequestDto dto = new CreateProductRequestDto("Some Unique Name", "Desc", null, 5);

        mockMvc.perform(asAdmin(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchProduct_shouldReturn200_whenAdminUpdatesName() throws Exception {
        PatchProductRequestDto dto = new PatchProductRequestDto("Updated Mug Name", null, null);

        mockMvc.perform(asAdmin(patch("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Mug Name"));
    }

    @Test
    void patchProduct_shouldReturn200_whenAdminUpdatesPrice() throws Exception {
        PatchProductRequestDto dto = new PatchProductRequestDto(null, null, new BigDecimal("99.99"));

        mockMvc.perform(asAdmin(patch("/products/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void patchProduct_shouldReturn200_whenAdminUpdatesDescription() throws Exception {
        PatchProductRequestDto dto = new PatchProductRequestDto(null, "Updated description here", null);

        mockMvc.perform(asAdmin(patch("/products/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description here"));
    }

    @Test
    void patchProduct_shouldReturn403_whenRegularUser() throws Exception {
        PatchProductRequestDto dto = new PatchProductRequestDto("Hacked", null, null);

        mockMvc.perform(asUser(patch("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchProduct_shouldReturn400_whenProductNotFound() throws Exception {
        PatchProductRequestDto dto = new PatchProductRequestDto("Test", null, null);

        mockMvc.perform(asAdmin(patch("/products/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProduct_shouldReturn204_whenAdmin() throws Exception {
        mockMvc.perform(asAdmin(delete("/products/15")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_shouldReturn403_whenRegularUser() throws Exception {
        mockMvc.perform(asUser(delete("/products/1")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_shouldReturn401_whenNoAuth() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteProduct_shouldReturn400_whenProductNotFound() throws Exception {
        mockMvc.perform(asAdmin(delete("/products/9999")))
                .andExpect(status().isBadRequest());
    }

    private CreateProductRequestDto buildCreateDto(String name, String desc, BigDecimal price, int quantity) {
        return new CreateProductRequestDto(name, desc, price, quantity);
    }
}
