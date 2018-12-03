package com.moneytransfer.dao;

import com.moneytransfer.dao.impl.AccountDAOImplCollections;
import com.moneytransfer.dao.impl.UserDAOImplCollections;

import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Collections DAO
 */
public class CollectionsDAOFactory extends DAOFactory {
	private static Logger log = Logger.getLogger(CollectionsDAOFactory.class);

	private static final UserDAOImplCollections userDAO = new UserDAOImplCollections();
	private static final AccountDAOImplCollections accountDAO = new AccountDAOImplCollections();

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	@Override
	public void populateTestData() throws CustomException {
		
		userDAO.dropCollections();
		accountDAO.dropCollections();
		
		log.info("Populating Test User collection and data ..... ");
		
		List<User> testUsers = new ArrayList<User>();
		testUsers.add(new User(1L, "test2", "test2@gmail.com"));
		testUsers.add(new User(2L, "test1", "test1@gmail.com"));
		testUsers.add(new User(3L, "yangluo", "yangluo@gmail.com"));
		testUsers.add(new User(4L, "qinfran", "qinfran@gmail.com"));
		testUsers.add(new User(5L, "liusisi", "liusisi@gmail.com"));
		
		for(User user : testUsers) {
			userDAO.insertUser(user);
		}
		
		List<Account> testAccounts = new ArrayList<Account>();
		testAccounts.add(new Account("yangluo", new BigDecimal(100), "USD"));
		testAccounts.add(new Account("qinfran", new BigDecimal(200), "USD"));
		testAccounts.add(new Account("yangluo", new BigDecimal(500), "EUR"));
		testAccounts.add(new Account("qinfran", new BigDecimal(500), "EUR"));
		testAccounts.add(new Account("yangluo", new BigDecimal(500), "GBP"));
		testAccounts.add(new Account("qinfran", new BigDecimal(500), "GBP"));
		
		for(Account account : testAccounts) {
			accountDAO.createAccount(account);
		}
		
	}
}
