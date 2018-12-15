package com.taskforce.moneyapp.services;

import com.moneytransfer.dao.DAOFactory;

import org.junit.BeforeClass;

public class TestH2AccountService extends TestAccountService {
    @BeforeClass
    public static void setup() throws Exception {
        DAOFactory.setFactoryCode(DAOFactory.H2);
        TestService.setup();
    }
}
