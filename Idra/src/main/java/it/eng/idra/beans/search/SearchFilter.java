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
package it.eng.idra.beans.search;

import it.eng.idra.utils.JsonRequired;

/**
 * This class represents a DCATDataset field on which to perform a search
 * 
 * @author ENG
 *
 */


public class SearchFilter {

	@JsonRequired
	private String field;
	
	@JsonRequired
	private String value;
	
	
	public SearchFilter(String field, String value) {
		super();
		this.field = field;
		this.value = value;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public String toString() {
		return "SearchFilter [field=" + field + ", value=" + value + "]";
	}
	
	
	
	
	
	
}
