package com.moneytransfer.dao.impl.hashimpl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class HashAccountDAOImpl implements AccountDAO {
    static Set<Account> AllAccounts = null;
    static long ID = 10;

    public AccountDAO init(){
        if (AllAccounts == null) {
            AllAccounts = new HashSet<>();
            AllAccounts.add(new Account(4, "qinfran", new BigDecimal(500), "EUR"));
            AllAccounts.add(new Account(6, "qinfran", new BigDecimal(500), "GBR"));
            AllAccounts.add(new Account(1, "yangluo", new BigDecimal(100), "USD"));
            AllAccounts.add(new Account(3, "yangluo", new BigDecimal(500), "EUR"));
            AllAccounts.add(new Account(5, "yangluo", new BigDecimal(500), "GBR"));
            AllAccounts.add(new Account(2, "qinfran", new BigDecimal(200), "USD"));
        }
        return this;
    }

    @Override
    public Set<Account> getAllAccounts() throws CustomException {
        return AllAccounts;
    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        for (Account account: AllAccounts) {
            if (account.getAccountId() == accountId) {
                return account;
            }
        }
        return null;
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        for (Account account: AllAccounts) {
            if (account.getUserName().equals(user)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        AllAccounts.add(new Account(ID, account.getUserName(), account.getBalance(), account.getCurrencyCode()));
        return ID++;
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        if (AllAccounts.remove(getAccountById(accountId))) {
            return 1;
        }
        return 0;
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        for (Account account: AllAccounts) {
            if (account.getAccountId() == accountId) {
                account.setBalance(deltaAmount);
                return 0;
            }
        }
        return 1;
    }

    @Override
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        Account fromAccount = null;
        Account toAccount = null;
        for (Account account: AllAccounts) {
            if (account.getAccountId() == userTransaction.getFromAccountId()) {
                fromAccount = account;
            }
            if (account.getAccountId() == userTransaction.getToAccountId()) {
                toAccount = account;
            }
        }
        if ((fromAccount == null) || (toAccount == null)) {
            return -1;
        }
        if (fromAccount.getBalance().compareTo(userTransaction.getAmount()) >= 0) {
            fromAccount.setBalance(fromAccount.getBalance().divide(userTransaction.getAmount()));
            toAccount.setBalance(fromAccount.getBalance().add(userTransaction.getAmount()));
            return 2;
        }
        return 0;
    }
}
