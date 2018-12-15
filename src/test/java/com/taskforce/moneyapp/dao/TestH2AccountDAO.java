package com.taskforce.moneyapp.dao;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;

import org.junit.BeforeClass;

public class TestH2AccountDAO extends TestAccountDAO {
	@BeforeClass
	public static void setup() throws CustomException {
		DAOFactory.setFactoryCode(DAOFactory.H2);
		TestAccountDAO.setup();
	}
}
