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
package it.eng.idra.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import it.eng.idra.authentication.basic.LoggedUser;
import it.eng.idra.beans.*;
import it.eng.idra.beans.ckan.CKANErrorResponse;
import it.eng.idra.beans.ckan.CKANSuccessResponse;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATProperty;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueImage;
import it.eng.idra.beans.odms.ODMSCatalogueMessage;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import it.eng.idra.beans.search.DCATAPSearchResult;
import it.eng.idra.beans.search.SearchFacet;
import it.eng.idra.beans.search.SearchFilter;
import it.eng.idra.beans.search.SearchRequest;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.beans.search.SparqlSearchRequest;
import it.eng.idra.beans.spod.SPODDataset;
import it.eng.idra.beans.spod.SPODExtraDeserializer;
import it.eng.idra.beans.spod.SPODGroupDeserializer;
import it.eng.idra.beans.spod.SPODTagDeserializer;
import it.eng.idra.beans.statistics.KeywordStatistics;
import it.eng.idra.beans.statistics.KeywordStatisticsResult;
import it.eng.idra.beans.statistics.StatisticsRequest;
import it.eng.idra.statistics.PlatformStatistcs;

public final class GsonUtil {

	private static DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

	public static Type nodeType = new TypeToken<ODMSCatalogue>() {
	}.getType();

	public static Type nodeListType = new TypeToken<List<ODMSCatalogue>>() {
	}.getType();

	public static Type messagesType = new TypeToken<HashMap<Integer, Long>>() {
	}.getType();

	public static Type messageListType = new TypeToken<List<ODMSCatalogueMessage>>() {
	}.getType();

	public static Type stdListType = new TypeToken<List<DCTStandard>>() {
	}.getType();

	public static Type licenseType = new TypeToken<DCTLicenseDocument>() {
	}.getType();

	public static Type checksumType = new TypeToken<SPDXChecksum>() {
	}.getType();

	public static Type messageType = new TypeToken<ODMSCatalogueMessage>() {
	}.getType();

	public static Type configurationType = new TypeToken<HashMap<String, Object>>() {
	}.getType();

	public static Type prefixListType = new TypeToken<List<RdfPrefix>>() {
	}.getType();

	public static Type prefixType = new TypeToken<RdfPrefix>() {
	}.getType();

	public static Type userType = new TypeToken<User>() {
	}.getType();

	public static Type loggedUserType = new TypeToken<LoggedUser>() {
	}.getType();

	public static Type stringListType = new TypeToken<List<String>>() {
	}.getType();

	public static Type keywordStatisticsResultListType = new TypeToken<List<KeywordStatisticsResult>>() {
	}.getType();

	public static Type keywordStatisticsType = new TypeToken<KeywordStatistics>() {
	}.getType();

	public static Type statisticsRequestType = new TypeToken<StatisticsRequest>() {
	}.getType();

	public static Type logType = new TypeToken<Log>() {
	}.getType();

	public static Type logsListType = new TypeToken<List<Log>>() {
	}.getType();

	public static Type logRequestType = new TypeToken<LogsRequest>() {
	}.getType();

	public static Type logsRequestListType = new TypeToken<List<LogsRequest>>() {
	}.getType();

	public static Type searchFilterListType = new TypeToken<List<SearchFilter>>() {
	}.getType();

	public static Type searchRequestType = new TypeToken<SearchRequest>() {
	}.getType();

	public static Type searchResultType = new TypeToken<SearchResult>() {
	}.getType();

	public static Type dcatapSearchResultType = new TypeToken<DCATAPSearchResult>() {
	}.getType();

	public static Type datasetType = new TypeToken<DCATDataset>() {
	}.getType();
	
	public static Type spodDatasetType = new TypeToken<SPODDataset>() {
	}.getType();
	
	public static Type datasetListType = new TypeToken<List<DCATDataset>>() {
	}.getType();
	
	public static Type orionDistributionListType = new TypeToken<List<OrionDistributionConfig>>() {
	}.getType();
	
	public static Type orionDistributionType = new TypeToken<OrionDistributionConfig>() {
	}.getType();
	
	public static Type sparqlSearchRequestType = new TypeToken<SparqlSearchRequest>() {
	}.getType();

	public static Type facetsType = new TypeToken<SearchFacet>() {
	}.getType();

	public static Type facetsListType = new TypeToken<List<SearchFacet>>() {
	}.getType();

	public static Type errorResponseSetType = new TypeToken<Set<ErrorResponse>>() {
	}.getType();

	public static Type dataletType = new TypeToken<Datalet>() {
	}.getType();

	public static Type dataletListType = new TypeToken<List<Datalet>>() {
	}.getType();

	public static Type conceptListType = new TypeToken<List<SKOSConcept>>() {
	}.getType();

	public static Type conceptType = new TypeToken<SKOSConcept>() {
	}.getType();
	
	public static Type prefLabelListType = new TypeToken<List<SKOSPrefLabel>>() {
	}.getType();

	public static Type prefLabelType = new TypeToken<SKOSPrefLabel>() {
	}.getType();
	
	public static Type extraListType = new TypeToken<List<org.ckan.Extra>>() {
	}.getType();
	
	public static Type platformStatsType = new TypeToken<PlatformStatistcs>() {
	}.getType();
	
	public static Type ckanSuccType = new TypeToken<CKANSuccessResponse<Object>>() {
	}.getType();
	public static Type ckanErrType = new TypeToken<CKANErrorResponse>() {
	}.getType();
	
