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
package it.eng.idra.beans;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "distribution_datalet")
public class Datalet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
//	@GeneratedValue(generator = "uuid")
//	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "datalet_html", columnDefinition = "LONGTEXT")
	private String datalet_html;
	
	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;
	
	//This field is true when the title is created by Federation Manager otherwise is false
	@Column(name = "customTitle")
	private boolean customTitle;
	
	@Column(name = "register_date", updatable = false)
	// @Temporal(TemporalType.TIMESTAMP)
	private ZonedDateTime registerDate;
	
	@Column(name = "last_seen_date", updatable = true)
	// @Temporal(TemporalType.TIMESTAMP)
	private ZonedDateTime lastSeenDate;
	
	@Column(name="views")
	private int views;
	
	@Column(name="dataset_id")
	private String datasetID;
	
//	@Transient
	@Column(name="nodeID")
	private String nodeID;
	
//	@Transient
	@Column(name="distribution_id")
	private String distributionID;
	
	public Datalet() {
		super();
	}
	
	public Datalet(String id, String datalet_html,String datasetID,String distributionID, String nodeID,boolean customTitle,ZonedDateTime registerDate,ZonedDateTime lastSeenDate,int views) {
		super();
		this.id = id;
		this.datalet_html = datalet_html;
		this.datasetID=datasetID;
		this.datasetID=datasetID;
		this.nodeID=nodeID;
		this.distributionID=distributionID;
		this.customTitle=customTitle;
		this.registerDate=registerDate;
		this.lastSeenDate = lastSeenDate;
		this.views=views;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDistributionID() {
		return distributionID;
	}

	public void setDistributionID(String distributionID) {
		this.distributionID = distributionID;
	}
	
	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getDatalet_html() {
		return datalet_html;
	}

	public void setDatalet_html(String datalet_html) {
		this.datalet_html = datalet_html;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDatasetID() {
		return datasetID;
	}

	public void setDatasetID(String datasetID) {
		this.datasetID = datasetID;
	}
	
	public boolean isCustomTitle() {
		return customTitle;
	}

	public void setCustomTitle(boolean customTitle) {
		this.customTitle = customTitle;
	}

	public ZonedDateTime getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(ZonedDateTime registerDate) {
		this.registerDate = registerDate;
	}

	public ZonedDateTime getLastSeenDate() {
		return lastSeenDate;
	}

	public void setLastSeenDate(ZonedDateTime lastSeenDate) {
		this.lastSeenDate = lastSeenDate;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "Datalet [id=" + id + ", \ndistID=" + distributionID + ", \ndatasetID="+datasetID+", \nnodeID="+nodeID+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Datalet other = (Datalet) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
