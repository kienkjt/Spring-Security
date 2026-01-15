-- Users
INSERT INTO `users` (`id`, `clearance_level`, `created_at`, `department`, `email`, `is_active`, `is_deleted`, `password`, `position`, `updated_at`, `username`) VALUES 
(1,3,'2026-01-14 14:49:45.000000','IT','admin@example.com', b'1', b'0','$2a$10$JjGLAiWM2tSH55wTFGxhoOdLXHsxb98JSCZpql15IJAa3N4PpU.EO','MANAGER','2026-01-14 14:49:45.000000','admin'),
(2,2,'2026-01-14 14:49:45.000000','IT','user1@example.com', b'1', b'0','$2a$10$N.zmdr9k7uOCQb07Onqn3eOHXEfXGbzj1UlHPPOCcVqr7bJXDxVG6','EMPLOYEE','2026-01-14 14:49:45.000000','user1'),
(3,1,'2026-01-14 14:49:45.000000','HR','user2@example.com', b'1', b'0','$2a$10$N.zmdr9k7uOCQb07Onqn3eOHXEfXGbzj1UlHPPOCcVqr7bJXDxVG6','EMPLOYEE','2026-01-14 14:49:45.000000','user2'),
(4,1,'2026-01-14 08:01:40.344814','GENERAL','kien@gmail.com', b'1', b'0','$2a$10$JjGLAiWM2tSH55wTFGxhoOdLXHsxb98JSCZpql15IJAa3N4PpU.EO','EMPLOYEE','2026-01-14 08:01:40.344814','user');

-- Roles
INSERT INTO `roles` (`id`, `created_at`, `description`, `is_deleted`, `name`, `updated_at`) VALUES 
(1,'2026-01-14 14:49:45.000000','Administrator role', b'0','ADMIN','2026-01-14 14:49:45.000000'),
(2,'2026-01-14 14:49:45.000000','Manager role', b'0','MANAGER','2026-01-14 14:49:45.000000'),
(3,'2026-01-14 14:49:45.000000','Regular user role', b'0','USER','2026-01-14 14:49:45.000000');

-- Permissions
INSERT INTO `permissions` (`id`, `created_at`, `description`, `is_deleted`, `name`, `updated_at`) VALUES 
(1,'2026-01-14 14:49:45.000000','Read access', b'0','READ','2026-01-14 14:49:45.000000'),
(2,'2026-01-14 14:49:45.000000','Write access', b'0','WRITE','2026-01-14 14:49:45.000000'),
(3,'2026-01-14 14:49:45.000000','Delete access', b'0','DELETE','2026-01-14 14:49:45.000000');

-- Policies (ABAC Rules)
INSERT INTO `policies` (`id`, `action`, `conditions`, `created_at`, `description`, `effect`, `is_active`, `is_deleted`, `name`, `priority`, `resource_type`, `updated_at`) VALUES 
(1,'READ','{"user.id": "resource.ownerId"}','2026-01-14 14:49:45.000000','Owners can read their own documents','ALLOW', b'1', b'0','Owner Read Access',100,'DOCUMENT','2026-01-14 14:49:45.000000'),
(2,'WRITE','{"user.id": "resource.ownerId"}','2026-01-14 14:49:45.000000','Owners can edit their own documents','ALLOW', b'1', b'0','Owner Write Access',100,'DOCUMENT','2026-01-14 14:49:45.000000'),
(3,'DELETE','{"user.id": "resource.ownerId"}','2026-01-14 14:49:45.000000','Owners can delete their own documents','ALLOW', b'1', b'0','Owner Delete Access',100,'DOCUMENT','2026-01-14 14:49:45.000000'),
(4,'READ','{"user.department": "resource.department", "resource.classificationLevel": "<=2"}','2026-01-14 14:49:45.000000','Users can read department documents with low classification','ALLOW', b'1', b'0','Department Read Access',80,'DOCUMENT','2026-01-14 14:49:45.000000'),
(5,'READ','{"user.position": "MANAGER", "user.department": "resource.department"}','2026-01-14 14:49:45.000000','Managers have full access to department documents','ALLOW', b'1', b'0','Manager Read Access',90,'DOCUMENT','2026-01-14 14:49:45.000000'),
(6,'WRITE','{"user.position": "MANAGER", "user.department": "resource.department"}','2026-01-14 14:49:45.000000','Managers can edit department documents','ALLOW', b'1', b'0','Manager Write Access',90,'DOCUMENT','2026-01-14 14:49:45.000000');

-- Documents
INSERT INTO `documents` (`id`, `classification_level`, `content`, `created_at`, `department`, `is_deleted`, `status`, `title`, `updated_at`, `owner_id`) VALUES 
(1,1,'This is a public document accessible by IT department.','2026-01-14 14:49:45.000000','IT', b'0','PUBLISHED','Public Document','2026-01-14 14:49:45.000000',1),
(2,2,'This is user1\'s private document.','2026-01-14 14:49:45.000000','IT', b'0','DRAFT','User1 Private Doc','2026-01-14 14:49:45.000000',2),
(3,1,'This is an HR department document.','2026-01-14 14:49:46.000000','HR', b'0','PUBLISHED','HR Document','2026-01-14 14:49:46.000000',3),
(4,2,'Doanh thu đạt 120 tỷ, lợi nhuận 25 tỷ.','2026-01-14 08:04:51.254496','IT', b'0','PUBLISHED','Báo cáo tài chính Q1','2026-01-14 08:04:51.254496',1);
