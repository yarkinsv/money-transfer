package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.List;

public class UserDAOCollectionImpl implements UserDAO {
    @Override
    public List<User> getAllUsers() throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getUserById(long userId) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getUserByName(String userName) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long insertUser(User user) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int updateUser(Long userId, User user) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int deleteUser(long userId) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
