package utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import mapping_quality.OutputView;
import mapping_quality.Tuple;


public class RelationalDatabaseUtils {

	private static final Logger logger = Logger.getLogger(RelationalDatabaseUtils.class.getName());

	// JDBC driver name and database URL
	static String JDBC_DRIVER = "org.postgresql.Driver";
	static String JDBC_DRIVER_TYPE = "JDBC_POSTGRES";
	static String CONNECTION_PREFIX = "jdbc:postgresql://";
	static String SERVER_URL = "localhost:5432/";
	static String VIRTUAL_DATABASE_NAME = "testsdatabase";

	// Database credentials
	// TODO create config file for these
	static String USER = "lara";
	static String PASSWORD = "postgres";

	public RelationalDatabaseUtils(String jdbcDriver, String serverURL, String username, String password) {
		JDBC_DRIVER = jdbcDriver;
		SERVER_URL = serverURL;
		USER = username;
		PASSWORD = password;
	}

	public static String getCONNECTION_PREFIX() {
		return CONNECTION_PREFIX;
	}

	public static void setCONNECTION_PREFIX(String cONNECTION_PREFIX) {
		CONNECTION_PREFIX = cONNECTION_PREFIX;
	}

	public static String getJDBC_DRIVER_TYPE() {
		return JDBC_DRIVER_TYPE;
	}

	public static void setJDBC_DRIVER_TYPE(String jDBC_DRIVER_TYPE) {
		JDBC_DRIVER_TYPE = jDBC_DRIVER_TYPE;
	}

	public static String getJDBC_DRIVER() {
		return JDBC_DRIVER;
	}

	public static void setJDBC_DRIVER(String jDBC_DRIVER) {
		JDBC_DRIVER = jDBC_DRIVER;
	}

	public static String getUSER() {
		return USER;
	}

	public static void setUSER(String uSER) {
		USER = uSER;
	}

	public static String getPASSWORD() {
		return PASSWORD;
	}

	public static void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	private static Connection getServerConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			logger.info("Connecting to server...");
			return DriverManager.getConnection(CONNECTION_PREFIX + SERVER_URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE,"Error! - The connection to the server could not be established! ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String get_db_url_string(String dbName){
		if (dbName == null || dbName.equals(""))
			dbName = VIRTUAL_DATABASE_NAME;
		String dbUrl = SERVER_URL + dbName;
		return dbUrl;
	}

