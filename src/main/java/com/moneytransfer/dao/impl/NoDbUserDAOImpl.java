package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NoDbUserDAOImpl implements UserDAO {

    private Map<Long, User> users = new ConcurrentHashMap<>();

    // key is username, value is user id
    private Map<String, Long> userNamesIndex = new ConcurrentHashMap<>();

    private AtomicLong lastUserId = new AtomicLong();

    @Override
    public List<User> getAllUsers() throws CustomException {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        return users.get(userId);
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        Long userId = userNamesIndex.get(userName);
        if (userId == null) {
            return null;
        }

        return users.get(userId);
    }

    @Override
    public long insertUser(User user) throws CustomException {
        long userId = user.getUserId();
        if (userId != 0 && users.get(userId) != null) {
            throw new CustomException(String.format("User with id %s already exists", userId));
        }

        userId = lastUserId.incrementAndGet();
        user.setUserId(userId);
        users.put(userId, user);
        userNamesIndex.put(user.getUserName(), userId);

        return userId;
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        User userToUpdate = users.get(userId);
        if (userToUpdate == null) {
            return 0;
        }

        users.put(userId, user);
        return 1;
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        User removedUser = users.remove(userId);
        return removedUser == null ? 0 : 1;
    }

    public void clear() {
        this.users = new ConcurrentHashMap<>();
        this.userNamesIndex = new ConcurrentHashMap<>();
        this.lastUserId = new AtomicLong();
    }
}
