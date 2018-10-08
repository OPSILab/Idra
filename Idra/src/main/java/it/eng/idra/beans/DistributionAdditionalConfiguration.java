package it.eng.idra.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "distribution_additional_config")
public class DistributionAdditionalConfiguration {

	private String id;
	private String nodeID;
	private String type;
	private String query;
	
	public DistributionAdditionalConfiguration() {
		super();
	}
	
	public DistributionAdditionalConfiguration(String id, String nodeID) {
		super();
		this.id = id;
		this.nodeID = nodeID;
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	//@Column(name = "orion_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Lob
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
}
