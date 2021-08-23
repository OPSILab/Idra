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
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.dcat.dump.DcatApDeserializer;
import it.eng.idra.dcat.dump.DcatApItDeserializer;
import it.eng.idra.dcat.dump.DcatApSerializer;
import it.eng.idra.management.OdmsManager;
import it.eng.idra.utils.PropertyManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatDumpConnector.
 */
public class DcatDumpConnector implements IodmsConnector {

  /** The node id. */
  private String nodeId;

  /** The node. */
  private OdmsCatalogue node;

  /** The deserializer. */
  // private DCATAPProfile profile;
  private DcatApDeserializer deserializer;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(DcatDumpConnector.class);

  /** The odms dump file path. */
  private static String odmsDumpFilePath = PropertyManager
      .getProperty(IdraProperty.ODMS_DUMP_FILE_PATH);

  /**
   * Instantiates a new dcat dump connector.
   */
  public DcatDumpConnector() {
  }

  /**
   * Instantiates a new dcat dump connector.
   *
   * @param node the node
   */
  public DcatDumpConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());

    if (node.getDcatProfile() != null) {
      switch (node.getDcatProfile()) {

        case DCATAP_IT:
          deserializer = new DcatApItDeserializer();
          break;
        default:
          // If no valid profile was provided, instantiate a base DCATAP Deserializer
          deserializer = new DcatApDeserializer();
          break;
      }

    } else {
      // If no profile was provided, instantiate a base DCATAP Deserializer and set
      // the profile
      node.setDcatProfile(DcatApProfile.DCATAP);
      deserializer = new DcatApDeserializer();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#findDatasets(java.util.HashMap)
   */
  @Override
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
    ArrayList<DcatDataset> resultDatasets = new ArrayList<DcatDataset>();
    return resultDatasets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.connectors.IodmsConnector#countSearchDatasets(java.util.HashMap)
   */
  @Override
  public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#countDatasets()
   */
  @Override
  public int countDatasets() throws Exception {
    // try {
    // Document doc = Jsoup.connect(node.getHost()).get();
    // return (doc != null) ? -1 : 0;
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // return 0;
    // }
    return -1;
    // try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
    // HttpGet httpget = new HttpGet(node.getHost());
    // System.out.println("Executing request " + httpget.getRequestLine());
    //
    // // Create a custom response handler
    // ResponseHandler<Integer> responseHandler = response -> {
    // int status = response.getStatusLine().getStatusCode();
    // if (status >= 200 && status < 300) {
    // return -1;
    // } else {
    // return 0;
    // }
    // };
    // return httpclient.execute(httpget, responseHandler);
    //
    // }

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   */
  @Override
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getDataset(java.lang.String)
   */
  @Override
  public DcatDataset getDataset(String datasetId) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {

    String dumpUrl = null;
    String dumpString = null;

    try {
      if (StringUtils.isNotBlank(dumpUrl = node.getDumpUrl())) {
        node.setDumpFilePath(null);
        return getDatasetsFromDumpUrl(dumpUrl);
      } else if (StringUtils.isNotBlank(dumpString = node.getDumpString())) {
        return getDatasetsFromDumpString(dumpString);
      } else {
        throw new Exception("The node must have either the dumpURL or dumpString");
      }

    } catch (IOException | RiotException e) {
      throw new Exception(e);
    }

  }

  /**
   * Gets the datasets from dump string.
   *
   * @param dumpString the dump string
   * @return the datasets from dump string
   * @throws Exception the exception
   */
  private List<DcatDataset> getDatasetsFromDumpString(String dumpString) throws Exception {

    List<DcatDataset> datasetsList = new ArrayList<DcatDataset>();

    // Pass the Node Host as base URI for the model
    Model m = deserializer.dumpToModel(dumpString, node);
    StmtIterator sit = m.listStatements(null, RDF.type, DCAT.Dataset);
    // Matcher matcher =
    // deserializer.getDatasetPattern(node.getDcatFormat()).matcher(dumpString);
    String datasetUri = null;
    while (sit.hasNext()) {
      datasetUri = null;
      try {
        //
        // switch (node.getDcatFormat()) {
        //
        // case TURTLE:
        // datasetURI = matcher.group(1);
        // break;
        //
        // // RDFXML is the default
        // default:
        // datasetURI = matcher.group(1);
        // break;
        //
        // }
        //
        // if (StringUtils.isNotBlank(datasetURI)) {
        //
        // Resource r = m.getResource(datasetURI);
        // System.out.println(r.getLocalName());
        // datasetsList.add(deserializer.resourceToDataset(nodeID, r));
        //
        Statement s = sit.nextStatement();
        Resource datasetResource = m.getResource(s.getSubject().getURI());
        datasetsList.add(deserializer.resourceToDataset(nodeId, datasetResource));

      } catch (Exception e) {
        logger.info("Skipped dataset - There was an error: " + e.getMessage()
            + " while deserializing dataset: " + datasetUri);

      }
    }

    if (datasetsList.size() != 0) {
      DcatApSerializer.writeModelToFile(m, DcatApFormat.RDFXML, odmsDumpFilePath,
          "dumpFileString_" + nodeId);
      node.setDumpFilePath(odmsDumpFilePath + "dumpFileString_" + nodeId);
      // Since the registration is finished we don't need the file in memory
      node.setDumpString(null);
      OdmsManager.updateOdmsCatalogue(node, true);
      return datasetsList;
    } else {
      throw new Exception("No Datasets retrieved from the provided DUMP!");
    }
  }

  /**
   * Gets the datasets from dump url.
   *
   * @param dumpUrl the dump url
   * @return the datasets from dump url
   * @throws Exception the exception
   */
  private List<DcatDataset> getDatasetsFromDumpUrl(String dumpUrl) throws Exception {

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet httpget = new HttpGet(dumpUrl);
      logger.info("Executing request " + httpget.getRequestLine());

      // Create a custom response handler
      ResponseHandler<String> responseHandler = response -> {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
          HttpEntity entity = response.getEntity();
          return entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
        } else {
          throw new ClientProtocolException("Unexpected response status: " + status);
        }
      };
      String responseBody = httpclient.execute(httpget, responseHandler);
      return getDatasetsFromDumpString(responseBody);

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getChangedDatasets(java.util.List,
   * java.lang.String)
   */
  @Override
  public OdmsSynchronizationResult getChangedDatasets(List<DcatDataset> oldDatasets,
      String startingDateString) throws Exception {

    ArrayList<DcatDataset> newDatasets = (ArrayList<DcatDataset>) getAllDatasets();

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

}
