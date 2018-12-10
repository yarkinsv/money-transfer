package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class AccountHMDAOImpl implements AccountDAO {

  private static Logger log = Logger.getLogger(AccountHMDAOImpl.class);

  private static final ConcurrentHashMap <Long, Account> accountsById   = new ConcurrentHashMap<>(500);
  private static final ConcurrentHashMap <userKey, Long> accountsByUser = new ConcurrentHashMap<>(500);
  private static final AtomicLong accountNewId = new AtomicLong(1);

  /**
   * Get all accounts.
   */
  public Set<Account> getAllAccounts() throws CustomException {
    Set<Account> allAccounts = new HashSet<>(accountsById.values());
    for (Account acc : allAccounts) {
      if (log.isDebugEnabled()) {
        log.debug("getAllAccounts(): Get  Account " + acc);
      }
    }
    return allAccounts;
  }

  /**
   * Get account by id
   */
  public Account getAccountById(long accountId) throws CustomException {
    Account acc;
    if (accountsById.containsKey(accountId))
      acc = accountsById.get(accountId);
    else
      return null;
/*    if (acc==null)
    {
      if (log.isDebugEnabled()) {
        log.error("getAccountById(): Account not find.");
      }
      throw new CustomException("Account not find");
    }*/
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account By Id: " + acc);
    }
    return acc;
  }

  public Account getAccountByUser(String user, String currency) throws CustomException {
    long accountId;
    if (accountsByUser.containsKey(new userKey(user,currency)))
      accountId = accountsByUser.get(new userKey(user,currency));
    else
      return null;
/*    if (accountId == 0) {
      if (log.isDebugEnabled()) {
        log.error("getAccountByUser(): Account ID not find.");
      }
      throw new CustomException("Account ID not find");
    }*/
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account ID By User: " + accountId);
    }
    Account acc=null;
    if (accountsById.containsKey(accountId))
      acc = accountsById.get(accountId);
    else
      return null;
/*    if (acc==null)
    {
      if (log.isDebugEnabled()) {
        log.error("getAccountByUser(): Account not find.");
      }
      throw new CustomException("Account not find");
    }*/
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account By User: " + acc);
    }
    return acc;
  }

  /**
   * Create account
   */
  public long createAccount(Account account) throws CustomException {
    if (accountsByUser.containsKey(new userKey(account.getUserName(),account.getCurrencyCode())))
    {
      log.error("createAccount(): Creating account failed, no rows affected.");
      throw new CustomException("Account Cannot be created");
    }
    Account acc = new Account(accountNewId.getAndIncrement(),account.getUserName(),account.getBalance(),account.getCurrencyCode());
    accountsById.put(acc.getAccountId(),acc);
    accountsByUser.put(new userKey(acc.getUserName(),acc.getCurrencyCode()),acc.getAccountId());
    return acc.getAccountId();
  }

  /**
   * Delete account by id
   */
  public int deleteAccountById(long accountId) throws CustomException {
    Account acc = accountsById.remove(accountId);
    if (acc==null)
    {
      log.error("deleteAccountById(): Error deleting user account Id " + accountId);
      throw new CustomException("Account Cannot be deleted");
    }
    long delAcc = accountsByUser.remove(new userKey(acc.getUserName(),acc.getCurrencyCode()));
    if (delAcc==0 || delAcc!=accountId)
    {
      log.error("deleteAccountById(): Error deleting user account Id " + accountId);
      throw new CustomException("Account Cannot be deleted");
    }
    return 1;
  }

  /**
   * Update account balance
   */
  public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
    Account targetAccount=null;
    if (accountsById.containsKey(accountId))
      targetAccount = accountsById.get(accountId);
    if (targetAccount==null)
    {
      if (log.isDebugEnabled()) {
        log.error("updateAccountBalance(): Account not find.");
      }
      throw new CustomException("Account not find");
    }
    targetAccount.getBalance().add(deltaAmount);
    if (targetAccount.getBalance().compareTo(MoneyUtil.zeroAmount) < 0) {
      throw new CustomException("Not sufficient Fund for account: " + accountId);
    }
    if (accountsById.replace(targetAccount.getAccountId(),targetAccount)==null && log.isDebugEnabled())
    {
      log.error("updateAccountBalance(): User Transaction Failed for: " + accountId);
      throw new CustomException("User transaction Failed");
    }
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
    Account fromAccount = null;
    Account toAccount = null;
    if (accountsById.containsKey(userTransaction.getFromAccountId()))
      fromAccount=accountsById.get(userTransaction.getFromAccountId());
    if (accountsById.containsKey(userTransaction.getToAccountId()))
      toAccount=accountsById.get(userTransaction.getToAccountId());

    // check locking status
    if (fromAccount == null || toAccount == null) {
      if (log.isDebugEnabled()) {
        log.error("transferAccountBalance(): Account not find.");
      }
      throw new CustomException("Fail to find both accounts");
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
      fromAccount.getBalance().subtract(userTransaction.getAmount());
      if (fromAccount.getBalance().compareTo(MoneyUtil.zeroAmount) < 0) {
        throw new CustomException("Not enough Fund from source Account ");
      }

    if (log.isDebugEnabled()) {
      log.debug("transferAccountBalance from Account: " + fromAccount);
      log.debug("transferAccountBalance to Account: " + toAccount);
    }

    if (accountsById.replace(fromAccount.getAccountId(), fromAccount) != null) {
      result++;
    }
    if (accountsById.replace(toAccount.getAccountId(), toAccount) != null) {
      result++;
    }
    return result;
  }

  private class userKey
  {
    final String user;
    final String currency;

    userKey(String user, String currency)
    {
      this.user=user;
      this.currency=currency;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      userKey key = (userKey) o;
      return Objects.equals(user, key.user) && Objects.equals(currency, key.currency);
    }

    @Override
    public int hashCode() {
      return 31*(user==null?0:user.hashCode())+(currency==null?0:currency.hashCode());
    }
  }
}
