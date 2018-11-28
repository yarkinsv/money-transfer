package com.taskforce.moneyapp.dao.collection;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.User;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;

public class TestUserDAO {

    private static Logger log = Logger.getLogger(TestUserDAO.class);

    private static final DAOFactory collectionDaoFactory = DAOFactory.getDAOFactory(DAOFactory.COLLECTION);

    @BeforeClass
    public static void setup() throws CustomException {
        // prepare test database and test data
        log.debug("setting up test database and sample data....");
        collectionDaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllUsers() throws CustomException {
        List<User> allUsers = collectionDaoFactory.getUserDAO().getAllUsers();
        assertTrue(allUsers.size() > 1);
    }

    @Test
    public void testGetUserById() throws CustomException {
        User u = collectionDaoFactory.getUserDAO().getUserById(2L);
        assertEquals("test1", u.getUserName());
    }

    @Test
    public void testGetNonExistingUserById() throws CustomException {
        User u = collectionDaoFactory.getUserDAO().getUserById(500L);
        assertNull(u);
    }

    @Test
    public void testGetNonExistingUserByName() throws CustomException {
        User u = collectionDaoFactory.getUserDAO().getUserByName("abcdeftg");
        assertNull(u);
    }

    @Test
    public void testCreateUser() throws CustomException {
        User u = new User("liandre", "liandre@gmail.com");
        long id = collectionDaoFactory.getUserDAO().insertUser(u);
        User uAfterInsert = collectionDaoFactory.getUserDAO().getUserById(id);
        assertEquals("liandre", uAfterInsert.getUserName());
        assertEquals("liandre@gmail.com", u.getEmailAddress());
    }

    @Test
    public void testUpdateUser() throws CustomException {
        User u = new User(1L, "test2", "test2@gmail.com");
        int rowCount = collectionDaoFactory.getUserDAO().updateUser(1L, u);
        // assert one row(user) updated
        assertEquals(1, rowCount);
        assertEquals("test2@gmail.com", collectionDaoFactory.getUserDAO().getUserById(1L).getEmailAddress());
    }

    @Test
    public void testUpdateNonExistingUser() throws CustomException {
        User u = new User(500L, "test2", "test2@gmail.com");
        int rowCount = collectionDaoFactory.getUserDAO().updateUser(500L, u);
        // assert one row(user) updated
        assertEquals(0, rowCount);
    }

    @Test
    public void testDeleteUser() throws CustomException {
        int rowCount = collectionDaoFactory.getUserDAO().deleteUser(1L);
        // assert one row(user) deleted
        assertEquals(1, rowCount);
        // assert user no longer there
        assertNull(collectionDaoFactory.getUserDAO().getUserById(1L));
    }

    @Test
    public void testDeleteNonExistingUser() throws CustomException {
        int rowCount = collectionDaoFactory.getUserDAO().deleteUser(500L);
        // assert no row(user) deleted
        assertEquals(0, rowCount);

    }

}
