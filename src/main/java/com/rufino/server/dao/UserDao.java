package com.rufino.server.dao;

import java.util.List;
import java.util.UUID;

import com.rufino.server.model.User;

public interface UserDao {
    User saveOrUpdateUser(User User);

    boolean deleteUserById(UUID id);

    List<User> getAll();

    User getUser(UUID id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);
}