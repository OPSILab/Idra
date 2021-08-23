/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.authentication.fiware.configuration;

// TODO: Auto-generated Javadoc
/**
 * The Enum IdmProperty.
 */
public enum IdmProperty {

  /** The idm version. */
  IDM_VERSION("idm.fiware.version"),
  /** The idm protocol. */
  IDM_PROTOCOL("idm.protocol"),
  /** The idm host. */
  IDM_HOST("idm.host"),

  /** The idm admin role name. */
  IDM_ADMIN_ROLE_NAME("idm.admin.role.name"),
  // IDM_PROTOCOL_DEFAULT("idm.protocol.default"),
  /** The idm client id. */
  // IDM_PORT_DEFAULT("idm.port.default"),
  IDM_CLIENT_ID("idm.client.id"),

  /** The idm client secret. */
  IDM_CLIENT_SECRET("idm.client.secret"),
  /** The idm redirect uri. */
  IDM_REDIRECT_URI("idm.redirecturi"),

  /** The idm logout callback. */
  IDM_LOGOUT_CALLBACK("idm.logout.callback"),

  /** The idm path base. */
  IDM_PATH_BASE("idm.path.base"),
  /** The idm path token. */
  IDM_PATH_TOKEN("idm.path.token"),

  /** The idm path user. */
  IDM_PATH_USER("idm.path.user"),
  /** The idm fiware keystone host. */
  IDM_FIWARE_KEYSTONE_HOST("idm.fiware.keystone.host"),

  /** The idm fiware keystone port. */
  IDM_FIWARE_KEYSTONE_PORT("idm.fiware.keystone.port"),

  /** The idm fiware keystone path tokens. */
  IDM_FIWARE_KEYSTONE_PATH_TOKENS("idm.fiware.keystone.path.tokens");

  /** The text. */
  private final String text;

  /**
   * Instantiates a new idm property.
   *
   * @param text the text
   */
  private IdmProperty(final String text) {
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
