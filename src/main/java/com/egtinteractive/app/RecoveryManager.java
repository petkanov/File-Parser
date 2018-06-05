package com.egtinteractive.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class RecoveryManager {

    private static ConnectionPool connectionPool;

    private static final Set<String> activeServices = new HashSet<>();

    public static void setConnectionPool(final ConnectionPool cPool) {
	if (connectionPool == null) {
	    connectionPool = cPool;
	    try (Connection con = connectionPool.getConnection(); Statement s = con.createStatement()) {
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS `old_files`(`file` VARCHAR(256) NOT NULL, `date` DATE NOT NULL, UNIQUE KEY(`file`))  ENGINE=InnoDB;");
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS `files_in_process`(`service` VARCHAR(256) NOT NULL, `file` VARCHAR(256) NOT NULL, `line` INT(12) NOT NULL, UNIQUE KEY(`service`))  ENGINE=InnoDB;");
		ResultSet res = s.executeQuery("select * from files_in_process");
		while(res.next()) {
		    activeServices.add(res.getString("service"));
		}
		System.out.println(activeServices);
	    } catch (SQLException e) {
		e.printStackTrace();
		Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	    }
	}
    }

    public static void saveFile(String fileName) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("insert into old_files(`file`,`date`) values(?,?)")) {
	    ps.setString(1, fileName);
	    ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
	    ps.executeUpdate();
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public static boolean isFileProcessed(String fileName) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from old_files where file=?")) {
	    ps.setString(1, fileName);
	    final ResultSet rs = ps.executeQuery();

	    if (rs.isBeforeFirst()) {
		return true;
	    }
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
	return false;
    }

    private static void createServiceRecord(String serviceName) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("insert into files_in_process(`service`,`file`,`line`) values(?,?,?)")) {
	    ps.setString(1, serviceName);
	    ps.setString(2, "");
	    ps.setInt(3, -1);
	    ps.executeUpdate();
	    activeServices.add(serviceName);
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public static void updateProcessingProgress(String serviceName, String fileName, int lineNumber) {
	if (!activeServices.contains(serviceName)) {
	    createServiceRecord(serviceName);
	}

    }

}
