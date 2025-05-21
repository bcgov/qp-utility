package qputility.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QPDBConnection
{

	/**
	 * Returns a database connection object for MSSQL
	 * 
	 * @author chris.ditcher
	 * @param url
	 * @param serverName
	 * @param portNumber
	 * @param databaseName
	 * @param userName
	 * @param password
	 * @throws SQLException
	 */
	public static Connection getConnection(String url, String serverName, String portNumber, String databaseName, String userName, String password) throws SQLException
	{
		Connection con = null;
		String selectMethod = "cursor";
		String connectionUrl = url + serverName + ":" + portNumber + ";databaseName=" + databaseName + ";selectMethod=" + selectMethod + ";";
		try
		{
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance();
			con = DriverManager.getConnection(connectionUrl, userName, password);
			if (con != null)
			{
				System.out.println("Connection Successful.");
			}
		}
		catch (Exception e)
		{
			if (con != null)
			{
				con.close();
			}
			e.printStackTrace();
			System.out.println("Error Trace in getConnection() : " + e.getMessage());
		}
		return con;
	}
}
