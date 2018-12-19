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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDAOImpl implements AccountDAO {

    private static AccountDAOImpl instance;
    private static Map<Long, Account> accountMap = new HashMap<>();
    private static long nextId = 0;

//    private static Logger log = Logger.getLogger(AccountDAOImpl.class);
//    private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
//    private final static String SQL_GET_ACC_BY_USER_ID = "SELECT * FROM Account WHERE UserName = ? AND CurrencyCode = ?";
//    private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
//    private final static String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
//    private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";
//    private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
//    private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM Account WHERE AccountId = ?";


    public static AccountDAOImpl getInstance() {
        if (instance == null) {
            instance = new AccountDAOImpl();
        }
        return instance;
    }

    /**
     * Get all accounts.
     */
    public Set<Account> getAllAccounts() throws CustomException {
        return accountMap.values().stream().collect(Collectors.toSet());
//    Connection conn = null;
//    PreparedStatement stmt = null;
//    ResultSet rs = null;
//    Set<Account> allAccounts = new HashSet<>();
//    try {
//      conn = H2DAOFactory.getConnection();
//      stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
//      rs = stmt.executeQuery();
//      while (rs.next()) {
//        Account acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
//            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
//        if (log.isDebugEnabled()) {
//          log.debug("getAllAccounts(): Get  Account " + acc);
//        }
//        allAccounts.add(acc);
//      }
//      return allAccounts;
//    } catch (SQLException e) {
//      throw new CustomException("getAccountById(): Error reading account data", e);
//    } finally {
//      DbUtils.closeQuietly(conn, stmt, rs);
//    }
    }

    /**
     * Get account by id
     */
    public Account getAccountById(long accountId) throws CustomException {
        return accountMap.get(accountId);
//    Connection conn = null;
//    PreparedStatement stmt = null;
//    ResultSet rs = null;
//    Account acc = null;
//    try {
//      conn = H2DAOFactory.getConnection();
//      stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
//      stmt.setLong(1, accountId);
//      rs = stmt.executeQuery();
//      if (rs.next()) {
//        acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
//            rs.getString("CurrencyCode"));
//        if (log.isDebugEnabled()) {
//          log.debug("Retrieve Account By Id: " + acc);
//        }
//      }
//      return acc;
//    } catch (SQLException e) {
//      throw new CustomException("getAccountById(): Error reading account data", e);
//    } finally {
//      DbUtils.closeQuietly(conn, stmt, rs);
//    }
    }

    public Account getAccountByUser(String user, String currency) throws CustomException {
        Account acc = null;
        for (Account account : accountMap.values()) {
            if ((account.getUserName().equals(user)) || (account.getCurrencyCode().equals(currency))) {
                acc = account;
                break;
            }
        }
        return acc;
//    Connection conn = null;
//    PreparedStatement stmt = null;
//    ResultSet rs = null;
//    Account acc = null;
//    try {
//      conn = H2DAOFactory.getConnection();
//      stmt = conn.prepareStatement(SQL_GET_ACC_BY_USER_ID);
//      stmt.setString(1, user);
//      stmt.setString(2, currency);
//      rs = stmt.executeQuery();
//      if (rs.next()) {
//        acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
//            rs.getString("CurrencyCode"));
//        if (log.isDebugEnabled()) {
//          log.debug("Retrieve Account By userId: " + acc);
//        }
//      }
//      return acc;
//    } catch (SQLException e) {
//      throw new CustomException("getAccountById(): Error reading account data", e);
//    } finally {
//      DbUtils.closeQuietly(conn, stmt, rs);
//    }
    }

    /**
     * Create account
     */
    public long createAccount(Account account) throws CustomException {
        long id = nextId;
        Account acc = new Account(nextId, account.getUserName(), account.getBalance(), account.getCurrencyCode());
        accountMap.put(id, acc);
        nextId++;
        return id;
//    Connection conn = null;
//    PreparedStatement stmt = null;
//    ResultSet generatedKeys = null;
//    try {
//      conn = H2DAOFactory.getConnection();
//      stmt = conn.prepareStatement(SQL_CREATE_ACC);
//      stmt.setString(1, account.getUserName());
//      stmt.setBigDecimal(2, account.getBalance());
//      stmt.setString(3, account.getCurrencyCode());
//      int affectedRows = stmt.executeUpdate();
//      if (affectedRows == 0) {
//        log.error("createAccount(): Creating account failed, no rows affected.");
//        throw new CustomException("Account Cannot be created");
//      }
//      generatedKeys = stmt.getGeneratedKeys();
//      if (generatedKeys.next()) {
//        return generatedKeys.getLong(1);
//      } else {
//        log.error("Creating account failed, no ID obtained.");
//        throw new CustomException("Account Cannot be created");
//      }
//    } catch (SQLException e) {
//      log.error("Error Inserting Account  " + account);
//      throw new CustomException("createAccount(): Error creating user account " + account, e);
//    } finally {
//      DbUtils.closeQuietly(conn, stmt, generatedKeys);
//    }
    }

    /**
     * Delete account by id
     */
    public int deleteAccountById(long accountId) throws CustomException {
        if (accountMap.containsKey(accountId)) {
            accountMap.remove(accountId);
            return 1;
        }
        return 0;
//    Connection conn = null;
//    PreparedStatement stmt = null;
//    try {
//      conn = H2DAOFactory.getConnection();
//      stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
//      stmt.setLong(1, accountId);
//      return stmt.executeUpdate();
//    } catch (SQLException e) {
//      throw new CustomException("deleteAccountById(): Error deleting user account Id " + accountId, e);
//    } finally {
//      DbUtils.closeQuietly(conn);
//      DbUtils.closeQuietly(stmt);
//    }
    }

    /**
     * Update account balance
     */
    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws CustomException {
        Account targetAccount = getAccountById(accountId);
        if (targetAccount == null) {
            throw new CustomException("updateAccountBalance(): fail to lock account : " + accountId);
        }
        BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
        if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not sufficient Fund for account: " + accountId);
        }
        Account acc = new Account(targetAccount.getAccountId(), targetAccount.getUserName(), balance, targetAccount.getCurrencyCode());
        accountMap.put(targetAccount.getAccountId(), acc);

        return 0;
//    Connection conn = null;
//    PreparedStatement lockStmt = null;
//    PreparedStatement updateStmt = null;
//    ResultSet rs = null;
//    Account targetAccount = null;
//    int updateCount = -1;
//    try {
//      conn = H2DAOFactory.getConnection();
//      conn.setAutoCommit(false);
//      // lock account for writing:
//      lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
//      lockStmt.setLong(1, accountId);
//      rs = lockStmt.executeQuery();
//      if (rs.next()) {
//        targetAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
//            rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
//        if (log.isDebugEnabled()) {
//          log.debug("updateAccountBalance from Account: " + targetAccount);
//        }
//      }
//
//      if (targetAccount == null) {
//        throw new CustomException("updateAccountBalance(): fail to lock account : " + accountId);
//      }
//      // update account upon success locking
//      BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
//      if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
//        throw new CustomException("Not sufficient Fund for account: " + accountId);
//      }
//
//      updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
//      updateStmt.setBigDecimal(1, balance);
//      updateStmt.setLong(2, accountId);
//      updateCount = updateStmt.executeUpdate();
//      conn.commit();
//      if (log.isDebugEnabled()) {
//        log.debug("New Balance after Update: " + targetAccount);
//      }
//      return updateCount;
//    } catch (SQLException se) {
//      // rollback transaction if exception occurs
//      log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
//      try {
//        if (conn != null)
//          conn.rollback();
//      } catch (SQLException re) {
//        throw new CustomException("Fail to rollback transaction", re);
//      }
//    } finally {
//      DbUtils.closeQuietly(conn);
//      DbUtils.closeQuietly(rs);
//      DbUtils.closeQuietly(lockStmt);
//      DbUtils.closeQuietly(updateStmt);
//    }
//    return updateCount;
    }

    /**
     * Transfer balance between two accounts.
     */
    public int transferAccountBalance(UserTransaction userTransaction) throws CustomException {
        Account fromAccount = accountMap.get(userTransaction.getFromAccountId());
        Account toAccount = accountMap.get(userTransaction.getToAccountId());

        //check locking status
        if (fromAccount == null || toAccount == null) {
            throw new CustomException("Fail to lock both accounts for write");
        }

        // check transaction currency
        if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
            throw new CustomException(
                    "Fail to transfer Fund, transaction ccy are different from source/destination");
        }

        // check equality cur code for both accounts
        if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
            throw new CustomException(
                    "Fail to transfer Fund, the source and destination account are in different currency");
        }
        BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
        if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
            throw new CustomException("Not enough Fund from source Account ");
        }
        BigDecimal toAccountLeftOver = toAccount.getBalance().add(userTransaction.getAmount());
        fromAccount = new Account(fromAccount.getAccountId(), fromAccount.getUserName(), fromAccountLeftOver, fromAccount.getCurrencyCode());
        toAccount = new Account(toAccount.getAccountId(), toAccount.getUserName(), toAccountLeftOver, toAccount.getCurrencyCode());
        accountMap.put(fromAccount.getAccountId(), fromAccount);
        accountMap.put(toAccount.getAccountId(), toAccount);

        return 0;
