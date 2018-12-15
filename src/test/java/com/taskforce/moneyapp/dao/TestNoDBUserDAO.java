package com.taskforce.moneyapp.dao;

import com.moneytransfer.dao.DAOFactory;
import com.moneytransfer.exception.CustomException;

import org.junit.BeforeClass;

public class TestNoDBUserDAO extends TestUserDAO {
	@BeforeClass
	public static void setup() throws CustomException {
		DAOFactory.setFactoryCode(DAOFactory.NODB);
		TestUserDAO.setup();
	}
}
