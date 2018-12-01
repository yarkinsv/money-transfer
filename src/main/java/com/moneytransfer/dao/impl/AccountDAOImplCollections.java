package com.moneytransfer.dao.impl;

import org.apache.log4j.Logger;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImplCollections implements AccountDAO {

  private static Logger log = Logger.getLogger(AccountDAOImplCollections.class);
  /**
   * Get all accounts.
   */
  public List<Account> getAllAccounts() throws CustomException {
    List<Account> allAccounts = new ArrayList<>();
    return allAccounts;
  }

  /**
   * Get account by id
   */
  public Account getAccountById(long accountId) throws CustomException {
    Account acc = new Account();
    return acc;
  }

  public Account getAccountByUser(String user, String currency) throws CustomException {
    Account acc = new Account();
    return acc;
  }

  /**
   * Create account
   */
  public long createAccount(Account account) throws CustomException {
    return 1;
  }

  /**
   * Delete account by id
   */
  public int deleteAccountById(long accountId) throws CustomException {
	  return 1;
  }

  /**
   * Update account balance
   */
  public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
    return 1;
  }

  /**
   * Transfer balance between two accounts.
   */
  public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
    return 1;
  }

}