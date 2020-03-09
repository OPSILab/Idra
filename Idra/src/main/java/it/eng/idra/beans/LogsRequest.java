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
package it.eng.idra.beans;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.logging.log4j.Level;

import it.eng.idra.utils.JsonRequired;

public class LogsRequest {

	@JsonRequired
	private List<String> levelList;
	@JsonRequired
	private ZonedDateTime startDate;
	@JsonRequired
	private ZonedDateTime endDate;

	public LogsRequest(List<String> levelList, ZonedDateTime startDate, ZonedDateTime endDate) {
		super();
		this.levelList = levelList;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public List<String> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<String> level) {
		this.levelList = level;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "LogsRequest [levelList=" + levelList + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	
}
