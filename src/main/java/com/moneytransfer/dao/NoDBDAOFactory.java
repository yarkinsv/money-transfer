package com.moneytransfer.dao;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.dao.UserDAO;
import com.moneytransfer.dao.impl.NoDBAccountDAO;
import com.moneytransfer.dao.impl.NoDBUserDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import com.moneytransfer.model.Account;

public class NoDBDAOFactory extends DAOFactory {
    private static Logger log = Logger.getLogger(NoDBDAOFactory.class);
    private static UserDAO userDAO = new NoDBUserDAO();
    private static AccountDAO accountDAO = new NoDBAccountDAO();

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    @Override
    public void populateTestData() throws CustomException {
        userDAO = new NoDBUserDAO();
        accountDAO = new NoDBAccountDAO();
        log.info("Populating Test User Table and data ..... ");
        userDAO.insertUser(new User("test2", "test2@gmail.com"));
        userDAO.insertUser(new User("test1", "test1@gmail.com"));
        userDAO.insertUser(new User("yangluo", "yangluo@gmail.com"));
        userDAO.insertUser(new User("qinfran", "qinfran@gmail.com"));
        userDAO.insertUser(new User("liusisi", "liusisi@gmail.com"));
        accountDAO.createAccount(new Account("yangluo", new BigDecimal("100.0000"), "USD"));
        accountDAO.createAccount(new Account("qinfran", new BigDecimal("200.0000"), "USD"));
        accountDAO.createAccount(new Account("yangluo", new BigDecimal("500.0000"), "EUR"));
        accountDAO.createAccount(new Account("qinfran", new BigDecimal("500.0000"), "EUR"));
        accountDAO.createAccount(new Account("yangluo", new BigDecimal("500.0000"), "GBP"));
        accountDAO.createAccount(new Account("qinfran", new BigDecimal("500.0000"), "GBP"));
    }
}
