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
	IDRA_VERSION("idra.version"),
	IDRA_RELEASE_TIMESTAMP("idra.release.timestamp"),
	DB_HOST("idra.db.host"),
	DB_HOST_MIN("idra.db.host.min"),
	DB_NAME("idra.db.name"),
	DB_USERNAME("idra.db.user"),
	DB_PASSWORD("idra.db.password"),
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
	LOAD_CACHE_FROM_DB("idra.cache.loadfromdb"),
	SYNCH_ON_START("idra.synch.onstart"),
	ODMS_DUMP_FILE_PATH("idra.odms.dump.file.path"),
	ODMS_DUMP_FILE_PREFIX("idra.odms.dump.file.prefix"),
	DUMP_FILE_PATH("idra.dump.file.path"),
	DUMP_FILE_NAME("idra.dump.file.name"),
	DUMP_FORMAT("idra.dump.format"),
	DUMP_PROFILE("idra.dump.profile"),
	DUMP_PERIOD("idra.dump.period"),
	DUMP_ZIP_FILE("idra.dump.file.zip"),
	DUMP_ON_START("idra.dump.onstart"),
	ENABLE_RDF("idra.lod.enable"),
	SESAME_REPO_NAME("idra.lod.repo.name"),
	SESAME_SERVER_URI("idra.lod.server.uri"),
	SESAME_ENDPOINT("idra.lod.server.uri.query"),
	WEB_CONNECTOR_DEFAULT_STOP_VALUES("idra.scraper.defaultStopValues"),
	ENABLE_STATISTICS("idra.statistics.enable"),
	AUTHENTICATION_METHOD("idra.authentication.method"),
	ORION_FILE_DUMP_PATH("idra.orion.orionDumpFilePath"),
	ORION_INTERNAL_API("idra.orion.orionInternalAPI"),
	IDRA_CATALOGUE_BASEPATH("idra.catalogue.basepath"),
	PREVIEW_TIMEOUT("idra.preview.timeout"),
	PREVIEW_MAX_SIZE("idra.preview.fileSize");
	
	
	private final String text;

	private ODFProperty(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
