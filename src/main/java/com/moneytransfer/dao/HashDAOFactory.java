package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.hashimpl.HashAccountDAOImpl;
import com.moneytransfer.dao.impl.hashimpl.HashUserDAOImpl;
import com.moneytransfer.exception.CustomException;

public class HashDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new HashUserDAOImpl();
    }

    @Override
    public AccountDAO getAccountDAO() {
        return new HashAccountDAOImpl();
    }

    @Override
    public void populateTestData() throws CustomException {

    }
}
