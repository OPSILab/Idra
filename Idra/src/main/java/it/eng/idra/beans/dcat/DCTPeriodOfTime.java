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
package it.eng.idra.beans.dcat;

import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;

import it.eng.idra.cache.CacheContentType;

@Entity
@Table(name = "dcat_periodOfTime")
public class DCTPeriodOfTime {

	private transient static final Resource RDFClass = DCTerms.PeriodOfTime;

	private String id;
	private transient String nodeID;
	private String uri;

	private DCATProperty startDate;
	private DCATProperty endDate;

	public DCTPeriodOfTime() {
	}

	public DCTPeriodOfTime(String uri, String startDate, String endDate, String nodeID) {
		super();
		setUri(uri);
		this.nodeID = nodeID;
		setStartDate(new DCATProperty(ResourceFactory.createProperty("http://schema.org#startDate"),
				RDFS.Literal.getURI(), startDate));
		setEndDate(new DCATProperty(ResourceFactory.createProperty("http://schema.org#endDate"), RDFS.Literal.getURI(),
				endDate));
	}

	public DCTPeriodOfTime(String uri, DCATProperty startDate, DCATProperty endDate, String nodeID) {
		super();
		setUri(uri);
		this.nodeID = nodeID;
		setStartDate(startDate);
		setEndDate(endDate);
	}
	
	
	
	
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "periodOfTime_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "startDate")) })
	public DCATProperty getStartDate() {
		return startDate;
	}

	public void setStartDate(DCATProperty startDate) {
		this.startDate = startDate;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "endDate")) })
	public DCATProperty getEndDate() {
		return endDate;
	}

	public void setEndDate(DCATProperty endDate) {
		this.endDate = endDate;
	}

	@Transient
	public static Resource getRDFClass() {
		return RDFClass;
	}

	public SolrInputDocument toDoc(CacheContentType contentType) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", this.id);
		doc.addField("nodeID", this.nodeID);
		doc.addField("content_type", contentType.toString());
		doc.addField("startDate", this.startDate != null ? this.startDate.getValue() : "");
		doc.addField("endDate", this.endDate != null ? this.endDate.getValue() : "");
		return doc;
	}

	public static DCTPeriodOfTime docToDCTPeriodOfTime(SolrDocument doc, String uri, String nodeID) {
		DCTPeriodOfTime p = new DCTPeriodOfTime(uri, doc.getFieldValue("startDate").toString(),
				doc.getFieldValue("endDate").toString(), nodeID);
		p.setId(doc.getFieldValue("id").toString());
		return p; 

	}

	@Override
	public String toString() {
		return "DCTPeriodOfTime [id=" + id + ", uri=" + uri + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

}
