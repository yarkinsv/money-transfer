package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountDAOCollectionImpl implements AccountDAO {
    private static final Logger log = Logger.getLogger(AccountDAOCollectionImpl.class);

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final ConcurrentHashMap<Long, Account> accountById = new ConcurrentHashMap<>(1000, 0.75f, 6);
    private static final ConcurrentHashMap<Pair, Long> accountIdByUser = new ConcurrentHashMap<>(1000, 0.75f, 6);
    private static final AtomicLong currentId = new AtomicLong(0);

    @Override
    public Set<Account> getAllAccounts() throws CustomException {
        return new HashSet<>(accountById.values());
    }

    @Override
    public Account getAccountById(long accountId) throws CustomException {
        Account account = accountById.getOrDefault(accountId, null);// null for consistency with H2DAO
        if (account != null && log.isDebugEnabled()) {
            log.debug("getAccountById(): Retrieve account: " + account);
        }
        return account;
    }

    @Override
    public Account getAccountByUser(String user, String currency) throws CustomException {
        Long accountId = accountIdByUser.get(new Pair(user, currency));
        Account account;
        if (accountId == null) {
            account = null;
        } else {
            account = accountById.get(accountId);
        }
        if (account != null && log.isDebugEnabled()) {
            log.debug("getAccountByUser(): Retrieve account: " + user);
        }
        return account;
    }

    @Override
    public long createAccount(Account account) throws CustomException {
        try {
            lock.writeLock().lock();
            Account insertAccount = new Account(
                    currentId.incrementAndGet(),
                    account.getUserName(),
                    account.getBalance(),
                    account.getCurrencyCode());
            accountById.put(insertAccount.getAccountId(), insertAccount);
            accountIdByUser.put(new Pair(account.getUserName(), account.getCurrencyCode()), insertAccount.getAccountId());
            return insertAccount.getAccountId();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int deleteAccountById(long accountId) throws CustomException {
        try {
            lock.writeLock().lock();
            Account remove = accountById.remove(accountId);
            if (remove != null) {
                accountIdByUser.remove(new Pair(remove.getUserName(), remove.getCurrencyCode()));
                return 1;
            }
            return 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        try {
            lock.writeLock().lock();
            Account account = accountById.get(accountId);
            if (account == null) {
                if (log.isDebugEnabled()) {
                    log.debug("updateAccountBalance() fail for account: " + account);
                }
                return 0;
            }
            if (log.isDebugEnabled()) {
                log.debug("updateAccountBalance from Account: " + account);
            }
            BigDecimal balance = account.getBalance().add(deltaAmount);
            if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
                throw new CustomException("Not sufficient Fund for account: " + accountId);
            }
            Account updateAccount = new Account(accountId, account.getUserName(), balance, account.getCurrencyCode());
            Account replace = accountById.replace(accountId, updateAccount);
            if (replace == null) {
                if (log.isDebugEnabled()) {
                    log.debug("updateAccountBalance from Account: " + account);
                }
                return 0;
            }
            return 1;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        try {
            lock.writeLock().lock();
            Account from = accountById.get(userTransaction.getFromAccountId());
            Account to = accountById.get(userTransaction.getToAccountId());
            if (from == null || to == null) {
                if (log.isDebugEnabled()) {
                    throw new CustomException("Fail to get one or both accounts");
                }
                return 0;
            }
            if (log.isDebugEnabled()) {
                log.debug("transferAccountBalance from Account: " + from);
                log.debug("transferAccountBalance to Account: " + to);
            }
            // check transaction currency
            if (!from.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
                throw new CustomException(
                        "Fail to transfer Fund, transaction ccy are different from source/destination");
            }
            // check ccy is the same for both accounts
            if (!from.getCurrencyCode().equals(to.getCurrencyCode())) {
                throw new CustomException(
                        "Fail to transfer Fund, the source and destination account are in different currency");
            }
            // check enough fund in source account
            BigDecimal fromAccountLeftOver = from.getBalance().subtract(userTransaction.getAmount());
            if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
                throw new CustomException("Not enough Fund from source Account ");
            }
            accountById.replace(from.getAccountId(), new Account(from.getAccountId(), from.getUserName(), fromAccountLeftOver, from.getCurrencyCode()));
            accountById.replace(to.getAccountId(), new Account(to.getAccountId(), to.getUserName(), to.getBalance().add(userTransaction.getAmount()), to.getCurrencyCode()));
            return 2;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private class Pair {
        final String user;
        final String currency;

        Pair(String user, String currency) {
            this.user = user;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(user, pair.user) &&
                    Objects.equals(currency, pair.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, currency);
        }
    }
}
