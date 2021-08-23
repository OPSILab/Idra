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

package it.eng.idra.utils;

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
import it.eng.idra.beans.ConfigurationParameter;
import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.LogsRequest;
import it.eng.idra.beans.PasswordChange;
import it.eng.idra.beans.RdfPrefix;
import it.eng.idra.beans.RemoteCatalogue;
import it.eng.idra.beans.User;
import it.eng.idra.beans.ckan.CkanErrorResponse;
import it.eng.idra.beans.ckan.CkanSuccessResponse;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueImage;
import it.eng.idra.beans.odms.OdmsCatalogueMessage;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import it.eng.idra.beans.search.DcatApSearchResult;
import it.eng.idra.beans.search.SearchFacet;
import it.eng.idra.beans.search.SearchFilter;
import it.eng.idra.beans.search.SearchRequest;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.beans.search.SparqlSearchRequest;
import it.eng.idra.beans.spod.SpodDataset;
import it.eng.idra.beans.spod.SpodExtraDeserializer;
import it.eng.idra.beans.spod.SpodGroupDeserializer;
import it.eng.idra.beans.spod.SpodTagDeserializer;
import it.eng.idra.beans.statistics.KeywordStatistics;
import it.eng.idra.beans.statistics.KeywordStatisticsResult;
import it.eng.idra.beans.statistics.StatisticsRequest;
import it.eng.idra.statistics.PlatformStatistcs;
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

// TODO: Auto-generated Javadoc
/**
 * The Class GsonUtil.
 */
public final class GsonUtil {

  /** The dt formatter. */
  private static DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
      .withZone(ZoneOffset.UTC);

  /** The node type. */
  public static Type nodeType = new TypeToken<OdmsCatalogue>() {
  }.getType();

  /** The node list type. */
  public static Type nodeListType = new TypeToken<List<OdmsCatalogue>>() {
  }.getType();

  /** The messages type. */
  public static Type messagesType = new TypeToken<HashMap<Integer, Long>>() {
  }.getType();

  /** The message list type. */
  public static Type messageListType = new TypeToken<List<OdmsCatalogueMessage>>() {
  }.getType();

  /** The std list type. */
  public static Type stdListType = new TypeToken<List<DctStandard>>() {
  }.getType();

  /** The license type. */
  public static Type licenseType = new TypeToken<DctLicenseDocument>() {
  }.getType();

  /** The checksum type. */
  public static Type checksumType = new TypeToken<SpdxChecksum>() {
  }.getType();

  /** The message type. */
  public static Type messageType = new TypeToken<OdmsCatalogueMessage>() {
  }.getType();

  /** The configuration type. */
  public static Type configurationType = new TypeToken<HashMap<String, Object>>() {
  }.getType();

  /** The prefix list type. */
  public static Type prefixListType = new TypeToken<List<RdfPrefix>>() {
  }.getType();

  /** The prefix type. */
  public static Type prefixType = new TypeToken<RdfPrefix>() {
  }.getType();

  /** The rem cat type. */
  public static Type remCatType = new TypeToken<RemoteCatalogue>() {
  }.getType();

  /** The rem cat list type. */
  public static Type remCatListType = new TypeToken<List<RemoteCatalogue>>() {
  }.getType();

  /** The user type. */
  public static Type userType = new TypeToken<User>() {
  }.getType();

  /** The logged user type. */
  public static Type loggedUserType = new TypeToken<LoggedUser>() {
  }.getType();

  /** The string list type. */
  public static Type stringListType = new TypeToken<List<String>>() {
  }.getType();

  /** The keyword statistics result list type. */
  public static Type keywordStatisticsResultListType = 
      new TypeToken<List<KeywordStatisticsResult>>() {}.getType();

  /** The keyword statistics type. */
  public static Type keywordStatisticsType = new TypeToken<KeywordStatistics>() {
  }.getType();

  /** The statistics request type. */
  public static Type statisticsRequestType = new TypeToken<StatisticsRequest>() {
  }.getType();

  /** The log type. */
  public static Type logType = new TypeToken<Log>() {
  }.getType();

  /** The logs list type. */
  public static Type logsListType = new TypeToken<List<Log>>() {
  }.getType();

  /** The log request type. */
  public static Type logRequestType = new TypeToken<LogsRequest>() {
  }.getType();

  /** The logs request list type. */
  public static Type logsRequestListType = new TypeToken<List<LogsRequest>>() {
  }.getType();

  /** The search filter list type. */
  public static Type searchFilterListType = new TypeToken<List<SearchFilter>>() {
  }.getType();

