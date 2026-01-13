package com.kjt.springsecurity.repository;

import com.kjt.springsecurity.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByName(String name);
    Permission findById(long id);
}
