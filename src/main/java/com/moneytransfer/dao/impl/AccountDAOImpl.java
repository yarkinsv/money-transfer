package com.moneytransfer.dao.impl;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.dao.H2DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.MoneyUtil;
import com.moneytransfer.model.UserTransaction;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

//Added by Isc
import java.util.HashMap;
import java.util.Map;
//---------------------

public class AccountDAOImpl implements AccountDAO {

  private static Logger log = Logger.getLogger(AccountDAOImpl.class);
  private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
  private final static String SQL_GET_ACC_BY_USER_ID = "SELECT * FROM Account WHERE UserName = ? AND CurrencyCode = ?";
  private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
  private final static String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
  private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";
  private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
  private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM Account WHERE AccountId = ?";

//Added by Isc
  private static Map<Long, Account> AccountId_Map = new HashMap<>();
  private static Map<String, Account> AccountUserName_Map = new HashMap<>();  
//  
  
  /**
   * Get all accounts.
   */
  public Set<Account> getAllAccounts() throws CustomException {
    Set<Account> allAccounts = new HashSet<Account>(AccountId_Map.values());
    return allAccounts;
    
/*  Commented by Isc	  
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
  
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
      rs = stmt.executeQuery();
      while (rs.next()) {
        Account acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
        if (log.isDebugEnabled()) {
          log.debug("getAllAccounts(): Get  Account " + acc);
        }
        allAccounts.add(acc);
      }
      return allAccounts;
    } catch (SQLException e) {
      throw new CustomException("getAccountById(): Error reading account data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
*/    
  }

  
  /* Added by Isc
   * get test accounts from DB
   */
  public long getTestAccountsFromDB() throws CustomException {
	  	long counter=0;
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    try {
	      conn = H2DAOFactory.getConnection();
	      stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
	      rs = stmt.executeQuery();
	      while (rs.next()) {
	        Account acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
	            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
	        if (log.isDebugEnabled()) {
	          log.debug("getAllAccounts(): Get  Account " + acc);
	        }
	        //Add test accounts
	        log.info("getTestAccountsFromDB(): Adding test Account " + acc);
	        createAccount(acc);
	        counter=counter+1;
	      }
	      return counter;
	    } catch (SQLException e) {
	      throw new CustomException("getAccountById(): Error reading account data", e);
	    } finally {
	      DbUtils.closeQuietly(conn, stmt, rs);
	    }
	  }
  
