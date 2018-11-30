package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

public class AccountHMDAOImpl implements AccountDAO {
    private static Logger log = Logger.getLogger(AccountHMDAOImpl.class);

    private HashMap<Long, Account> accounts = new HashMap<>();
    private HashMap<UserNameCurrency, Account> userToAccount = new HashMap<>();
    private long accountNum = 1;

    private class UserNameCurrency {
        private String userName;
        private String currency;
        UserNameCurrency(String name, String cur) {
            userName = name;
            currency = cur;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserNameCurrency that = (UserNameCurrency) o;
            return Objects.equals(userName, that.userName) &&
                    Objects.equals(currency, that.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userName, currency);
        }
    }
    @Override
    public Set<Account> getAllAccounts() throws CustomException {
        return new HashSet<>(accounts.values());

    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        return accounts.get(accountId);
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        return userToAccount.get(new UserNameCurrency(user, currency));
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        UserNameCurrency userNameCurrency =
            new UserNameCurrency(account.getUserName(), account.getCurrencyCode());
        if (userToAccount.containsKey(userNameCurrency)) {
            throw new CustomException("Account already exists;");
        }
        Account newAccount = new Account(accountNum,
                                        account.getUserName(),
                                        account.getBalance(),
                                        account.getCurrencyCode()
                                        );
        accounts.put(accountNum, newAccount);
        userToAccount.put(new UserNameCurrency(newAccount.getUserName(),
                                newAccount.getCurrencyCode()
                                ),
                          newAccount);
        return accountNum++;
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        if (accounts.containsKey(accountId)) {
            Account account = accounts.get(accountId);
            userToAccount.remove(new UserNameCurrency(account.getUserName(), account.getCurrencyCode()));
            accounts.remove(accountId);
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public synchronized int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new CustomException("Account " + accountId + " doesn't exists");
        }
        BigDecimal amountAfterAlteration = account.getBalance().add(deltaAmount);
        if (amountAfterAlteration.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund");
        }
        BigDecimal updatedBalance = account.getBalance().add(deltaAmount);
        Account updatedAcc = new Account(accountId, account.getUserName(), updatedBalance, account.getCurrencyCode());
        UserNameCurrency updated = new UserNameCurrency(account.getUserName(), account.getCurrencyCode());
        accounts.put(accountId, updatedAcc);
        userToAccount.put(updated, updatedAcc);
        return 1;
    }

    @Override
    public synchronized int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        long fromAccountId = userTransaction.getFromAccountId();
        Account fromAccount = accounts.get(fromAccountId);
        long toAccountId = userTransaction.getToAccountId();
        Account toAccount = accounts.get(toAccountId);
        String transactionCurrency = userTransaction.getCurrencyCode();

        if (fromAccount == null || toAccount == null) {
            throw new CustomException("One of accounts doesn't exist");
        }

        if (!fromAccount.getCurrencyCode().equals(transactionCurrency)) {
            throw new CustomException(
                    "Fail to transfer funds, transaction currency is different from source/destination");
        }

        if (!fromAccount.getCurrencyCode().equals(transactionCurrency)) {
            throw new CustomException(
                    "Fail to transfer funds, transaction currency is different from source/destianation");
        }
        BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
        if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not enough funds in source Account");
        }
        fromAccount = new Account(fromAccountId, fromAccount.getUserName(), fromAccountLeftOver, transactionCurrency);
        accounts.put(fromAccountId, fromAccount);
        userToAccount.put(new UserNameCurrency(fromAccount.getUserName(), transactionCurrency), fromAccount);

        BigDecimal toAccountLeftOver = toAccount.getBalance().add(userTransaction.getAmount());
        toAccount = new Account(toAccountId, toAccount.getUserName(), toAccountLeftOver, transactionCurrency);
        accounts.put(toAccountId, toAccount);
        userToAccount.put(new UserNameCurrency(toAccount.getUserName(), transactionCurrency), toAccount);
        return 2;
    }
}
