/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2025 Engineering Ingegneria Informatica S.p.A.
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

package it.eng.idra.beans.dcat;

import it.eng.idra.cache.CacheContentType;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.annotations.SerializedName;

// TODO: Auto-generated Javadoc
/**
 * Represents a DCAT Relationship.
 *
 * @author
 */

@Entity
@Table(name = "dcat_relationship")
public class Relationship {

    /** The Constant RDFClass. */
    private static final transient Resource RDFClass = DCAT.Relationship;

    /** The id. */
    private String relationship_id;

    /** The resource uri. */
    private DcatProperty had_role;

    /** The property uri. */
    private DcatProperty relation;

    /** The node id. */
    @SerializedName(value = "nodeID")
    @Column(name = "nodeID")
    private String nodeId;

    /**
     * Instantiates a new Relationship.
     */
    public Relationship() {
    }

    /**
     * Instantiates a new Relationship.
     *
     * @param had_role the property uri
     * @param relation the resource uri
     * @param nodeId   the node ID
     */
    public Relationship(String had_role, String relation, String nodeId) {
        setHad_role(new DcatProperty(DCAT.hadRole, DCAT.Role, had_role));
        setRelation(new DcatProperty(DCTerms.relation.getURI(), DCAT.Relationship, relation));
        setNodeId(nodeId);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "relationship_id")
    public String getRelationship_id() {
        return relationship_id;
    }

    /**
     * Sets the id.
     *
     * @param relationship_id the new id
     */
    public void setRelationship_id(String relationship_id) {
        this.relationship_id = relationship_id;
    }

    /**
     * Gets the property had_role.
     *
     * @return the property had_role
     */
    // @Transient
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "had_role")) })
    public DcatProperty getHad_role() {
        return had_role;
    }

    /**
     * Sets the property had_role.
     *
     * @param had_role the new property had_role
     */
    public void setHad_role(DcatProperty had_role) {
        this.had_role = had_role;
    }

    /**
     * Gets the property relation.
     *
     * @return the property relation
     */
    // @Transient
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "relation")) })
    public DcatProperty getRelation() {
        return relation;
    }

    /**
     * Sets the resource relation.
     *
     * @param relation the new resource relation
     */

    public void setRelation(DcatProperty relation) {
        this.relation = relation;
    }

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId the new node id
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Gets the rdf class.
     *
     * @return the rdf class
     */
    @Transient
    public static Resource getRdfClass() {
        return RDFClass;
    }

    /**
     * To doc.
     *
     * @param contentType the content type
     * @return the solr input document
     */
    public SolrInputDocument toDoc(CacheContentType contentType) {

        SolrInputDocument doc = new SolrInputDocument();
        if (relationship_id != null) {
            doc.addField("id", this.relationship_id);
        }
        if (had_role != null) {
            doc.addField("had_role", this.had_role);
        }
        if (relation != null) {
            doc.addField("relation", this.relation);
        }
        if (this.nodeId != null)
            doc.addField("nodeID", this.nodeId);

        return doc;
    }

    /**
     * Doc
     *
     * @param doc      the doc
     * @param relation the property relation
     * @param had_role the had_role
     * @return
     */
    public static Relationship docToRelationship(SolrDocument doc, String nodeId) {

        String had_role = null;
        String relation = null;

        if (doc.getFieldValue("had_role") != null) {
            had_role = doc.getFieldValue("had_role").toString();
        }
        if (doc.getFieldValue("relation") != null) {
            relation = doc.getFieldValue("relation").toString();
        }

        Relationship f = new Relationship(had_role, relation, nodeId);

        if (doc.getFieldValue("id") != null) {
            f.setRelationship_id(doc.getFieldValue("id").toString());
        }
        return f;
    }

    public static List<Relationship> jsonArrayToRelationship(JSONArray array, String nodeId) {
        List<Relationship> result = new ArrayList<Relationship>();

        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);
            result.add(new Relationship(
                    obj.optString("had_role"),
                    obj.optString("relation"),
                    nodeId));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Relationship [id=" + relationship_id + ", had_role=" + had_role
                + ", relation=" + relation + "]";
    }

}