package com.apimaster.service;

import com.apimaster.entity.User;

public interface UserService {
    User register(User user);
    String login(String username, String password);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
}
