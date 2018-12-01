package com.moneytransfer.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

public class UserDAOImplCollections implements UserDAO {

	  private static Logger log = Logger.getLogger(UserDAOImplCollections.class);

	  /**
	   * Find all users
	   */
	  public List<User> getAllUsers() throws CustomException {
	    List<User> users = new ArrayList<User>();
	    return users;
	  }

	  /**
	   * Find user by userId
	   */
	  public User getUserById(long userId) throws CustomException {
	    User u = new User();
	    return u;
	  }

	  /**
	   * Find user by userName
	   */
	  public User getUserByName(String userName) throws CustomException {
	    User u = new User();
	    return u;
	  }

	  /**
	   * Save User
	   */
	  public long insertUser(User user) throws CustomException {

	    return 1;

	  }

	  /**
	   * Update User
	   */
	  public int updateUser(Long userId, User user) throws CustomException {
;
	    return 1;

	  }

	  /**
	   * Delete User
	   */
	  public int deleteUser(long userId) throws CustomException {

	    return 1;

	}
}
