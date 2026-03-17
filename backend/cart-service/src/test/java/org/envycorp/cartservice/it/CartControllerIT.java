package org.envycorp.cartservice.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.envycorp.cartservice.model.dto.request.AddCartItemRequestDto;
import org.envycorp.cartservice.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CartControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CartRepository cartRepository;

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

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    @Transactional
    void cleanUp() {
        cartRepository.deleteAll();
        cartRepository.flush();
    }

    @Test
    void getCart_shouldReturn200_andEmptyCart_whenNoCartExists() throws Exception {
        mockMvc.perform(asUser(get("/cart"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void getCart_shouldIsolateCartsByUser() throws Exception {
        mockMvc.perform(asUser(get("/cart"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L));

        mockMvc.perform(asUser(get("/cart"), 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(2L));
    }

    @Test
    void addItem_shouldReturn200_andAddItemToCart() throws Exception {
        AddCartItemRequestDto dto = new AddCartItemRequestDto(100L, 2);

        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(100L))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void addItem_shouldReturn200_andIncreaseQuantity_whenSameProductAddedTwice() throws Exception {
        AddCartItemRequestDto first = new AddCartItemRequestDto(100L, 2);
        AddCartItemRequestDto second = new AddCartItemRequestDto(100L, 3);

        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(first)), 1L));

        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(second)), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity").value(5)); // 2 + 3
    }

    @Test
    void addItem_shouldReturn200_andAddMultipleDifferentItems() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AddCartItemRequestDto(100L, 1))), 1L));

        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new AddCartItemRequestDto(200L, 2))), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void addItem_shouldReturn200_whenAdmin() throws Exception {
        AddCartItemRequestDto dto = new AddCartItemRequestDto(100L, 1);

        mockMvc.perform(asAdmin(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(9L));
    }

    @Test
    void addItem_shouldReturn400_whenQuantityIsZero() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new AddCartItemRequestDto(100L, 0))), 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturn400_whenQuantityIsNegative() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new AddCartItemRequestDto(100L, -1))), 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturn400_whenProductIdIsNull() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new AddCartItemRequestDto(null, 2))), 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_shouldReturn400_whenQuantityIsNull() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new AddCartItemRequestDto(100L, null))), 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteItem_shouldReturn200_andRemoveItem_whenItemExists() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AddCartItemRequestDto(100L, 2))), 1L));

        mockMvc.perform(asUser(delete("/cart/item/100"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void deleteItem_shouldReturn200_andRemoveOnlyTargetItem_whenMultipleItemsExist() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AddCartItemRequestDto(100L, 1))), 1L));
        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AddCartItemRequestDto(200L, 2))), 1L));

        mockMvc.perform(asUser(delete("/cart/item/100"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(200L));
    }

    @Test
    void deleteItem_shouldReturn200_andNotChangeCart_whenItemDoesNotExist() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AddCartItemRequestDto(100L, 2))), 1L));

        mockMvc.perform(asUser(delete("/cart/item/999"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1))); // item 100 still there
    }

    @Test
    void deleteItem_shouldReturn200_andReturnEmptyCart_whenCartDoesNotExist() throws Exception {
        mockMvc.perform(asUser(delete("/cart/item/100"), 50L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(50L))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void clearCart_shouldReturn204_whenCartDoesNotExist() throws Exception {
        mockMvc.perform(asUser(delete("/cart"), 50L))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearCart_shouldOnlyAffectOwnerCart_notOtherUsers() throws Exception {
        mockMvc.perform(asUser(post("/cart/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new AddCartItemRequestDto(100L, 2))), 1L));

        mockMvc.perform(asUser(delete("/cart"), 2L))
                .andExpect(status().isNoContent());

        mockMvc.perform(asUser(get("/cart"), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }
}
