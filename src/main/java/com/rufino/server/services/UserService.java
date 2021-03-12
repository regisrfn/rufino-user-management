package com.rufino.server.services;

import java.util.List;

import com.rufino.server.model.User;

public interface UserService {

    User register(String firstName, String lastName, String username, String email);

    List<User> getUsers();

    User getUserByUsername(String username);

    User getUserByEmail(String email);

}
