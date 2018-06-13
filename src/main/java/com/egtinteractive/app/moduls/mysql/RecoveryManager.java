package com.egtinteractive.app.moduls.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.egtinteractive.app.moduls.logger.FPLogger;

public class RecoveryManager {

    private final ConnectionPool connectionPool;
    private final Set<String> filesAlreadySeen;
    private volatile FPLogger logger;

    public RecoveryManager(final ConnectionPool cPool) {
	connectionPool = cPool;
	filesAlreadySeen = ConcurrentHashMap.newKeySet();
    }

    public void addToAlreadySeenFiles(final String fileName) {
	filesAlreadySeen.add(fileName);
    }

    public void removeFromAlreadySeenFiles(final String fileName) {
	filesAlreadySeen.remove(fileName);
    }

    public boolean isFileAlreadySeen(final String fileName) {
	return filesAlreadySeen.contains(fileName);
    } 

    public boolean isFileProcessed(String fileName) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from file_stats where file=? and line=-1")) {
	    ps.setString(1, fileName);
	    final ResultSet rs = ps.executeQuery();

	    if (rs.isBeforeFirst()) {
		return true;
	    }
	} catch (SQLException e) {
	    if (logger != null) {
		logger.logErrorMessage(this.getClass(), e.getMessage());
	    }
	}
	return false;
    }

    public void updateFileProcessingProgress(String parserName, String fileName, int lineNumber) {
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement(
			"INSERT INTO file_stats(id,parser,file,line) VALUES(MD5(?),?,?,?) ON DUPLICATE KEY UPDATE parser=?,file=?,line=?")) {
	    ps.setString(1, parserName + fileName);
	    ps.setString(2, parserName);
	    ps.setString(3, fileName);
	    ps.setInt(4, lineNumber);
	    ps.setString(5, parserName);
	    ps.setString(6, fileName);
	    ps.setInt(7, lineNumber);
	    ps.executeUpdate();
	} catch (SQLException e) {
	    if (logger != null) {
		logger.logErrorMessage(this.getClass(), e.getMessage());
	    }
	}
    }

    public int getLineOfLastParsedObject(String parserName, String fileName) {
	int line = 0;
	try (Connection con = connectionPool.getConnection();
		PreparedStatement ps = con.prepareStatement("select line from file_stats where id=MD5(?)")) {
	    ps.setString(1, parserName + fileName);
	    final ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
		line = rs.getInt("line");
	    }
	} catch (SQLException e) {
	    if (logger != null) {
		logger.logErrorMessage(this.getClass(), e.getMessage());
	    }
	}
	return line > 0 ? line : 0;
    }

    public void setLogger(FPLogger logger) {
	this.logger = logger;
    }
}
