package org.envycorp.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.cartservice.model.dto.request.AddCartItemRequestDto;
import org.envycorp.cartservice.model.dto.response.CartResponseDto;
import org.envycorp.cartservice.model.entity.Cart;
import org.envycorp.cartservice.model.entity.CartItem;
import org.envycorp.cartservice.repository.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    public CartResponseDto getCart(Long customerId) {
        return modelMapper.map(getOrCreateCartEager(customerId), CartResponseDto.class);
    }

    @Transactional
    public CartResponseDto addItem(Long customerId, AddCartItemRequestDto cartItem) {
        Cart cart = getOrCreateCartLazy(customerId);

        cart.getCartItems().stream()
                .filter(item -> Objects.equals(item.getProductId(), cartItem.getProductId()))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + cartItem.getQuantity()),
                        () -> {
                            CartItem item = new CartItem();
                            item.setProductId(cartItem.getProductId());
                            item.setQuantity(cartItem.getQuantity());
                            item.setCart(cart);
                            cart.getCartItems().add(item);
                        });

        return modelMapper.map(cartRepository.save(cart), CartResponseDto.class);
    }

    public CartResponseDto removeItem(Long customerId, Long productId) {
        Cart cart = getOrCreateCartLazy(customerId);
        cart.getCartItems().removeIf(item -> Objects.equals(item.getProductId(), productId));
        return modelMapper.map(cartRepository.save(cart), CartResponseDto.class);
    }

    public void clearCart(Long customerId){
        cartRepository.findCartByCustomerId(customerId)
                .ifPresent(cart -> {
                    cart.getCartItems().clear();
                    cartRepository.save(cart);
                });
    }

    private Cart getOrCreateCartEager(Long customerId) {
        return cartRepository.findCartByCustomerIdEager(customerId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomerId(customerId);
                    return cartRepository.save(cart);
                });
    }

    private Cart getOrCreateCartLazy(Long customerId) {
        return cartRepository.findCartByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomerId(customerId);
                    return cartRepository.save(cart);
                });
    }
}
