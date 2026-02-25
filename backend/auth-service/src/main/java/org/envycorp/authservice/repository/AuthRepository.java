package org.envycorp.authservice.repository;

import org.envycorp.authservice.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<UserAuth, Long> {
}
