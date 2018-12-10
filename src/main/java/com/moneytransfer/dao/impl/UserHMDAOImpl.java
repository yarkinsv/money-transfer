package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.HMDAOFactory;
import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserHMDAOImpl implements UserDAO {

  private static Logger log = Logger.getLogger(UserHMDAOImpl.class);

  private static final ConcurrentHashMap <Long, User> usersById   = new ConcurrentHashMap<>(500);
  private static final ConcurrentHashMap <String, Long> usersByName = new ConcurrentHashMap<>(500);
  private static final AtomicLong userNewId = new AtomicLong(1);

  /**
   * Find all users
   */
  public List<User> getAllUsers() throws CustomException {
    List<User> users = new ArrayList<>(usersById.values());
    for (User u : users) {
      if (log.isDebugEnabled()) {
        log.debug("getAllUsers(): Retrieve User " + u);
      }
    }
    return users;
  }

  /**
   * Find user by userId
   */
  public User getUserById(long userId) throws CustomException {
    User u;
    if (usersById.containsKey(userId))
      u = usersById.get(userId);
    else
      return null;
/*    if (u==null)
    {
      if (log.isDebugEnabled()) {
        log.error("getUserById(): User not find.");
      }
      throw new CustomException("User not find");
    }*/
    if (log.isDebugEnabled()) {
      log.debug("getUserById(): Retrieve User: " + u);
    }
    return u;
  }

  /**
   * Find user by userName
   */
  public User getUserByName(String userName) throws CustomException {
    long userId;
    if (usersByName.containsKey(userName))
      userId = usersByName.get(userName);
    else
      return null;
/*    if (userId == 0) {
      if (log.isDebugEnabled()) {
        log.error("getUserByName(): User ID not find.");
      }
      throw new CustomException("User ID not find");
    }*/
    if (log.isDebugEnabled()) {
      log.debug("Retrieve User ID By Name: " + userId);
    }
    User u;
    if (usersById.containsKey(userId))
      u = usersById.get(userId);
    else
      return null;
/*    if (u==null)
    {
      if (log.isDebugEnabled()) {
        log.error("getUserByName(): User not find.");
      }
      throw new CustomException("User not find");
    }*/
    if (log.isDebugEnabled()) {
      log.debug("Retrieve User: " + u);
    }
    return u;
  }

  /**
   * Save User
   */
  public long insertUser(User user) throws CustomException {
    if (usersByName.containsKey(user.getUserName()))
    {
      log.error("insertUser(): Creating user failed, no rows affected." + user);
      throw new CustomException("Users Cannot be created");
    }
    User u = new User(userNewId.getAndIncrement(),user.getUserName(),user.getEmailAddress());
    usersById.put(u.getUserId(),u);
    usersByName.put(u.getUserName(),u.getUserId());
    return u.getUserId();
  }

  /**
   * Update User
   */
  public int updateUser(Long userId, User user) throws CustomException {
    if (!usersById.containsKey(userId)) {
      if (log.isDebugEnabled()) {
        log.error("updateUser(): User not find.");
      }
      throw new CustomException("User not find");
    }
    User u=usersById.get(userId);
    if (!usersByName.containsKey(u.getUserName())) {
      if (log.isDebugEnabled()) {
        log.error("updateUser(): User not find.");
      }
      throw new CustomException("User not find");
    }
    usersByName.remove(u.getUserName(),userId);
    usersByName.put(user.getUserName(),userId);
    if (userId==user.getUserId())
      usersById.replace(userId,u,user);
    else {
      u = new User(userId, user.getUserName(), user.getEmailAddress());
      usersById.replace(userId, u);
    }
    return 1;
  }

  /**
   * Delete User
   */
  public int deleteUser(long userId) throws CustomException {
    if (!usersById.containsKey(userId)) {
      if (log.isDebugEnabled()) {
        log.error("deleteUser(): User not find.");
      }
      throw new CustomException("User not find");
    }
    User u = usersById.get(userId);
    if (!usersByName.containsKey(u.getUserName())) {
      if (log.isDebugEnabled()) {
        log.error("deleteUser(): User not find.");
      }
      throw new CustomException("User not find");
    }
    usersByName.remove(u.getUserName(), userId);
    usersById.remove(u.getUserId(), u);
    return 1;
  }
}
