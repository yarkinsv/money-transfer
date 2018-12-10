package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.AccountHMDAOImpl;
import com.moneytransfer.dao.impl.UserHMDAOImpl;
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
public class HMDAOFactory extends DAOFactory {

	private static Logger log = Logger.getLogger(H2DAOFactory.class);

	private static final UserHMDAOImpl userDAO = new UserHMDAOImpl();
	private static final AccountHMDAOImpl accountDAO = new AccountHMDAOImpl();

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	@Override
	public void populateTestData() throws CustomException {
		log.info("Populating Test User Table and data ..... ");
		Boolean f=true;
		if (userDAO.insertUser(new User("test2","test2@gmail.com"))==0) f=false;
		if (userDAO.insertUser(new User("test1","test1@gmail.com"))==0) f=false;
		if (userDAO.insertUser(new User("yangluo","yangluo@gmail.com"))==0) f=false;
		if (userDAO.insertUser(new User("qinfran","qinfran@gmail.com"))==0) f=false;
		if (userDAO.insertUser(new User("liusisi","liusisi@gmail.com"))==0) f=false;
		if (accountDAO.createAccount(new Account("yangluo",new BigDecimal(100.0000),"USD"))==0) f=false;
		if (accountDAO.createAccount(new Account("qinfran",new BigDecimal(200.0000),"USD"))==0) f=false;
		if (accountDAO.createAccount(new Account("yangluo",new BigDecimal(500.0000),"EUR"))==0) f=false;
		if (accountDAO.createAccount(new Account("qinfran",new BigDecimal(500.0000),"EUR"))==0) f=false;
		if (accountDAO.createAccount(new Account("yangluo",new BigDecimal(500.0000),"GBP"))==0) f=false;
		if (accountDAO.createAccount(new Account("qinfran",new BigDecimal(500.0000),"GBP"))==0) f=false;
		if (f==false){
			log.error("populateTestData(): Error populating user data");
			throw new RuntimeException();
		}
	}
}
