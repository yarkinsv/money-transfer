package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NoDbAccountDAOImpl implements AccountDAO {

    private Logger log = Logger.getLogger(AccountDAOImpl.class);

    private Map<Long, Account> accounts = new ConcurrentHashMap<>();

    // key is hash of userName and currency, value is id of account
    private Map<Integer, Long> accountUserNameIndex = new ConcurrentHashMap<>();

    private AtomicLong lastAccountId = new AtomicLong();

    @Override
    public Set<Account> getAllAccounts() throws CustomException {
        return new HashSet<>(accounts.values());
    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        Account account = accounts.get(accountId);

        if (account == null && log.isDebugEnabled()) {
            log.debug(String.format("Account with id %s wasn't found", accountId));
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Retrieve Account By Id: " + account);
        }

        return account;
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        Long accountId = accountUserNameIndex.get(Objects.hash(user + currency));
        Account account = accounts.get(accountId);
        if (account == null && log.isDebugEnabled()) {
            log.debug(
                    String.format("Account wasn't found with user name = '%s' and currency '%s'", user, currency)
            );
        }

        return account;
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        long accountId = account.getAccountId();
        if (accountId != 0 && accounts.get(accountId) != null) {
            throw new CustomException(String.format("Account with id %s already exists", accountId));
        }

        if (accountUserNameIndex.get(account.getUserAndCurrencyHash()) != null) {
            throw new CustomException(String.format("Account with username %s and currency %s already exists", account.getUserName(), account.getCurrencyCode()));
        }

        accountId = lastAccountId.incrementAndGet();
        account.setAccountId(accountId);
        accounts.put(accountId, account);
        accountUserNameIndex.put(account.getUserAndCurrencyHash(), accountId);
        return accountId;
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        Account removedAccount = accounts.remove(accountId);
        return removedAccount == null ? 0 : 1;
    }

    @Override
    public synchronized int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        Account account = accounts.get(accountId);
        if (account == null) {
            return 0;
        }

        BigDecimal oldBalance = account.getBalance();
        BigDecimal newBalance = oldBalance.add(deltaAmount);
        if (newBalance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + account);
        }

        account.addToBalance(deltaAmount);
        return 1;
    }

    @Override
    public synchronized int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        Account fromAccount = accounts.get(userTransaction.getFromAccountId());
        Account toAccount = accounts.get(userTransaction.getToAccountId());
        String transactionCurrencyCode = userTransaction.getCurrencyCode();

        if (!fromAccount.getCurrencyCode().equals(transactionCurrencyCode)
                || !toAccount.getCurrencyCode().equals(transactionCurrencyCode)) {
            throw new CustomException(
                    "Fail to transfer Fund, the source and destination account are in different currency");
        }

        BigDecimal fromBalance = fromAccount.getBalance();
        BigDecimal newFromBalance = fromBalance.subtract(userTransaction.getAmount());

        if (newFromBalance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + fromAccount);
        }

        BigDecimal toBalance = toAccount.getBalance();
        BigDecimal newToBalance = toBalance.add(userTransaction.getAmount());

        if (newToBalance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + fromAccount);
        }

        fromAccount.subtractFromBalance(userTransaction.getAmount());
        toAccount.addToBalance(userTransaction.getAmount());

        return 2;
    }

    public void clear() {
        this.accounts = new ConcurrentHashMap<>();
        this.accountUserNameIndex = new ConcurrentHashMap<>();
        this.lastAccountId = new AtomicLong();
    }
}
