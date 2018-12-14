package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.H2DAOFactory;
import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//Added by Isc
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
//---------------------
import java.util.Set;

public class UserDAOImpl implements UserDAO {

  private static Logger log = Logger.getLogger(UserDAOImpl.class);
  private final static String SQL_GET_USER_BY_ID = "SELECT * FROM User WHERE UserId = ? ";
  private final static String SQL_GET_ALL_USERS = "SELECT * FROM User";
  private final static String SQL_GET_USER_BY_NAME = "SELECT * FROM User WHERE UserName = ? ";
  private final static String SQL_INSERT_USER = "INSERT INTO User (UserId, UserName, EmailAddress) VALUES (?, ?, ?)";
  private final static String SQL_UPDATE_USER = "UPDATE User SET UserName = ?, EmailAddress = ? WHERE UserId = ? ";
  private final static String SQL_DELETE_USER_BY_ID = "DELETE FROM User WHERE UserId = ? ";

  //Commented by Isc
  //private List<User> fetched = new ArrayList<>(); 
  //--------------------------
  // Added by Isc
  private static Map<Long, User> userId_Map = new HashMap<>();
  private static Map<String, User> userName_Map = new HashMap<>();

  /**
   * Find all users
   */
  public List<User> getAllUsers() throws CustomException {
	List<User> allUsers = new ArrayList<User>(userId_Map.values());
	return allUsers;
  
/* Commented by Isc	  
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<User> users = new ArrayList<User>();
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_GET_ALL_USERS);
      rs = stmt.executeQuery();
      while (rs.next()) {
        User u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
        users.add(u);
        if (log.isDebugEnabled())
          log.debug("getAllUsers() Retrieve User: " + u);
      }
      //fetched.addAll(users); Commented by Isc
      return users;
    } catch (SQLException e) {
      throw new CustomException("Error reading user data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
*/    
  }

  /**
   * Find user by userId
   */
  public User getUserById(long userId) throws CustomException {
	User u = userId_Map.get(userId);
	if (log.isDebugEnabled()) {
		log.debug("getUserById(): Retrieve User: " + u);
	}
	return u;
/* Commented by Isc	  
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    User u = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_GET_USER_BY_ID);
      stmt.setLong(1, userId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
        if (log.isDebugEnabled())
          log.debug("getUserById(): Retrieve User: " + u);
      }
      return u;
    } catch (SQLException e) {
      throw new CustomException("Error reading user data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
*/    
  }

  /**
   * Find user by userName
   */
  public User getUserByName(String userName) throws CustomException {
	User u = userName_Map.get(userName);
	if (log.isDebugEnabled()) {
		log.debug("Retrieve User: " + u);
	}
	return u;  
	
/* Commented by Isc	
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    User u = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_GET_USER_BY_NAME);
      stmt.setString(1, userName);
      rs = stmt.executeQuery();
      if (rs.next()) {
        u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
        if (log.isDebugEnabled())
          log.debug("Retrieve User: " + u);
      }
      return u;
    } catch (SQLException e) {
      throw new CustomException("Error reading user data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
*/    
  }

  /**
   * Save User
   */
  public long insertUser(User user) throws CustomException {
	String UserName = user.getUserName();
	if (getUserByName(UserName) != null) {
	    log.error("insertUser(): Creating user failed," + user + ", already exists.");
	    throw new CustomException("Error creating user data", null);
	}
	
	long num = userId_Map.size()+1;
	User u = new User(num, UserName, user.getEmailAddress());
	userId_Map.put(num, u);
	userName_Map.put(UserName, u);
	return num;
	
/*	Commented by Isc  
	  Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
      long id = getAllUsers().stream().mapToLong(User::getUserId).max().orElse(0) + 1;
      stmt.setLong(1,  id);
      stmt.setString(2, user.getUserName());
      stmt.setString(3, user.getEmailAddress());
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        log.error("insertUser(): Creating user failed, no rows affected." + user);
        throw new CustomException("Users Cannot be created");
      }
      return id;
    } catch (SQLException e) {
      log.error("Error Inserting User :" + user);
      throw new CustomException("Error creating user data", e);
    } finally {
      DbUtils.closeQuietly(conn);
    }
*/
  }

  /**
   * Update User
   */
  public int updateUser(Long userId, User user) throws CustomException {
	  User user_in_db = getUserById(userId);
	  if (user_in_db == null) {
		  log.error("No user found" + userId);
	      throw new CustomException("Error update user data");
	  }
	  user_in_db.setUserName(user.getUserName());
	  user_in_db.setEmailAddress(user.getEmailAddress());
	  try {
		  userName_Map.remove(user_in_db.getUserName());
	      User u = new User(userId, user.getUserName(), user.getEmailAddress());
	      userName_Map.put(user.getUserName(), u);
	  } catch (Exception e) {
	      log.error("Error Updating User :" + user);
	      throw new CustomException("Error update user data", e);
	  }
	  return 1;
	    
/*	Commented by Isc  
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_UPDATE_USER);
      stmt.setString(1, user.getUserName());
      stmt.setString(2, user.getEmailAddress());
      stmt.setLong(3, userId);
      return stmt.executeUpdate();
    } catch (SQLException e) {
      log.error("Error Updating User :" + user);
      throw new CustomException("Error update user data", e);
    } finally {
      DbUtils.closeQuietly(conn);
      DbUtils.closeQuietly(stmt);
    }
*/    
  }

  /**
   * Delete User
   */
  public int deleteUser(long userId) throws CustomException {
	User user = getUserById(userId);
	if (user == null) {
	   log.error("Error Deleting User :" + userId);
	   throw new CustomException("Error Deleting User ID:" + userId);
	}
	
	userId_Map.remove(userId);
	userName_Map.remove(user.getUserName());
	return 1;
	 
/*	Commented by Isc  
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_DELETE_USER_BY_ID);
      stmt.setLong(1, userId);
      return stmt.executeUpdate();
    } catch (SQLException e) {
      log.error("Error Deleting User :" + userId);
      throw new CustomException("Error Deleting User ID:" + userId, e);
    } finally {
      DbUtils.closeQuietly(conn);
      DbUtils.closeQuietly(stmt);
    }
    */
  }
 
}
