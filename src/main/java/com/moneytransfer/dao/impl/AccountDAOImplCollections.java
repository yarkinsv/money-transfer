package com.moneytransfer.dao.impl;

import org.apache.log4j.Logger;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AccountDAOImplCollections implements AccountDAO {

  private static Logger log = Logger.getLogger(AccountDAOImplCollections.class);
  
  private static Map<Long, Account> accountIdMap = new HashMap<>();
  private static Map<AccountParams, Account> accountUserMap = new HashMap<>();
  
  private class AccountParams {
	  
	  private String userName;
	  private String currencyCode;
	  
	  public AccountParams(String userName, String currencyCode) {
		  this.userName = userName;
		  this.currencyCode = currencyCode;
	  }
	  
	  @Override
	  public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;

	    AccountParams accountParams = (AccountParams) o;

	    if (!userName.equals(accountParams.userName)) return false;
	    return currencyCode.equals(accountParams.currencyCode);
	  }

	  @Override
	  public int hashCode() {
			int result = userName.hashCode();
			result = 31 * result + currencyCode.hashCode();
			return result;
	  }
  }
  
  /**
   * Get all accounts.
   */
  public List<Account> getAllAccounts() throws CustomException {
    return new ArrayList<Account>(accountIdMap.values());
  }

  /**
   * Get account by id
   */
  public Account getAccountById(long accountId) throws CustomException {
    Account acc = accountIdMap.get(accountId);
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account By Id: " + acc);
    }
    return acc;
  }

  public Account getAccountByUser(String user, String currency) throws CustomException {
    AccountParams accountParams = new AccountParams(user, currency);
    Account acc = accountUserMap.get(accountParams);
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account By userId: " + acc);
    }
    return acc;
  }

  /**
   * Create account
   */
  public long createAccount(Account account) throws CustomException {
	  String userName = account.getUserName();
	  String currencyCode = account.getCurrencyCode();
	  if (getAccountByUser(userName, currencyCode) != null) {
		  throw new CustomException("Account with such parameters already exists.");
	  }
    long id = accountIdMap.size() + 1;
    Account acc = new Account(id, userName, account.getBalance(), currencyCode);
	  AccountParams currentAccountParams = new AccountParams(userName, currencyCode);
	  try {
	    accountIdMap.put(id, acc);
	    accountUserMap.put(currentAccountParams, acc);
	  } catch(Exception e) {
		  try {
		    accountIdMap.remove(id);
		    accountUserMap.remove(currentAccountParams);
		  } catch (Exception re) {
		      log.error("Error rollback Inserting Account  " + acc);
			  throw new CustomException("createAccount(): Error rollback creating user account " + acc, re);
		  }
	      log.error("Error Inserting Account  " + acc);
		  throw new CustomException("createAccount(): Error creating user account " + acc, e);
	  }
	  return id;
  }

  /**
   * Delete account by id
   */
  public int deleteAccountById(long accountId) throws CustomException {
	  Account account = getAccountById(accountId);
	  if (account == null) {
		  return 0;
	  }
	  AccountParams currentAccountParams = new AccountParams(account.getUserName(), account.getCurrencyCode());
	  try {
		  accountIdMap.remove(accountId);
		  accountUserMap.remove(currentAccountParams);
	  } catch (Exception e) {
	      throw new CustomException("deleteAccountById(): Error deleting user account Id " + accountId, e);
	  }
	  return 1;
  }

  /**
   * Update account balance
   */
  public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
    Account targetAccount = getAccountById(accountId);
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
    targetAccount.setBalance(balance);
    if (log.isDebugEnabled()) {
      log.debug("New Balance after Update: " + targetAccount);
    }
    return 1;
  }

  /**
   * Transfer balance between two accounts.
   */
  public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
	  int result = 0;
    Account fromAccount = getAccountById(userTransaction.getFromAccountId());
    if (log.isDebugEnabled()) {
        log.debug("transferAccountBalance from Account: " + fromAccount);
    }
    Account toAccount = getAccountById(userTransaction.getToAccountId());
    if (log.isDebugEnabled()) {
        log.debug("transferAccountBalance to Account: " + toAccount);
    }
    // check locking status
    if (fromAccount == null || toAccount == null) {
      throw new CustomException("Fail to lock both accounts for write");
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
    // proceed with update
    BigDecimal oldFromAccountBalance = fromAccount.getBalance();
    BigDecimal oldToAccountBalance = toAccount.getBalance();
    try {
      fromAccount.setBalance(fromAccountLeftOver);
      toAccount.setBalance(toAccount.getBalance().add(userTransaction.getAmount()));
      result = 2;
      if (log.isDebugEnabled()) {
          log.debug("Number of rows updated for the transfer : " + result);
      }
    } catch (Exception e) {
      log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction, e);
      try {
        fromAccount.setBalance(oldFromAccountBalance);
        toAccount.setBalance(oldToAccountBalance);
      } catch (Exception re) {
        throw new CustomException("Fail to rollback transaction", re);
      }
    }
    return result;
  }

  public void dropCollections() {
	  accountIdMap.clear();
	  accountUserMap.clear();
  }
  
}