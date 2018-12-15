package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.NoDbAccountDAOImpl;
import com.moneytransfer.dao.impl.NoDbUserDAOImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import java.math.BigDecimal;

public class NoDbDAOFactory extends DAOFactory {

    private static final NoDbUserDAOImpl userDAO = new NoDbUserDAOImpl();
    private static final NoDbAccountDAOImpl accountDAO = new NoDbAccountDAOImpl();

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
        userDAO.clear();
        accountDAO.clear();

        userDAO.insertUser(new User("test2", "test2@gmail.com"));
        userDAO.insertUser(new User("test1", "test1@gmail.com"));
        userDAO.insertUser(new User("yangluo", "yangluo@gmail.com"));
        userDAO.insertUser(new User("qinfran", "qinfran@gmail.com"));
        userDAO.insertUser(new User("liusisi", "liusisi@gmail.com"));

        accountDAO.createAccount(new Account("yangluo",new BigDecimal(100),"USD"));
        accountDAO.createAccount(new Account("qinfran",new BigDecimal(200.0000),"USD"));
        accountDAO.createAccount(new Account("yangluo",new BigDecimal(500.0000),"EUR"));
        accountDAO.createAccount(new Account("qinfran",new BigDecimal(500.0000),"EUR"));
        accountDAO.createAccount(new Account("yangluo",new BigDecimal(500.0000),"GBP"));
        accountDAO.createAccount(new Account("qinfran",new BigDecimal(500.0000),"GBP"));
    }
}
