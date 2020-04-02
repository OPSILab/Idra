/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.idra.search;

import org.apache.http.HttpException;

import org.apache.jena.query.QueryParseException;

import it.eng.idra.beans.search.SparqlResultFormat;
import it.eng.idra.cache.*;

public class SPARQLFederatedSearch {

	public SPARQLFederatedSearch() {
	}

	public static String runQuery(String query, SparqlResultFormat formatType)
			throws QueryParseException, HttpException {

		return LODCacheManager.runQuery(query, formatType);

	}

}
