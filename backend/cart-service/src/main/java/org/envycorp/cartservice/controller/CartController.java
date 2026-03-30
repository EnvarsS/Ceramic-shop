package org.envycorp.cartservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.cartservice.model.dto.response.CartResponseDto;
import org.envycorp.cartservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartResponseDto getCart(@RequestHeader("X-User-Id") Long customerId) {
        System.out.println(customerId);
        return cartService.getCart(customerId);
    }

    @PatchMapping("/item/{productId}/increase")
    @ResponseStatus(HttpStatus.OK)
    public CartResponseDto increaseCartItemQuantity(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long productId
    ){
        return cartService.increaseCartItemQuantity(customerId, productId);
    }

    @PatchMapping("/item/{productId}/decrease")
    @ResponseStatus(HttpStatus.OK)
    public CartResponseDto decreaseCartItemQuantity(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long productId
    ){
        return cartService.decreaseCartItemQuantity(customerId, productId);
    }

    @DeleteMapping("/item/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public CartResponseDto deleteCartItem(
            @RequestHeader("X-User-Id") Long customerId,
            @PathVariable Long productId
    ){
        return cartService.removeItem(customerId, productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@RequestHeader("X-User-Id") Long customerId){
        cartService.clearCart(customerId);
    }
}
