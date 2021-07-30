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

package it.eng.idra.beans.odms;

import it.eng.idra.beans.dcat.DcatDataset;
import java.util.ArrayList;
import java.util.List;

public class OdmsSynchronizationResult {

  private List<DcatDataset> addedDatasets;
  private List<DcatDataset> deletedDatasets;
  private List<DcatDataset> changedDatasets;

  /**
   * Instantiates a new odms synchronization result.
   */
  public OdmsSynchronizationResult() {
    addedDatasets = new ArrayList<DcatDataset>();
    deletedDatasets = new ArrayList<DcatDataset>();
    changedDatasets = new ArrayList<DcatDataset>();

  }

  public List<DcatDataset> getAddedDatasets() {
    return addedDatasets;
  }

  public void setAddedDatasets(List<DcatDataset> addedDatasets) {
    this.addedDatasets = addedDatasets;
  }

  public List<DcatDataset> getDeletedDatasets() {
    return deletedDatasets;
  }

  public void setDeletedDatasets(List<DcatDataset> deletedDatasets) {
    this.deletedDatasets = deletedDatasets;
  }

  public List<DcatDataset> getChangedDatasets() {
    return changedDatasets;
  }

  public void setChangedDatasets(List<DcatDataset> changedDatasets) {
    this.changedDatasets = changedDatasets;
  }

  public void addToAddedList(DcatDataset dataset) {
    this.addedDatasets.add(dataset);
  }

  public void addToChangedList(DcatDataset dataset) {
    this.changedDatasets.add(dataset);
  }

  public void addToDeletedList(DcatDataset dataset) {
    this.deletedDatasets.add(dataset);
  }

  public boolean isEmpty() {
    return (changedDatasets.size() == 0 
        && addedDatasets.size() == 0 && deletedDatasets.size() == 0);
  }

}
