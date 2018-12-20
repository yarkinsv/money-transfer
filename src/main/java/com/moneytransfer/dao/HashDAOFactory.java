package com.moneytransfer.dao;

import com.moneytransfer.dao.hashimpl.AccountDAOImpl;
import com.moneytransfer.dao.hashimpl.UserDAOImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import org.apache.log4j.Logger;

import java.math.BigDecimal;


public class HashDAOFactory extends DAOFactory {
    private static Logger log = Logger.getLogger(H2DAOFactory.class);

    private static final UserDAO userDAOImpl = new UserDAOImpl();
    private static final AccountDAO accountDAOImpl = new AccountDAOImpl();

    public UserDAO getUserDAO() {
        return userDAOImpl;
    }

    public AccountDAO getAccountDAO() {
        return accountDAOImpl;
    }

    @Override
    public void populateTestData() throws CustomException {
        userDAOImpl.insertUser(new User(1, "test2", "test2@gmail.com"));
        userDAOImpl.insertUser(new User(2, "test1", "test1@gmail.com"));
        userDAOImpl.insertUser(new User(3, "yangluo", "yangluo@gmail.com"));
        userDAOImpl.insertUser(new User(4, "qinfran", "qinfran@gmail.com"));
        userDAOImpl.insertUser(new User(5, "liusisi", "liusisi@gmail.com"));

        accountDAOImpl.createAccount(new Account("yangluo", BigDecimal.valueOf(100.0000),"USD"));
        accountDAOImpl.createAccount(new Account("qinfran", BigDecimal.valueOf(200.0000),"USD"));
        accountDAOImpl.createAccount(new Account("yangluo", BigDecimal.valueOf(500.0000),"EUR"));
        accountDAOImpl.createAccount(new Account("qinfran", BigDecimal.valueOf(500.0000),"EUR"));
        accountDAOImpl.createAccount(new Account("yangluo", BigDecimal.valueOf(500.0000),"GBP"));
        accountDAOImpl.createAccount(new Account("qinfran", BigDecimal.valueOf(500.0000),"GBP"));
    }
}
