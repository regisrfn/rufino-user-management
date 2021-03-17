package com.rufino.server.services;

import java.util.List;

import com.rufino.server.model.User;

import org.springframework.http.ResponseEntity;

public interface UserService {

    User register(User user);

    ResponseEntity<User> login(User user);

    List<User> getUsers();

    User getUserByUsername(String username);

    User getUserByEmail(String email);

}
