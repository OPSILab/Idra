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
package it.eng.idra.authentication.fiware.model;

public enum FiwareIDMVersion {

	FIWARE_IDM_VERSION_6("6"), FIWARE_IDM_VERSION_7("7");

	private final String text;

	private FiwareIDMVersion(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static FiwareIDMVersion fromString(String text) {
		for (FiwareIDMVersion b : FiwareIDMVersion.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

}
