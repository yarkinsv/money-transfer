package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface AccountDAO {

  Set<Account> getAllAccounts() throws CustomException;

  Account getAccountById(long accountId) throws CustomException;

  Account getAccountByUser(String user, String currency) throws CustomException;

  long createAccount(Account account) throws CustomException;

  int deleteAccountById(long accountId) throws CustomException;

  int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException;

  int transferAccountBalance(UserTransaction userTransaction) throws CustomException;
}
