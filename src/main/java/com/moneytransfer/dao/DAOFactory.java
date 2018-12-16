package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;

public abstract class DAOFactory {

	private static final H2DAOFactory h2DaoFactory  = new H2DAOFactory();

	public static final int H2 = 1;

	public abstract UserDAO getUserDAO();

	public abstract AccountDAO getAccountDAO();

	public abstract void populateTestData() throws CustomException;

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return h2DaoFactory;
		default:
			// by default using H2 in memory database
			return h2DaoFactory;
		}
	}
}
