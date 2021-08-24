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

package it.eng.idra.beans.odms;

import it.eng.idra.beans.dcat.DcatDataset;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsSynchronizationResult.
 */
public class OdmsSynchronizationResult {

  /** The added datasets. */
  private List<DcatDataset> addedDatasets;

  /** The deleted datasets. */
  private List<DcatDataset> deletedDatasets;

  /** The changed datasets. */
  private List<DcatDataset> changedDatasets;

  /**
   * Instantiates a new odms synchronization result.
   */
  public OdmsSynchronizationResult() {
    addedDatasets = new ArrayList<DcatDataset>();
    deletedDatasets = new ArrayList<DcatDataset>();
    changedDatasets = new ArrayList<DcatDataset>();

  }

  /**
   * Gets the added datasets.
   *
   * @return the added datasets
   */
  public List<DcatDataset> getAddedDatasets() {
    return addedDatasets;
  }

  /**
   * Sets the added datasets.
   *
   * @param addedDatasets the new added datasets
   */
  public void setAddedDatasets(List<DcatDataset> addedDatasets) {
    this.addedDatasets = addedDatasets;
  }

  /**
   * Gets the deleted datasets.
   *
   * @return the deleted datasets
   */
  public List<DcatDataset> getDeletedDatasets() {
    return deletedDatasets;
  }

  /**
   * Sets the deleted datasets.
   *
   * @param deletedDatasets the new deleted datasets
   */
  public void setDeletedDatasets(List<DcatDataset> deletedDatasets) {
    this.deletedDatasets = deletedDatasets;
  }

  /**
   * Gets the changed datasets.
   *
   * @return the changed datasets
   */
  public List<DcatDataset> getChangedDatasets() {
    return changedDatasets;
  }

  /**
   * Sets the changed datasets.
   *
   * @param changedDatasets the new changed datasets
   */
  public void setChangedDatasets(List<DcatDataset> changedDatasets) {
    this.changedDatasets = changedDatasets;
  }

  /**
   * Adds the to added list.
   *
   * @param dataset the dataset
   */
  public void addToAddedList(DcatDataset dataset) {
    this.addedDatasets.add(dataset);
  }

  /**
   * Adds the to changed list.
   *
   * @param dataset the dataset
   */
  public void addToChangedList(DcatDataset dataset) {
    this.changedDatasets.add(dataset);
  }

  /**
   * Adds the to deleted list.
   *
   * @param dataset the dataset
   */
  public void addToDeletedList(DcatDataset dataset) {
    this.deletedDatasets.add(dataset);
  }

  /**
   * Checks if is empty.
   *
   * @return true, if is empty
   */
  public boolean isEmpty() {
    return (changedDatasets.size() == 0 && addedDatasets.size() == 0
        && deletedDatasets.size() == 0);
  }

}
