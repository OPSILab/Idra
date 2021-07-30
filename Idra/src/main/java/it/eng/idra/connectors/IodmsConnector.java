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

package it.eng.idra.connectors;

import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface IodmsConnector.
 */
public interface IodmsConnector {

  /**
   * Find datasets.
   *
   * @param searchParameters the search parameters
   * @return the list
   * @throws Exception the exception
   */
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception;

  /**
   * Count search datasets.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws Exception the exception
   */
  public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception;

  /**
   * Count datasets.
   *
   * @return the int
   * @throws Exception the exception
   */
  public int countDatasets() throws Exception;

  /**
   * Dataset to dcat.
   *
   * @param dataset the dataset
   * @param node    the node
   * @return the dcat dataset
   * @throws Exception the exception
   */
  DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception;

  /**
   * Gets the dataset.
   *
   * @param datasetId the dataset id
   * @return the dataset
   * @throws Exception the exception
   */
  public DcatDataset getDataset(String datasetId) throws Exception;

  /**
   * Gets the all datasets.
   *
   * @return the all datasets
   * @throws Exception the exception
   */
  public List<DcatDataset> getAllDatasets() throws Exception;

  /**
   * Gets the changed datasets.
   *
   * @param oldDatasets  the old datasets
   * @param startingDate the starting date
   * @return the changed datasets
   * @throws Exception the exception
   */
  public OdmsSynchronizationResult getChangedDatasets(List<DcatDataset> oldDatasets,
      String startingDate) throws Exception;
}
