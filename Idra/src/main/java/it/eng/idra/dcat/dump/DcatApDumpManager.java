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

package it.eng.idra.dcat.dump;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatApWriteType;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.cache.LodCacheManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.utils.PropertyManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import java.nio.charset.Charset;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatApDumpManager.
 */
public class DcatApDumpManager {

  /** The logger. */
  public static Logger logger = LogManager.getLogger(DcatApDumpManager.class);

  /** The Constant dumpOnStart. */
  private static final Boolean dumpOnStart = Boolean
      .parseBoolean(PropertyManager.getProperty(IdraProperty.DUMP_ON_START));

  /** The Constant dumpPeriod. */
  private static final Long dumpPeriod = Long
      .parseLong(PropertyManager.getProperty(IdraProperty.DUMP_PERIOD)) * 1000;

  /** The dump format. */
  private static DcatApFormat dumpFormat = DcatApFormat
      .fromString(PropertyManager.getProperty(IdraProperty.DUMP_FORMAT));

  /** The dump profile. */
  private static DcatApProfile dumpProfile = DcatApProfile
      .fromString(PropertyManager.getProperty(IdraProperty.DUMP_PROFILE));

  /** The dump zip. */
  private static Boolean dumpZip = Boolean
      .parseBoolean(PropertyManager.getProperty(IdraProperty.DUMP_ZIP_FILE));

  /** The Constant globalDumpFilePath. */
  private static final String globalDumpFilePath = PropertyManager
      .getProperty(IdraProperty.DUMP_FILE_PATH);

  /** The Constant globalDumpFileName. */
  public static final String globalDumpFileName = PropertyManager
      .getProperty(IdraProperty.DUMP_FILE_NAME);

  /**
   * Instantiates a new dcat ap dump manager.
   */
  private DcatApDumpManager() {
  }

  /**
   * Gets the dataset dump from file.
   *
   * @param nodeId    the node id
   * @param forceDump the force dump
   * @param returnZip the return zip
   * @return the dataset dump from file
   * @throws IOException              Signals that an I/O exception has occurred.
   * @throws NumberFormatException    the number format exception
   * @throws DatasetNotFoundException the dataset not found exception
   * @throws SolrServerException      the solr server exception
   */
  public static byte[] getDatasetDumpFromFile(String nodeId, Boolean forceDump, Boolean returnZip)
      throws IOException, NumberFormatException, DatasetNotFoundException, SolrServerException {
    /*
     * Non necessario unzippare perchè ci teniamo entrambe le versioni del dump
     * 
     */
    // ZipFile zipFile = new ZipFile(globalDumpFilePath + globalDumpFileName +
    // ".zip");
    // InputStream in = null;
    // ByteArrayOutputStream out = null;
    // try {
    // in = zipFile.getInputStream(zipFile.getEntry(globalDumpFileName));
    // out = new ByteArrayOutputStream();
    // IOUtils.copy(in, out);
    // return out.toByteArray();
    // } finally {
    // zipFile.close();
    // IOUtils.closeQuietly(in);
    // IOUtils.closeQuietly(out);
    // }

    try {
      if (forceDump) {
        if (StringUtils.isBlank(nodeId)) {
          logger.info("Forcing dump creation for all datasets");
          return DcatApSerializer.searchResultToDcatAp(MetadataCacheManager.searchAllDatasets(),
              dumpFormat, dumpProfile, DcatApWriteType.FILE).getBytes(StandardCharsets.UTF_8);
        } else {
          logger.info("Forcing dump creation for nodeID: " + nodeId);
          return DcatApSerializer.searchResultToDcatApByNode(nodeId,
              MetadataCacheManager.searchAllDatasetsByOdmsNode(Integer.parseInt(nodeId)),
              dumpFormat, dumpProfile, DcatApWriteType.FILE).getBytes(StandardCharsets.UTF_8);
        }
      } else {
        logger.info("Reading dump file" + (StringUtils.isNotBlank(nodeId) ? ("for nodeID: " + nodeId)
            : "" + " from file system"));
        byte[] val = Files.readAllBytes(Paths.get(globalDumpFilePath + globalDumpFileName
            + (StringUtils.isBlank(nodeId) ? "" : "_node_" + nodeId)
            + (returnZip ? ".zip" : "")));
            // read as ANSI
            String ansiString = new String(val, Charset.forName("ISO-8859-1"));
            return ansiString.getBytes(StandardCharsets.UTF_8);
      }

    } catch (NoSuchFileException e) {
      logger.info("No dump file found" + (StringUtils.isNotBlank(nodeId) ? ("for nodeID: " + nodeId)
          : "" + " Starting to create a new one"));

      // TODO Provide dumpReady flag of the Node for availability polling

      // Create the dump file for the relative node

      if (StringUtils.isBlank(nodeId)) {
        logger.info("Creating dump for all datasets");
        return DcatApSerializer.searchResultToDcatAp(MetadataCacheManager.searchAllDatasets(),
            dumpFormat, dumpProfile, DcatApWriteType.FILE).getBytes();
      } else {
        logger.info("Creating dump for nodeID: " + nodeId);
        return DcatApSerializer.searchResultToDcatApByNode(nodeId,
            MetadataCacheManager.searchAllDatasetsByOdmsNode(Integer.parseInt(nodeId)), dumpFormat,
            dumpProfile, DcatApWriteType.FILE).getBytes(StandardCharsets.UTF_8);
      }

    }

  }

  /**
   * Send dump to repository.
   *
   * @param node the node
   * @throws Exception the exception
   */
  public static void sendDumpToRepository(OdmsCatalogue node) throws Exception {
    byte[] file = Files
        .readAllBytes(Paths.get(globalDumpFilePath + globalDumpFileName + "_node_" + node.getId()));
    // Context of the RDF equals the catalogue's host
    LodCacheManager.deleteRdf(node.getHost());
    LodCacheManager.addCatalogueDump(node, file);
  }
}
