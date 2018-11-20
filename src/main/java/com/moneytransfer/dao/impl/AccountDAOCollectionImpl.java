package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.Set;

public class AccountDAOCollectionImpl implements AccountDAO {
    @Override
    public Set<Account> getAllAccounts() throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