  /** The search request type. */
  public static Type searchRequestType = new TypeToken<SearchRequest>() {
  }.getType();

  /** The search result type. */
  public static Type searchResultType = new TypeToken<SearchResult>() {
  }.getType();

  /** The dcatap search result type. */
  public static Type dcatapSearchResultType = new TypeToken<DcatApSearchResult>() {
  }.getType();

  /** The dataset type. */
  public static Type datasetType = new TypeToken<DcatDataset>() {
  }.getType();

  /** The distribution type. */
  public static Type distributionType = new TypeToken<DcatDistribution>() {
  }.getType();

  /** The distribution list type. */
  public static Type distributionListType = new TypeToken<List<DcatDistribution>>() {
  }.getType();

  /** The spod dataset type. */
  public static Type spodDatasetType = new TypeToken<SpodDataset>() {
  }.getType();

  /** The dataset list type. */
  public static Type datasetListType = new TypeToken<List<DcatDataset>>() {
  }.getType();

  /** The orion distribution list type. */
  public static Type orionDistributionListType = new TypeToken<List<OrionDistributionConfig>>() {
  }.getType();

  /** The orion distribution type. */
  public static Type orionDistributionType = new TypeToken<OrionDistributionConfig>() {
  }.getType();

  /** The sparql search request type. */
  public static Type sparqlSearchRequestType = new TypeToken<SparqlSearchRequest>() {
  }.getType();

  /** The facets type. */
  public static Type facetsType = new TypeToken<SearchFacet>() {
  }.getType();

  /** The facets list type. */
  public static Type facetsListType = new TypeToken<List<SearchFacet>>() {
  }.getType();

  /** The error response set type. */
  public static Type errorResponseSetType = new TypeToken<Set<ErrorResponse>>() {
  }.getType();

  /** The datalet type. */
  public static Type dataletType = new TypeToken<Datalet>() {
  }.getType();

  /** The datalet list type. */
  public static Type dataletListType = new TypeToken<List<Datalet>>() {
  }.getType();

  /** The concept list type. */
  public static Type conceptListType = new TypeToken<List<SkosConcept>>() {
  }.getType();

  /** The concept type. */
  public static Type conceptType = new TypeToken<SkosConcept>() {
  }.getType();

  /** The pref label list type. */
  public static Type prefLabelListType = new TypeToken<List<SkosPrefLabel>>() {
  }.getType();

  /** The pref label type. */
  public static Type prefLabelType = new TypeToken<SkosPrefLabel>() {
  }.getType();

  /** The extra list type. */
  public static Type extraListType = new TypeToken<List<org.ckan.Extra>>() {
  }.getType();

  /** The platform stats type. */
  public static Type platformStatsType = new TypeToken<PlatformStatistcs>() {
  }.getType();

  /** The ckan succ type. */
  public static Type ckanSuccType = new TypeToken<CkanSuccessResponse<Object>>() {
  }.getType();

  /** The ckan err type. */
  public static Type ckanErrType = new TypeToken<CkanErrorResponse>() {
  }.getType();

