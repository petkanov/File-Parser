package com.egtinteractive.app.moduls.recovery;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DBConnection {
    private volatile DataSource dataSource;
    private final String jdbcDatabaseUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String dbType;

    public DBConnection(String jdbcDatabaseUrl, String dbUser, String dbPassword, String dbType) {
	this.jdbcDatabaseUrl = jdbcDatabaseUrl;
	this.dbUser = dbUser;
	this.dbPassword = dbPassword;
	this.dbType = dbType;
    }

    public synchronized Connection getConnection() throws SQLException {
	if(dataSource == null) {
	    createConnection();
	}
	return dataSource.getConnection();
    }
    
    private void createConnection() {
	if (dbType.equals("mysql")) {
	    MysqlDataSource mysqlDS = null;
	    mysqlDS = new MysqlDataSource();
	    mysqlDS.setURL(jdbcDatabaseUrl);
	    mysqlDS.setUser(dbUser);
	    mysqlDS.setPassword(dbPassword);
	    dataSource = mysqlDS;
	} else {
	    throw new RuntimeException("Entered database type is eihter wrong or not supported: " + dbType);
	}
    }
}