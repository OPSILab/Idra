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

public class SearchStatisticsResult {

	private int live;
	private int sparql;
	private int cache;
	// private int day;
	// private int month;
	// private int week;
	// private int year;

	private String startLabel;
	private String endLabel;

	public SearchStatisticsResult() {

	}

	public SearchStatisticsResult(int live, int cache, int sparql) {
		this.live = live;
		this.cache = cache;
		this.sparql = sparql;
	}

	public int getLive() {
		return live;
	}

	public void setLive(int live) {
		this.live = live;
	}

	public int getSparql() {
		return sparql;
	}

	public void setSparql(int sparql) {
		this.sparql = sparql;
	}

	public int getCache() {
		return cache;
	}

	public void setCache(int cache) {
		this.cache = cache;
	}

	// public int getDay() {
	// return day;
	// }
	//
	// public void setDay(int day) {
	// this.day = day;
	// }
	//
	// public int getMonth() {
	// return month;
	// }
	//
	// public void setMonth(int month) {
	// this.month = month;
	// }
	//
	// public int getWeek() {
	// return week;
	// }
	//
	// public void setWeek(int week) {
	// this.week = week;
	// }
	//
	// public int getYear() {
	// return year;
	// }
	//
	// public void setYear(int year) {
	// this.year = year;
	// }

	public String getStartLabel() {
		return startLabel;
	}

	public void setStartLabel(String startLabel) {
		this.startLabel = startLabel;
	}

	public String getEndLabel() {
		return endLabel;
	}

	public void setEndLabel(String endLabel) {
		this.endLabel = endLabel;
	}

	@Override
	public String toString() {
		return "SearchStatisticsResult [live=" + live + ", sparql=" + sparql + ", cache=" + cache + "]";
	}

}
