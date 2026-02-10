-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS api_master CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE api_master;

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'user',
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入默认用户（密码：admin123）
INSERT INTO user (username, password, email, nickname, role, status) 
VALUES ('admin', '$2a$10$E5e9tJVlH539O8T2YQ2GouGHI8LfbZ9Q1G9yVtcjF5v09e5rXW9V6', 'admin@example.com', '管理员', 'admin', 1)
ON DUPLICATE KEY UPDATE username = username;
