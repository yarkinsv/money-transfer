package com.moneytransfer.dao;

import com.moneytransfer.exception.CustomException;

public abstract class DAOFactory {

    public static final int DEFAULT = 0;
	public static final int H2 = 1;
	public static final int COLLECTION = 2;

    public abstract UserDAO getUserDAO();

	public abstract AccountDAO getAccountDAO();

	public abstract void populateTestData() throws CustomException;

	public static DAOFactory getDAOFactory(String factoryName) {
	    if (factoryName == null || "".equals(factoryName)) {
	        return getDAOFactory(DEFAULT);
        }
	    switch (factoryName.toLowerCase()) {
            case "h2":
                return getDAOFactory(H2);
            case "collection":
                return getDAOFactory(COLLECTION);
            default:
                throw new IllegalArgumentException("Unknown DAO implementation " + factoryName);
        }
    }

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
            case H2:
                return new H2DAOFactory();
            case COLLECTION:
                return new CollectionDAOFactory();
            default:
                // by default using H2 in memory database
                return new H2DAOFactory();
		}
	}
}
