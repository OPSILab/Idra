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
package it.eng.idraportal.idm.configuration;

public enum IDMProperty {
	IDM_VERSION("idm.fiware.version"),
	IDM_PROTOCOL("idm.protocol"),
	IDM_HOST("idm.host"),
	IDM_ADMIN_ROLE_NAME("idm.admin.role.name"),
	IDM_CLIENT_ID("idm.client.id"),
	IDM_CLIENT_SECRET("idm.client.secret"),
	IDM_REDIRECT_URI("idm.redirecturi"),
	IDM_PATH_BASE("idm.path.base"),
	IDM_PATH_TOKEN("idm.path.token"),
	IDM_PATH_USER("idm.path.user"),
	IDM_FIWARE_KEYSTONE_HOST("idm.fiware.keystone.host"),
	IDM_FIWARE_KEYSTONE_PORT("idm.fiware.keystone.port"),
	IDM_FIWARE_KEYSTONE_PATH_TOKENS("idm.fiware.keystone.path.tokens");
	

	private final String text;

	private IDMProperty(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}