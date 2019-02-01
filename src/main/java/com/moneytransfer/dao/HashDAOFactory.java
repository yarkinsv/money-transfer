package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.hashimpl.HashAccountDAOImpl;
import com.moneytransfer.dao.impl.hashimpl.HashUserDAOImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import java.math.BigDecimal;

public class HashDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() throws CustomException {
        return new HashUserDAOImpl().init();
    }

    @Override
    public AccountDAO getAccountDAO() throws CustomException {
        return new HashAccountDAOImpl().init();
    }

    @Override
    public void populateTestData() throws CustomException {

    }
}
