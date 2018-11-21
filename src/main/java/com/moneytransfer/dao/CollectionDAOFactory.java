package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.AccountDAOCollectionImpl;
import com.moneytransfer.dao.impl.UserDAOCollectionImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import java.math.BigDecimal;

public class CollectionDAOFactory extends DAOFactory {
    private static final UserDAO userDAO = new UserDAOCollectionImpl();
    private static final AccountDAO accountDAO = new AccountDAOCollectionImpl();

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
        ((UserDAOCollectionImpl) userDAO).clean();
        ((AccountDAOCollectionImpl) accountDAO).clean();

        userDAO.insertUser(new User(1, "test2", "test2@gmail.com"));
        userDAO.insertUser(new User(2, "test1", "test1@gmail.com"));
        userDAO.insertUser(new User(3, "yangluo", "yangluo@gmail.com"));
        userDAO.insertUser(new User(4, "qinfran", "qinfran@gmail.com"));
        userDAO.insertUser(new User(5, "liusisi", "liusisi@gmail.com"));

        accountDAO.createAccount(new Account("yangluo", BigDecimal.valueOf(100.0000),"USD"));
        accountDAO.createAccount(new Account("qinfran", BigDecimal.valueOf(200.0000),"USD"));
        accountDAO.createAccount(new Account("yangluo", BigDecimal.valueOf(500.0000),"EUR"));
        accountDAO.createAccount(new Account("qinfran", BigDecimal.valueOf(500.0000),"EUR"));
        accountDAO.createAccount(new Account("yangluo", BigDecimal.valueOf(500.0000),"GBP"));
        accountDAO.createAccount(new Account("qinfran", BigDecimal.valueOf(500.0000),"GBP"));
    }
}
