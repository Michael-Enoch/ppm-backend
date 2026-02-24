package com.company.ppm.repository;

import com.company.ppm.domain.entity.Role;
import com.company.ppm.domain.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
