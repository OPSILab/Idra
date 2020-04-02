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
package it.eng.idra.management;

import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.statistics.AggregationLevelEnum;
import it.eng.idra.beans.statistics.KeywordStatistics;
import it.eng.idra.beans.statistics.KeywordStatisticsResult;
import it.eng.idra.beans.statistics.ODMSStatistics;
import it.eng.idra.beans.statistics.ODMSStatisticsResult;
import it.eng.idra.beans.statistics.SearchStatistics;
import it.eng.idra.beans.statistics.SearchStatisticsResult;
import it.eng.idra.utils.PropertyManager;


public class StatisticsManager {

	private static boolean enableStatistics;
	
	static {
		enableStatistics = Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.ENABLE_STATISTICS).trim());
	}
	
	// method to ODMS statistics
	public static void odmsStatistics(ODMSCatalogue node, int addedDataset, int updatedDataset, int deletedDataset,
			int addedRDF, int updatedRDF, int deletedRDF) {
		if(!enableStatistics) return;
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {

			ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
			int day = now.getDayOfMonth();
			int month = now.getMonthValue();
			// int week = g.get(g.WEEK_OF_MONTH);
			int year = now.getYear();

			ODMSStatistics odmsStat = manageBeansJpa.getODMSStatistics(node, year, month, day);

			if (odmsStat == null) {

				odmsStat = new ODMSStatistics(node.getId(), node.getName(), day, month, year, addedDataset,
						updatedDataset, deletedDataset, addedRDF, updatedRDF, deletedRDF);

				manageBeansJpa.persistODMSStatistic(odmsStat);

			} else {

				odmsStat.incAdded_datasets(addedDataset);
				odmsStat.incDeleted_datasets(deletedDataset);
				odmsStat.incUpdated_datasets(updatedDataset);
				odmsStat.incAdded_RDF(addedRDF);
				odmsStat.incDeleted_RDF(deletedRDF);
				odmsStat.incUpdated_RDF(updatedRDF);
				manageBeansJpa.updateODMSStatistics(odmsStat);
			}

		} finally {
			manageBeansJpa.jpaClose();
		}

	}

	public static void searchStatistics(String ip, String searchType) throws SQLException {
		if(!enableStatistics) return;
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			String country = manageBeansJpa.getCountryFromIp(ip);
			if (country.equalsIgnoreCase("")) {
				country = "Unknown";
			}
			GregorianCalendar g = new GregorianCalendar();
			int day = g.get(Calendar.DAY_OF_MONTH);
			int month = g.get(Calendar.MONTH);
			int week = g.get(Calendar.WEEK_OF_MONTH);
			int year = g.get(Calendar.YEAR);

			SearchStatistics existStat = manageBeansJpa.getCountryStatistics(country, searchType, day, month, year);

			if (existStat == null) {
				existStat = new SearchStatistics(country, 0, 0, 0, day, week, month, year);

				switch (searchType) {
				case "live":
					existStat.setLive(1);
					break;
				case "cache":
					existStat.setCache(1);
					break;
				case "sparql":
					existStat.setSparql(1);
					break;
				default:
					break;
				}

				manageBeansJpa.persistCountryStatistic(existStat);
			} else {

				switch (searchType) {
				case "live":
					existStat.incLive(1);
					break;
				case "cache":
					existStat.incCache(1);
					break;
				case "sparql":
					existStat.incSparql(1);
					break;
				default:
					break;
				}
				manageBeansJpa.updateCountryStatistics(existStat);
			}
		} finally {
			manageBeansJpa.jpaClose();
		}

	}

	private static void storeKeywordStatisticsToDB(String keyword) throws SQLException {
		if(!enableStatistics) return;
	}

	public static List<String> getAllCountries() throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getAllCountries();
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static DateTime getMinDateSearchStatistics() throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getMinDateSearchStatistics();
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static DateTime getMinDateNodesStatistics() throws SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getMinDateNodesStatistics();
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static SearchStatisticsResult getAggregateValues(String[] countries, AggregationLevelEnum aggregationLevel,
			int day, int month, int year) throws SQLException {

		String select = " SELECT ";
		String from = " FROM search_statistics ";
		String where = " WHERE ";

		String groupBy = " group by " + aggregationLevel + " ";

		select += " sum(live) as live , sum(cache) as cache , sum(sparql) as sparql,  ";

		if (countries.length != 0) {
			for (int i = 0; i < countries.length; i++) {
				if (i == 0) {
					where += " ( ";
				}

				where += " country='" + countries[i] + "' ";

				if (i == countries.length - 1) {
					where += " ) AND ";
				} else {
					where += " OR ";
				}
			}
		}

		switch (aggregationLevel.name()) {
		case "day":
			select += " day, month, year ";
			where += "  day = " + day + " AND " + "  month = " + month + " AND " + "  year = " + year + "  ";
			break;
		case "week":
			select += " week, month, year ";
			where += "  day = " + day + " AND " + "  month = " + month + " AND " + "  year = " + year + "  ";
			break;
		case "month":
			select += " month, year ";
			where += "  month = " + month + " AND " + "  year = " + year + "  ";
			break;
		default:
			select += " year ";
			where += "  year = " + year + "  ";
			break;
		}
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getSearchStatistics(select + from + where + groupBy);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static String getMonthForInt(int num) {
		String month = "wrong";
		DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
		String[] months = dfs.getMonths();
		num -= 1;
		if (num >= 0 && num <= 11) {
			month = months[num];
		}
		return WordUtils.capitalize(month.substring(0, 3));
	}

	// TODO sostituire JSONObject con bean per le statistiche aggregate
	// per più nodi
	public static JSONObject getSearchStatistics(String[] countries, AggregationLevelEnum aggregationLevel,
			ZonedDateTime startDate, ZonedDateTime endDate) throws ParseException, SQLException {

		SearchStatisticsResult stats = null;
		JSONObject result = new JSONObject();
		JSONArray sparql = new JSONArray();
		JSONArray live = new JSONArray();
		JSONArray cache = new JSONArray();
		JSONArray label = new JSONArray();
		JSONObject tmp = new JSONObject();

		while (startDate.isBefore(endDate)) {

			stats = getAggregateValues(countries, aggregationLevel, startDate.getDayOfMonth(),
					startDate.getMonthValue() - 1, startDate.getYear());

			sparql.put(stats.getSparql());
			live.put(stats.getLive());
			cache.put(stats.getCache());

			switch (aggregationLevel.name()) {
			case "day":
				label.put(startDate.getYear() + "-" + getMonthForInt(startDate.getMonthValue()) + "-"
						+ startDate.getDayOfMonth());
				startDate = startDate.plusDays(1);
				break;
			case "week":
				label.put(startDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + "-"
						+ getMonthForInt(startDate.getMonthValue()) + "-" + startDate.getDayOfMonth());
				startDate = startDate.plusWeeks(1);
				break;
			case "month":
				label.put(startDate.getYear() + "-" + getMonthForInt(startDate.getMonthValue()));
				startDate = startDate.plusMonths(1);
				break;
			default:
				label.put(startDate.getYear());
				startDate = startDate.plusYears(1);
				break;
			}

		}

		if (aggregationLevel.equals("month")) {

			if (endDate.getDayOfMonth() <= startDate.getDayOfMonth()) {

				stats = getAggregateValues(countries, aggregationLevel, endDate.getDayOfMonth(),
						endDate.getMonthValue() - 1, endDate.getYear());

				sparql.put(stats.getSparql());
				live.put(stats.getLive());
				cache.put(stats.getCache());

				label.put(endDate.getYear() + "-" + getMonthForInt(endDate.getMonthValue()));
			}
		} else if (aggregationLevel.equals("year")) {
			if (endDate.getMonthValue() <= startDate.getMonthValue()) {

				stats = getAggregateValues(countries, aggregationLevel, endDate.getDayOfMonth(),
						endDate.getMonthValue() - 1, endDate.getYear());

				sparql.put(stats.getSparql());
				live.put(stats.getLive());
				cache.put(stats.getCache());

				label.put(endDate.getYear());
			}

		}

		result.put("sparql", sparql);
		result.put("live", live);
		result.put("cache", cache);
		result.put("label", label);

		return result;
	}

	public static SearchStatisticsResult getDetailsValue(String country, AggregationLevelEnum aggregationLevel,
			ZonedDateTime date) throws SQLException {
		String select = " SELECT ";
		String from = " FROM search_statistics ";
		String where = " WHERE country='" + country + "' AND ";

		String groupBy = " group by " + aggregationLevel + " ";

		select += " sum(live) as live , sum(cache) as cache , sum(sparql) as sparql  ";

		switch (aggregationLevel.name()) {
		case "day":
			where += "  day = " + date.getDayOfMonth() + " AND " + "  month = " + date.getMonthValue() + " AND "
					+ "  year = " + date.getYear() + "  ";
			break;
		case "week":
			where += "  day = " + date.getDayOfMonth() + " AND " + "  month = " + date.getMonthValue() + " AND "
					+ "  year = " + date.getYear() + "  ";
			break;
		case "month":
			where += "  month = " + date.getMonthValue() + " AND " + "  year = " + date.getYear() + "  ";
			break;
		default:
			where += "  year = " + date.getYear() + "  ";
			break;
		}

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getSearchStatistics(select + from + where + groupBy);
		} finally {
			manageBeansJpa.jpaClose();
		}

	}

	public static JSONObject getSearchStatisticsDetails(String[] countries, AggregationLevelEnum aggregationLevel,
			ZonedDateTime date) throws ParseException, SQLException {
		SearchStatisticsResult stats = null;
		JSONObject res = new JSONObject();
		JSONArray sparql = new JSONArray();
		JSONArray live = new JSONArray();
		JSONArray cache = new JSONArray();
		JSONArray label = new JSONArray();

		for (int i = 0; i < countries.length; i++) {
			stats = getDetailsValue(countries[i], aggregationLevel, date);
			sparql.put(stats.getSparql());
			live.put(stats.getLive());
			cache.put(stats.getCache());
			label.put(countries[i]);
		}

		res.put("sparql", sparql);
		res.put("live", live);
		res.put("cache", cache);
		res.put("label", label);

		return res;
	}

	public static ODMSStatisticsResult getAggregateValuesNODES(String[] nodeID, AggregationLevelEnum aggregationLevel,
			int day, int month, int year) throws SQLException {

		String select = " SELECT ";
		String from = " FROM odms_statistics ";
		String where = " WHERE ";

		String groupBy = " group by " + aggregationLevel + " ";

		select += " sum(added_datasets) as added , sum(deleted_datasets) as deleted , sum(updated_datasets) as updated,"
				+ " sum(added_RDF) as added_RDF , sum(deleted_RDF) as deleted_RDF , sum(updated_RDF) as updated_RDF,  ";

		if (nodeID.length != 0) {
			for (int i = 0; i < nodeID.length; i++) {
				if (i == 0) {
					where += " ( ";
				}

				where += " nodeID='" + nodeID[i] + "' ";

				if (i == nodeID.length - 1) {
					where += " ) AND ";
				} else {
					where += " OR ";
				}
			}
		}

		switch (aggregationLevel.name()) {
		case "day":
			select += " day, month, year ";
			where += "  day = " + day + " AND " + "  month = " + month + " AND " + "  year = " + year + "  ";
			break;
		case "week":
			select += " week, month, year ";
			where += "  day = " + day + " AND " + "  month = " + month + " AND " + "  year = " + year + "  ";
			break;
		case "month":
			select += " month, year ";
			where += "  month = " + month + " AND " + "  year = " + year + "  ";
			break;
		default:
			select += " year ";
			where += "  year = " + year + "  ";
			break;
		}

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getODMSCataloguesStatistics(select + from + where + groupBy);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	// TODO sostituire JSONObject con bean per le statistiche aggregate
	// per più nodi
	public static JSONObject getNodesStatistics(String[] nodesID, AggregationLevelEnum aggregationLevel,
			ZonedDateTime startDate, ZonedDateTime endDate) throws ParseException, SQLException {

		ODMSStatisticsResult stats = null;
		JSONObject result = new JSONObject();
		JSONArray added = new JSONArray();
		JSONArray deleted = new JSONArray();
		JSONArray updated = new JSONArray();
		JSONArray added_RDF = new JSONArray();
		JSONArray deleted_RDF = new JSONArray();
		JSONArray updated_RDF = new JSONArray();
		JSONArray label = new JSONArray();
		JSONObject tmp = new JSONObject();

		while (startDate.isBefore(endDate)) {

			stats = getAggregateValuesNODES(nodesID, aggregationLevel, startDate.getDayOfMonth(),
					startDate.getMonthValue() - 1, startDate.getYear());

			added.put(stats.getAdded());
			deleted.put(stats.getDeleted());
			updated.put(stats.getUpdated());
			added_RDF.put(stats.getAdded_RDF());
			deleted_RDF.put(stats.getDeleted_RDF());
			updated_RDF.put(stats.getUpdated_RDF());

			switch (aggregationLevel.name()) {
			case "day":
				label.put(startDate.getYear() + "-" + getMonthForInt(startDate.getMonthValue()) + "-"
						+ startDate.getDayOfMonth());
				startDate = startDate.plusDays(1);
				break;
			case "week":
				label.put(startDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + "-"
						+ getMonthForInt(startDate.getMonthValue()) + "-" + startDate.getDayOfMonth());
				startDate = startDate.plusWeeks(1);
				break;
			case "month":
				label.put(startDate.getYear() + "-" + getMonthForInt(startDate.getMonthValue()));
				startDate = startDate.plusMonths(1);
				break;
			default:
				label.put(startDate.getYear());
				startDate = startDate.plusYears(1);
				break;
			}
		}

		if (aggregationLevel.equals("month")) {

			if (endDate.getDayOfMonth() <= startDate.getDayOfMonth()) {

				stats = getAggregateValuesNODES(nodesID, aggregationLevel, endDate.getDayOfMonth(),
						endDate.getMonthValue() - 1, endDate.getYear());

				added.put(stats.getAdded());
				deleted.put(stats.getDeleted());
				updated.put(stats.getUpdated());
				added_RDF.put(stats.getAdded_RDF());
				deleted_RDF.put(stats.getDeleted_RDF());
				updated_RDF.put(stats.getUpdated_RDF());

				label.put(endDate.getYear() + "-" + getMonthForInt(endDate.getMonthValue()));
			}
		} else if (aggregationLevel.equals("year")) {
			if (endDate.getMonthValue() <= startDate.getMonthValue()) {

				stats = getAggregateValuesNODES(nodesID, aggregationLevel, endDate.getDayOfMonth(),
						endDate.getMonthValue() - 1, endDate.getYear());

				added.put(stats.getAdded());
				deleted.put(stats.getDeleted());
				updated.put(stats.getUpdated());
				added_RDF.put(stats.getAdded_RDF());
				deleted_RDF.put(stats.getDeleted_RDF());
				updated_RDF.put(stats.getUpdated_RDF());
				label.put(endDate.getYear());
			}

		}

		result.put("added", added);
		result.put("deleted", deleted);
		result.put("updated", updated);
		result.put("added_RDF", added_RDF);
		result.put("deleted_RDF", deleted_RDF);
		result.put("updated_RDF", updated_RDF);
		result.put("label", label);

		return result;

	}

	public static ODMSStatisticsResult getDetailsValueNodes(String nodeID, AggregationLevelEnum aggregationLevel,
			ZonedDateTime date) throws SQLException {
		String select = " SELECT ";
		String from = " FROM odms_statistics ";
		String where = " WHERE nodeID=" + nodeID + " AND ";

		String groupBy = " group by " + aggregationLevel + " ";

		select += " sum(added_datasets) as added , sum(deleted_datasets) as deleted , sum(updated_datasets) as updated,"
				+ " sum(added_RDF) as added_RDF , sum(deleted_RDF) as deleted_RDF , sum(updated_RDF) as updated_RDF,  ";

		switch (aggregationLevel.name()) {
		case "day":
			where += "  day = " + date.getDayOfMonth() + " AND " + "  month = " + date.getMonthValue() + " AND "
					+ "  year = " + date.getYear() + "  ";
			break;
		case "week":
			where += "  day = " + date.getDayOfMonth() + " AND " + "  month = " + date.getMonthValue() + " AND "
					+ "  year = " + date.getYear() + "  ";
			break;
		case "month":
			where += "  month = " + date.getDayOfMonth() + " AND " + "  year = " + date.getYear() + "  ";
			break;
		default:
			where += "  year = " + date.getYear() + "  ";
			break;
		}

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getODMSCataloguesStatistics(select + from + where + groupBy);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static JSONObject getNodeStatisticsDetails(String[] nodesID, AggregationLevelEnum aggregationLevel,
			ZonedDateTime date) throws ParseException, SQLException {

		JSONObject result = new JSONObject();
		JSONArray added = new JSONArray();
		JSONArray deleted = new JSONArray();
		JSONArray updated = new JSONArray();
		JSONArray added_RDF = new JSONArray();
		JSONArray deleted_RDF = new JSONArray();
		JSONArray updated_RDF = new JSONArray();
		JSONArray label = new JSONArray();

		for (int i = 0; i < nodesID.length; i++) {
			ODMSStatisticsResult stats = getDetailsValueNodes(nodesID[i], aggregationLevel, date);
			added.put(stats.getAdded());
			deleted.put(stats.getDeleted());
			updated.put(stats.getUpdated());
			added_RDF.put(stats.getAdded_RDF());
			deleted_RDF.put(stats.getDeleted_RDF());
			updated_RDF.put(stats.getUpdated_RDF());
			label.put(nodesID[i]);
		}

		result.put("added", added);
		result.put("deleted", deleted);
		result.put("updated", updated);
		result.put("added_RDF", added_RDF);
		result.put("deleted_RDF", deleted_RDF);
		result.put("updated_RDF", updated_RDF);

		result.put("label", label);

		return result;
	}

	public static List<KeywordStatisticsResult> getKeywordStatistics() throws NullPointerException, SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getPercentageKeyword();
		} finally {
			manageBeansJpa.jpaClose();
		}

	}

	/**
	 * Store the keywords to Statistics DB
	 * 
	 * @param value
	 *            The string from which to extract the keywords to be added to
	 *            Statistics
	 * @returns void
	 */
	public static void storeKeywordsStatistic(String value) {
		if(!enableStatistics) return;
		Matcher matcher = Pattern.compile("\\s*([a-zA-Z0-9]+)\\s*").matcher((String) value);

		while (matcher.find()) {
			try {

				PersistenceManager manageBeansJpa = new PersistenceManager();
				try {
					KeywordStatistics stats = manageBeansJpa.getKeywordStatistics(matcher.group(1));

					if (stats == null) {
						stats = new KeywordStatistics(matcher.group(1), 1);
						manageBeansJpa.persistKeywordStatistics(stats);
					} else {
						stats.incCounter();
						manageBeansJpa.updateKeywordStatistics(stats);
					}
				} finally {
					manageBeansJpa.jpaClose();
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
