package com.moneytransfer.dao.impl.hashimpl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.ArrayList;
import java.util.List;

public class HashUserDAOImpl implements UserDAO {
    static List<User> AllUser = null;
    static long ID = 10;

    public UserDAO init() throws CustomException {
        if (AllUser == null) {
            AllUser = new ArrayList<>();
            AllUser.add(new User(1, "test2", "test2@gmail.com"));
            AllUser.add(new User(2, "test1", "test1@gmail.com"));
            AllUser.add(new User(3, "yangluo", "yangluo@gmail.com"));
            AllUser.add(new User(4, "qinfran", "qinfran@gmail.com"));
            AllUser.add(new User(5, "liusisi", "liusisi@gmail.com"));
        }
        return this;
    }

    @Override
    public List<User> getAllUsers() throws CustomException {
        return AllUser;
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        for (User user: AllUser){
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        for (User user: AllUser){
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public long insertUser(User user) throws CustomException {
        AllUser.add(new User(ID, user.getUserName(), user.getEmailAddress()));
        return ID++;
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        for(User u: AllUser) {
            if (u.getUserId() == userId) {
                u.setUserName(user.getUserName());
                u.setEmailAddress(user.getEmailAddress());
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        if (AllUser.remove(getUserById(userId))) {
            return 1;
        }
        return -1;
    }
}
