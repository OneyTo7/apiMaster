package com.apimaster.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    
    // 健康检查
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User service is running");
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
    
    // 模拟用户注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> mockUser = new HashMap<>();
            mockUser.put("id", 1L);
            mockUser.put("username", userData.get("username"));
            mockUser.put("email", userData.get("email"));
            mockUser.put("nickname", userData.get("nickname"));
            mockUser.put("role", "user");
            mockUser.put("status", 1);
            
            return ResponseEntity.ok(mockUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 模拟用户登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            if (username != null && password != null) {
                return ResponseEntity.ok(Map.of("token", "mock-jwt-token-" + System.currentTimeMillis()));
            } else {
                return ResponseEntity.badRequest().body("Username and password are required");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 模拟根据用户名获取用户
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", 1L);
        mockUser.put("username", username);
        mockUser.put("email", username + "@example.com");
        mockUser.put("nickname", "测试用户" + username);
        mockUser.put("role", "user");
        mockUser.put("status", 1);
        return ResponseEntity.ok(mockUser);
    }
    
    // 模拟根据邮箱获取用户
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", 1L);
        mockUser.put("username", email.split("@")[0]);
        mockUser.put("email", email);
        mockUser.put("nickname", "测试用户" + email.split("@")[0]);
        mockUser.put("role", "user");
        mockUser.put("status", 1);
        return ResponseEntity.ok(mockUser);
    }
}
