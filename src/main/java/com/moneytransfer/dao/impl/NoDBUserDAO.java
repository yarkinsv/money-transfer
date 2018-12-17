package com.moneytransfer.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

public class NoDBUserDAO implements UserDAO {
    private final Map<Long,User> allUsers = new ConcurrentHashMap<> ();
    private final Map<String,Long> usersByName = new ConcurrentHashMap<>();
    private final LongSupplier nextUserId = new LongSupplier(){
            private long next = 0;
            @Override
            public long getAsLong() {
		return ++next;
            }
        };

    private long getNextUserId() {
        return nextUserId.getAsLong();
    }

    public List<User> getAllUsers() throws CustomException {
        return new ArrayList<User>(allUsers.values());
    }

    public User getUserById(long userId) throws CustomException {
        return allUsers.get(userId);
    }

    public User getUserByName(String userName) throws CustomException {
        Long userId = usersByName.get(userName);
        return userId == null ? null : getUserById(userId);
    }

    public long insertUser(User user) throws CustomException {
        long nextUserId = getNextUserId();
        String userName = user.getUserName();
        if (getUserByName(userName) != null)
            // !!!SHAME!!!
            throw new CustomException
                ("", new CustomException("Unique index or primary key violation"));
        allUsers.put(nextUserId, new User(nextUserId, userName, user.getEmailAddress()));
        usersByName.put(userName, nextUserId);
        return nextUserId;
    }

    public int updateUser(Long userId, User user) throws CustomException {
        User oldUser = allUsers.get(userId);
        if (oldUser == null) return 0;
        String userName = user.getUserName();
        User updatedUser = new User(userId, userName, user.getEmailAddress());
        usersByName.remove(oldUser.getUserName());
        allUsers.put(userId, updatedUser);
        usersByName.put(userName, userId);
        return 1;
    }

    public int deleteUser(long userId) throws CustomException {
        User deletedUser = allUsers.get(userId);
        if (deletedUser == null) return 0;
        usersByName.remove(deletedUser.getUserName());
        allUsers.remove(userId);
        return 1;
    }
}