  /**
   * Get account by id
   */
  public Account getAccountById(long accountId) throws CustomException {
	Account acc = AccountId_Map.get(accountId);
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account By Id: " + acc);
    }
    return acc;
/*    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Account acc = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
      stmt.setLong(1, accountId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
            rs.getString("CurrencyCode"));
        if (log.isDebugEnabled()) {
          log.debug("Retrieve Account By Id: " + acc);
        }
      }
      return acc;
    } catch (SQLException e) {
      throw new CustomException("getAccountById(): Error reading account data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
*/    
  }

  public Account getAccountByUser(String user, String currency) throws CustomException {
	Account acc = AccountUserName_Map.get(user+currency);
    if (log.isDebugEnabled()) {
      log.debug("Retrieve Account By userId: " + acc);
    }
    return acc;
/* Commented by Isc	finally  
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Account acc = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_GET_ACC_BY_USER_ID);
      stmt.setString(1, user);
      stmt.setString(2, currency);
      rs = stmt.executeQuery();
      if (rs.next()) {
        acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
            rs.getString("CurrencyCode"));
        if (log.isDebugEnabled()) {
          log.debug("Retrieve Account By userId: " + acc);
        }
      }
      return acc;
/* Commented by Isc      
      return getAllAccounts()
          .stream()
          .filter(account -> account.getUserName().equals(user) && account.getCurrencyCode().equals(currency))
          .findFirst()
          .orElse(null);
*/
/* Commented by Isc	finally
    } catch (SQLException e) {
      throw new CustomException("getAccountById(): Error reading account data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
*/
  }

  /**
   * Create account
   */
  public long createAccount(Account account) throws CustomException {
	String UserName = account.getUserName();
	String CurrencyCode = account.getCurrencyCode();
	if (getAccountByUser(UserName, CurrencyCode) != null) {
		log.error("Error Inserting Account  " + account);
		throw new CustomException("Account already exists" + account, null);
	}
    long num = AccountId_Map.size()+1;
    Account acc = new Account(num, UserName, account.getBalance(), CurrencyCode);
	AccountId_Map.put(num, acc);
	AccountUserName_Map.put(UserName+CurrencyCode, acc);
	
	return num;  
/*	Commented by Isc  
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_CREATE_ACC);
      stmt.setString(1, account.getUserName());
      stmt.setBigDecimal(2, account.getBalance());
      stmt.setString(3, account.getCurrencyCode());
      int affectedRows = stmt.executeUpdate();
      if (affectedRows == 0) {
        log.error("createAccount(): Creating account failed, no rows affected.");
        throw new CustomException("Account Cannot be created");
      }
      generatedKeys = stmt.getGeneratedKeys();
      if (generatedKeys.next()) {
        return generatedKeys.getLong(1);
      } else {
        log.error("Creating account failed, no ID obtained.");
        throw new CustomException("Account Cannot be created");
      }
    } catch (SQLException e) {
      log.error("Error Inserting Account  " + account);
      throw new CustomException("createAccount(): Error creating user account " + account, e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, generatedKeys);
    }
*/    
  }

  /**
   * Delete account by id
   */
  public int deleteAccountById(long accountId) throws CustomException {
	Account acc = getAccountById(accountId);
	if (acc == null) {
        if (log.isDebugEnabled()) {
            log.debug("No account found with userId: " + accountId);
        }
		
		return 0;	//no
	}
	
	AccountId_Map.remove(accountId);
	AccountUserName_Map.remove(acc.getUserName()+acc.getCurrencyCode());

	return 1;
/* Commented by Isc 	  
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
      stmt.setLong(1, accountId);
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new CustomException("deleteAccountById(): Error deleting user account Id " + accountId, e);
    } finally {
      DbUtils.closeQuietly(conn);
      DbUtils.closeQuietly(stmt);
    }
*/    
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
/*	  
    Connection conn = null;
    PreparedStatement lockStmt = null;
    PreparedStatement updateStmt = null;
    ResultSet rs = null;
    Account targetAccount = null;
    int updateCount = -1;
    try {
      conn = H2DAOFactory.getConnection();
      conn.setAutoCommit(false);
      // lock account for writing:
      lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
      lockStmt.setLong(1, accountId);
      rs = lockStmt.executeQuery();
      if (rs.next()) {
        targetAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
        if (log.isDebugEnabled()) {
          log.debug("updateAccountBalance from Account: " + targetAccount);
        }
      }

      if (targetAccount == null) {
        throw new CustomException("updateAccountBalance(): fail to lock account : " + accountId);
      }
      // update account upon success locking
      BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
      if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
        throw new CustomException("Not sufficient Fund for account: " + accountId);
      }

      updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
      updateStmt.setBigDecimal(1, balance);
      updateStmt.setLong(2, accountId);
      updateCount = updateStmt.executeUpdate();
      conn.commit();
      if (log.isDebugEnabled()) {
        log.debug("New Balance after Update: " + targetAccount);
      }
      return updateCount;
    } catch (SQLException se) {
      // rollback transaction if exception occurs
      log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
      try {
        if (conn != null)
          conn.rollback();
      } catch (SQLException re) {
        throw new CustomException("Fail to rollback transaction", re);
      }
    } finally {
      DbUtils.closeQuietly(conn);
      DbUtils.closeQuietly(rs);
      DbUtils.closeQuietly(lockStmt);
      DbUtils.closeQuietly(updateStmt);
    }
    return updateCount;
*/    
  }

  /**
   * Transfer balance between two accounts.
   */
  public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
	int result = -1;
	Account fromAccount = getAccountById(userTransaction.getFromAccountId());
	if (fromAccount == null) {
	    throw new CustomException("Fail to lock fromAccount for write");
	}
	if (log.isDebugEnabled()) {
	    log.debug("transferAccountBalance from Account: " + fromAccount);
	}
	
	Account toAccount = getAccountById(userTransaction.getToAccountId());
	if (toAccount == null) {
	    throw new CustomException("Fail to lock toAccount for write");
	}
	if (log.isDebugEnabled()) {
	    log.debug("transferAccountBalance to Account: " + toAccount);
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
	BigDecimal FromAccountBalance_saved = fromAccount.getBalance();
	BigDecimal ToAccountBalance_saved = toAccount.getBalance();
	try {
		BigDecimal FromAccountBalance_new = fromAccountLeftOver;
		BigDecimal ToAccountBalance_new = toAccount.getBalance().add(userTransaction.getAmount());
		fromAccount.setBalance(FromAccountBalance_new);
	    toAccount.setBalance(ToAccountBalance_new);
	    if (log.isDebugEnabled()) {
	        log.debug("Number of rows updated for the transfer : " + result);
	    }
	    
	    result = 2;	    
	} catch (Exception e) {
	    log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction, e);
	    try {
	        fromAccount.setBalance(FromAccountBalance_saved);
	        toAccount.setBalance(ToAccountBalance_saved);
	    } catch (Exception re) {
	        throw new CustomException("Fail to rollback transaction", re);
	    }
	}

	return result;
    
/* Commented by Isc 	  
	  int result = -1;
    Connection conn = null;
    PreparedStatement lockStmt = null;
    PreparedStatement updateStmt = null;
    ResultSet rs = null;
    Account fromAccount = null;
    Account toAccount = null;

    try {
      conn = H2DAOFactory.getConnection();
      conn.setAutoCommit(false);
      // lock the credit and debit account for writing:
      lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
      lockStmt.setLong(1, userTransaction.getFromAccountId());
      rs = lockStmt.executeQuery();
      if (rs.next()) {
        fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
        if (log.isDebugEnabled()) {
          log.debug("transferAccountBalance from Account: " + fromAccount);
        }
      }
      lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
      lockStmt.setLong(1, userTransaction.getToAccountId());
      rs = lockStmt.executeQuery();
      if (rs.next()) {
        toAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
            rs.getString("CurrencyCode"));
        if (log.isDebugEnabled()) {
          log.debug("transferAccountBalance to Account: " + toAccount);
        }
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
      updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
      updateStmt.setBigDecimal(1, fromAccountLeftOver);
      updateStmt.setLong(2, userTransaction.getFromAccountId());
      updateStmt.addBatch();
      updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
      updateStmt.setLong(2, userTransaction.getToAccountId());
      updateStmt.addBatch();
      int[] rowsUpdated = updateStmt.executeBatch();
      result = rowsUpdated[0] + rowsUpdated[1];
      if (log.isDebugEnabled()) {
        log.debug("Number of rows updated for the transfer : " + result);
      }
      // If there is no error, commit the transaction
      conn.commit();
    } catch (SQLException se) {
      // rollback transaction if exception occurs
      log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction,
          se);
      try {
        if (conn != null)
          conn.rollback();
      } catch (SQLException re) {
        throw new CustomException("Fail to rollback transaction", re);
      }
    } finally {
      DbUtils.closeQuietly(conn);
      DbUtils.closeQuietly(rs);
      DbUtils.closeQuietly(lockStmt);
      DbUtils.closeQuietly(updateStmt);
    }
    return result;
  }
*/  
}
  
}