	private static Connection getDBConnection(String dbName) {
		Connection connection = null;

		if (dbName == null || dbName.equals(""))
			dbName = VIRTUAL_DATABASE_NAME;
		String dbUrl = CONNECTION_PREFIX + SERVER_URL + dbName;

		try {
			Class.forName("org.postgresql.Driver");
			logger.info("Connecting to database...");
			connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// Something went wrong and the db doesn't exist
			logger.info("The database was not found so it will be created.");
			// TODO change this after we no longer use Spicy
			createDatabase(dbName);
		}

		if (connection == null) {
			try {
				connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	@SuppressWarnings("unused")
	private static void createDatabase() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getServerConnection();
			System.out.println("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE " + VIRTUAL_DATABASE_NAME;
			stmt.executeUpdate(sql);
			System.out.println("Database '" + VIRTUAL_DATABASE_NAME + "' created successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
	}

	/**
	 * This is just a temporary method that will be used in order to work with
	 * Spicy Spicy uses the DB name, not the schema name, so instead of creating
	 * schemas, we have to create DBs that are fed into spicy.
	 * 
	 * This will be deprecated after we will no longer work with Spicy.
	 * 
	 * @param newDBname
	 */
	private static void createDatabase(String newDBname) {
		Connection conn = getServerConnection();

		if (newDBname == null || newDBname.equals(""))
			newDBname = VIRTUAL_DATABASE_NAME;

		Statement stmt = null;
		try {
			System.out.println("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE " + newDBname;
			stmt.executeUpdate(sql);
			System.out.println("Database '" + newDBname + "' created successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
	}

	
	

	
	

	public static String getServerURL() {
		return SERVER_URL;
	}

	public static OutputView readRelation(String dbName, String schemaName, String relationName, String pkName, ArrayList<String> attributesNames) {
		
		OutputView ov = new OutputView();
		HashMap<String,Tuple> tuples = new HashMap<>();
		Connection connection = getDBConnection(dbName);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM "+schemaName+"."+relationName);
			while (rs.next()) {
				Tuple newTuple = new Tuple();
				HashMap<String,String> values = new HashMap<>();
				for (String attributeName:attributesNames) {
					String value = rs.getString(attributeName).trim();
					values.put(attributeName, value);
				}
				String pkValue = rs.getString(pkName);
				newTuple.setValues(values);
				tuples.put(pkValue, newTuple);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return ov;
		
	}
	
	public static OutputView readRelation(String dbName, String sqlQuery, String pkName, ArrayList<String> attributesNames) {
		
		OutputView ov = new OutputView();
		HashMap<String,ArrayList<Tuple>> tuples = new HashMap<>();
		Connection connection = getDBConnection(dbName);
		
		if (connection==null)
			return null;
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st = connection.createStatement();
			rs = st.executeQuery(sqlQuery);
			long cardinality = 0l; 
			while (rs.next()) {
				Tuple newTuple = new Tuple();
				
				HashMap<String, String> values = new HashMap<>();
				for (String attributeName : attributesNames) {
					String value = rs.getString(attributeName);
					values.put(attributeName, value);
				}
				String pkValue = rs.getString(pkName);
				newTuple.setValues(values);
				if (!tuples.containsKey(pkValue)) {
					ArrayList<Tuple> new_tuples = new ArrayList<>();
					new_tuples.add(newTuple);
					tuples.put(pkValue, new_tuples);
				} else {
					ArrayList<Tuple> old_tuples = tuples.get(pkValue);
					old_tuples.add(newTuple);
					tuples.put(pkValue, old_tuples);
				}
				cardinality++;
			}
			
			ov.setDatabaseName(dbName);
			ov.setTuples(tuples);
			ov.setSqlQuery(sqlQuery);
			ov.setCardinality(cardinality);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return ov;
		
	}
	
	public static String readPrimaryKeyName(String dbName, String schemaName, String relationName) {
		String columnName ="";

		Connection connection = getDBConnection(dbName);
		
		if (connection==null)
			return null;
		
		try {
			DatabaseMetaData dbmd = connection.getMetaData();
			ResultSet pkColumns = dbmd.getPrimaryKeys(null, schemaName, relationName);
			while (pkColumns.next()) {
				columnName = pkColumns.getString("COLUMN_NAME");
//				String pkName = pkColumns.getString("PK_NAME");
			}
		} catch (SQLException e) {
			logger.severe("Error when reading primary keys for database: " + schemaName);
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return columnName;
	}
	
	public static ArrayList<String> readAttributesNames(String dbName, String schemaName, String relationName) {
		ArrayList<String> columnNames =new ArrayList<>();

		Connection connection = getDBConnection(dbName);
		
		if (connection==null)
			return null;
		
		try {
			DatabaseMetaData dbmd = connection.getMetaData();
			ResultSet columns = dbmd.getColumns(null, schemaName, relationName, null);
			while (columns.next()) {
				columnNames.add(columns.getString("COLUMN_NAME"));
//				String pkName = pkColumns.getString("PK_NAME");
			}
		} catch (SQLException e) {
			logger.severe("Error when reading attribute names for relation: " + schemaName+"."+relationName);
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return columnNames;
	}
	
	public static ArrayList<String> readAttributesNames(String dbName, String sqlQuery) {
		ArrayList<String> columnNames =new ArrayList<>();

		Connection connection = getDBConnection(dbName);
		
		if (connection==null)
			return null;
		
		try {
			Statement st = connection.createStatement();
			ResultSet rset = st.executeQuery(sqlQuery);
			ResultSetMetaData md = rset.getMetaData();
			logger.info("Reading attribute names ...");
			for (int i=1; i<=md.getColumnCount(); i++)
			{
				columnNames.add(md.getColumnLabel(i));
			}
			
		} catch (SQLException e) {
			logger.severe("Error when reading attribute names for query: " + sqlQuery);
			e.printStackTrace();
		}finally {
			try {
				logger.info("Closed database connection.");
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return columnNames;
	}
}
