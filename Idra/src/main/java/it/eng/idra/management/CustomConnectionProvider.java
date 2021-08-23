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

import it.eng.idra.beans.IdraProperty;
import java.util.Map;
import java.util.Optional;
import org.hibernate.HibernateException;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomConnectionProvider.
 */
public class CustomConnectionProvider extends HikariCPConnectionProvider {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5309283059350710680L;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.hibernate.hikaricp.internal.HikariCPConnectionProvider#configure(java.
   * util.Map)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void configure(Map conf) throws HibernateException {

    if (Optional.ofNullable(System.getenv(IdraProperty.DB_USERNAME.toString())).isPresent()) {
      conf.put("hibernate.hikari.dataSource.user",
          System.getenv(IdraProperty.DB_USERNAME.toString()));
    }

    if (Optional.ofNullable(System.getenv(IdraProperty.DB_PASSWORD.toString())).isPresent()) {
      conf.put("hibernate.hikari.dataSource.password",
          System.getenv(IdraProperty.DB_PASSWORD.toString()));
    }

    if (Optional.ofNullable(System.getenv(IdraProperty.DB_HOST.toString())).isPresent()) {
      conf.put("hibernate.hikari.dataSource.url", System.getenv(IdraProperty.DB_HOST.toString()));
    }

    super.configure(conf);
  }

}
