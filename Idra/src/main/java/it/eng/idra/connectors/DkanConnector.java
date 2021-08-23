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
package it.eng.idra.connectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class DkanConnector.
 */
public class DkanConnector implements IodmsConnector {

  /** The node. */
  private OdmsCatalogue node;

  /** The node id. */
  private String nodeId;

  /** The datasets array. */
  private JSONArray datasetsArray;

  /** The Constant JSON_TYPE. */
  public static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(DkanConnector.class);

  /**
   * Instantiates a new dkan connector.
   *
   * @param node the node
   */
  public DkanConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());
  }

  /**
   * Gets the json datasets.
   *
   * @return the json datasets
   * @throws JSONException                   the JSON exception
   * @throws URISyntaxException              the URI syntax exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws IOException                     Signals that an I/O exception has
   *                                         occurred.
   */
  private JSONArray getJsonDatasets()
      throws JSONException, URISyntaxException, OdmsCatalogueOfflineException,
      OdmsCatalogueForbiddenException, OdmsCatalogueNotFoundException, IOException {

    logger.info("-- DKAN Connector Request sent -- " + node.getHost());

    String returnedJson = sendGetRequest1(node.getHost() + "/data.json");

    if (!returnedJson.startsWith("{")) {
      if (returnedJson.matches(".*The requested URL could not be retrieved.*")
          || returnedJson.matches(".*does not exist.*")) {
        throw new OdmsCatalogueNotFoundException(" The ODMS host does not exist");
      } else if (returnedJson.contains("403")) {
        throw new OdmsCatalogueForbiddenException(" The ODMS node is forbidden");
      } else {
        throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
      }
    }

    JSONObject jsonObject = new JSONObject(returnedJson);
    JSONArray jsonArray = jsonObject.getJSONArray("dataset");
    logger.info("-- DKAN Connector Response - Result count:" + jsonArray.length());

    return jsonArray;
  }

  /**
   * Performs mapping from DKAN JSON Dataset object to DCATDataset object.
   *
   * @param d    DKAN Dataset to be mapped
   * @param node Node which dataset belongs to
   * @return the dcat dataset
   * @throws JSONException  the JSON exception
   * @throws ParseException JSONException
   * @returns DCATDataset resulting mapped object
   */
  @Override
  public DcatDataset datasetToDcat(Object d, OdmsCatalogue node)
      throws JSONException, ParseException {

    JSONObject dataset = (JSONObject) d;

    // Properties to be mapped to DKAN metadata
    String identifier = null;
    String description = null;
    String issued = null;
    String modified = null;
    String landingPage = null;
    FoafAgent publisher = null;
    List<String> keywords = new ArrayList<String>();
    DctLicenseDocument license = null;
    ArrayList<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();
    /*
     * DON'T TOUCH - GetString and not OptString for identifier, because it's
     * mandatory and eventually throws an exception
     */
    identifier = dataset.getString("identifier");

    landingPage = dataset.optString("landingPage");
    description = dataset.optString("description");

    try {
      issued = CommonUtil.fromLocalToUtcDate(dataset.optString("issued"), null);
    } catch (IllegalArgumentException skip) {
      logger.debug(skip.getLocalizedMessage());
    }

    try {
      modified = CommonUtil.fromLocalToUtcDate(dataset.optString("modified"), null);
    } catch (IllegalArgumentException skip) {
      logger.debug(skip.getLocalizedMessage());
    }

    if (dataset.has("keyword")) {
      JSONArray keywordArray = dataset.getJSONArray("keyword");
      for (int i = 0; i < keywordArray.length(); i++) {
        keywords.add(keywordArray.getString(i));
      }
    }
    List<VcardOrganization> contactPointList = null;
    contactPointList = deserializeContactPoint(dataset);
    String title = null;
    title = dataset.optString("title");

    if (dataset.has("publisher")) {
      publisher = deserializeFoafAgent(dataset, "publisher", DCTerms.publisher, nodeId);
    }

    if (dataset.has("license")) {
      license = deserializeLicense(dataset, nodeId);
    }
    List<SkosConceptTheme> themeList = null;
    themeList = deserializeConcept(dataset, "theme", DCAT.theme, nodeId, SkosConceptTheme.class);

    if (dataset.has("distribution")) {
      JSONArray distrArray = dataset.getJSONArray("distribution");
      for (int i = 0; i < distrArray.length(); i++) {
        try {
          distributionList.add(distributionToDcat(distrArray.getJSONObject(i), license, nodeId));
        } catch (Exception e) {
          logger.info("There was an error while deserializing a Distribution: " + e.getMessage()
              + " - SKIPPED");
        }
      }
    }

    return new DcatDataset(nodeId, identifier, title, description, distributionList, themeList,
        publisher, contactPointList, keywords, null, null, null, null, null, null, landingPage,
        null, null, issued, modified, null, null, null, null, null, null, null, null, null, null,
        null, null);
  }

  /**
   * Distribution to DCAT.
   *
   * @param obj     the obj
   * @param license the license
   * @param nodeId  the node ID
   * @return the dcat distribution
   * @throws Exception the exception
   */
  protected DcatDistribution distributionToDcat(JSONObject obj, DctLicenseDocument license,
      String nodeId) throws Exception {

    String accessUrl = null;
    // String description = null;
    String format = null;
    // String byteSize = null;
    String downloadUrl = null;
    String mediaType = null;
    // String releaseDate = null;
    // String updateDate = null;
    // String rights = null;
    String title = null;
    // SpdxChecksum checksum = null;
    // List<String> documentation = new ArrayList<String>();
    // List<String> language = new ArrayList<String>();
    // List<DctStandard> linkedSchemas = null;
    // SkosConceptStatus status = null;

    mediaType = obj.optString("mediaType");
    if (obj.has("format")) {
      format = obj.getString("format");
    }

    if (obj.has("title")) {
      title = obj.getString("title");
    }

    if (obj.has("downloadURL")) {
      accessUrl = downloadUrl = obj.getString("downloadURL");
      if (obj.has("accessURL")) {
        accessUrl = obj.getString("accessURL");
      }
    } else if (obj.has("accessURL")) {
      accessUrl = downloadUrl = obj.getString("accessURL");
    }

    return new DcatDistribution(nodeId, accessUrl, null, format, license, null, null,
        new ArrayList<String>(), downloadUrl, new ArrayList<String>(), null, mediaType, null, null,
        null, null, title);

  }

  /**
   * Deserialize contact point.
   *
   * @param dataset the dataset
   * @return the list
   */
  protected List<VcardOrganization> deserializeContactPoint(JSONObject dataset) {

    List<VcardOrganization> result = new ArrayList<VcardOrganization>();

    if (dataset.has("contactPoint")) {

      JSONObject contactObj = dataset.optJSONObject("contactPoint");
      if (contactObj != null) {
        result.add(new VcardOrganization(DCAT.contactPoint.getURI(), null,
            contactObj.optString("fn"), contactObj.optString("hasEmail"),
            contactObj.optString("hasURL"), contactObj.optString("hasTelephoneValue"),
            contactObj.optString("hasTelephoneType"), nodeId));
      }
    }
    return result;
  }

  /**
   * Deserialize foaf agent.
   *
   * @param dataset   the dataset
   * @param fieldName the field name
   * @param property  the property
   * @param nodeId    the node id
   * @return the foaf agent
   */
  protected FoafAgent deserializeFoafAgent(JSONObject dataset, String fieldName, Property property,
      String nodeId) {

    try {
      JSONObject obj = dataset.getJSONObject(fieldName);
      return new FoafAgent(property.getURI(), obj.optString("resourceUri"), obj.optString("name"),
          obj.optString("mbox"), obj.optString("homepage"), obj.optString("type"),
          obj.optString("identifier"), nodeId);
    } catch (JSONException ignore) {
      logger.info("Agent object not valid! - Skipped");
    }
    return null;
  }

  /**
   * Deserialize license.
   *
   * @param obj    the obj
   * @param nodeId the node id
   * @return the dct license document
   */
  protected DctLicenseDocument deserializeLicense(JSONObject obj, String nodeId) {

    try {

      return new DctLicenseDocument(obj.getString("license"), null, null, null, nodeId);

    } catch (JSONException ignore) {
      logger.info("License not valid! - Skipped");
    }

    return null;

  }

  /**
   * Deserialize concept.
   *
   * @param           <T> the generic type
   * @param obj       the obj
   * @param fieldName the field name
   * @param property  the property
   * @param nodeId    the node id
   * @param type      the type
   * @return the list
   * @throws JSONException the JSON exception
   */
  protected <T extends SkosConcept> List<T> deserializeConcept(JSONObject obj, String fieldName,
      Property property, String nodeId, Class<T> type) throws JSONException {

    List<T> result = new ArrayList<T>();
    JSONArray conceptArray = obj.optJSONArray(fieldName);

    if (conceptArray != null) {
      for (int i = 0; i < conceptArray.length(); i++) {

        String label = conceptArray.getString(i);
        if (StringUtils.isNotBlank(label)) {

          List<SkosPrefLabel> prefLabelList = Arrays.asList(new SkosPrefLabel(null, label, nodeId));
          try {
            result.add(type.getDeclaredConstructor(SkosConcept.class)
                .newInstance(new SkosConcept(property.getURI(), null, prefLabelList, nodeId)));
          } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
              | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#countDatasets()
   */
  @Override
  public int countDatasets()
      throws ParseException, URISyntaxException, OdmsCatalogueOfflineException,
      OdmsCatalogueForbiddenException, OdmsCatalogueNotFoundException, IOException {

    return getAllDatasets().size();

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#findDatasets(java.util.HashMap)
   */
  // Live search is not available on current SODA API
  @Override
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters) {
    ArrayList<DcatDataset> resultDatasets = new ArrayList<DcatDataset>();
    return resultDatasets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getDataset(java.lang.String)
   */
  // Individual get of the datasets is not available on current SODA API
  @Override
  public DcatDataset getDataset(String datasetId) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets()
      throws ParseException, URISyntaxException, OdmsCatalogueOfflineException,
      OdmsCatalogueForbiddenException, OdmsCatalogueNotFoundException, IOException {

    ArrayList<DcatDataset> dcatDatasets = new ArrayList<DcatDataset>();

    datasetsArray = getJsonDatasets();
    for (int i = 0; i < datasetsArray.length(); i++) {
      try {
        JSONObject dataset = datasetsArray.getJSONObject(i);
        dcatDatasets.add(datasetToDcat(dataset, node));
        dataset = null;

      } catch (Exception e) {
        logger.info("There was an error: " + e.getMessage() + " while deserializing Dataset - " + i
            + " - SKIPPED");
      }
    }

    datasetsArray = null;
    System.gc();

    return dcatDatasets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getChangedDatasets(java.util.List,
   * java.lang.String)
   */
  @Override
  public OdmsSynchronizationResult getChangedDatasets(List<DcatDataset> oldDatasets,
      String startingDateString)
      throws ParseException, URISyntaxException, OdmsCatalogueOfflineException,
      OdmsCatalogueForbiddenException, OdmsCatalogueNotFoundException, IOException {

    ArrayList<DcatDataset> newDatasets = (ArrayList<DcatDataset>) getAllDatasets();
    // ArrayList<DCATDataset> newDatasets = new ArrayList<DCATDataset>();
    // newDatasets.add(new
    // newDatasets.add(new

    OdmsSynchronizationResult syncrhoResult = new OdmsSynchronizationResult();

    ImmutableSet<DcatDataset> newSets = ImmutableSet.copyOf(newDatasets);
    ImmutableSet<DcatDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

    int deleted = 0;
    int added = 0;
    int changed = 0;

    /// Find added datasets
    // difference(current,present)
    SetView<DcatDataset> diff = Sets.difference(newSets, oldSets);
    logger.info("New Packages: " + diff.size());
    for (DcatDataset d : diff) {
      syncrhoResult.addToAddedList(d);
      added++;
    }

    // Find removed datasets
    // difference(present,current)
    SetView<DcatDataset> diff1 = Sets.difference(oldSets, newSets);
    logger.info("Deleted Packages: " + diff1.size());
    for (DcatDataset d : diff1) {
      syncrhoResult.addToDeletedList(d);
      deleted++;
    }

    // Find updated datasets
    // intersection(present,current)
    SetView<DcatDataset> intersection = Sets.intersection(newSets, oldSets);
    logger.fatal("Changed Packages: " + intersection.size());

    GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // SimpleDateFormat DCATDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz
    // yyyy", Locale.US);

    int exception = 0;
    for (DcatDataset d : intersection) {
      try {
        int oldIndex = oldDatasets.indexOf(d);
        int newIndex = newDatasets.indexOf(d);
        oldDate.setTime(iso.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
        newDate.setTime(iso.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

        if (newDate.after(oldDate)) {
          syncrhoResult.addToChangedList(d);
          changed++;
        }
      } catch (Exception ex) {
        exception++;
        if (exception % 1000 == 0) {
          ex.printStackTrace();
        }
      }
    }
    logger.info("Changed " + syncrhoResult.getChangedDatasets().size());
    logger.info("Added " + syncrhoResult.getAddedDatasets().size());
    logger.info("Deleted " + syncrhoResult.getDeletedDatasets().size());
    logger.info("Expected new dataset count: " + (node.getDatasetCount() - deleted + added));

    return syncrhoResult;
  }

  // @Override
  // public HashMap<DCATDataset, String> getChangedDatasets(List<DCATDataset>
  // oldDatasets, String startingDateString) throws ParseException,
  // URISyntaxException, ODMSCatalogueOfflineException,
  // ODMSCatalogueForbiddenException,
  // ODMSCatalogueNotFoundException{
  // HashMap<DCATDataset,String> result = new HashMap<DCATDataset, String>();
  // ArrayList<DCATDataset> newDatasets = (ArrayList <DCATDataset>)
  // getAllDatasets();
  //// ArrayList<DCATDataset> newDatasets = new ArrayList<DCATDataset>();
  ////
  //// newDatasets.add(new
  //
  //
  //
  // ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
  // ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);
  //
  // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  // GregorianCalendar startingDate = new
  // GregorianCalendar(TimeZone.getTimeZone("UTC"));
  // startingDate.setTime(sdf.parse(startingDateString));
  //
  // int deleted = 0;
  // int added = 0;
  // int changed = 0;
  //
  //
  // /// Find added datasets
  // //difference(current,present)
  // SetView<DCATDataset> diff = Sets.difference(newSets,oldSets);
  // logger.info("New Package "+diff.size());
  // for( DCATDataset d: diff ) {
  // result.put(d, "new package");
  // added++;
  // }
  //
  // // Find removed datasets
  // //difference(present,current)
  // SetView<DCATDataset> diff1 = Sets.difference(oldSets,newSets);
  // logger.info("Deleted Package "+diff1.size());
  // for( DCATDataset d: diff1 ) {
  // result.put(d, "deleted package");
  // deleted++;
  // }
  //
  // //Find updated datasets
  // //intersection(present,current)
  //// SimpleDateFormat sdf1 = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z
  // yyyy", Locale.US);
  //// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  //// GregorianCalendar startingDate = new
  // GregorianCalendar(TimeZone.getTimeZone("UTC"));
  // SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
  // logger.fatal("Intersection Package " + intersection.size());
  //
  // GregorianCalendar oldDate = new
  // GregorianCalendar(TimeZone.getTimeZone("UTC"));
  // oldDate.setLenient(false);
  // GregorianCalendar newDate = new
  // GregorianCalendar(TimeZone.getTimeZone("UTC"));
  // oldDate.setLenient(false);
  // SimpleDateFormat DKANDateF = new
  // SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  //
  // SimpleDateFormat DCATDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss
  // zzz yyyy", Locale.US);
  //
  // int exception = 0;
  // for (DCATDataset d: intersection ){
  // try{
  // int oldIndex = oldDatasets.indexOf(d);
  // int newIndex = newDatasets.indexOf(d);
  // System.out.println("-----------------------\n");
  // System.out.println(oldDatasets.get(oldIndex).getDcat_modified().getValue());
  // System.out.println(newDatasets.get(newIndex).getDcat_modified().getValue());
  // System.out.println("-----------------------\n\n");
  // oldDate.setTime(DCATDateF.parse(oldDatasets.get(oldIndex).getDcat_modified().getValue()));
  // newDate.setTime(DKANDateF.parse(newDatasets.get(newIndex).getDcat_modified().getValue()));
  //// oldDate.setTime(sdf.parse(oldDatasets.get(oldIndex).getDcat_modified().getValue()));
  //
  //
  //
  // if(newDate.after(oldDate)){
  // result.put(d, "changed package");
  // changed++;
  // }
  // }catch(Exception ex){
  // exception++;
  // if(exception%1000 == 0){
  // ex.printStackTrace();
  // }
  // }
  // }
  // System.out.println("Exceptions: "+exception);
  // System.out.println("Deleted "+deleted);
  // System.out.println("Changed "+changed);
  // System.out.println("Added "+added);
  //
  // return result;
  // }

  /**
   * Send get request 1.
   *
   * @param urlString the url string
   * @return the string
   */
  private String sendGetRequest1(String urlString) {

    try {
      TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }
      } };

      SSLContext ctx = null;
      try {
        ctx = SSLContext.getInstance("TLS");
        ctx.init(null, certs, new SecureRandom());
      } catch (java.security.GeneralSecurityException e) {
        logger.error("", e);
        // throw OurExceptionUtils.wrapInRuntimeExceptionIfNecessary(e);
      }

      HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

      ClientBuilder clientBuilder = ClientBuilder.newBuilder();
      try {
        clientBuilder.sslContext(ctx);
        clientBuilder.hostnameVerifier(new HostnameVerifier() {
          @Override
          public boolean verify(String hostname, SSLSession session) {
            return true;
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
        logger.error("", e);
        // throw OurExceptionUtils.wrapInRuntimeExceptionIfNecessary(e);
      }

      Client client = ClientBuilder.newBuilder().build();

      WebTarget webTarget = client.target(urlString);

      Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
      Response response = invocationBuilder.get();

      StatusType status = response.getStatusInfo();
      String res = response.readEntity(String.class);
      return res;
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }

  }

  /**
   * Send get request.
   *
   * @param urlString the url string
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private String sendGetRequest(String urlString) throws IOException {
    URL url = null;

    try {
      // url = new URL( this.m_host + ":" + this.m_port + path);
      url = new URL(urlString);
    } catch (MalformedURLException mue) {
      System.err.println(mue);
      return null;
    }

    String body = "";

    final HttpParams httpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParams, 3000000);
    // if(!(data.contains("\"rows\":\"1\"") ||
    // data.contains("\"rows\":\"0\"") || path.contains("package_list")) )
    // HttpConnectionParams.setSoTimeout(httpParams, 6);
    // else
    HttpConnectionParams.setSoTimeout(httpParams, 9000000);

    // apache HttpClient version >4.2 should use BasicClientConnectionManager
    HttpClient httpclient = new DefaultHttpClient(httpParams);

    // HttpClient httpclient = new DefaultHttpClient(httpParams);
    /*
     * Set an HTTP proxy if it is specified in system properties.
     * 
     * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
     * http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/
     * http/examples/client/ClientExecuteProxy.java
     */
    if (Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_ENABLED).trim())
        && StringUtils
            .isNotBlank(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST).trim())) {

      int port = 80;
      if (isSet(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PORT))) {
        port = Integer.parseInt(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PORT));
      }
      HttpHost proxy = new HttpHost(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST), port,
          "http");
      httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
      if (isSet(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_USER))) {
        ((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
            new AuthScope(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST), port),
            (Credentials) new UsernamePasswordCredentials(
                PropertyManager.getProperty(IdraProperty.HTTP_PROXY_USER),
                PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PASSWORD)));
      }
    }
    try {

      HttpGet getRequest = new HttpGet(url.toString());
      getRequest.addHeader("accept", "application/json");

      HttpResponse response = httpclient.execute(getRequest);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException(
            "Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      BufferedReader rd = new BufferedReader(
          new InputStreamReader(response.getEntity().getContent()));

      StringBuffer result = new StringBuffer();
      String line = "";
      while ((line = rd.readLine()) != null) {
        result.append(line);
      }

      body = result.toString();

    } finally {
      httpclient.getConnectionManager().shutdown();
    }

    return body;
  }

  /**
   * Checks if is sets the.
   *
   * @param string the string
   * @return true, if is sets the
   */
  private static boolean isSet(String string) {
    return string != null && string.length() > 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.connectors.IodmsConnector#countSearchDatasets(java.util.HashMap)
   */
  @Override
  public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
    // TODO Auto-generated method stub
    return 0;
  }

}
