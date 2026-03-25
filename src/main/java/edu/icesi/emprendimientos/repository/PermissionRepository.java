package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
}