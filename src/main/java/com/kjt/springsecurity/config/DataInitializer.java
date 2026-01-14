package com.kjt.springsecurity.config;

import com.kjt.springsecurity.entity.*;
import com.kjt.springsecurity.enums.DocumentStatus;
import com.kjt.springsecurity.enums.PolicyEffect;
import com.kjt.springsecurity.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple ABAC demo data initializer
 */
@Configuration
public class DataInitializer {

        @Bean
        public CommandLineRunner initData(
                        UserRepository userRepository,
                        PolicyRepository policyRepository,
                        DocumentRepository documentRepository,
                        PasswordEncoder passwordEncoder) {

                return args -> {
                        if (userRepository.count() > 0) {
                                System.out.println("Data already exists, skipping initialization...");
                                return;
                        }

                        System.out.println("=== Initializing Simple ABAC Demo ===");

                        // Create 3 demo users
                        User admin = createUser(userRepository, passwordEncoder,
                                        "admin", "admin123", "admin@example.com",
                                        "IT", "MANAGER", 3);

                        User user1 = createUser(userRepository, passwordEncoder,
                                        "user1", "user123", "user1@example.com",
                                        "IT", "EMPLOYEE", 2);

                        User user2 = createUser(userRepository, passwordEncoder,
                                        "user2", "user123", "user2@example.com",
                                        "HR", "EMPLOYEE", 1);

                        System.out.println("✓ Created 3 users");

                        // Create 3 simple ABAC policies

                        // Policy 1: Owner can read/write/delete their own documents
                        Map<String, Object> ownerCondition = new HashMap<>();
                        ownerCondition.put("user.id", "resource.ownerId");

                        createPolicy(policyRepository, "Owner Full Access",
                                        "Owners can fully manage their documents",
                                        "DOCUMENT", "READ", ownerCondition, PolicyEffect.ALLOW, 100);

                        createPolicy(policyRepository, "Owner Write Access",
                                        "Owners can edit their documents",
                                        "DOCUMENT", "WRITE", ownerCondition, PolicyEffect.ALLOW, 100);

                        createPolicy(policyRepository, "Owner Delete Access",
                                        "Owners can delete their documents",
                                        "DOCUMENT", "DELETE", ownerCondition, PolicyEffect.ALLOW, 100);

                        // Policy 2: Same department can read low classification documents
                        Map<String, Object> deptCondition = new HashMap<>();
                        deptCondition.put("user.department", "resource.department");
                        deptCondition.put("resource.classificationLevel", "<=2");

                        createPolicy(policyRepository, "Department Read Access",
                                        "Users can read department documents with low classification",
                                        "DOCUMENT", "READ", deptCondition, PolicyEffect.ALLOW, 80);

                        // Policy 3: Manager can access all department documents
                        Map<String, Object> managerCondition = new HashMap<>();
                        managerCondition.put("user.position", "MANAGER");
                        managerCondition.put("user.department", "resource.department");

                        createPolicy(policyRepository, "Manager Full Access",
                                        "Managers have full access to department documents",
                                        "DOCUMENT", "READ", managerCondition, PolicyEffect.ALLOW, 90);

                        createPolicy(policyRepository, "Manager Write Access",
                                        "Managers can edit department documents",
                                        "DOCUMENT", "WRITE", managerCondition, PolicyEffect.ALLOW, 90);

                        System.out.println("✓ Created 6 ABAC policies");

                        // Create 3 demo documents
                        createDocument(documentRepository, "Public Document",
                                        "This is a public document accessible by IT department.",
                                        admin, "IT", 1, DocumentStatus.PUBLISHED);

                        createDocument(documentRepository, "User1 Private Doc",
                                        "This is user1's private document.",
                                        user1, "IT", 2, DocumentStatus.DRAFT);

                        createDocument(documentRepository, "HR Document",
                                        "This is an HR department document.",
                                        user2, "HR", 1, DocumentStatus.PUBLISHED);

                        System.out.println("✓ Created 3 documents");
                        System.out.println("\n=== Demo Credentials ===");
                        System.out.println("Admin (IT Manager): admin / admin123");
                        System.out.println("User1 (IT Employee): user1 / user123");
                        System.out.println("User2 (HR Employee): user2 / user123");
                        System.out.println("========================\n");
                };
        }

        private User createUser(UserRepository repository, PasswordEncoder passwordEncoder,
                        String username, String password, String email,
                        String department, String position, Integer clearanceLevel) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setEmail(email);
                user.setDepartment(department);
                user.setPosition(position);
                user.setClearanceLevel(clearanceLevel);
                user.setIsActive(true);
                user.setIsDeleted(false);
                return repository.save(user);
        }

        private Policy createPolicy(PolicyRepository repository, String name, String description,
                        String resourceType, String action, Map<String, Object> conditions,
                        PolicyEffect effect, Integer priority) {
                Policy policy = new Policy();
                policy.setName(name);
                policy.setDescription(description);
                policy.setResourceType(resourceType);
                policy.setAction(action);
                policy.setConditions(conditions);
                policy.setEffect(effect);
                policy.setPriority(priority);
                policy.setIsActive(true);
                return repository.save(policy);
        }

        private Document createDocument(DocumentRepository repository, String title, String content,
                        User owner, String department, Integer classificationLevel,
                        DocumentStatus status) {
                Document document = new Document();
                document.setTitle(title);
                document.setContent(content);
                document.setOwner(owner);
                document.setDepartment(department);
                document.setClassificationLevel(classificationLevel);
                document.setStatus(status);
                return repository.save(document);
        }
}
