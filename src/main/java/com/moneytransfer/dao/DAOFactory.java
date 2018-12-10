package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;

public abstract class DAOFactory {

	public static final int H2 = 1;

	public static final int HM = 2;

	private static int DAOType = 0;

	public abstract UserDAO getUserDAO();

	public abstract AccountDAO getAccountDAO();

	public abstract void populateTestData() throws CustomException;

	public static void setDAOFactory(String factoryName) {
		if (factoryName==null || !factoryName.toLowerCase().equals("hm"))
			DAOType = H2;
		else
			DAOType = HM;
	}

	public static DAOFactory getDAOFactory() {
		return getDAOFactory(DAOType);
	}

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return new H2DAOFactory();
		case HM:
			return new HMDAOFactory();
		default:
			// by default using H2 in memory database
			return new H2DAOFactory();
		}
	}
}
