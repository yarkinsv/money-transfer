package com.moneytransfer.dao.impl.hashimpl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.List;

public class HashUserDAOImpl implements UserDAO {
    @Override
    public List<User> getAllUsers() throws CustomException {
        return null;
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        return null;
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        return null;
    }

    @Override
    public long insertUser(User user) throws CustomException {
        return 0;
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        return 0;
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        return 0;
    }
}
