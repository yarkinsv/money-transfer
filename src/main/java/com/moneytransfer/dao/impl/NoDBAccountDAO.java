package com.moneytransfer.dao.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;

public class NoDBAccountDAO implements AccountDAO {
    private final Map<Long,Account> allAccounts = new ConcurrentHashMap<>();
    private final Map<String,ConcurrentHashMap<String,Long>> accountsByUser =
        new ConcurrentHashMap<String,ConcurrentHashMap<String,Long>>();
    private final LongSupplier nextAccountId = new LongSupplier(){
            private long next = 0;
            @Override
            public long getAsLong() {
		return ++next;
            }
        };

    private long getNextAccountId() {
        return nextAccountId.getAsLong();
    }

    public Set<Account> getAllAccounts() throws CustomException {
        return new HashSet<Account>(allAccounts.values());
    }

    public Account getAccountById(long accountId) throws CustomException {
        return allAccounts.get(accountId);
    }

    public Account getAccountByUser(String user, String currency) throws CustomException{
        Map<String,Long> userAccounts = accountsByUser.get(user);
        return userAccounts == null ? null : getAccountById(userAccounts.get(currency));
    }

    public long createAccount(Account account) throws CustomException {
        String user = account.getUserName();
        BigDecimal balance = account.getBalance();
        String currency = account.getCurrencyCode();

        ConcurrentHashMap<String,Long> userAccounts = accountsByUser.get(user);
        if (userAccounts != null && userAccounts.containsKey(currency))
            throw new CustomException("Error: Creating existing account!");

        long accountId = getNextAccountId();
        Account newAccount = new Account(accountId, user, balance, currency);
        allAccounts.put(accountId, newAccount);

        if (userAccounts != null){
            userAccounts.put(currency, accountId);
            accountsByUser.put(user, userAccounts);
        }
        else {
            userAccounts = new ConcurrentHashMap<>();
            userAccounts.put(currency, accountId);
            accountsByUser.put(user, userAccounts);
        }

        return accountId;
    }

    public int deleteAccountById(long accountId) throws CustomException {
        Account deletedAccount = getAccountById(accountId);
        if (deletedAccount == null) return 0;
        String user = deletedAccount.getUserName();
        Map<String,Long> userAccounts = accountsByUser.get(user);
        if (userAccounts.size() > 1) userAccounts.remove(deletedAccount.getCurrencyCode());
        else accountsByUser.remove(user);
        allAccounts.remove(accountId);
        return 1;
    }

    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        Account updatedAccount = getAccountById(accountId);
        if (updatedAccount == null) return 0;
        BigDecimal updatedBalance = updatedAccount.getBalance().add(deltaAmount);
        if (updatedBalance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + accountId);
        }
        String user = updatedAccount.getUserName();
        String currency = updatedAccount.getCurrencyCode();
        updatedAccount = new Account(accountId, user, updatedBalance, currency);
        accountsByUser.get(user).put(currency, accountId);
        allAccounts.put(accountId, updatedAccount);
        return 1;
    }

    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        long fromAccountId = userTransaction.getFromAccountId();
        long toAccountId = userTransaction.getToAccountId();
        BigDecimal amount = userTransaction.getAmount();
        String currency = userTransaction.getCurrencyCode();

        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);

        if (!fromAccount.getCurrencyCode().equals(currency))
            throw new CustomException
                ("Fail to transfer Fund, transaction ccy are different from source");

        if (!toAccount.getCurrencyCode().equals(currency))
            throw new CustomException
                ("Fail to transfer Fund, transaction ccy are different from destination");

        BigDecimal fromAccountLeftOver =
            fromAccount.getBalance().subtract(userTransaction.getAmount());
        if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0)
            throw new CustomException("Not enough Fund from source Account ");

        Account updatedFromAccount = new Account
            (fromAccountId, fromAccount.getUserName(), fromAccountLeftOver, currency);
        Account updatedToAccount = new Account
            (toAccountId, toAccount.getUserName(), toAccount.getBalance().add(amount), currency);

        allAccounts.put(fromAccountId, updatedFromAccount);
        allAccounts.put(toAccountId, updatedToAccount);

        return 2;
    }

}

