package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.List;

public interface UserDAO {
	
	List<User> getAllUsers() throws CustomException;

	User getUserById(long userId) throws CustomException;

	User getUserByName(String userName) throws CustomException;

	long insertUser(User user) throws CustomException;

	int updateUser(Long userId, User user) throws CustomException;

	int deleteUser(long userId) throws CustomException;
}
