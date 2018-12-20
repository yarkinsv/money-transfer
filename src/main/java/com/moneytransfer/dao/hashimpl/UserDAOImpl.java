package com.moneytransfer.dao.hashimpl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDAOImpl implements UserDAO {

    private static final Logger log = Logger.getLogger(UserDAOImpl.class);

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private TreeMap<Long, User> users = new TreeMap<>();
    private HashMap<String, Long> userNameIndex = new HashMap<>();

    private Long userIdSeq = new Long(0);

    @Override
    public List<User> getAllUsers() throws CustomException {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        User user = users.get(userId);
        if (user != null && log.isDebugEnabled()) {
            log.debug("getUserById(): Retrieve User: " + user);
        }
        return user;
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        Long id = userNameIndex.get(userName);
        User user;
        if (id == null) {
            user = null;
        } else {
            user = users.get(id);
        }

        if (user != null && log.isDebugEnabled()) {
            log.debug("getUserByName(): Retrieve User: " + user);
        }

        return user;
    }

    @Override
    public long insertUser(User user) throws CustomException {
        Long id = userIdSeq++;
        String name = user.getUserName();
        User newUser = new User(id , name, user.getEmailAddress());
        users.put(id, user);
        userNameIndex.put(name, id);
        return id;
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        User oldUser = users.get(userId);
        if (oldUser == null) {
            if (log.isDebugEnabled()) {
                log.error("Error Updating User :" + user);
            }
            return 0;
        }

        User newUser = new User(userId, user.getUserName(), user.getEmailAddress());
        if (!newUser.getUserName().equals(oldUser.getUserName())) {
            userNameIndex.remove(oldUser.getUserName());
            userNameIndex.put(user.getUserName(), userId);
        }
        users.replace(userId, newUser);
        return 1;
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        User user = users.remove(userId);
        if (user == null)
            return 0;

        userNameIndex.remove(user.getUserName());
        return 1;
    }

}