//        int result = -1;
//        Connection conn = null;
//        PreparedStatement lockStmt = null;
//        PreparedStatement updateStmt = null;
//        ResultSet rs = null;
//        Account fromAccount = null;
//        Account toAccount = null;
//
//        try {
//            conn = H2DAOFactory.getConnection();
//            conn.setAutoCommit(false);
//            // lock the credit and debit account for writing:
//            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
//            lockStmt.setLong(1, userTransaction.getFromAccountId());
//            rs = lockStmt.executeQuery();
//            if (rs.next()) {
//                fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"),
//                        rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
//                if (log.isDebugEnabled()) {
//                    log.debug("transferAccountBalance from Account: " + fromAccount);
//                }
//            }
//            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
//            lockStmt.setLong(1, userTransaction.getToAccountId());
//            rs = lockStmt.executeQuery();
//            if (rs.next()) {
//                toAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"),
//                        rs.getString("CurrencyCode"));
//                if (log.isDebugEnabled()) {
//                    log.debug("transferAccountBalance to Account: " + toAccount);
//                }
//            }
//
//            // check locking status
//            if (fromAccount == null || toAccount == null) {
//                throw new CustomException("Fail to lock both accounts for write");
//            }
//
//            // check transaction currency
//            if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
//                throw new CustomException(
//                        "Fail to transfer Fund, transaction ccy are different from source/destination");
//            }
//
//            // check ccy is the same for both accounts
//            if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
//                throw new CustomException(
//                        "Fail to transfer Fund, the source and destination account are in different currency");
//            }
//
//            // check enough fund in source account
//            BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
//            if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
//                throw new CustomException("Not enough Fund from source Account ");
//            }
//            // proceed with update
//            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
//            updateStmt.setBigDecimal(1, fromAccountLeftOver);
//            updateStmt.setLong(2, userTransaction.getFromAccountId());
//            updateStmt.addBatch();
//            updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
//            updateStmt.setLong(2, userTransaction.getToAccountId());
//            updateStmt.addBatch();
//            int[] rowsUpdated = updateStmt.executeBatch();
//            result = rowsUpdated[0] + rowsUpdated[1];
//            if (log.isDebugEnabled()) {
//                log.debug("Number of rows updated for the transfer : " + result);
//            }
//            // If there is no error, commit the transaction
//            conn.commit();
//        } catch (SQLException se) {
//            // rollback transaction if exception occurs
//            log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction,
//                    se);
//            try {
//                if (conn != null)
//                    conn.rollback();
//            } catch (SQLException re) {
//                throw new CustomException("Fail to rollback transaction", re);
//            }
//        } finally {
//            DbUtils.closeQuietly(conn);
//            DbUtils.closeQuietly(rs);
//            DbUtils.closeQuietly(lockStmt);
//            DbUtils.closeQuietly(updateStmt);
//        }
//        return result;
    }
}
