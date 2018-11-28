package com.taskforce.moneyapp.dao.collection;

import com.moneytransfer.dao.AccountDAO;
import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.UserTransaction;
import com.taskforce.moneyapp.dao.h2.TestAccountDAO;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;

public class TestAccountBalance {

    private static Logger log = Logger.getLogger(TestAccountDAO.class);
    private static final DAOFactory collectionDaoFactory = DAOFactory.getDAOFactory(DAOFactory.COLLECTION);
    private static final int THREADS_COUNT = 100;

    @BeforeClass
    public static void setup() throws CustomException {
        // prepare test database and test data, Test data are initialised from hard coded values
        collectionDaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAccountSingleThreadSameCcyTransfer() throws CustomException {

        final AccountDAO accountDAO = collectionDaoFactory.getAccountDAO();

        BigDecimal transferAmount = BigDecimal.valueOf(50.01234).setScale(4, RoundingMode.HALF_EVEN);

        UserTransaction transaction = new UserTransaction("EUR", transferAmount, 3L, 4L);

        long startTime = System.currentTimeMillis();

        accountDAO.transferAccountBalance(transaction);
        long endTime = System.currentTimeMillis();

        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        Account accountFrom = accountDAO.getAccountById(3);

        Account accountTo = accountDAO.getAccountById(4);

        log.debug("Account From: " + accountFrom);

        log.debug("Account From: " + accountTo);

        assertEquals(0, accountFrom.getBalance().compareTo(BigDecimal.valueOf(449.9877).setScale(4, RoundingMode.HALF_EVEN)));
        assertEquals(accountTo.getBalance(), BigDecimal.valueOf(550.0123).setScale(4, RoundingMode.HALF_EVEN));

    }

    @Test
    public void testAccountMultiThreadedTransfer() throws InterruptedException, CustomException {
        final AccountDAO accountDAO = collectionDaoFactory.getAccountDAO();
        // transfer a total of 200USD from 100USD balance in multi-threaded
        // mode, expect half of the transaction fail
        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
        for (int i = 0; i < THREADS_COUNT; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        UserTransaction transaction = new UserTransaction("USD",
                                new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
                        accountDAO.transferAccountBalance(transaction);
                    } catch (Exception e) {
                        log.error("Error occurred during transfer ", e);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }

        latch.await();

        Account accountFrom = accountDAO.getAccountById(1);

        Account accountTo = accountDAO.getAccountById(2);

        log.debug("Account From: " + accountFrom);

        log.debug("Account From: " + accountTo);

        assertEquals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN), accountFrom.getBalance());
        assertEquals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN), accountTo.getBalance());

    }
}
