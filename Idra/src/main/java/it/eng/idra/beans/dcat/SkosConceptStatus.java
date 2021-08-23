/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.dcat;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.solr.common.SolrDocument;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class SkosConceptStatus.
 */
@Entity
@Table(name = "status")
@DiscriminatorValue("3")
public class SkosConceptStatus extends SkosConcept {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new skos concept status.
   */
  public SkosConceptStatus() {
    super();
  }

  /**
   * Instantiates a new skos concept status.
   *
   * @param concept the concept
   */
  public SkosConceptStatus(SkosConcept concept) {
    super(concept.getPropertyUri(), concept.getResourceUri(), concept.getPrefLabel(),
        concept.getNodeId());
  }

  /**
   * Instantiates a new skos concept status.
   *
   * @param propertyUri the property uri
   * @param resourceUri the resource uri
   * @param prefLabel   the pref label
   * @param nodeId      the node id
   */
  public SkosConceptStatus(String propertyUri, String resourceUri, List<SkosPrefLabel> prefLabel,
      String nodeId) {
    super(propertyUri, resourceUri, prefLabel, nodeId);
  }

  /**
   * Json to SKOS concept.
   *
   * @param obj         the obj
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the skos concept status
   */
  public static SkosConceptStatus jsonToSkosConcept(JSONObject obj, String propertyUri,
      String nodeId) {

    return new SkosConceptStatus(propertyUri, obj.optString("resourceUri"),
        SkosPrefLabel.jsonArrayToPrefLabelList(obj.getJSONArray("prefLabel"), nodeId), nodeId);
  }

  /**
   * Doc to skos concept.
   *
   * @param doc         the doc
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the skos concept status
   */
  public static SkosConceptStatus docToSkosConcept(SolrDocument doc, String propertyUri,
      String nodeId) {

    SkosConceptStatus t = new SkosConceptStatus(propertyUri,
        (String) doc.getFieldValue("resourceUri"), SkosPrefLabel.jsonArrayToPrefLabelList(
            new JSONArray(doc.getFieldValue("prefLabel").toString()), nodeId),
        nodeId);
    t.setId(doc.getFieldValue("id").toString());

    return t;

  }

}
