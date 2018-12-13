package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMapImpl implements UserDAO {


    private static UserMapImpl instance;
    private static Map<Long,User> userMap = new HashMap<>();
    private static  long nextId = 0;


    private UserMapImpl() {

    }

    public static UserMapImpl getInstance(){
        if (instance==null) {
            instance  = new UserMapImpl();
        }
        return instance;
    }


    @Override
    public List<User> getAllUsers(){
        return userMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public User getUserById(long userId) {
        return userMap.get(userId);
    }

    @Override
    public User getUserByName(String userName){
        User user = null;
        for(User usr:userMap.values())
        {
            if((usr.getUserName().equals(userName)))
            {
                user = usr;
                break;
            }
        }
        return user;
    }

    @Override
    public long insertUser(User user) {
        long id = nextId;
        User user1 = new User(id,user.getUserName(),user.getEmailAddress());
        userMap.put(id,user1);
        nextId++;
        return id;
    }


    @Override
    public int updateUser(Long userId, User user){
        User user1 = new User(userId, user.getUserName(),user.getEmailAddress());
        userMap.put(userId,user1);
        return 0;
    }

    @Override
    public int deleteUser(long userId)  {
        if(userMap.containsKey(userId))
        {
            userMap.remove(userId);
            return 1;
        }
        return 0;
    }
}
