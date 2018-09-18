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
package it.eng.idra.beans;

public enum ODFProperty {
	IDRA_VERSION("IDRA_VERSION"),
	IDRA_RELEASE_TIMESTAMP("IDRA_RELEASE_TIMESTAMP"),
	DB_HOST("DB_HOST"),
	DB_HOST_MIN("DB_HOST_MIN"),
	DB_NAME("DB_NAME"),
	DB_USERNAME("DB_USERNAME"),
	DB_PASSWORD("DB_PASSWORD"),
	HTTP_PROXY_ENABLED("http.proxyEnabled"),
	HTTP_PROXY_HOST("http.proxyHost"),
	HTTP_PROXY_USER("http.proxyUser"),
	HTTP_PROXY_PORT("http.proxyPort"),
	HTTP_PROXY_PASSWORD("http.proxyPassword"),
	HTTP_PROXY_NONPROXYHOSTS("http.nonProxyHosts"),
	HTTPS_PROXY_HOST("https.proxyHost"),
	HTTPS_PROXY_USER("https.proxyUser"),
	HTTPS_PROXY_PORT("https.proxyPort"),
	HTTPS_PROXY_PASSWORD("https.proxyPassword"),
	HTTPS_PROXY_NONPROXYHOSTS("https.nonProxyHosts"),
	LOAD_CACHE_FROM_DB("loadCacheFromDB"),
	SYNCH_ON_START("synchOnStart"),
	ODMS_DUMP_FILE_PATH("odmsDumpFilePath"),
	ODMS_DUMP_FILE_PREFIX("odmsDumpFilePrefix"),
	DUMP_FILE_PATH("dumpFilePath"),
	DUMP_FILE_NAME("dumpFileName"),
	DUMP_FORMAT("dumpFormat"),
	DUMP_PROFILE("dumpProfile"),
	DUMP_PERIOD("dumpPeriod"),
	DUMP_ZIP_FILE("dumpZipFile"),
	DUMP_ON_START("dumpOnStart"),
	ENABLE_RDF("enableRdf"),
	SESAME_REPO_NAME("sesameRepositoryName"),
	SESAME_SERVER_URI("sesameServerURI"),
	SESAME_ENDPOINT("sesameEndPoint"),
	WEB_CONNECTOR_DEFAULT_STOP_VALUES("webConnectorDefaultStopValues"),
	ENABLE_STATISTICS("enableStatistics"),
	ENABLE_IDM("idm.enable");

	private final String text;

	private ODFProperty(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
