package com.taskforce.moneyapp.dao;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
public abstract class TestUserDAO {
	
	private static Logger log = Logger.getLogger(TestUserDAO.class);
	
	private static DAOFactory daoFactory;

	public static void setup() throws CustomException {
		daoFactory = DAOFactory.getDAOFactory();
		// prepare test database and test data by executing sql script demo.sql
		log.debug("setting up test database and sample data....");
		daoFactory.populateTestData();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetAllUsers() throws CustomException {
		List<User> allUsers = daoFactory.getUserDAO().getAllUsers();
		assertTrue(allUsers.size() > 1);
	}

	@Test
	public void testGetUserById() throws CustomException {
		User u = daoFactory.getUserDAO().getUserById(2L);
		assertTrue(u.getUserName().equals("test1"));
	}

	@Test
	public void testGetNonExistingUserById() throws CustomException {
		User u = daoFactory.getUserDAO().getUserById(500L);
		assertTrue(u == null);
	}

	@Test
	public void testGetNonExistingUserByName() throws CustomException {
		User u = daoFactory.getUserDAO().getUserByName("abcdeftg");
		assertTrue(u == null);
	}

	@Test
	public void testCreateUser() throws CustomException {
		User u = new User("liandre", "liandre@gmail.com");
		long id = daoFactory.getUserDAO().insertUser(u);
		User uAfterInsert = daoFactory.getUserDAO().getUserById(id);
		assertTrue(uAfterInsert.getUserName().equals("liandre"));
		assertTrue(u.getEmailAddress().equals("liandre@gmail.com"));
	}

	@Test
	public void testUpdateUser() throws CustomException {
		User u = new User(1L, "test2", "test2@gmail.com");
		int rowCount = daoFactory.getUserDAO().updateUser(1L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 1);
		assertTrue(daoFactory.getUserDAO().getUserById(1L).getEmailAddress().equals("test2@gmail.com"));
	}

	@Test
	public void testUpdateNonExistingUser() throws CustomException {
		User u = new User(500L, "test2", "test2@gmail.com");
		int rowCount = daoFactory.getUserDAO().updateUser(500L, u);
		// assert one row(user) updated
		assertTrue(rowCount == 0);
	}

	@Test
	public void testDeleteUser() throws CustomException {
		int rowCount = daoFactory.getUserDAO().deleteUser(1L);
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(daoFactory.getUserDAO().getUserById(1L) == null);
	}

	@Test
	public void testDeleteNonExistingUser() throws CustomException {
		int rowCount = daoFactory.getUserDAO().deleteUser(500L);
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

}
