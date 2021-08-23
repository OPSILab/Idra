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
 * The Class SkosConceptTheme.
 */
@Entity
@Table(name = "theme")
@DiscriminatorValue("1")
public class SkosConceptTheme extends SkosConcept {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new skos concept theme.
   */
  public SkosConceptTheme() {
    super();
  }

  /**
   * Instantiates a new skos concept theme.
   *
   * @param concept the concept
   */
  public SkosConceptTheme(SkosConcept concept) {
    super(concept.getPropertyUri(), concept.getResourceUri(), concept.getPrefLabel(),
        concept.getNodeId());
  }

  /**
   * Instantiates a new skos concept theme.
   *
   * @param propertyUri the property uri
   * @param resourceUri the resource uri
   * @param prefLabel   the pref label
   * @param nodeId      the node id
   */
  public SkosConceptTheme(String propertyUri, String resourceUri, List<SkosPrefLabel> prefLabel,
      String nodeId) {
    super(propertyUri, resourceUri, prefLabel, nodeId);
  }

  /**
   * Json to skos concept.
   *
   * @param obj         the obj
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the skos concept theme
   */
  public static SkosConceptTheme jsonToSkosConcept(JSONObject obj, String propertyUri,
      String nodeId) {

    return new SkosConceptTheme(propertyUri, obj.optString("resourceUri"),
        SkosPrefLabel.jsonArrayToPrefLabelList(obj.getJSONArray("prefLabel"), nodeId), nodeId);
  }

  /**
   * Doc to SKOS concept.
   *
   * @param doc         the doc
   * @param propertyUri the property uri
   * @param nodeId      the node ID
   * @return the skos concept theme
   */
  public static SkosConceptTheme docToSkosConcept(SolrDocument doc, String propertyUri,
      String nodeId) {
    SkosConceptTheme t = new SkosConceptTheme(propertyUri,
        (String) doc.getFieldValue("resourceUri"), SkosPrefLabel.jsonArrayToPrefLabelList(
            new JSONArray(doc.getFieldValue("prefLabel").toString()), nodeId),
        nodeId);
    t.setId(doc.getFieldValue("id").toString());
    return t;
  }

}
