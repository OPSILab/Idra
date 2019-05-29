/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.eng.idra.management;

//import org.hibernate.annotations.common.util.impl.Log_.logger;
import org.apache.logging.log4j.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.PropertyManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class manage the connection to db. Only an instance of this class can be
 * created.
 */
public class DBConnectionManager {

//	private static Logger logger = LogManager.getLogger(DBConnectionManager.class);

//	private static interface Singleton {
	private static DBConnectionManager INSTANCE;
	static {
		INSTANCE = new DBConnectionManager();
	}

	private final HikariDataSource cpds;
	// private Properties PropertyManager;

	/**
	 * A private constructor since this is a Singleton
	 */
	private DBConnectionManager() {

		// PropertyManager = new Properties();
		// try {
		// PropertyManager.load(this.getClass().getClassLoader().getResourceAsStream("configuration.properties"));
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(PropertyManager.getProperty(IdraProperty.DB_HOST_MIN));
		
		config.setUsername(PropertyManager.getProperty(IdraProperty.DB_USERNAME));
		config.setPassword(PropertyManager.getProperty(IdraProperty.DB_PASSWORD));
		config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		config.setIdleTimeout(30000);
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(10);
		
		config.addDataSourceProperty("databaseName", PropertyManager.getProperty(IdraProperty.DB_NAME));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		
		cpds = new HikariDataSource(config);
//		try {
//			Connection asd = cpds.getConnection();
//			System.out.println(asd.isClosed());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		this.cpds = new ComboPooledDataSource();
//
//		// loads the jdbc driver
//		try {
//			cpds.setDriverClass("com.mysql.jdbc.Driver");
//		} catch (PropertyVetoException e) {
//			e.printStackTrace();
//		}
//
//		cpds.setJdbcUrl(PropertyManager.getProperty(IdraProperty.DB_HOST));
//		cpds.setUser(PropertyManager.getProperty(IdraProperty.DB_USERNAME));
//		cpds.setPassword(PropertyManager.getProperty(IdraProperty.DB_PASSWORD));
////		cpds.setDataSourceName(PropertyManager.getProperty("DB_NAME"));
//		// cpds.setMaxIdleTime(15);
//		cpds.setMaxConnectionAge(27500);
//		// cpds.setPreferredTestQuery("SELECT 1");
//		// cpds.setTestConnectionOnCheckout(true);
//		// the settings below are optional -- c3p0 can work with defaults
//		cpds.setMinPoolSize(5);
//		cpds.setAcquireIncrement(5);
//		cpds.setMaxPoolSize(20);
//		cpds.setMaxStatements(180);

	}

	public static Connection getDbConnection() throws SQLException {
		return INSTANCE.cpds.getConnection();
	}

	public static void closeDbConnection() throws SQLException {
		INSTANCE.cpds.close();
	}
}
