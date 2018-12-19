package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.AccountDAOImpl;
import com.moneytransfer.dao.impl.UserDAOImpl;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;
import com.moneytransfer.utils.Utils;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * H2 DAO
 */
public class H2DAOFactory extends DAOFactory {
    private static final String h2_driver = Utils.getStringProperty("h2_driver");
    private static final String h2_connection_url = Utils.getStringProperty("h2_connection_url");
    private static final String h2_user = Utils.getStringProperty("h2_user");
    private static final String h2_password = Utils.getStringProperty("h2_password");
    private static Logger log = Logger.getLogger(H2DAOFactory.class);

    //	private static final UserDAOImpl userDAO = new UserDAOImpl();
//	private static final AccountDAOImpl accountDAO = new AccountDAOImpl();
    private static final UserDAOImpl userDAO = UserDAOImpl.getInstance();
    private static final AccountDAOImpl accountDAO = AccountDAOImpl.getInstance();

    static {
        // init: load driver
        DbUtils.loadDriver(h2_driver);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);
    }

    public UserDAO getUserDAO() {
        return userDAO;
//		DbUtils.loadDriver(h2_driver);
//		return new UserDAOImpl();
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
//		DbUtils.loadDriver(h2_driver);
//		return new AccountDAOImpl();
    }

    @Override
    public void populateTestData() throws CustomException {
        userDAO.insertUser(new User(1, "yangluo", "yangluo@gmail.com"));
        userDAO.insertUser(new User(2, "qinfran", "qinfran@gmail.com"));
        userDAO.insertUser(new User(3, "yangluo", "yangluo@gmail.com"));
        userDAO.insertUser(new User(4, "qinfran", "qinfran@gmail.com"));
        userDAO.insertUser(new User(5, "liusisi", "liusisi@gmail.com"));

        accountDAO.createAccount(new Account("yangluo", new BigDecimal(100.0000), "USD"));
        accountDAO.createAccount(new Account("qinfran", new BigDecimal(200.0000), "USD"));
        accountDAO.createAccount(new Account("yangluo", new BigDecimal(500.0000), "EUR"));
        accountDAO.createAccount(new Account("qinfran", new BigDecimal(500.0000), "EUR"));
        accountDAO.createAccount(new Account("yangluo", new BigDecimal(500.0000), "GBP"));
        accountDAO.createAccount(new Account("qinfran", new BigDecimal(500.0000), "GBP"));

//		log.info("Populating Test User Table and data ..... ");
//		Connection conn = null;
//		try {
//			conn = H2DAOFactory.getConnection();
//			RunScript.execute(conn, new FileReader("src/test/resources/demo.sql"));
//		} catch (SQLException e) {
//			log.error("populateTestData(): Error populating user data: ", e);
//			throw new RuntimeException(e);
//		} catch (FileNotFoundException e) {
//			log.error("populateTestData(): Error finding test script file ", e);
//			throw new RuntimeException(e);
//		} finally {
//			DbUtils.closeQuietly(conn);
//		}


    }


}
