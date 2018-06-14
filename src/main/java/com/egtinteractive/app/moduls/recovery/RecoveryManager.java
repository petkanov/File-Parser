package com.egtinteractive.app.moduls.recovery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RecoveryManager {

    private final DBConnection dbConnection;
    private final Set<String> filesAlreadySeen;

    public RecoveryManager(final DBConnection dbConnection) {
	this.dbConnection = dbConnection;
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
	try (Connection con = dbConnection.getConnection();
		PreparedStatement ps = con.prepareStatement("select * from file_stats where file=? and line=-1")) {
	    ps.setString(1, fileName);
	    final ResultSet rs = ps.executeQuery();

	    if (rs.isBeforeFirst()) {
		return true;
	    }
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
	return false;
    }

    public void updateFileProcessingProgress(String parserName, String fileName, int lineNumber) {
	try (Connection con = dbConnection.getConnection();
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
	    throw new RuntimeException(e);
	}
    }

    public int getLineOfLastParsedObject(String parserName, String fileName) {
	int line = 0;
	try (Connection con = dbConnection.getConnection();
		PreparedStatement ps = con.prepareStatement("select line from file_stats where id=MD5(?)")) {
	    ps.setString(1, parserName + fileName);
	    final ResultSet rs = ps.executeQuery();
	    if (rs.next()) {
		line = rs.getInt("line");
	    }
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
	return line > 0 ? line : 0;
    } 
}
