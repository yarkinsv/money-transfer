package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.*;

public class AccountDAOImplHashMap implements AccountDAO {

    HashMap<Long, Account> accounts = new HashMap<>();
    long counter = 1;

    @Override
    public List<Account> getAllAccounts() throws CustomException {
        List<Account> result = new ArrayList<>();

        for (Account value : accounts.values()) {
            result.add(value);
        }
        return result;
    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        return accounts.get(accountId);
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        return getAllAccounts()
                .stream()
                .filter(account -> account.getUserName().equals(user) && account.getCurrencyCode().equals(currency))
                .findFirst()
                .orElse(null);
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        accounts.put(counter, account);
        return counter++;
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        if (accounts.containsKey(accountId)) {
            accounts.remove(accountId);
            return 1;
        }
        return 0;
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        if (accounts.containsKey(accountId)) {
            accounts.get(accountId).getBalance().add(deltaAmount);
            return 1;
        }
        return 0;
    }

    @Override
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        long from = userTransaction.getFromAccountId();
        long to = userTransaction.getToAccountId();
        if (accounts.containsKey(from) && accounts.containsKey(to)) {
            updateAccountBalance(from, userTransaction.getAmount().negate());
            updateAccountBalance(to, userTransaction.getAmount());
            return 2;
        }
        return 0;
    }
}
