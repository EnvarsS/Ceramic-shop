package org.envycorp.authservice.repository;

import org.envycorp.authservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
