-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS api_master CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用api_master数据库
USE api_master;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `avatar` VARCHAR(255),
  `role` VARCHAR(20) NOT NULL DEFAULT 'user',
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_username` (`username`),
  INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建项目表
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `owner_id` BIGINT NOT NULL,
  `status` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_owner_id` (`owner_id`),
  CONSTRAINT `fk_project_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建API分类表
CREATE TABLE IF NOT EXISTS `api_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`),
  CONSTRAINT `fk_category_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建API表
CREATE TABLE IF NOT EXISTS `api` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `category_id` BIGINT,
  `path` VARCHAR(255) NOT NULL,
  `method` VARCHAR(10) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `request_headers` JSON,
  `request_params` JSON,
  `request_body` JSON,
  `response` JSON,
  `status_code` INT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_path_method` (`path`, `method`),
  CONSTRAINT `fk_api_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_api_category` FOREIGN KEY (`category_id`) REFERENCES `api_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建测试用例表
CREATE TABLE IF NOT EXISTS `test_case` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `api_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `request_data` JSON NOT NULL,
  `expected_response` JSON,
  `pre_script` TEXT,
  `post_script` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_api_id` (`api_id`),
  CONSTRAINT `fk_test_case_api` FOREIGN KEY (`api_id`) REFERENCES `api` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建Mock规则表
CREATE TABLE IF NOT EXISTS `mock_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `api_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `condition` JSON,
  `response` JSON NOT NULL,
  `priority` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_api_id` (`api_id`),
  CONSTRAINT `fk_mock_rule_api` FOREIGN KEY (`api_id`) REFERENCES `api` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建工作区表
CREATE TABLE IF NOT EXISTS `workspace` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `owner_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_owner_id` (`owner_id`),
  CONSTRAINT `fk_workspace_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建工作区成员表
CREATE TABLE IF NOT EXISTS `workspace_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `workspace_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role` VARCHAR(20) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_workspace_user` (`workspace_id`, `user_id`),
  CONSTRAINT `fk_workspace_member_workspace` FOREIGN KEY (`workspace_id`) REFERENCES `workspace` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_workspace_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建环境表
CREATE TABLE IF NOT EXISTS `environment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `base_url` VARCHAR(255) NOT NULL,
  `variables` JSON,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`),
  CONSTRAINT `fk_environment_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建迭代分支表
CREATE TABLE IF NOT EXISTS `iteration_branch` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_project_id` (`project_id`),
  INDEX `idx_created_by` (`created_by`),
  CONSTRAINT `fk_iteration_branch_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_iteration_branch_user` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建API历史表
CREATE TABLE IF NOT EXISTS `api_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `api_id` BIGINT NOT NULL,
  `branch_id` BIGINT,
  `content` JSON NOT NULL,
  `created_by` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_api_id` (`api_id`),
  INDEX `idx_branch_id` (`branch_id`),
  INDEX `idx_created_by` (`created_by`),
  CONSTRAINT `fk_api_history_api` FOREIGN KEY (`api_id`) REFERENCES `api` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_api_history_branch` FOREIGN KEY (`branch_id`) REFERENCES `iteration_branch` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_api_history_user` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建监控数据表
CREATE TABLE IF NOT EXISTS `monitor_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `api_id` BIGINT NOT NULL,
  `response_time` INT NOT NULL,
  `status_code` INT NOT NULL,
  `success` BOOLEAN NOT NULL,
  `error_message` TEXT,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_api_id` (`api_id`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_monitor_data_api` FOREIGN KEY (`api_id`) REFERENCES `api` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认管理员用户
INSERT INTO `user` (`username`, `email`, `password`, `nickname`, `role`, `status`) 
VALUES ('admin', 'admin@example.com', '$2a$10$eCwW6t9Q5Z4V3vZ7X8Y9Ue7f8G9H0I1J2K3L4M5N6O7P8Q9R0S', '管理员', 'admin', 1)
ON DUPLICATE KEY UPDATE `nickname` = '管理员', `role` = 'admin', `status` = 1;

-- 插入默认项目
INSERT INTO `project` (`name`, `description`, `owner_id`, `status`) 
VALUES ('默认项目', '系统默认项目', 1, 1)
ON DUPLICATE KEY UPDATE `description` = '系统默认项目', `status` = 1;

-- 插入默认API分类
INSERT INTO `api_category` (`project_id`, `name`, `description`) 
VALUES (1, '默认分类', '系统默认分类')
ON DUPLICATE KEY UPDATE `description` = '系统默认分类';

-- 插入默认环境
INSERT INTO `environment` (`project_id`, `name`, `base_url`, `variables`) 
VALUES (1, '开发环境', 'http://localhost:8080', '{"baseUrl": "http://localhost:8080"}')
ON DUPLICATE KEY UPDATE `base_url` = 'http://localhost:8080', `variables` = '{"baseUrl": "http://localhost:8080"}';
