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
package it.eng.idra.utils.restclient.configuration;

public enum RestProperty {
	HTTP_PROXY_ENABLED("http.proxyEnabled"),
	HTTP_PROXY_HOST("http.proxyHost"),
	HTTP_PROXY_USER("http.proxyUser"),
	HTTP_PROXY_PORT("http.proxyPort"),
	HTTP_PROXY_PASSWORD("http.proxyPassword"),
	HTTP_PROXY_NONPROXYHOSTS("http.nonProxyHosts");

	private final String text;

	private RestProperty(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}