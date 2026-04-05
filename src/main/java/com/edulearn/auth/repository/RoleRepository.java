package com.edulearn.auth.repository;

import com.edulearn.auth.entity.Role;
import com.edulearn.auth.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleName name);
}
