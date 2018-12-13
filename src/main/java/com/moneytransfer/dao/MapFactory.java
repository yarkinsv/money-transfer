package com.moneytransfer.dao;


import com.moneytransfer.dao.impl.AccountMapImpl;
import com.moneytransfer.dao.impl.UserMapImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import java.math.BigDecimal;

public class MapFactory extends DAOFactory{

    private static final UserDAO userDAO = UserMapImpl.getInstance();
    private static final AccountDAO accountDAO = AccountMapImpl.getInstance();

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public AccountDAO getAccountDAO() {

        return accountDAO;
    }

    @Override
    public void populateTestData() throws CustomException {
         userDAO.insertUser(new User(1, "test2","test2@gmail.com"));
         userDAO.insertUser(new User(2, "test1","test1@gmail.com"));
         userDAO.insertUser(new User( 3, "yangluo","yangluo@gmail.com"));
         userDAO.insertUser(new User(4, "qinfran","qinfran@gmail.com"));
         userDAO.insertUser(new User( 5, "liusisi","liusisi@gmail.com"));

         accountDAO.createAccount( new Account("yangluo",new BigDecimal(100.0000),"USD"));
         accountDAO.createAccount( new Account("qinfran",new BigDecimal(200.0000),"USD"));
         accountDAO.createAccount( new Account("yangluo",new BigDecimal(500.0000),"EUR"));
         accountDAO.createAccount( new Account("qinfran",new BigDecimal(500.0000),"EUR"));
         accountDAO.createAccount( new Account("yangluo",new BigDecimal(500.0000),"GBP"));
         accountDAO.createAccount( new Account("qinfran",new BigDecimal(500.0000),"GBP"));
    }


    public static DAOFactory getDAOFactory() {
        return new MapFactory();
    }
}
