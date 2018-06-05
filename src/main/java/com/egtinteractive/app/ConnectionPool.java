package com.egtinteractive.app;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

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

    public Connection getConnection() {
	if(dataSource == null) {
	    createPool();
	}
	try {
	    return dataSource.getConnection();
	} catch (SQLException e) {
	    Logger.getLogger(this.getClass()).error(e.getMessage());
	}
	return null;
    }

//    public static void main(String[] args) throws Exception {
//	ResultSet rsObj = null;
//	Connection con = null;
//	PreparedStatement pstmtObj = null;
//	ConnectionPool jdbcObj = new ConnectionPool();
//	DataSource dataSource = jdbcObj.setUpPool();
//
//	try {
//	    jdbcObj.printDbStatus();
//
//	    // Performing Database Operation!
//	    System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
//	    con = dataSource.getConnection();
//	    jdbcObj.printDbStatus();
//
//	    pstmtObj = con.prepareStatement("SELECT * FROM car");
//	    rsObj = pstmtObj.executeQuery();
//	    while (rsObj.next()) {
//		System.out.println("Model: " + rsObj.getString("model"));
//	    }
//	    System.out.println("\n=====Releasing Connection Object To Pool=====\n");
//	} catch (Exception sqlException) {
//	    sqlException.printStackTrace();
//	} finally {
//	    try {
//		// Closing ResultSet Object
//		if (rsObj != null) {
//		    rsObj.close();
//		}
//		// Closing PreparedStatement Object
//		if (pstmtObj != null) {
//		    pstmtObj.close();
//		}
//		// Closing Connection Object
//		if (con != null) {
//		    // con.close();
//		}
//	    } catch (Exception sqlException) {
//		sqlException.printStackTrace();
//	    }
//	}
//	jdbcObj.printDbStatus();
//
//	Connection con2 = dataSource.getConnection();
//
//	try (
//
//		PreparedStatement psAcc = con2.prepareStatement(
//			"insert into account(`owner`,`owner_birthday`,`balance`,`date_created`,`is_active`,`rate`,`credit_rating`,`time_milisec`) values(?,?,?,?,?,?,?,?)");
//		PreparedStatement psEmp = con2.prepareStatement("insert into employee(`name`,`age`,`occupation`) values(?,?,?)");
//		PreparedStatement psCar = con2.prepareStatement("insert into car(`model`,`color`,`age`) values(?,?,?)")) {
//
//	    for (int i = 0; i < 2; i++) {
//		psAcc.setString(1, "Monica_" + UUID.randomUUID().toString().substring(1, 4));
//		psAcc.setDate(2, new java.sql.Date(System.currentTimeMillis()));
//		psAcc.setBigDecimal(3, new BigDecimal("423342345234344243"));
//		psAcc.setDate(4, new java.sql.Date(System.currentTimeMillis()));
//		psAcc.setInt(5, new Random().nextInt(2));
//		psAcc.setFloat(6, 0.15f);
//		psAcc.setString(7, "A");
//		psAcc.setLong(8, System.currentTimeMillis());
//
//		psAcc.executeUpdate();
//	    }
//
//	    for (int i = 0; i < 2; i++) {
//		psEmp.setString(1, "Melany_" + UUID.randomUUID().toString().substring(1, 4));
//		psEmp.setInt(2, i + 19);
//		psEmp.setString(3, "Hostess_" + UUID.randomUUID().toString().substring(1, 3));
//
//		psEmp.executeUpdate();
//	    }
//
//	    for (int i = 0; i < 2; i++) {
//		psCar.setString(1, UUID.randomUUID().toString().substring(0, 5));
//		psCar.setString(2, UUID.randomUUID().toString().substring(0, 3));
//		psCar.setInt(3, i);
//
//		psCar.executeUpdate();
//	    }
//
//	} catch (Exception e) {
//	    throw new RuntimeException(e);
//	}
//	jdbcObj.printDbStatus();
//    }
}