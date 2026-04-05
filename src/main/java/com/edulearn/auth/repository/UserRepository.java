package com.edulearn.auth.repository;

import com.edulearn.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            select count(distinct u.id)
            from User u join u.roles r
            where r.name = :roleName
            """)
    long countByRoleName(@Param("roleName") com.edulearn.auth.entity.RoleName roleName);
}
