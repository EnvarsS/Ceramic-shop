package org.envycorp.cartservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.envycorp.cartservice.model.dto.request.AddCartItemRequestDto;
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
        return cartService.getCart(customerId);
    }

    @PostMapping("/item")
    @ResponseStatus(HttpStatus.OK)
    public CartResponseDto addCartItem(
            @RequestHeader("X-User-Id") Long customerId,
            @RequestBody @Valid AddCartItemRequestDto addCartItemRequestDto) {
        return cartService.addItem(customerId, addCartItemRequestDto);
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
