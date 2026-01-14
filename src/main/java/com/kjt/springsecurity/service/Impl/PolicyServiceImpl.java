package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.dto.PolicyDto;
import com.kjt.springsecurity.entity.Policy;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.entity.Document;
import com.kjt.springsecurity.enums.PolicyEffect;
import com.kjt.springsecurity.repository.PolicyRepository;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.repository.DocumentRepository;
import com.kjt.springsecurity.service.PolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository,
            UserRepository userRepository,
            DocumentRepository documentRepository) {
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
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
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return false;

        // Get applicable policies
        List<Policy> policies = policyRepository.findApplicablePolicies(resourceType, action);
        if (policies.isEmpty())
            return false;

        // For DOCUMENT resource type
        if ("DOCUMENT".equals(resourceType) && resourceId != null) {
            Document document = documentRepository.findById(resourceId).orElse(null);
            if (document == null)
                return false;

            return evaluateDocumentPolicies(policies, user, document);
        }

        return false;
    }

    private boolean evaluateDocumentPolicies(List<Policy> policies, User user, Document document) {
        boolean hasAllowPolicy = false;

        for (Policy policy : policies) {
            if (evaluatePolicy(policy, user, document)) {
                if (policy.getEffect() == PolicyEffect.DENY) {
                    return false; // Deny overrides
                }
                if (policy.getEffect() == PolicyEffect.ALLOW) {
                    hasAllowPolicy = true;
                }
            }
        }

        return hasAllowPolicy;
    }

    private boolean evaluatePolicy(Policy policy, User user, Document document) {
        Map<String, Object> conditions = policy.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        // Simple condition evaluation
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object expectedValue = entry.getValue();

            if (!evaluateCondition(key, expectedValue, user, document)) {
                return false;
            }
        }

        return true;
    }

    private boolean evaluateCondition(String key, Object expectedValue, User user, Document document) {
        Object actualValue = getAttributeValue(key, user, document);

        if (expectedValue instanceof String) {
            String expected = (String) expectedValue;

            // Handle comparison operators
            if (expected.startsWith(">=")) {
                return compareNumeric(actualValue, expected.substring(2).trim(), ">=");
            } else if (expected.startsWith("<=")) {
                return compareNumeric(actualValue, expected.substring(2).trim(), "<=");
            } else if (expected.equals(actualValue)) {
                return true;
            }
        }

        return expectedValue != null && expectedValue.equals(actualValue);
    }

    private Object getAttributeValue(String key, User user, Document document) {
        String[] parts = key.split("\\.");
        if (parts.length != 2)
            return null;

        String category = parts[0];
        String attribute = parts[1];

        if ("user".equals(category)) {
            return switch (attribute) {
                case "id" -> user.getId();
                case "department" -> user.getDepartment();
                case "position" -> user.getPosition();
                case "clearanceLevel" -> user.getClearanceLevel();
                default -> null;
            };
        } else if ("resource".equals(category)) {
            return switch (attribute) {
                case "ownerId" -> document.getOwner().getId();
                case "department" -> document.getDepartment();
                case "classificationLevel" -> document.getClassificationLevel();
                default -> null;
            };
        }

        return null;
    }

    private boolean compareNumeric(Object actual, String expected, String operator) {
        try {
            double actualNum = Double.parseDouble(String.valueOf(actual));
            double expectedNum = Double.parseDouble(expected);

            return switch (operator) {
                case ">=" -> actualNum >= expectedNum;
                case "<=" -> actualNum <= expectedNum;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false;
        }
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
