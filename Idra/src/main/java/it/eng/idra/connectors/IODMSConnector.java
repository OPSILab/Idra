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
package it.eng.idra.connectors;

import java.util.HashMap;
import java.util.List;

import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;

public interface IODMSConnector {

	public List<DCATDataset> findDatasets(HashMap<String,Object> searchParameters) throws Exception;
	public int countSearchDatasets(HashMap<String,Object> searchParameters) throws Exception;
	public int countDatasets() throws Exception;
	DCATDataset datasetToDCAT(Object dataset,ODMSCatalogue node) throws Exception;
	public DCATDataset getDataset(String datasetId) throws Exception;
	public List<DCATDataset> getAllDatasets() throws Exception;
//	public HashMap<DCATDataset, String> getChangedDatasets(List<DCATDataset> oldDatasets,String startingDate) throws Exception;
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets,String startingDate) throws Exception;
}
