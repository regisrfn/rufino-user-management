package com.rufino.server.services;

import java.util.List;

import com.rufino.server.model.User;

public interface UserService {

    User register(User user);

    List<User> getUsers();

    User getUserByUsername(String username);

    User getUserByEmail(String email);

}
