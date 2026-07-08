package com.dynamiconlineshopping.backend.repository.user;

import com.dynamiconlineshopping.backend.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    long countByName(String name);
}