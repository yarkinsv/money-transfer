package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;

public abstract class DAOFactory {

	public static final int H2 = 1;

	public abstract UserDAO getUserDAO();

	public abstract AccountDAO getAccountDAO();

	public abstract void populateTestData() throws CustomException;

    private static H2DAOFactory h2DAOFactory = null;
    private static DAOFactory getH2DAOFactory(){
        if (h2DAOFactory == null)
            h2DAOFactory = new H2DAOFactory();
        return h2DAOFactory;
    }


	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return getH2DAOFactory();
		default:
			// by default using H2 in memory database
			return getH2DAOFactory();
		}
	}
}
