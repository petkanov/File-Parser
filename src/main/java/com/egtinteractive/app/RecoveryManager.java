package com.egtinteractive.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class RecoveryManager {

    private final ConnectionPool connectionPool;
    private final Set<String> filesAlreadySeen;

    public RecoveryManager(final ConnectionPool cPool) {
	connectionPool = cPool;
	filesAlreadySeen = new HashSet<>();
    }
    
    public void addToAlreadySeenFiles(final String fileName) {
	filesAlreadySeen.add(fileName);
	 System.out.println(filesAlreadySeen);
    }
    
    public void removeFromAlreadySeenFiles(final String fileName) {
	filesAlreadySeen.remove(fileName);
	System.out.println(filesAlreadySeen);
    }
    
    public boolean isFileAlreadySeen(final String fileName) {
	return filesAlreadySeen.contains(fileName);
    }

    public void createMemoryTablesIfNotExist() {
	try (Connection con = connectionPool.getConnection(); Statement s = con.createStatement()) {
	    s.executeUpdate(
		    "CREATE TABLE IF NOT EXISTS `old_files`(`file` VARCHAR(256) NOT NULL, `date` TIMESTAMP NOT NULL, UNIQUE KEY(`file`))  ENGINE=InnoDB;");
	    s.executeUpdate(
		    "CREATE TABLE IF NOT EXISTS `files_in_process`(id VARCHAR(256) NOT NULL, `parser` VARCHAR(256) NOT NULL, `file` VARCHAR(256) NOT NULL, `line` INT(12) NOT NULL, UNIQUE KEY(`id`))  ENGINE=InnoDB;");
	} catch (SQLException e) {
	    e.printStackTrace();
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public void clearRecoveryDatabase() {
	try (Connection con = connectionPool.getConnection(); Statement s = con.createStatement()) {
	    s.executeUpdate("DROP TABLE IF EXISTS `old_files`");
	    s.executeUpdate("DROP TABLE IF EXISTS `files_in_process`");
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public void clearParsersTable() {
	try (Connection con = connectionPool.getConnection(); Statement s = con.createStatement()) {
	    s.executeUpdate("DELETE FROM `files_in_process`");
	} catch (SQLException e) {
	    e.printStackTrace();
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public void clearOldParserLogs() {
	try (Connection con = connectionPool.getConnection(); Statement s = con.createStatement()) {
	    final ResultSet rs = s.executeQuery("SELECT parser, file FROM files_in_process WHERE line=-1");
	    final List<String> delQueries = new ArrayList<>();
	    while (rs.next()) {
		delQueries.add("DELETE FROM `files_in_process` WHERE parser=\'" + rs.getString("parser") + "\' AND file=\'"
			+ rs.getString("file") + "\'");
	    }
	    for (String query : delQueries) {
		s.execute(query);
	    }
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public void saveFile(String fileName) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("insert into old_files(`file`,`date`) values(?,NOW())")) {
	    ps.setString(1, fileName);
	    ps.executeUpdate();
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public boolean isFileProcessed(String fileName) {
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

    public void updateFileProcessingProgress(String parserName, String fileName, int lineNumber) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement(
			"INSERT INTO files_in_process(id,parser,file,line) VALUES(MD5(?),?,?,?) ON DUPLICATE KEY UPDATE parser=?,file=?,line=?")) {
	    ps.setString(1, parserName + fileName);
	    ps.setString(2, parserName);
	    ps.setString(3, fileName);
	    ps.setInt(4, lineNumber);
	    ps.setString(5, parserName);
	    ps.setString(6, fileName);
	    ps.setInt(7, lineNumber);
	    ps.executeUpdate();
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
    }

    public int getLineOfLastParsedObject(String parserName, String fileName) {
	int line = 0;
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("select line from files_in_process where id=MD5(?)")) {
	    ps.setString(1, parserName + fileName);
	    final ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
		line = rs.getInt("line");
	    }
	} catch (SQLException e) {
	    Logger.getLogger(RecoveryManager.class).error(e.getMessage());
	}
	return line > 0 ? line : 0;
    }
}