	private static GsonBuilder gsonBuilder = new GsonBuilder()
			.registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
				public ZonedDateTime deserialize(JsonElement jsonElement, Type type,
						JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
					return ZonedDateTime.from(dtFormatter.parse(jsonElement.getAsString()));
				}
			}).registerTypeAdapter(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
				@Override
				public JsonElement serialize(ZonedDateTime zonedDateTime, Type type,
						JsonSerializationContext jsonSerializationContext) {
					return new JsonPrimitive(dtFormatter.format(zonedDateTime.truncatedTo(ChronoUnit.SECONDS)));
				}
			})
			 .registerTypeAdapter(DCATProperty.class, new JsonSerializer<DCATProperty>() {
			 @Override
			 public JsonElement serialize(DCATProperty property, Type type,
			 JsonSerializationContext jsonSerializationContext) {
			 return new JsonPrimitive(property.getValue().toString());
			 }
			 })
			.registerTypeAdapter(ODMSCatalogue.class, new AnnotatedDeserializer<ODMSCatalogue>())
			.registerTypeAdapter(ConfigurationParameter.class, new AnnotatedDeserializer<ConfigurationParameter>())
			.registerTypeAdapter(RdfPrefix.class, new AnnotatedDeserializer<RdfPrefix>())
			.registerTypeAdapter(User.class, new AnnotatedDeserializer<User>())
			.registerTypeAdapter(PasswordChange.class, new AnnotatedDeserializer<PasswordChange>())
			.registerTypeAdapter(StatisticsRequest.class, new AnnotatedDeserializer<StatisticsRequest>())
			.registerTypeAdapter(LogsRequest.class, new AnnotatedDeserializer<LogsRequest>())
			.registerTypeAdapter(SearchRequest.class, new AnnotatedDeserializer<LogsRequest>())
			.registerTypeAdapter(SearchFilter.class, new AnnotatedDeserializer<LogsRequest>())
			.registerTypeAdapter(ErrorResponse.class, new AnnotatedDeserializer<ErrorResponse>())
			.registerTypeHierarchyAdapter(GregorianCalendar.class, new CalendarAdapter())
			.registerTypeAdapter(ODMSCatalogueImage.class, new ImageSerializer())
			
			.registerTypeAdapter(org.ckan.Tag.class, new SPODTagDeserializer())
			.registerTypeAdapter(org.ckan.Group.class, new SPODGroupDeserializer())
			.registerTypeAdapter(extraListType , new SPODExtraDeserializer());
	
	private static Gson gson = gsonBuilder.create();
	private static Gson gsonExcludeFields = gsonBuilder.excludeFieldsWithoutExposeAnnotation().create();

	public static <T> T json2Obj(String json, Class<T> t) throws GsonUtilException {
		T obj = null;
		try {
			obj = gson.fromJson(json, t);
		} catch (Exception e) {

			throw new GsonUtilException("JSON to OBJECT failed: " + e.getMessage());
		}
		return obj;
	}

	public static <T> T json2Obj(String json, Type t) throws GsonUtilException {

		T obj = null;
		try {
			obj = gson.fromJson(json, t);
		} catch (Exception e) {

			throw new GsonUtilException("JSON to OBJECT failed: " + e.getMessage());
		}
		return obj;
	}

	public static <T> String obj2Json(Object obj, Class<T> t) throws GsonUtilException {
		String json = null;

		try {
			json = gson.toJson(obj, t);
		} catch (Exception e) {
			throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
		}
		return json;
	}

	public static <T> String obj2Json(Object obj, Type t) throws GsonUtilException {
		String json = null;

		try {
			json = gson.toJson(obj, t);
		} catch (Exception e) {
			throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
		}
		return json;
	}
	
	public static <T> String obj2JsonWithExclude(Object obj, Class<T> t) throws GsonUtilException {
		String json = null;

		try {
			json = gsonExcludeFields.toJson(obj, t);
		} catch (Exception e) {
			throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
		}
		return json;
	}

	public static <T> String obj2JsonWithExclude(Object obj, Type t) throws GsonUtilException {
		String json = null;

		try {
			json = gsonExcludeFields.toJson(obj, t);
		} catch (Exception e) {
			throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
		}
		return json;
	}

	static class AnnotatedDeserializer<T> implements JsonDeserializer<T> {

		public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
			T pojo = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
				public ZonedDateTime deserialize(JsonElement jsonElement, Type type,
						JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
					DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);
					return ZonedDateTime.from(fmt.parse(jsonElement.getAsString()));
				}
			}).registerTypeAdapter(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
				@Override
				public JsonElement serialize(ZonedDateTime zonedDateTime, Type type,
						JsonSerializationContext jsonSerializationContext) {
					DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);
					return new JsonPrimitive(fmt.format(zonedDateTime.truncatedTo(ChronoUnit.SECONDS)));
				}
			}).create().fromJson(je, type);

			Field[] fields = pojo.getClass().getDeclaredFields();
			for (Field f : fields) {
				if (f.getAnnotation(JsonRequired.class) != null) {
					try {
						f.setAccessible(true);
						if (f.get(pojo) == null) {
							throw new JsonParseException("Missing field in JSON: " + f.getName());
						}
					} catch (IllegalArgumentException | IllegalAccessException ex) {
						throw new JsonParseException("There was an exception while deserializing the json object: "
								+ AnnotatedDeserializer.class.getName());
					}
				}
			}
			return pojo;

		}
	}	
}
