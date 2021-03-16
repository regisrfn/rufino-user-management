package com.rufino.server.repository;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.JpaDao;
import com.rufino.server.dao.UserDao;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements UserDao {

    private JpaDao jpaDataAccess;

    @Autowired
    public UserRepository(JpaDao jpaDataAccess) {
        this.jpaDataAccess = jpaDataAccess;
    }

    @Override
    public User saveOrUpdateUser(User user) {
        return jpaDataAccess.save(user);
    }

    @Override
    public boolean deleteUserById(UUID id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<User> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUser(UUID id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUserByNickname(String nickname) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User getUserByEmail(String nickname) {
        // TODO Auto-generated method stub
        return null;
    }

}
