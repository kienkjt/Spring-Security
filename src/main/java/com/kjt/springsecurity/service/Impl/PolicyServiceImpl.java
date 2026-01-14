package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.dto.PolicyDto;
import com.kjt.springsecurity.entity.Policy;
import com.kjt.springsecurity.enums.PolicyEffect;
import com.kjt.springsecurity.repository.PolicyRepository;
import com.kjt.springsecurity.service.PolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public PolicyDto createPolicy(PolicyDto dto) {
        Policy policy = new Policy();
        policy.setName(dto.getName());
        policy.setDescription(dto.getDescription());
        policy.setResourceType(dto.getResourceType());
        policy.setAction(dto.getAction());
        policy.setConditions(dto.getConditions());
        policy.setEffect(PolicyEffect.valueOf(dto.getEffect()));
        policy.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        policy.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        Policy saved = policyRepository.save(policy);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyDto getPolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        return toDto(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyDto> getAllPolicies() {
        return policyRepository.findAll().stream()
                .filter(p -> !p.getIsDeleted())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PolicyDto updatePolicy(Long id, PolicyDto dto) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (dto.getName() != null)
            policy.setName(dto.getName());
        if (dto.getDescription() != null)
            policy.setDescription(dto.getDescription());
        if (dto.getResourceType() != null)
            policy.setResourceType(dto.getResourceType());
        if (dto.getAction() != null)
            policy.setAction(dto.getAction());
        if (dto.getConditions() != null)
            policy.setConditions(dto.getConditions());
        if (dto.getEffect() != null)
            policy.setEffect(PolicyEffect.valueOf(dto.getEffect()));
        if (dto.getPriority() != null)
            policy.setPriority(dto.getPriority());
        if (dto.getIsActive() != null)
            policy.setIsActive(dto.getIsActive());

        Policy updated = policyRepository.save(policy);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deletePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setIsDeleted(true);
        policyRepository.save(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccess(String resourceType, String action, Long userId, Long resourceId) {
        // This method is now handled by PolicyEvaluatorService
        throw new UnsupportedOperationException("Use PolicyEvaluatorService instead");
    }

    private PolicyDto toDto(Policy policy) {
        PolicyDto dto = new PolicyDto();
        dto.setId(policy.getId());
        dto.setName(policy.getName());
        dto.setDescription(policy.getDescription());
        dto.setResourceType(policy.getResourceType());
        dto.setAction(policy.getAction());
        dto.setConditions(policy.getConditions());
        dto.setEffect(policy.getEffect().name());
        dto.setPriority(policy.getPriority());
        dto.setIsActive(policy.getIsActive());
        return dto;
    }
}
