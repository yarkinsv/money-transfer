package com.moneytransfer.dao.hashimpl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;


public class AccountDAOImpl implements AccountDAO {
    private static Logger log = Logger.getLogger(com.moneytransfer.dao.impl.AccountDAOImpl.class);

    private static final TreeMap<Long, Account> accounts = new TreeMap<>();
    private static final HashMap<Pair, Long> accountsUserCcIndex = new HashMap<>();
    private static Long accountsIdSeq = new Long(0);


    public Set<Account> getAllAccounts() throws CustomException {
        return new HashSet<>(accounts.values());
    }

    /**
     * Get account by id
     */
    public Account getAccountById(long accountId) throws CustomException {
        Account acc = accounts.get(accountId);
        if (log.isDebugEnabled()) {
            log.debug("Retrieve Account By Id: " + acc);
        }
        return acc;
    }


    /**
     * Get account by user and currency
     */
    public Account getAccountByUser(String user, String currency) throws CustomException {
        Long id = accountsUserCcIndex.get(new Pair(user, currency));

        if(id == null)
            return null;

        Account acc = accounts.get(id);

        if (acc != null && log.isDebugEnabled()) {
            log.debug("Retrieve Account By userId: " + acc);
        }
        return acc;
    }

    /**
     * Create new account
     */
    public long createAccount(Account account) throws CustomException {
        Long id = accountsIdSeq ++;

        Account acc = new Account(
            id,
            account.getUserName(),
            account.getBalance(),
            account.getCurrencyCode()
        );

        accounts.put(id, acc);
        accountsUserCcIndex.put(new Pair(account.getUserName(), account.getCurrencyCode()), id);
        return id;
    }

    public int deleteAccountById(long accountId) throws CustomException {
        Account acc = accounts.remove(accountId);

        if (acc != null) {
            accountsUserCcIndex.remove(new Pair(acc.getUserName(), acc.getCurrencyCode()));
            return 1;
        }
        else return 0;
    }

    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        Account targetAccount = accounts.get(accountId);

        if (targetAccount == null) {
            throw new CustomException("updateAccountBalance(): fail to lock account : " + accountId);
        }

        if (log.isDebugEnabled()) {
            log.debug("updateAccountBalance from Account: " + targetAccount);
        }
        BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
        if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + accountId);
        }

        if (accounts.replace(
                accountId,
                new Account(
                        accountId,
                        targetAccount.getUserName(),
                        balance,
                        targetAccount.getCurrencyCode()
                ))!=null)
        {
            if (log.isDebugEnabled()) {
                log.debug("New Balance after Update: " + targetAccount);
            }
            return 1;
        }
        return 0;
    }

    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        int result = -1;
        Account fromAccount = accounts.get(userTransaction.getFromAccountId());
        if (log.isDebugEnabled()) {
            log.debug("transferAccountBalance from Account: " + fromAccount);
        }

        Account toAccount = accounts.get(userTransaction.getToAccountId());
        if (log.isDebugEnabled()) {
            log.debug("transferAccountBalance to Account: " + toAccount);
        }

        // check locking status
        if (fromAccount == null || toAccount == null) {
            throw new CustomException("Fail to open both accounts for write");
        }

        // check transaction currency
        if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
            throw new CustomException(
                    "Fail to transfer Fund, transaction ccy are different from source/destination");
        }

        // check ccy is the same for both accounts
        if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
            throw new CustomException(
                    "Fail to transfer Fund, the source and destination account are in different currency");
        }

        // check enough fund in source account
        BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
        if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not enough Fund from source Account ");
        }

        accounts.replace(fromAccount.getAccountId(),
                new Account(fromAccount.getAccountId(), fromAccount.getUserName(), fromAccountLeftOver, fromAccount.getCurrencyCode()));
        accounts.replace(toAccount.getAccountId(),
                new Account(toAccount.getAccountId(), toAccount.getUserName(), toAccount.getBalance().add(userTransaction.getAmount()), toAccount.getCurrencyCode()));



        return 0;
    }

}