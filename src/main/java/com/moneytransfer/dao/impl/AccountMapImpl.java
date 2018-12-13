package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountMapImpl implements AccountDAO {

    private static AccountMapImpl instance;
    private static Map<Long,Account> accountMap = new HashMap<>();
    private static  long nextId = 0;


    private AccountMapImpl() {

    }

    public static AccountMapImpl getInstance(){
        if (instance==null) {
            instance  = new AccountMapImpl();
        }
        return instance;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public Account getAccountById(long accountId) {
        return accountMap.get(accountId);
    }


    @Override
    public Account getAccountByUser(String user, String currency) {
        Account acc = null;
        for(Account account:accountMap.values())
        {
            if((account.getUserName().equals(user))||(account.getCurrencyCode().equals(currency)))
            {
                acc = account;
                break;
            }
        }
        return acc;
    }

    @Override
    public long createAccount(Account account) {
        long id = nextId;
        Account acc = new Account(nextId,account.getUserName(),account.getBalance(),account.getCurrencyCode());
        accountMap.put(id,acc);
        nextId++;
        return id;
    }

    @Override
    public int deleteAccountById(long accountId) {
        if(accountMap.containsKey(accountId)) {
            accountMap.remove(accountId);
            return 1;
        }
        return 0;
    }

    @Override
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        Account targetAccount = getAccountById(accountId);
        if(targetAccount==null) {
            throw new CustomException("updateAccountBalance(): fail to lock account : " + accountId);
        }

        BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
        if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + accountId);
        }

        Account acc = new Account(targetAccount.getAccountId(),targetAccount.getUserName(),balance,targetAccount.getCurrencyCode());
        accountMap.put(targetAccount.getAccountId(), acc);

        return 0;
    }

    @Override
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        Account fromAcc = accountMap.get(userTransaction.getFromAccountId());
        Account toAcc = accountMap.get(userTransaction.getToAccountId());

        //check locking status
        if (fromAcc == null || toAcc == null) {
            throw new CustomException("Fail to lock both accounts for write");
        }

        // check transaction currency
        if (!fromAcc.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
            throw new CustomException(
                    "Fail to transfer Fund, transaction ccy are different from source/destination");
        }

        // check ccy is the same for both accounts
        if (!fromAcc.getCurrencyCode().equals(toAcc.getCurrencyCode())) {
            throw new CustomException(
                    "Fail to transfer Fund, the source and destination account are in different currency");
        }

        BigDecimal fromAccountLeftOver = fromAcc.getBalance().subtract(userTransaction.getAmount());
        if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not enough Fund from source Account ");
        }
        BigDecimal toAccountLeftOver = toAcc.getBalance().add(userTransaction.getAmount());

        fromAcc = new Account(fromAcc.getAccountId(),fromAcc.getUserName(),fromAccountLeftOver,fromAcc.getCurrencyCode());
        toAcc = new Account(toAcc.getAccountId(),toAcc.getUserName(),toAccountLeftOver,toAcc.getCurrencyCode());
        accountMap.put(fromAcc.getAccountId(),fromAcc);
        accountMap.put(toAcc.getAccountId(),toAcc);

        return 0;
    }
}
