package com.taskforce.moneyapp.services;

import com.moneytransfer.dao.DAOFactory;

import org.junit.BeforeClass;

public class TestNoDBTransactionService extends TestTransactionService {
    @BeforeClass
    public static void setup() throws Exception {
        DAOFactory.setFactoryCode(DAOFactory.NODB);
        TestService.setup();
    }
}
