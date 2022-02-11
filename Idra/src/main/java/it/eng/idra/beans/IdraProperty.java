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

package it.eng.idra.beans;

// TODO: Auto-generated Javadoc
/**
 * The Enum IdraProperty.
 */
public enum IdraProperty {

  /** The idra version. */
  IDRA_VERSION("idra.version"),

  /** The idra release timestamp. */
  IDRA_RELEASE_TIMESTAMP("idra.release.timestamp"),
  /** The db host. */
  DB_HOST("idra.db.host"),

  /** The db host min. */
  DB_HOST_MIN("idra.db.host.min"),
  /** The db name. */
  DB_NAME("idra.db.name"),
  /** The db username. */
  DB_USERNAME("idra.db.user"),

  /** The db password. */
  DB_PASSWORD("idra.db.password"),

  /** The http proxy enabled. */
  HTTP_PROXY_ENABLED("http.proxyEnabled"),
  /** The http proxy host. */
  HTTP_PROXY_HOST("http.proxyHost"),

  /** The http proxy user. */
  HTTP_PROXY_USER("http.proxyUser"),

  /** The http proxy port. */
  HTTP_PROXY_PORT("http.proxyPort"),

  /** The http proxy password. */
  HTTP_PROXY_PASSWORD("http.proxyPassword"),

  /** The http proxy nonproxyhosts. */
  HTTP_PROXY_NONPROXYHOSTS("http.nonProxyHosts"),
  /** The https proxy host. */
  HTTPS_PROXY_HOST("https.proxyHost"),

  /** The https proxy user. */
  HTTPS_PROXY_USER("https.proxyUser"),

  /** The https proxy port. */
  HTTPS_PROXY_PORT("https.proxyPort"),
  /** The https proxy password. */
  HTTPS_PROXY_PASSWORD("https.proxyPassword"),

  /** The https proxy nonproxyhosts. */
  HTTPS_PROXY_NONPROXYHOSTS("https.nonProxyHosts"),
  /** The load cache from db. */
  LOAD_CACHE_FROM_DB("idra.cache.loadfromdb"),

  /** The synch on start. */
  SYNCH_ON_START("idra.synch.onstart"),
  /** The odms dump file path. */
  ODMS_DUMP_FILE_PATH("idra.odms.dump.file.path"),

  /** The odms dump file prefix. */
  ODMS_DUMP_FILE_PREFIX("idra.odms.dump.file.prefix"),
  /** The dump file path. */
  DUMP_FILE_PATH("idra.dump.file.path"),

  /** The dump file name. */
  DUMP_FILE_NAME("idra.dump.file.name"),

  /** The dump format. */
  DUMP_FORMAT("idra.dump.format"),
  /** The dump profile. */
  DUMP_PROFILE("idra.dump.profile"),

  /** The dump period. */
  DUMP_PERIOD("idra.dump.period"),

  /** The dump zip file. */
  DUMP_ZIP_FILE("idra.dump.file.zip"),
  /** The dump on start. */
  DUMP_ON_START("idra.dump.onstart"),

  /** The enable rdf. */
  ENABLE_RDF("idra.lod.enable"),

  /** The sesame repo name. */
  SESAME_REPO_NAME("idra.lod.repo.name"),
  /** The sesame server uri. */
  SESAME_SERVER_URI("idra.lod.server.uri"),

  /** The sesame endpoint. */
  SESAME_ENDPOINT("idra.lod.server.uri.query"),

  /** The web connector default stop values. */
  WEB_CONNECTOR_DEFAULT_STOP_VALUES("idra.scraper.defaultStopValues"),

  /** The enable statistics. */
  ENABLE_STATISTICS("idra.statistics.enable"),
  /** The authentication method. */
  AUTHENTICATION_METHOD("idra.authentication.method"),

  /** The orion file dump path. */
  ORION_FILE_DUMP_PATH("idra.orion.orionDumpFilePath"),

  /** The orion internal api. */
  ORION_INTERNAL_API("idra.orion.orionInternalAPI"),
  
  /** The context broker manager url. */
  ORION_MANAGER_URL("idra.orion.manager.url"),

  /** The idra catalogue basepath. */
  IDRA_CATALOGUE_BASEPATH("idra.catalogue.basepath"),

  /** The web scraper pagination retry num. */
  WEB_SCRAPER_PAGINATION_RETRY_NUM("idra.scraper.pagination.retry"),

  /** The web scraper page retry num. */
  WEB_SCRAPER_PAGE_RETRY_NUM("idra.scraper.page.retry"),

  /** The web scraper range retry num. */
  WEB_SCRAPER_RANGE_RETRY_NUM("idra.scraper.range.retry"),

  /** The web scraper range scale num. */
  WEB_SCRAPER_RANGE_SCALE_NUM("idra.scraper.range.scale"),

  /** The web scraper dataset retry num. */
  WEB_SCRAPER_DATASET_RETRY_NUM("idra.scraper.dataset.retry"),

  /** The web scraper page timeout. */
  WEB_SCRAPER_PAGE_TIMEOUT("idra.scraper.page.timeout"),

  /** The web scraper dataset timeout. */
  WEB_SCRAPER_DATASET_TIMEOUT("idra.scraper.dataset.timeout"),

  /** The web scraper global timeout. */
  WEB_SCRAPER_GLOBAL_TIMEOUT("idra.scraper.global.timeout"),

  /** The web scraper global throttiling. */
  WEB_SCRAPER_GLOBAL_THROTTILING("idra.scraper.throttling"),

  /** The preview timeout. */
  PREVIEW_TIMEOUT("idra.preview.timeout"),
  /** The preview max size. */
  PREVIEW_MAX_SIZE("idra.preview.fileSize"),
  /** The server base url. */
  IDRA_SERVER_BASEURL("idra.server.baseurl");

  /** The text. */
  private final String text;

  /**
   * Instantiates a new idra property.
   *
   * @param text the text
   */
  private IdraProperty(final String text) {
    this.text = text;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
}
