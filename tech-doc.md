# API Master 技术方案文档

## 1. 项目概述

API Master 是一个一体化的 API 设计、测试和协作平台，旨在帮助开发团队更高效地管理 API 全生命周期。

## 2. 技术栈

### 2.1 前端技术栈
- Vue 3 + TypeScript
- Vue Router
- Pinia (状态管理)
- Element Plus (UI 框架)
- Axios (HTTP 客户端)
- ECharts (图表库)
- Monaco Editor (代码编辑器)

### 2.2 后端技术栈
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Spring Data Redis
- RabbitMQ
- MinIO
- MySQL

## 3. 核心功能模块

### 3.1 项目管理模块 (新增)
- 项目创建、编辑、删除
- 项目成员管理
- 项目权限控制
- 项目环境配置

### 3.2 API 设计模块
- 可视化 API 编辑器
- 参数管理
- 请求体设计
- 响应设计
- API 版本管理

### 3.3 API 测试模块
- 请求构建
- 响应分析
- 测试用例管理
- 批量测试

### 3.4 Mock 服务模块
- 自动生成 Mock 数据
- 自定义 Mock 规则
- Mock 服务管理

### 3.5 文档生成模块
- 自动生成 API 文档
- 多格式导出 (HTML, Markdown, PDF)
- 文档版本管理

### 3.6 用户管理模块
- 用户注册、登录
- 角色权限管理
- 个人中心

## 4. 系统架构

### 4.1 前端架构
- 单页应用 (SPA)
- 组件化设计
- 状态管理 (Pinia)
- 路由管理 (Vue Router)

### 4.2 后端架构
- 微服务架构
-  RESTful API
- 数据持久化 (MySQL)
- 缓存 (Redis)
- 消息队列 (RabbitMQ)
- 对象存储 (MinIO)

## 5. 数据库设计

### 5.1 核心表结构

#### 5.1.1 projects 表
| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| id | BIGINT | 项目 ID |
| name | VARCHAR(255) | 项目名称 |
| description | TEXT | 项目描述 |
| creator_id | BIGINT | 创建者 ID |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |
| status | INT | 项目状态 |

#### 5.1.2 project_members 表
| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| id | BIGINT | 记录 ID |
| project_id | BIGINT | 项目 ID |
| user_id | BIGINT | 用户 ID |
| role | INT | 角色 (1: 所有者, 2: 管理员, 3: 成员) |
| joined_at | TIMESTAMP | 加入时间 |

#### 5.1.3 apis 表
| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| id | BIGINT | API ID |
| project_id | BIGINT | 项目 ID |
| name | VARCHAR(255) | API 名称 |
| path | VARCHAR(255) | API 路径 |
| method | VARCHAR(10) | 请求方法 |
| description | TEXT | API 描述 |
| status | INT | API 状态 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

#### 5.1.4 api_versions 表
| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| id | BIGINT | 版本 ID |
| api_id | BIGINT | API ID |
| version | VARCHAR(50) | 版本号 |
| content | TEXT | API 定义内容 |
| created_at | TIMESTAMP | 创建时间 |

#### 5.1.5 test_cases 表
| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| id | BIGINT | 测试用例 ID |
| project_id | BIGINT | 项目 ID |
| api_id | BIGINT | API ID |
| name | VARCHAR(255) | 测试用例名称 |
| request | TEXT | 请求配置 |
| expected_response | TEXT | 期望响应 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

#### 5.1.6 users 表
| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| id | BIGINT | 用户 ID |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(255) | 密码 (加密存储) |
| nickname | VARCHAR(50) | 昵称 |
| email | VARCHAR(100) | 邮箱 |
| role | INT | 全局角色 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

## 6. API 设计

### 6.1 项目管理 API
- `GET /api/projects` - 获取项目列表
- `POST /api/projects` - 创建项目
- `GET /api/projects/{id}` - 获取项目详情
- `PUT /api/projects/{id}` - 更新项目
- `DELETE /api/projects/{id}` - 删除项目
- `GET /api/projects/{id}/members` - 获取项目成员
- `POST /api/projects/{id}/members` - 添加项目成员
- `DELETE /api/projects/{id}/members/{userId}` - 移除项目成员

### 6.2 API 管理 API
- `GET /api/projects/{projectId}/apis` - 获取项目下的 API 列表
- `POST /api/projects/{projectId}/apis` - 创建 API
- `GET /api/apis/{id}` - 获取 API 详情
- `PUT /api/apis/{id}` - 更新 API
- `DELETE /api/apis/{id}` - 删除 API

### 6.3 测试管理 API
- `GET /api/projects/{projectId}/test-cases` - 获取项目下的测试用例
- `POST /api/projects/{projectId}/test-cases` - 创建测试用例
- `GET /api/test-cases/{id}` - 获取测试用例详情
- `PUT /api/test-cases/{id}` - 更新测试用例
- `DELETE /api/test-cases/{id}` - 删除测试用例
- `POST /api/test-cases/{id}/run` - 运行测试用例

## 7. 部署方案

### 7.1 本地开发环境
- Docker Compose 启动依赖服务
- 前端：`npm run dev`
- 后端：`mvn spring-boot:run`

### 7.2 生产环境
- Kubernetes 集群部署
- 负载均衡
- 自动扩缩容
- 监控告警

## 8. 安全性考虑

- HTTPS 加密传输
- JWT 身份认证
- 基于角色的权限控制
- 输入验证
- 防 SQL 注入
- 防 XSS 攻击
- 防 CSRF 攻击

## 9. 性能优化

- 前端代码分割
- 后端缓存策略
- 数据库索引优化
- 异步处理
- 负载均衡

## 10. 未来规划

- 集成 CI/CD 流程
- 支持更多 API 规范 (OpenAPI 3.0, GraphQL)
- 提供 API 市场
- 增强数据分析能力
- 支持多语言 SDK 生成
