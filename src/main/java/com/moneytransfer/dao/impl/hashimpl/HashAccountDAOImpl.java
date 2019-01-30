package com.moneytransfer.dao.impl.hashimpl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.Set;

public class HashAccountDAOImpl implements AccountDAO {
    @Override
    public Set<Account> getAllAccounts() throws CustomException {
        return null;
    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        return null;
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        return null;
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        return 0;
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        return 0;
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        return 0;
    }

    @Override
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        return 0;
    }
}
