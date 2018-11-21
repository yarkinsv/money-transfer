package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDAOCollectionImpl implements UserDAO {
    private static final Logger log = Logger.getLogger(UserDAOCollectionImpl.class);

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private ConcurrentHashMap<Long, User> usersById = new ConcurrentHashMap<>(1000, 0.75f, 6);
    private ConcurrentHashMap<String, Long> userIdByName = new ConcurrentHashMap<>(1000, 0.75f, 6);

    private AtomicLong currentId = new AtomicLong(0);

    @Override
    public List<User> getAllUsers() throws CustomException {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        User user = usersById.get(userId);
        if (user != null && log.isDebugEnabled()) {
            log.debug("getUserById(): Retrieve User: " + user);
        }
        return user;
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        Long userId = userIdByName.get(userName);
        User user;
        if (userId == null) {
            user = null;
        } else {
            user = usersById.get(userId);
        }

        if (user != null && log.isDebugEnabled()) {
            log.debug("getUserByName(): Retrieve User: " + user);
        }
        return user;
    }

    @Override
    public long insertUser(User user) throws CustomException {
        User insertUser = new User(currentId.incrementAndGet(), user.getUserName(), user.getEmailAddress());
        try {
            lock.writeLock().lock();
            usersById.put(insertUser.getUserId(), user);
            userIdByName.put(insertUser.getUserName(), insertUser.getUserId());
        } finally {
            lock.writeLock().unlock();
        }
        return insertUser.getUserId();
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        User updated = new User(userId, user.getUserName(), user.getEmailAddress());
        try {
            lock.writeLock().lock();
            User oldUser = usersById.get(userId);
            if (oldUser == null) {
                if (log.isDebugEnabled()) {
                    log.error("Error Updating User :" + user);
                }
                return 0;
            }
            if (!updated.getUserName().equals(oldUser.getUserName())) {
                userIdByName.remove(oldUser.getUserName());
                userIdByName.put(updated.getUserName(), userId);
            }
            usersById.replace(userId, updated);
            return 1;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        try {
            lock.writeLock().lock();
            User remove = usersById.remove(userId);
            if (remove != null) {
                userIdByName.remove(remove.getUserName());
                return 1;
            }
            return 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clean() {
        currentId.set(0);
        userIdByName.clear();
        usersById.clear();
    }
}
