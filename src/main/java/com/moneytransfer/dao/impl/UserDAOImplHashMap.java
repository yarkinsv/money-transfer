package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDAOImplHashMap implements UserDAO {

    HashMap<Long, User> users = new HashMap<>();
    long counter = 1;

    @Override
    public List<User> getAllUsers() throws CustomException {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        return users.get(userId);
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        return getAllUsers()
                .stream()
                .filter(account -> account.getUserName().equals(userName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public long insertUser(User user) throws CustomException {
        users.put(counter, user);
        return counter++;
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        if (users.containsKey(userId)) {
            users.put(userId, user);
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        if (users.containsKey(userId)) {
            users.remove(userId);
            return 1;
        }
        return 0;
    }
}
