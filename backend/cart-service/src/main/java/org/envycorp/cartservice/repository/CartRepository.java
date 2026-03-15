package org.envycorp.cartservice.repository;

import org.envycorp.cartservice.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findCartByCustomerId(Long customerId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems WHERE c.customerId = :customerId")
    Optional<Cart> findCartByCustomerIdEager(Long customerId);
}
