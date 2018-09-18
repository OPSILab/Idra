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
package it.eng.idra.utils.idm.fiware.configuration;

public enum IDMProperty {
	IDM_VERSION("idm.fiware.version"),
	@Deprecated
	IDM_FIWARE_PROTOCOL("idm.fiware.protocol"),
	@Deprecated
	IDM_FIWARE_HOST("idm.fiware.host"),
	IDM_FIWARE_PATH_BASE("idm.fiware.path.base"),
	IDM_FIWARE_PATH_TOKEN("idm.fiware.path.token"),
	IDM_FIWARE_PATH_USER("idm.fiware.path.user"),
	@Deprecated
	IDM_FIWARE_KEYSTONE_HOST("idm.fiware.keystone.host"),
	@Deprecated
	IDM_FIWARE_KEYSTONE_PORT("idm.fiware.keystone.port"),
	@Deprecated
	IDM_FIWARE_KEYSTONE_PATH_TOKENS("idm.fiware.keystone.path.tokens"),
	IDM_PROTOCOL_DEFAULT("idm.protocol.default"),
	IDM_PORT_DEFAULT("idm.port.default"),
	IDM_ADMIN_ROLE_NAME("idm.admin.role.name");
	
	private final String text;

	private IDMProperty(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}