  /** The gson builder. */
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
          return new JsonPrimitive(
              dtFormatter.format(zonedDateTime.truncatedTo(ChronoUnit.SECONDS)));
        }
      }).registerTypeAdapter(DcatProperty.class, new JsonSerializer<DcatProperty>() {
        @Override
        public JsonElement serialize(DcatProperty property, Type type,
            JsonSerializationContext jsonSerializationContext) {
          return new JsonPrimitive(property.getValue().toString());
        }
      }).registerTypeAdapter(OdmsCatalogue.class, new AnnotatedDeserializer<OdmsCatalogue>())
      .registerTypeAdapter(ConfigurationParameter.class,
          new AnnotatedDeserializer<ConfigurationParameter>())
      .registerTypeAdapter(RdfPrefix.class, new AnnotatedDeserializer<RdfPrefix>())
      .registerTypeAdapter(User.class, new AnnotatedDeserializer<User>())
      .registerTypeAdapter(PasswordChange.class, new AnnotatedDeserializer<PasswordChange>())
      .registerTypeAdapter(StatisticsRequest.class, new AnnotatedDeserializer<StatisticsRequest>())
      .registerTypeAdapter(LogsRequest.class, new AnnotatedDeserializer<LogsRequest>())
      .registerTypeAdapter(SearchRequest.class, new AnnotatedDeserializer<LogsRequest>())
      .registerTypeAdapter(SearchFilter.class, new AnnotatedDeserializer<LogsRequest>())
      .registerTypeAdapter(ErrorResponse.class, new AnnotatedDeserializer<ErrorResponse>())
      .registerTypeHierarchyAdapter(GregorianCalendar.class, new CalendarAdapter())
      .registerTypeAdapter(OdmsCatalogueImage.class, new ImageSerializer())

      .registerTypeAdapter(org.ckan.Tag.class, new SpodTagDeserializer())
      .registerTypeAdapter(org.ckan.Group.class, new SpodGroupDeserializer())
      .registerTypeAdapter(extraListType, new SpodExtraDeserializer());

  /** The gson. */
  private static Gson gson = gsonBuilder.create();

  /** The gson exclude fields. */
  private static Gson gsonExcludeFields = gsonBuilder.excludeFieldsWithoutExposeAnnotation()
      .create();

  /**
   * Json 2 obj.
   *
   * @param      <T> the generic type
   * @param json the json
   * @param t    the t
   * @return the t
   * @throws GsonUtilException the gson util exception
   */
  public static <T> T json2Obj(String json, Class<T> t) throws GsonUtilException {
    T obj = null;
    try {
      obj = gson.fromJson(json, t);
    } catch (Exception e) {

      throw new GsonUtilException("JSON to OBJECT failed: " + e.getMessage());
    }
    return obj;
  }

  /**
   * Json 2 obj.
   *
   * @param      <T> the generic type
   * @param json the json
   * @param t    the t
   * @return the t
   * @throws GsonUtilException the gson util exception
   */
  public static <T> T json2Obj(String json, Type t) throws GsonUtilException {

    T obj = null;
    try {
      obj = gson.fromJson(json, t);
    } catch (Exception e) {

      throw new GsonUtilException("JSON to OBJECT failed: " + e.getMessage());
    }
    return obj;
  }

  /**
   * Obj 2 json.
   *
   * @param     <T> the generic type
   * @param obj the obj
   * @param t   the t
   * @return the string
   * @throws GsonUtilException the gson util exception
   */
  public static <T> String obj2Json(Object obj, Class<T> t) throws GsonUtilException {
    String json = null;

    try {
      json = gson.toJson(obj, t);
    } catch (Exception e) {
      throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
    }
    return json;
  }

  /**
   * Obj 2 json.
   *
   * @param     <T> the generic type
   * @param obj the obj
   * @param t   the t
   * @return the string
   * @throws GsonUtilException the gson util exception
   */
  public static <T> String obj2Json(Object obj, Type t) throws GsonUtilException {
    String json = null;

    try {
      json = gson.toJson(obj, t);
    } catch (Exception e) {
      throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
    }
    return json;
  }

  /**
   * Obj 2 json with exclude.
   *
   * @param     <T> the generic type
   * @param obj the obj
   * @param t   the t
   * @return the string
   * @throws GsonUtilException the gson util exception
   */
  public static <T> String obj2JsonWithExclude(Object obj, Class<T> t) throws GsonUtilException {
    String json = null;

    try {
      json = gsonExcludeFields.toJson(obj, t);
    } catch (Exception e) {
      throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
    }
    return json;
  }

  /**
   * Obj 2 json with exclude.
   *
   * @param     <T> the generic type
   * @param obj the obj
   * @param t   the t
   * @return the string
   * @throws GsonUtilException the gson util exception
   */
  public static <T> String obj2JsonWithExclude(Object obj, Type t) throws GsonUtilException {
    String json = null;

    try {
      json = gsonExcludeFields.toJson(obj, t);
    } catch (Exception e) {
      throw new GsonUtilException("Object to JSON failed: " + e.getMessage());
    }
    return json;
  }

  /**
   * The Class AnnotatedDeserializer.
   *
   * @param <T> the generic type
   */
  static class AnnotatedDeserializer<T> implements JsonDeserializer<T> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
        throws JsonParseException {
      T pojo = new GsonBuilder()
          .registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            public ZonedDateTime deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
              DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                  .withZone(ZoneOffset.UTC);
              return ZonedDateTime.from(fmt.parse(jsonElement.getAsString()));
            }
          }).registerTypeAdapter(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
            @Override
            public JsonElement serialize(ZonedDateTime zonedDateTime, Type type,
                JsonSerializationContext jsonSerializationContext) {
              DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                  .withZone(ZoneOffset.UTC);
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
            throw new JsonParseException("There was an exception while deserializing the "
                + "json object: " + AnnotatedDeserializer.class.getName());
          }
        }
      }
      return pojo;

    }
  }
}
