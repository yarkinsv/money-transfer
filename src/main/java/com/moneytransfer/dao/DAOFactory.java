package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;

public abstract class DAOFactory {
	static DAOFactory factory = null;

	public static final int H2 = 1;
	public static final int HASH = 2;

	public abstract UserDAO getUserDAO() throws CustomException;

	public abstract AccountDAO getAccountDAO() throws CustomException;

	public abstract void populateTestData() throws CustomException;

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return new H2DAOFactory();
		case HASH:
			if (factory == null) {
				factory = new HashDAOFactory();
			}
			return factory;
		default:
			// by default using H2 in memory database
			return new H2DAOFactory();
		}
	}
}
