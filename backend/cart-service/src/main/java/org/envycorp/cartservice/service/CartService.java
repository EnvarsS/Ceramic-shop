package org.envycorp.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.cartservice.exception.NoSuchItemException;
import org.envycorp.cartservice.model.dto.response.CartResponseDto;
import org.envycorp.cartservice.model.entity.Cart;
import org.envycorp.cartservice.model.entity.CartItem;
import org.envycorp.cartservice.model.event.CartProductEvent;
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
        Cart cart = getOrCreateCartEager(customerId);
        System.out.println(cart.getCartItems().size());
        return modelMapper.map(getOrCreateCartEager(customerId), CartResponseDto.class);
    }

    @Transactional
    public void addItem(CartProductEvent cartProductEvent) {
        Cart cart = getOrCreateCartLazy(cartProductEvent.userId());

        cart.getCartItems().stream()
                .filter(item -> Objects.equals(item.getProductId(), cartProductEvent.productId()))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + 1),
                        () -> {
                            CartItem item = new CartItem();
                            item.setProductId(cartProductEvent.productId());
                            item.setQuantity(1);
                            item.setCart(cart);
                            cart.getCartItems().add(item);
                        });
    }

    @Transactional
    public CartResponseDto removeItem(Long customerId, Long productId) {
        Cart cart = getOrCreateCartLazy(customerId);
        cart.getCartItems().removeIf(item -> Objects.equals(item.getProductId(), productId));
        return modelMapper.map(cartRepository.save(cart), CartResponseDto.class);
    }

    @Transactional
    public void clearCart(Long customerId) {
        cartRepository.findCartByCustomerId(customerId)
                .ifPresent(cart -> {
                    cart.getCartItems().clear();
                    cartRepository.save(cart);
                });
    }

    @Transactional
        public CartResponseDto increaseCartItemQuantity(Long customerId, Long productId) {
        Cart cart = getOrCreateCartLazy(customerId);
        CartItem item = cart.getCartItems().stream()
                .filter(i -> Objects.equals(i.getProductId(), productId))
                .findFirst()
                .orElseThrow(() -> new NoSuchItemException("Item with product id " + productId + " is not in the cart"));

        item.setQuantity(item.getQuantity() + 1);
        cartRepository.save(cart);

        return modelMapper.map(cartRepository.save(cart), CartResponseDto.class);
    }

    @Transactional
        public CartResponseDto decreaseCartItemQuantity(Long customerId, Long productId) {
        Cart cart = getOrCreateCartLazy(customerId);
        CartItem item = cart.getCartItems().stream()
                .filter(i -> Objects.equals(i.getProductId(), productId))
                .findFirst()
                .orElseThrow(() -> new NoSuchItemException("Item with product id " + productId + " is not in the cart"));

        item.setQuantity(item.getQuantity() - 1);
        cartRepository.save(cart);

        return modelMapper.map(cartRepository.save(cart), CartResponseDto.class);
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
