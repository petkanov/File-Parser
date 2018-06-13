package com.egtinteractive.app.moduls.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionPool { 
    private final DataSource dataSource;

    public ConnectionPool(String jdbcDatabaseUrl, String dbUser, String dbPassword, int poolSize) { 
	final GenericObjectPool gPool = new GenericObjectPool();
	gPool.setMaxActive(poolSize);
	final ConnectionFactory cf = new DriverManagerConnectionFactory(jdbcDatabaseUrl, dbUser, dbPassword);
	new PoolableConnectionFactory(cf, gPool, null, null, false, true);
	this.dataSource = new PoolingDataSource(gPool);
    } 

    public Connection getConnection() throws SQLException { 
	return dataSource.getConnection();
    }
}