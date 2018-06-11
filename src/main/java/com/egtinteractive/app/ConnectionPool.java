package com.egtinteractive.app;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionPool {
    private final String jdbcDatabaseUrl;
    private final String dbUser;
    private final String dbPassword;
    private final int poolSize;
    private DataSource dataSource;

    public ConnectionPool(String jdbcDatabaseUrl, String dbUser, String dbPassword, int poolSize) {
	this.jdbcDatabaseUrl = jdbcDatabaseUrl;
	this.dbUser = dbUser;
	this.dbPassword = dbPassword;
	this.poolSize = poolSize;
    }

    public void createPool() {
	GenericObjectPool gPool = new GenericObjectPool();
	gPool.setMaxActive(poolSize);
	ConnectionFactory cf = new DriverManagerConnectionFactory(jdbcDatabaseUrl, dbUser, dbPassword);
	new PoolableConnectionFactory(cf, gPool, null, null, false, true);
	this.dataSource = new PoolingDataSource(gPool);
    }

    public Connection getConnection() throws SQLException {
	if (dataSource == null) {
	    createPool();
	}
	return dataSource.getConnection();
    }
}