package com.apimaster.service.impl;

import com.apimaster.entity.User;
import com.apimaster.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public User register(User user) {
        // 模拟检查用户名是否已存在
        // 模拟检查邮箱是否已存在
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认角色和状态
        if (user.getRole() == null) {
            user.setRole("user");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        // 模拟保存用户
        user.setId(1L);
        return user;
    }
    
    @Override
    public String login(String username, String password) {
        // 模拟根据用户名查找用户
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("user");
        user.setStatus(1);
        
        // 模拟检查密码
        // 模拟检查用户状态
        
        // 生成JWT令牌
        return "mock-jwt-token-" + user.getId();
    }
    
    @Override
    public User getUserByUsername(String username) {
        // 模拟返回用户
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setNickname("测试用户" + username);
        user.setRole("user");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    @Override
    public User getUserByEmail(String email) {
        // 模拟返回用户
        User user = new User();
        user.setId(1L);
        user.setUsername(email.split("@")[0]);
        user.setEmail(email);
        user.setNickname("测试用户" + email.split("@")[0]);
        user.setRole("user");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
