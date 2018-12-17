package com.moneytransfer.dao;

import java.util.HashMap;

import com.moneytransfer.exception.CustomException;

public abstract class DAOFactory {

	public static final int H2 = 1;
	public static final int NODB = 2;

	public abstract UserDAO getUserDAO();

	public abstract AccountDAO getAccountDAO();

	public abstract void populateTestData() throws CustomException;

	private static final HashMap<Integer, DAOFactory> daoFactories = new HashMap<>();
	private static Integer factoryCode = null;

	public static DAOFactory getDAOFactory() {
		if (factoryCode == null) setFactoryCode(H2);
		return daoFactories.get(factoryCode);
	}

	public static void setFactoryCode(int factoryCode) {
		DAOFactory.factoryCode = new Integer(factoryCode);
		DAOFactory daoFactory = daoFactories.get(factoryCode);
		if (daoFactory == null)
			switch (factoryCode) {
			case H2:
				daoFactory = new H2DAOFactory();
				daoFactories.put(factoryCode, daoFactory);
			case NODB:
				daoFactory = new NoDBDAOFactory();
				daoFactories.put(factoryCode, daoFactory);
			default:
				// by default using H2 in memory database
				DAOFactory.setFactoryCode(H2);
			}
	}
}
