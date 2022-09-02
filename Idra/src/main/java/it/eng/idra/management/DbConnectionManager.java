/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.management;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.PropertyManager;
import java.sql.Connection;
import java.sql.SQLException;

// TODO: Auto-generated Javadoc
/**
 * This class manage the connection to db. Only an instance of this class can be
 * created.
 */
public class DbConnectionManager {

  /** The instance. */
  private static DbConnectionManager INSTANCE;

  static {
    INSTANCE = new DbConnectionManager();
  }

  /** The cpds. */
  private final HikariDataSource cpds;
  // private Properties PropertyManager;

  /**
   * A private constructor since this is a Singleton.
   */
  private DbConnectionManager() {

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(PropertyManager.getProperty(IdraProperty.DB_HOST_MIN));

    config.setUsername(PropertyManager.getProperty(IdraProperty.DB_USERNAME));
    config.setPassword(PropertyManager.getProperty(IdraProperty.DB_PASSWORD));
    config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
    config.setIdleTimeout(30000);
    config.setMinimumIdle(5);
    config.setMaximumPoolSize(10);

    config.addDataSourceProperty("databaseName", PropertyManager.getProperty(IdraProperty.DB_NAME));
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    cpds = new HikariDataSource(config);
  }

  /**
   * Gets the db connection.
   *
   * @return the db connection
   * @throws SQLException the SQL exception
   */
  public static Connection getDbConnection() throws SQLException {
    return INSTANCE.cpds.getConnection();
  }

  /**
   * Close db connection.
   *
   * @throws SQLException the SQL exception
   */
  public static void closeDbConnection() throws SQLException {
    INSTANCE.cpds.close();
  }
}
