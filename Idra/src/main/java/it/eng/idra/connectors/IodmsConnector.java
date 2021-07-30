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

public interface IodmsConnector {

  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception;

  public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception;

  public int countDatasets() throws Exception;

  DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception;

  public DcatDataset getDataset(String datasetId) throws Exception;

  public List<DcatDataset> getAllDatasets() throws Exception;

  public OdmsSynchronizationResult getChangedDatasets(
      List<DcatDataset> oldDatasets, String startingDate)
      throws Exception;
}
