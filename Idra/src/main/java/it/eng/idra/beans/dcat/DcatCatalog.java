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

package it.eng.idra.beans.dcat;

import it.eng.idra.beans.odms.OdmsCatalogue;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

public class DcatCatalog {

  private static final transient Resource RDFClass = DCAT.Catalog;

  private List<DcatDataset> datasets;

  private List<DcatProperty> titles;

  private List<DcatProperty> descriptions;

  private FoafAgent publisher;

  private DcatProperty releaseDate;

  private DcatProperty updateDate;

  private List<DcatProperty> themeTaxonomy;

  private List<DcatProperty> languages;

  private DcatProperty homepage;

  private DcatProperty license;

  private DcatProperty rigths;

  private List<DcatProperty> spatials;

  /**
   * Instantiates a new dcat catalog.
   *
   * @param node the node
   * @param datasets the datasets
   * @param titles the titles
   * @param descriptions the descriptions
   * @param publisher the publisher
   * @param releaseDate the release date
   * @param updateDate the update date
   * @param themeTaxonomy the theme taxonomy
   * @param languages the languages
   * @param homepage the homepage
   * @param license the license
   * @param rigths the rigths
   * @param spatials the spatials
   */
  public DcatCatalog(OdmsCatalogue node, List<DcatDataset> datasets,
      List<String> titles, List<String> descriptions,
      FoafAgent publisher, String releaseDate, 
      String updateDate, List<String> themeTaxonomy, List<String> languages,
      String homepage, String license, String rigths, List<String> spatials) {

    super();
    this.datasets = datasets;
    this.titles = titles.stream().map(title -> new DcatProperty("dct:title", RDFS.Literal, title))
        .collect(Collectors.toList());
    this.descriptions = descriptions.stream()
        .map(description -> new DcatProperty("dct:description", RDFS.Literal, description))
        .collect(Collectors.toList());
    this.publisher = (publisher != null ? publisher
        : new FoafAgent(DCTerms.publisher.getURI(),
            "", "", "", "", "", "", String.valueOf(node.getId())));
    this.releaseDate = new DcatProperty("dct:releaseDate", RDFS.Literal, releaseDate);
    this.updateDate = new DcatProperty("dct:updateDate", RDFS.Literal, updateDate);
    this.themeTaxonomy = themeTaxonomy.stream()
        .map(theme -> new DcatProperty("dcat:themeTaxonomy", 
            SKOS.ConceptScheme, theme)).collect(Collectors.toList());
    this.languages = languages.stream()
        .map(language -> new DcatProperty("dct:language", DCTerms.LinguisticSystem, language))
        .collect(Collectors.toList());
    this.homepage = new DcatProperty("foaf:homepage", FOAF.Document, homepage);
    this.license = new DcatProperty("dct:license", DCTerms.LicenseDocument, license);
    this.rigths = new DcatProperty("dct:rights", DCTerms.RightsStatement, rigths);
    this.spatials = spatials.stream().map(spatial -> 
    new DcatProperty("dct:spatial", DCTerms.Location, spatial))
        .collect(Collectors.toList());

  }

  public List<DcatDataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<DcatDataset> datasets) {
    this.datasets = datasets;
  }

  public List<DcatProperty> getTitle() {
    return titles;
  }

  public void setTitle(List<DcatProperty> title) {
    this.titles = title;
  }

  public List<DcatProperty> getDescription() {
    return descriptions;
  }

  public void setDescription(List<DcatProperty> description) {
    this.descriptions = description;
  }

  public FoafAgent getPublisher() {
    return publisher;
  }

  public void setPublisher(FoafAgent publisher) {
    this.publisher = publisher;
  }

  public DcatProperty getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(DcatProperty releaseDate) {
    this.releaseDate = releaseDate;
  }

  public DcatProperty getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(DcatProperty updateDate) {
    this.updateDate = updateDate;
  }

  public List<DcatProperty> getThemeTaxonomy() {
    return themeTaxonomy;
  }

  public void setThemeTaxonomy(List<DcatProperty> themeTaxonomy) {
    this.themeTaxonomy = themeTaxonomy;
  }

  public List<DcatProperty> getLanguage() {
    return languages;
  }

  public void setLanguage(List<DcatProperty> language) {
    this.languages = language;
  }

  public DcatProperty getHomepage() {
    return homepage;
  }

  public void setHomepage(DcatProperty homepage) {
    this.homepage = homepage;
  }

  public DcatProperty getLicense() {
    return license;
  }

  public void setLicense(DcatProperty license) {
    this.license = license;
  }

  public DcatProperty getRigths() {
    return rigths;
  }

  public void setRigths(DcatProperty rigths) {
    this.rigths = rigths;
  }

  public List<DcatProperty> getSpatial() {
    return spatials;
  }

  public void setSpatial(List<DcatProperty> spatial) {
    this.spatials = spatial;
  }

  /**
   * Gets the RDF class.
   *
   * @return the RDF class
   */
  public static Resource getRdfClass() {
    return RDFClass;
  }

  @Override
  public String toString() {
    return "DCATCatalog [datasets=" + datasets 
        + ", titles=" + titles + ", descriptions=" + descriptions
        + ", publisher=" + publisher + ", releaseDate=" + releaseDate + ", updateDate=" + updateDate
        + ", themeTaxonomy=" + themeTaxonomy + ", languages=" 
        + languages + ", homepage=" + homepage + ", license="
        + license + ", rigths=" + rigths + ", spatials=" + spatials + "]";
  }

}
