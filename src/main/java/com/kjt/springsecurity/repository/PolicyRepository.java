package com.kjt.springsecurity.repository;

import com.kjt.springsecurity.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p WHERE p.resourceType = :resourceType " +
            "AND p.action = :action AND p.isActive = true AND p.isDeleted = false " +
            "ORDER BY p.priority DESC")
    List<Policy> findApplicablePolicies(@Param("resourceType") String resourceType,
            @Param("action") String action);

    List<Policy> findByResourceTypeAndIsActiveTrueAndIsDeletedFalse(String resourceType);

    List<Policy> findByIsActiveTrueAndIsDeletedFalseOrderByPriorityDesc();
}
