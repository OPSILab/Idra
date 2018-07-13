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

import java.sql.SQLException;
import java.util.List;

import it.eng.idra.beans.RdfPrefix;

public class RdfPrefixManager {

	public static List<RdfPrefix> getAllPrefixes() throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getAllPrefixes();
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static RdfPrefix getPrefix(int id) throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getPrefix(id);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static boolean addPrefix(RdfPrefix prefix) throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			if (!manageBeansJpa.checkPrefixExists(prefix))
				return manageBeansJpa.addPrefix(prefix);
		} finally {
			manageBeansJpa.jpaClose();
		}
		return false;
	}

	public static boolean deletePrefix(int id) throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.deletePrefix(id);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static boolean updatePrefix(RdfPrefix prefix) throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.updatePrefix(prefix);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

}
