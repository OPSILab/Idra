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
package it.eng.idra.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * This class exposes a mockup example of Federation APIs. To be used only for
 * test purposes.
 */
@Path("/mockup")

public class FederationApiMockup {

  /**
   * Native search.
   *
   * @param input the input
   * @return the response
   */
  @POST
  @Path("/odf/odms/search")
  @Produces("application/json")
  public Response nativeSearch(final String input) {

    try {

      JSONObject json = new JSONObject(
          "{\"count\":\"1\",\"results\":[{\"title\": \"eee\",\"description\": \"eee\","
              + "\"identifier\": \"eee\",\"altIndentifier\": \"eee\","
              + "\"issued\": \"2015-10-12T12:03:22Z\"," + "\"modified\": \"2015-10-12T12:03:22Z\","
              + "\"versionInfo\":\"ddd\",\"versionNotes\":\"eeee\","
              + "\"landingPage\":\"baseUrl/datasets/identifier\",\"accrualPeriodicity\":\"eee\","
              + "\"spatial\":\"eee\",\"temporal\":\"eee\","
              + "\"language\":\"english\",\"licenseTitle\":\"eee\","
              + "\"keyword\":[\"pollution\",\"metro\"],"
              + "\"publisher\":{\"name\":\"eee\",\"mbox\":\"eee\","
              + "\"homepage\":\"rrr\",\"type\":\"eee\"},"
              + "\"contactPoint\": {\"fn\":\"eee\",\"hasEmail\":\"eeee\"},"
              + "\"distribution\":[{\"title\":\"ee\","
              + "\"accessURL\": \"eee\",\"description\": \"ddd\","
              + "\"mediaType\":\"eee\",\"format\": \"eee\",\"license\": \"eee\","
              + "\"issued\": \"2015-10-12T12:03:14Z\","
              + "\"modified\": \"2015-10-12T12:03:14Z\",\"byteSize\":\"1024\"}]}]}");
      return Response.status(Response.Status.OK).entity(json.toString()).build();
    } catch (JSONException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Gets the native dataset.
   *
   * @param datasetId the dataset id
   * @return the native dataset
   */
  @GET
  @Path("/odf/odms/datasets/{id}")
  @Produces("application/json")
  public Response getNativeDataset(@PathParam("id") String datasetId) {

    try {

      JSONObject json = new JSONObject();

      json.put("title", "title");
      json.put("description", "description");
      json.put("identifier", "identifier");
      json.put("altIdentifier", "altIdentifier");
      json.put("issued", "2015-10-12T12:03:22Z");
      json.put("modified", "2015-10-12T12:03:22Z");
      json.put("versionInfo", "versionInfo");
      json.put("versionNotes", "versionNotes");
      json.put("landingPage", "baseUrl/datasets/identifier");
      json.put("accrualPeriodicity", "accrualPeriodicity");
      json.put("spatial", "spatial");

      json.put("temporal", "temporal");
      json.put("language", "language");
      json.put("licenseTitle", "licenseTitle");

      JSONArray keywords = new JSONArray();// {"pollution","metro"};
      keywords.put("pollution");
      keywords.put("metro");
      json.put("keyword", keywords);
      
      JSONObject publisher = new JSONObject();
      publisher.put("name", "publisherName");
      publisher.put("mbox", "publisherMbox");
      publisher.put("homepage", "publisherHomepage");
      publisher.put("type", "publisherType");
      json.put("publisher", publisher);

      JSONObject contacpPoint = new JSONObject();
      contacpPoint.put("fn", "contactPointFN");
      contacpPoint.put("hasEmail", "contactPointHasEmail");
      json.put("contactPoint", contacpPoint);

      JSONObject dist1 = new JSONObject();
      dist1.put("title", "distTitle");
      dist1.put("accessURL", "distAccessURL");
      dist1.put("description", "distDescription");
      dist1.put("mediaType", "distMediaType");
      dist1.put("format", "distFormat");
      dist1.put("license", "distLicense");
      dist1.put("issued", "2015-10-12T12:03:14Z");
      dist1.put("modified", "2015-10-12T12:03:14Z");
      dist1.put("byteSize", "1024");

      JSONArray distributions = new JSONArray();
      distributions.put(dist1);
      json.put("distribution", distributions);

      System.out.println(json.toString());
      return Response.status(Response.Status.OK).entity(json.toString()).build();
    } catch (JSONException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Gets the native datasets identifier.
   *
   * @return the native datasets identifier
   */
  @GET
  @Path("/odf/odms/datasets/info")
  @Produces("application/json")
  public Response getNativeDatasetsIdentifier() {

    try {

      JSONArray json = new JSONArray("[{\"identifier\":\"agid:D.300-90\","
          + "\"issued\":\"2015-10-12T12:03:14Z\",\"modified\":\"2015-10-12T12:03:14Z\"}]");
      return Response.status(Response.Status.OK).entity(json.toString()).build();
    } catch (JSONException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Gets the native datasets.
   *
   * @param rows   the rows
   * @param offset the offset
   * @return the native datasets
   */
  @GET
  @Path("/odf/odms/datasets")
  @Produces("application/json")
  public Response getNativeDatasets(@QueryParam("rows") String rows,
      @QueryParam("offset") String offset) {

    try {

      String json = "[{\"id\":\"agid:D.301-2\",\"nodeID\":\"2\","
          + "\"hasStoredRDF\":false,\"title\":\"Contratti del Sistema Pubblico di Connettività\","
          + "\"description\":\"Il dataset LOD che contiene i contratti SPC del Lotto 1 (2007)\","
          + "\"distributions\":[{\"id\":\"4e581d9d-20d3-4802-80a0-55f9b660d227\""
          + ",\"storedRDF\":false," + "\"accessURL\":\"spcdata.digitpa.gov.it:8899/sparql\","
          + "\"description\":\"Questa è la distribuzione N3 del dataset"
          + "Linked Open Data relativo ai contratti del Sistema Pubblico di Connettività\","
          + "\"format\":\"RDF\","
          + "\"license\":{\"uri\":\"http://creativecommons.org/licenses/by/4.0/\","
          + "\"name\":\"CC BY\",\"type\":\"http://purl.org/adms/licencetype/Attribution\","
          + "\"versionInfo\":\"4.0\"},\"byteSize\":\"\",\"documentation\":[\"\"],"
          + "\"downloadURL\":\"http://spcdata.digitpa.gov.it/data/contrattiLotto1.nt\","
          + "\"language\":[\"\"],\"linkedSchemas\":[],\"mediaType\":\"\","
          + "\"releaseDate\":\"\",\"updateDate\":\"\"," + "\"rights\":\"\",\"status\":\"\","
          + "\"title\":\"Distribuzione Turtle di LOD SPC Contratti\"," + "\"hasDatalets\":false}],"
          + "\"theme\":[{\"resourceUri\":\"http://publications.europa.eu/"
          + "resource/authority/data-theme/ECON\","
          + "\"propertyUri\":\"http://www.w3.org/ns/dcat#theme\","
          + "\"prefLabel\":[{\"language\":\"it\",\"value\":\"Economia e Finanze\"}]}],"
          + "\"publisher\":{\"name\":\"Agenzia per l'Italia Digitale\","
          + "\"resourceUri\":\"http://dati.gov.it/resource/Amministrazione/agid\","
          + "\"propertyUri\":\"http://purl.org/dc/terms/publisher\","
          + "\"mbox\":\"\",\"homepage\":\"\",\"type\":\"\",\"identifier\":\"agid\"},"
          + "\"contactPoint\":[{\"id\":\"d747a45a-f5a2-4899-90fe-804b8ccb4bf3\","
          + "\"resourceUri\":\"http://dati.gov.it/resource/PuntoContatto/contactPointLODIPA\","
          + "\"propertyUri\":\"http://www.w3.org/ns/dcat#contactPoint\","
          + "\"fn\":\"banche dati e open data\",\"nodeID\":\"2\","
          + "\"hasEmail\":\"mailto:info@agid.gov.it\","
          + "\"hasURL\":\"http://spcdata.digitpa.gov.it/contattaci.html\","
          + "\"hasTelephoneValue\":\"06123456\","
          + "\"hasTelephoneType\":\"http://www.w3.org/2006/vcard/ns#Voice\"}],"
          + "\"keywords\":[\"Contratto pubblico\","
          + "\"Acquisizione\",\"SPC\"],\"accessRights\":\"\","
          + "\"conformsTo\":[],\"documentation\":[\"\"],"
          + "\"frequency\":\"NEVER\",\"hasVersion\":[\"\"]," + "\"isVersionOf\":[\"\"],"
          + "\"landingPage\":\"http://dati.gov.it/resource"
          + "/Dataset/ContrattiSPC_agid\",\"language\":[\"\"],"
          + "\"provenance\":[\"\"],\"releaseDate\":\"1970-01-01T00:00:00Z\","
          + "\"updateDate\":\"2015-05-25T00:00:00Z\","
          + "\"identifier\":\"agid:D.301\",\"otherIdentifier\":[\"\"],"
          + "\"sample\":[\"\"],\"source\":[\"\"],"
          + "\"spatialCoverage\":{\"uri\":\"http://purl.org/dc/terms/spatial\","
          + "\"geographicalIdentifier\":\"http://www.geonames.org/3169070\","
          + "\"geographicalName\":\"\",\"geometry\":\"\"},"
          + "\"temporalCoverage\":{\"uri\":\"http://purl.org/dc/terms/temporal\","
          + "\"startDate\":\"2007-01-01\",\"endDate\":\"2012-12-31\"},"
          + "\"type\":\"\",\"version\":\"\",\"versionNotes\":[\"\"],"
          + "\"rightsHolder\":{\"name\":\"Agenzia per l'Italia Digitale\","
          + "\"resourceUri\":\"http://dati.gov.it/resource/Amministrazione/agid\","
          + "\"propertyUri\":\"http://purl.org/dc/terms/rightsHolder\","
          + "\"mbox\":\"\",\"homepage\":\"\",\"type\":\"\","
          + "\"identifier\":\"agid\"},\"creator\":{\"name\":\"Agenzia per l'Italia Digitale\","
          + "\"resourceUri\":\"http://dati.gov.it/resource/Amministrazione/agid\","
          + "\"propertyUri\":\"http://purl.org/dc/terms/creator\","
          + "\"mbox\":\"\",\"homepage\":\"\",\"type\":\"\",\"identifier\":\"agid\"},"
          + "\"subject\":[{\"resourceUri\":\"http://eurovoc.europa.eu/3193\","
          + "\"propertyUri\":\"http://purl.org/dc/terms/subject\","
          + "\"prefLabel\":[{\"language\":\"it\",\"value\":\"beni e servizi\"}]},"
          + "{\"resourceUri\":\"http://eurovoc.europa.eu/1810\","
          + "\"propertyUri\":\"http://purl.org/dc/terms/subject\","
          + "\"prefLabel\":[{\"language\":\"it\"," + "\"value\":\"appalto pubblico\"}]}],"
          + "\"legacyIdentifier\":\"\"}]";

      return Response.status(Response.Status.OK).entity(json.toString()).build();
    } catch (JSONException e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

}
