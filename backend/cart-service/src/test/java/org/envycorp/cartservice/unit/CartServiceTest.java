package org.envycorp.cartservice.unit;

import org.envycorp.cartservice.model.dto.request.AddCartItemRequestDto;
import org.envycorp.cartservice.model.dto.response.CartItemResponseDto;
import org.envycorp.cartservice.model.dto.response.CartResponseDto;
import org.envycorp.cartservice.model.entity.Cart;
import org.envycorp.cartservice.model.entity.CartItem;
import org.envycorp.cartservice.repository.CartRepository;
import org.envycorp.cartservice.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CartService cartService;

    @Test
    void getCart_shouldCreateNewCart_whenCartDoesNotExist() {
        Cart newCart = buildCart(1L, 99L);
        CartResponseDto dto = buildCartResponseDto(1L, 99L, List.of());

        when(cartRepository.findCartByCustomerIdEager(99L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(modelMapper.map(newCart, CartResponseDto.class)).thenReturn(dto);

        CartResponseDto result = cartService.getCart(99L);

        assertEquals(1L, result.getId());
        assertEquals(99L, result.getCustomerId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void getCart_shouldReturnExistingCart_whenCartExists() {
        Cart existingCart = buildCart(1L, 1L);
        CartResponseDto dto = buildCartResponseDto(1L, 1L, List.of());

        when(cartRepository.findCartByCustomerIdEager(1L)).thenReturn(Optional.of(existingCart));
        when(modelMapper.map(existingCart, CartResponseDto.class)).thenReturn(dto);

        CartResponseDto result = cartService.getCart(1L);

        assertEquals(1L, result.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItem_shouldCreateCartAndAddItem_whenCartDoesNotExist() {
        Cart newCart = buildCart(1L, 1L);
        CartResponseDto dto = buildCartResponseDto(1L, 1L,
                List.of(new CartItemResponseDto(10L, 2)));
        AddCartItemRequestDto request = new AddCartItemRequestDto(10L, 2);

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(modelMapper.map(any(Cart.class), eq(CartResponseDto.class))).thenReturn(dto);

        CartResponseDto result = cartService.addItem(1L, request);

        assertEquals(1, result.getItems().size());
        assertEquals(10L, result.getItems().get(0).getProductId());
        // save called twice: once to create cart, once to save with new item
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    void addItem_shouldAddNewItem_whenProductNotInCart() {
        Cart cart = buildCart(1L, 1L);
        CartItem existingItem = buildCartItem(1L, 5L, 3, cart);
        cart.getCartItems().add(existingItem);

        CartResponseDto dto = buildCartResponseDto(1L, 1L,
                List.of(new CartItemResponseDto(5L, 3), new CartItemResponseDto(10L, 2)));
        AddCartItemRequestDto request = new AddCartItemRequestDto(10L, 2);

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(dto);

        cartService.addItem(1L, request);

        assertEquals(2, cart.getCartItems().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void addItem_shouldIncreaseQuantity_whenProductAlreadyInCart() {
        Cart cart = buildCart(1L, 1L);
        CartItem existingItem = buildCartItem(1L, 10L, 3, cart);
        cart.getCartItems().add(existingItem);

        CartResponseDto dto = buildCartResponseDto(1L, 1L,
                List.of(new CartItemResponseDto(10L, 5)));
        AddCartItemRequestDto request = new AddCartItemRequestDto(10L, 2);

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(dto);

        cartService.addItem(1L, request);

        // quantity should be 3 + 2 = 5
        assertEquals(5, existingItem.getQuantity());
        assertEquals(1, cart.getCartItems().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void addItem_shouldKeepOtherItemsIntact_whenAddingNewProduct() {
        Cart cart = buildCart(1L, 1L);
        CartItem item1 = buildCartItem(1L, 5L, 3, cart);
        CartItem item2 = buildCartItem(2L, 6L, 2, cart);
        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);

        AddCartItemRequestDto request = new AddCartItemRequestDto(10L, 1);
        CartResponseDto dto = buildCartResponseDto(1L, 1L, List.of());

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(dto);

        cartService.addItem(1L, request);

        assertEquals(3, cart.getCartItems().size());
        assertEquals(3, item1.getQuantity());
        assertEquals(2, item2.getQuantity());
    }

    @Test
    void removeItem_shouldRemoveItem_whenItemExistsInCart() {
        Cart cart = buildCart(1L, 1L);
        CartItem item = buildCartItem(1L, 10L, 2, cart);
        cart.getCartItems().add(item);

        CartResponseDto dto = buildCartResponseDto(1L, 1L, List.of());

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(dto);

        cartService.removeItem(1L, 10L);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeItem_shouldNotChangeCart_whenItemNotInCart() {
        Cart cart = buildCart(1L, 1L);
        CartItem item = buildCartItem(1L, 5L, 2, cart);
        cart.getCartItems().add(item);

        CartResponseDto dto = buildCartResponseDto(1L, 1L,
                List.of(new CartItemResponseDto(5L, 2)));

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(dto);

        cartService.removeItem(1L, 999L);

        assertEquals(1, cart.getCartItems().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeItem_shouldOnlyRemoveTargetItem_whenMultipleItemsExist() {
        Cart cart = buildCart(1L, 1L);
        CartItem item1 = buildCartItem(1L, 10L, 2, cart);
        CartItem item2 = buildCartItem(2L, 20L, 1, cart);
        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);

        CartResponseDto dto = buildCartResponseDto(1L, 1L,
                List.of(new CartItemResponseDto(20L, 1)));

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(dto);

        cartService.removeItem(1L, 10L);

        assertEquals(1, cart.getCartItems().size());
        assertEquals(20L, cart.getCartItems().get(0).getProductId());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeItem_shouldCreateCart_whenCartDoesNotExist() {
        Cart newCart = buildCart(1L, 1L);
        CartResponseDto dto = buildCartResponseDto(1L, 1L, List.of());

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(modelMapper.map(any(Cart.class), eq(CartResponseDto.class))).thenReturn(dto);

        cartService.removeItem(1L, 10L);

        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    void clearCart_shouldClearAllItems_whenCartExists() {
        Cart cart = buildCart(1L, 1L);
        cart.getCartItems().add(buildCartItem(1L, 10L, 2, cart));
        cart.getCartItems().add(buildCartItem(2L, 20L, 1, cart));

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        cartService.clearCart(1L);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_shouldDoNothing_whenCartDoesNotExist() {
        when(cartRepository.findCartByCustomerId(99L)).thenReturn(Optional.empty());

        cartService.clearCart(99L);

        verify(cartRepository, never()).save(any());
    }

    @Test
    void clearCart_shouldDoNothing_whenCartIsAlreadyEmpty() {
        Cart cart = buildCart(1L, 1L); // no items

        when(cartRepository.findCartByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        cartService.clearCart(1L);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    private Cart buildCart(Long id, Long customerId) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setCustomerId(customerId);
        cart.setCartItems(new ArrayList<>());
        return cart;
    }

    private CartItem buildCartItem(long id, Long productId, int quantity, Cart cart) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setCart(cart);
        return item;
    }

    private CartResponseDto buildCartResponseDto(Long id, Long customerId, List<CartItemResponseDto> items) {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(id);
        dto.setCustomerId(customerId);
        dto.setItems(items);
        return dto;
    }
}
