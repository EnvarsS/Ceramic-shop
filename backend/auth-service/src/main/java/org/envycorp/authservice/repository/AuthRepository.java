package org.envycorp.authservice.repository;

import org.envycorp.authservice.model.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<UserAuth, Long> {
    boolean existsUserAuthByUsername(String username);

    UserAuth findByUsername(String username);
}
