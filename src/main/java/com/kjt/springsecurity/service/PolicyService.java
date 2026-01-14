package com.kjt.springsecurity.service;

import com.kjt.springsecurity.dto.PolicyDto;

import java.util.List;

public interface PolicyService {
    PolicyDto createPolicy(PolicyDto dto);

    PolicyDto getPolicy(Long id);

    List<PolicyDto> getAllPolicies();

    PolicyDto updatePolicy(Long id, PolicyDto dto);

    void deletePolicy(Long id);

    boolean checkAccess(String resourceType, String action, Long userId, Long resourceId);
}
