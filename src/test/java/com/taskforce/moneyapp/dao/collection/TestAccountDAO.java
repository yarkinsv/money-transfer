package com.taskforce.moneyapp.dao.collection;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class TestAccountDAO {
    private static final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.COLLECTION);

    @BeforeClass
    public static void setup() throws CustomException {
        // prepare test database and test data. Test data are initialised from
        // src/test/resources/demo.sql
        daoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllAccounts() throws CustomException {
        Set<Account> allAccounts = daoFactory.getAccountDAO().getAllAccounts();
        assertTrue(allAccounts.size() > 1);
    }

    @Test
    public void testGetAccountById() throws CustomException {
        Account account = daoFactory.getAccountDAO().getAccountById(1L);
        assertTrue(account.getUserName().equals("yangluo"));
    }

    @Test
    public void testGetNonExistingAccById() throws CustomException {
        Account account = daoFactory.getAccountDAO().getAccountById(100L);
        assertTrue(account == null);
    }

    @Test
    public void testCreateAccount() throws CustomException {
        BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        Account a = new Account("test2", balance, "CNY");
        long aid = daoFactory.getAccountDAO().createAccount(a);
        Account afterCreation = daoFactory.getAccountDAO().getAccountById(aid);
        assertTrue(afterCreation.getUserName().equals("test2"));
        assertTrue(afterCreation.getCurrencyCode().equals("CNY"));
        assertTrue(afterCreation.getBalance().equals(balance));
    }

    @Test
    public void testDeleteAccount() throws CustomException {
        int rowCount = daoFactory.getAccountDAO().deleteAccountById(2L);
        // assert one row(user) deleted
        assertTrue(rowCount == 1);
        // assert user no longer there
        assertTrue(daoFactory.getAccountDAO().getAccountById(2L) == null);
    }

    @Test
    public void testDeleteNonExistingAccount() throws CustomException {
        int rowCount = daoFactory.getAccountDAO().deleteAccountById(500L);
        // assert no row(user) deleted
        assertTrue(rowCount == 0);

    }

    @Test
    public void testUpdateAccountBalanceSufficientFund() throws CustomException {

        BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdated = daoFactory.getAccountDAO().updateAccountBalance(1L, deltaDeposit);
        assertTrue(rowsUpdated == 1);
        assertTrue(daoFactory.getAccountDAO().getAccountById(1L).getBalance().equals(afterDeposit));
        BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = daoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
        assertTrue(rowsUpdatedW == 1);
        assertTrue(daoFactory.getAccountDAO().getAccountById(1L).getBalance().equals(afterWithDraw));

    }

    @Test(expected = CustomException.class)
    public void testUpdateAccountBalanceNotEnoughFund() throws CustomException {
        BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsUpdatedW = daoFactory.getAccountDAO().updateAccountBalance(1L, deltaWithDraw);
        assertTrue(rowsUpdatedW == 0);

    }


}
