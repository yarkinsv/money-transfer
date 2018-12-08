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
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class AccountDAOImpl implements AccountDAO {

  private static Logger log = Logger.getLogger(AccountDAOImpl.class);
  private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
  private final static String SQL_GET_ACC_BY_USER_ID = "SELECT * FROM Account WHERE UserName = ? AND CurrencyCode = ?";
  private final static String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
  private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = Balance + ? WHERE AccountId = ?";
  private final static String SQL_UPDATE_ACC_BALANCE_WITH_CUR = "UPDATE Account SET Balance = Balance + ? WHERE AccountId = ? AND CurrencyCode = ?";
  private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
  private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM Account WHERE AccountId = ?";

  /**
   * Get all accounts.
   */
  public Set<Account> getAllAccounts() throws CustomException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Set<Account> allAccounts = new HashSet<>();
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
  }

  /**
   * Get account by id
   */
  public Account getAccountById(long accountId) throws CustomException {
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
  }

  public Account getAccountByUser(String user, String currency) throws CustomException {
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
    } catch (SQLException e) {
      throw new CustomException("getAccountById(): Error reading account data", e);
    } finally {
      DbUtils.closeQuietly(conn, stmt, rs);
    }
  }

  /**
   * Create account
   */
  public long createAccount(Account account) throws CustomException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;
    try {
      conn = H2DAOFactory.getConnection();
      stmt = conn.prepareStatement(SQL_CREATE_ACC, Statement.RETURN_GENERATED_KEYS);
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
  }

  /**
   * Delete account by id
   */
  public int deleteAccountById(long accountId) throws CustomException {
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
  }

  /**
   * Update account balance
   */
  public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
    Connection conn = null;
    PreparedStatement stmt = null;
    int updateCount = -1;
    try {
      conn = H2DAOFactory.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
      stmt.setBigDecimal(1, deltaAmount);
      stmt.setLong(2, accountId);
      updateCount = stmt.executeUpdate();
      conn.commit();
      if (log.isDebugEnabled()) {
        log.debug("New Balance after Update: " + accountId);
      }
      return updateCount;
    } catch (SQLException se) {
      if (se.toString().contains("Check constraint violation"))
        throw new CustomException("Not sufficient Fund for account: " + accountId);
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
      DbUtils.closeQuietly(stmt);
    }
    return updateCount;
  }

  /**
   * Transfer balance between two accounts.
   */
  public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
    int result = -1;
    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = H2DAOFactory.getConnection();
      conn.setAutoCommit(false);
      stmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE_WITH_CUR);
      stmt.setBigDecimal(1, MoneyUtil.zeroAmount.subtract(userTransaction.getAmount()));
      stmt.setLong(2, userTransaction.getFromAccountId());
      stmt.setString(3, userTransaction.getCurrencyCode());
      if (log.isDebugEnabled()) {
        log.debug("transferAccountBalance from Account: " + userTransaction.getFromAccountId());
      }
      stmt.addBatch();
      stmt.setBigDecimal(1, userTransaction.getAmount());
      stmt.setLong(2, userTransaction.getToAccountId());
      stmt.setString(3, userTransaction.getCurrencyCode());
      if (log.isDebugEnabled()) {
        log.debug("transferAccountBalance to Account: " + userTransaction.getToAccountId());
      }
      stmt.addBatch();
      int[] rowsUpdated = stmt.executeBatch();
      result = rowsUpdated[0] + rowsUpdated[1];
      if (result != 2) {
        throw new CustomException("Fail to transfer Fund");
      }
      if (log.isDebugEnabled()) {
        log.debug("Number of rows updated for the transfer : " + result);
      }
      // If there is no error, commit the transaction
      conn.commit();
    } catch (SQLException se) {
      // check enough fund in source account
      if (se.toString().contains("Check constraint violation"))
        throw new CustomException("Not enough Fund from source Account " +
                                  userTransaction.getToAccountId());
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
      DbUtils.closeQuietly(stmt);
    }
    return result;
  }
}
