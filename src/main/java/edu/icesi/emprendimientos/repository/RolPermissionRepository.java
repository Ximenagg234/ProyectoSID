package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.RolPermission;
import edu.icesi.emprendimientos.entity.keys.RolPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolPermissionRepository extends JpaRepository<RolPermission, RolPermissionId> {
}