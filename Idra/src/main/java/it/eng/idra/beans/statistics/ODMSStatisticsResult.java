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
package it.eng.idra.beans.statistics;

public class ODMSStatisticsResult {

	private int added;
	private int deleted;
	private int updated;
	private int added_RDF;
	private int deleted_RDF;
	private int updated_RDF;
//	private int day;
//	private int month;
//	private int week;
//	private int year;
	
	public ODMSStatisticsResult(){
		
	}

	public ODMSStatisticsResult(int added, int deleted,int updated,int added_RDF, int deleted_RDF,int updated_RDF){
		this.added=added;
		this.deleted=deleted;
		this.updated=updated;
		this.added_RDF=added_RDF;
		this.deleted_RDF=deleted_RDF;
		this.updated_RDF=updated_RDF;
	}
	
	public int getAdded() {
		return added;
	}

	public void setAdded(int added) {
		this.added = added;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public int getUpdated() {
		return updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}

	public int getAdded_RDF() {
		return added_RDF;
	}

	public void setAdded_RDF(int added_RDF) {
		this.added_RDF = added_RDF;
	}

	public int getDeleted_RDF() {
		return deleted_RDF;
	}

	public void setDeleted_RDF(int deleted_RDF) {
		this.deleted_RDF = deleted_RDF;
	}

	public int getUpdated_RDF() {
		return updated_RDF;
	}

	public void setUpdated_RDF(int updated_RDF) {
		this.updated_RDF = updated_RDF;
	}

	@Override
	public String toString() {
		return "ODMSStatisticsResult [added=" + added + ", deleted=" + deleted + ", updated=" + updated
				+ ", added_RDF=" + added_RDF + ", deleted_RDF=" + deleted_RDF + ", updated_RDF=" + updated_RDF + "]";
	}

	
	
//	public int getDay() {
//		return day;
//	}
//
//	public void setDay(int day) {
//		this.day = day;
//	}
//
//	public int getMonth() {
//		return month;
//	}
//
//	public void setMonth(int month) {
//		this.month = month;
//	}
//
//	public int getWeek() {
//		return week;
//	}
//
//	public void setWeek(int week) {
//		this.week = week;
//	}
//
//	public int getYear() {
//		return year;
//	}
//
//	public void setYear(int year) {
//		this.year = year;
//	}
	 
	
	
}
