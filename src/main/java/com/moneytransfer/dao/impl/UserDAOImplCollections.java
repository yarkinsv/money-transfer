package com.moneytransfer.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

public class UserDAOImplCollections implements UserDAO {

	  private static Logger log = Logger.getLogger(UserDAOImplCollections.class);
	  
	  private static Map<Long, User> userIdMap = new HashMap<>();
	  private static Map<String, User> userNameMap = new HashMap<>();

	  /**
	   * Find all users
	   */
	  public List<User> getAllUsers() throws CustomException {
	    return new ArrayList<User>(userIdMap.values());
	  }

	  /**
	   * Find user by userId
	   */
	  public User getUserById(long userId) throws CustomException {
	    User u = userIdMap.get(userId);
			if (log.isDebugEnabled()) {
				log.debug("getUserById(): Retrieve User: " + u);
			}
	    return u;
	  }

	  /**
	   * Find user by userName
	   */
	  public User getUserByName(String userName) throws CustomException {
	    User u = userNameMap.get(userName);
			if (log.isDebugEnabled()) {
				log.debug("getUserByName(): Retrieve User: " + u + ". userName = " + userName);
			}
	    return u;
	  }

	  /**
	   * Save User
	   */
	  public long insertUser(User user) throws CustomException {
			//log.info("Begin insert user: " + user);
	    if (getUserByName(user.getUserName()) != null) {
	      log.error("Error Inserting User  " + user + ". User with such name already exists.");
		  	throw new CustomException("User with such name already exists.");
	    }
			long id = userIdMap.size() + 1;
			User u = new User(id, user.getUserName(), user.getEmailAddress());
			try {
				userIdMap.put(id, u);
				userNameMap.put(user.getUserName(), u);
				//log.info("Put user data in maps: " + user);
				//log.info("userNameMap: " + userNameMap);
			} catch(Exception e) {
				try {
					userIdMap.remove(id);
					userNameMap.remove(user.getUserName());
				} catch (Exception re) {
						log.error("Error rollback Inserting User  " + u);
						throw new CustomException("insertUser(): Error rollback inserting user " + u, re);
				}
				log.error("Error Inserting User  " + u);
				throw new CustomException("insertUser(): Error inserting user " + u, e);
	    }
	    return id;
	  }

	  /**
	   * Update User
	   */
	  public int updateUser(Long userId, User user) throws CustomException {
	    User oldUser = getUserById(userId);
	    if (oldUser == null) {
	    	return 0;
	    }
	    try {
	      userNameMap.remove(oldUser.getUserName());
	      userNameMap.put(user.getUserName(), user);
	      userIdMap.put(userId, user);
	    } catch (Exception e) {
	      log.error("Error Updating User :" + user);
	      throw new CustomException("Error update user data", e);
	    }
	    return 1;
	  }

	  /**
	   * Delete User
	   */
	  public int deleteUser(long userId) throws CustomException {
	    User user = getUserById(userId);
	    if (user == null) {
	    	return 0;
	    }
	    try {
	      userIdMap.remove(userId);
	      userNameMap.remove(user.getUserName());
	    } catch(Exception e) {
	      try {
	        userIdMap.put(userId, user);
	        userNameMap.put(user.getUserName(), user);
	      } catch (Exception re) {
	        log.error("Error rollback Deleting User :" + userId);
	        throw new CustomException("Error rollback Deleting User ID:" + userId, e);
	      }
	      log.error("Error Deleting User :" + userId);
	      throw new CustomException("Error Deleting User ID:" + userId, e);
	    }
	    return 1;
	}
	  
	public void dropCollections() {
	  userIdMap.clear();
	  userNameMap.clear();
	}
	  
}
