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
package it.eng.idra.beans.odms;

import java.util.ArrayList;
import java.util.List;

import it.eng.idra.beans.dcat.DCATDataset;

public class ODMSSynchronizationResult {
	
	private List<DCATDataset> addedDatasets;
	private List<DCATDataset> deletedDatasets;
	private List<DCATDataset> changedDatasets;
	
	public ODMSSynchronizationResult(){
		addedDatasets = new ArrayList<DCATDataset>();
		deletedDatasets = new ArrayList<DCATDataset>();
		changedDatasets = new ArrayList<DCATDataset>();
		
	}
	
	public List<DCATDataset> getAddedDatasets() {
		return addedDatasets;
	}
	public void setAddedDatasets(List<DCATDataset> addedDatasets) {
		this.addedDatasets = addedDatasets;
	}
	public List<DCATDataset> getDeletedDatasets() {
		return deletedDatasets;
	}
	public void setDeletedDatasets(List<DCATDataset> deletedDatasets) {
		this.deletedDatasets = deletedDatasets;
	}
	public List<DCATDataset> getChangedDatasets() {
		return changedDatasets;
	}
	public void setChangedDatasets(List<DCATDataset> changedDatasets) {
		this.changedDatasets = changedDatasets;
	}
	
	public void addToAddedList(DCATDataset dataset){
		this.addedDatasets.add(dataset);
	}
	
	public void addToChangedList(DCATDataset dataset){
		this.changedDatasets.add(dataset);
	}
	
	public void addToDeletedList(DCATDataset dataset){
		this.deletedDatasets.add(dataset);
	}
	
    public boolean isEmpty() 
    {
        return ( changedDatasets.size()==0 && addedDatasets.size()==0 && deletedDatasets.size()==0 );
    }
	
	
	
	
}
