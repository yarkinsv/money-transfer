package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.AccountHMDAOImpl;
import com.moneytransfer.dao.impl.UserHMDAOImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class HMDAOFactory extends DAOFactory {
    private static Logger log = Logger.getLogger(HMDAOFactory.class);

    private static final UserDAO userDAO = new UserHMDAOImpl();
    private static final AccountDAO accountDAO = new AccountHMDAOImpl();


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
        log.info("Populating Test User Table and data ..... ");
        userDAO.insertUser(new User("test2", "test2@gmail.com"));
        userDAO.insertUser(new User("test1","test1@gmail.com"));
        userDAO.insertUser(new User("yangluo","yangluo@gmail.com"));
        userDAO.insertUser(new User("qinfran","qinfran@gmail.com"));
        userDAO.insertUser(new User("liusisi","liusisi@gmail.com"));


        accountDAO.createAccount(new Account("yangluo", new BigDecimal(100.0000),"USD"));
        accountDAO.createAccount (new Account("qinfran",new BigDecimal(200.0000),"USD"));
        accountDAO.createAccount (new Account("yangluo",new BigDecimal(500.0000),"EUR"));
        accountDAO.createAccount(new Account("qinfran",new BigDecimal(500.0000),"EUR"));
        accountDAO.createAccount(new Account("yangluo",new BigDecimal(500.0000),"GBP"));
        accountDAO.createAccount(new Account("qinfran",new BigDecimal(500.0000),"GBP"));


    }
}
