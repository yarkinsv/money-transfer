package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.HMDAOFactory;
import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import org.apache.log4j.jmx.HierarchyDynamicMBean;

import java.util.*;

public class UserHMDAOImpl implements UserDAO {
    private HashMap<Long, User> users = new HashMap<>();
    private HashMap<String, Long> namesToIds = new HashMap<>();
    private long userId = 1;

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
        Long id;
        if ((id = namesToIds.get(userName)) == null) {
            return null;
        }
        return users.get(id);
    }

    @Override
    public long insertUser(User user) throws CustomException {
        long newUserId = userId++;
        users.put(newUserId, new User(newUserId, user.getUserName(), user.getEmailAddress()));
        namesToIds.put(user.getUserName(), newUserId);
        return newUserId;
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        if (users.get(userId) == null) {
            return 0;
        }
        User oldUser = users.get(userId);
        if (!oldUser.getUserName().equals(user.getUserName())) {
            namesToIds.remove(oldUser.getUserName());
            namesToIds.put(user.getUserName(), userId);
        }
        users.put(userId, user);
        return 1;
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        if (users.get(userId) == null) {
            return 0;
        }
        User user = users.get(userId);
        String userName = user.getUserName();
        users.remove(userId);
        namesToIds.remove(userName);
        return 1;
    }
}
