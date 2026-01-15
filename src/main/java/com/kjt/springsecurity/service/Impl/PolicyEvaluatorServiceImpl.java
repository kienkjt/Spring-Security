package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.entity.Document;
import com.kjt.springsecurity.entity.Policy;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.enums.PolicyEffect;
import com.kjt.springsecurity.repository.PolicyRepository;
import com.kjt.springsecurity.service.PolicyEvaluatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PolicyEvaluatorServiceImpl implements PolicyEvaluatorService {

    private final PolicyRepository policyRepository;

    public PolicyEvaluatorServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public boolean checkDocumentAccess(User user, Document document, String action) {
        List<Policy> policies = policyRepository.findApplicablePolicies("DOCUMENT", action);
        if (policies.isEmpty())
            return false;

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

            // Handle dynamic references (e.g., "resource.ownerId")
            if (expected.startsWith("user.") || expected.startsWith("resource.")) {
                Object resolvedExpectedValue = getAttributeValue(expected, user, document);
                return resolvedExpectedValue != null && resolvedExpectedValue.equals(actualValue);
            }

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
}
