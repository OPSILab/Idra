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

import it.eng.idra.beans.odms.OdmsCatalogue;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatCatalog.
 */
public class DcatCatalog {

  /** The Constant RDFClass. */
  private static final transient Resource RDFClass = DCAT.Catalog;

  /** The datasets. */
  private List<DcatDataset> datasets;

  /** The titles. */
  private List<DcatProperty> titles;

  /** The descriptions. */
  private List<DcatProperty> descriptions;

  /** The publisher. */
  private FoafAgent publisher;

  /** The release date. */
  private DcatProperty releaseDate;

  /** The update date. */
  private DcatProperty updateDate;

  /** The theme taxonomy. */
  private List<DcatProperty> themeTaxonomy;

  /** The languages. */
  private List<DcatProperty> languages;

  /** The homepage. */
  private DcatProperty homepage;

  /** The license. */
  private DcatProperty license;

  /** The rigths. */
  private DcatProperty rigths;

  /** The spatials. */
  private List<DcatProperty> spatials;

  /**
   * Instantiates a new dcat catalog.
   *
   * @param node          the node
   * @param datasets      the datasets
   * @param titles        the titles
   * @param descriptions  the descriptions
   * @param publisher     the publisher
   * @param releaseDate   the release date
   * @param updateDate    the update date
   * @param themeTaxonomy the theme taxonomy
   * @param languages     the languages
   * @param homepage      the homepage
   * @param license       the license
   * @param rigths        the rigths
   * @param spatials      the spatials
   */
  public DcatCatalog(OdmsCatalogue node, List<DcatDataset> datasets, List<String> titles,
      List<String> descriptions, FoafAgent publisher, String releaseDate, String updateDate,
      List<String> themeTaxonomy, List<String> languages, String homepage, String license,
      String rigths, List<String> spatials) {

    super();
    this.datasets = datasets;
    this.titles = titles.stream().map(title -> new DcatProperty("dct:title", RDFS.Literal, title))
        .collect(Collectors.toList());
    this.descriptions = descriptions.stream()
        .map(description -> new DcatProperty("dct:description", RDFS.Literal, description))
        .collect(Collectors.toList());
    this.publisher = (publisher != null ? publisher
        : new FoafAgent(DCTerms.publisher.getURI(), "", "", "", "", "", "",
            String.valueOf(node.getId())));
    this.releaseDate = new DcatProperty("dct:releaseDate", RDFS.Literal, releaseDate);
    this.updateDate = new DcatProperty("dct:updateDate", RDFS.Literal, updateDate);
    this.themeTaxonomy = themeTaxonomy.stream()
        .map(theme -> new DcatProperty("dcat:themeTaxonomy", SKOS.ConceptScheme, theme))
        .collect(Collectors.toList());
    this.languages = languages.stream()
        .map(language -> new DcatProperty("dct:language", DCTerms.LinguisticSystem, language))
        .collect(Collectors.toList());
    this.homepage = new DcatProperty("foaf:homepage", FOAF.Document, homepage);
    this.license = new DcatProperty("dct:license", DCTerms.LicenseDocument, license);
    this.rigths = new DcatProperty("dct:rights", DCTerms.RightsStatement, rigths);
    this.spatials = spatials.stream()
        .map(spatial -> new DcatProperty("dct:spatial", DCTerms.Location, spatial))
        .collect(Collectors.toList());

  }

  /**
   * Gets the datasets.
   *
   * @return the datasets
   */
  public List<DcatDataset> getDatasets() {
    return datasets;
  }

  /**
   * Sets the datasets.
   *
   * @param datasets the new datasets
   */
  public void setDatasets(List<DcatDataset> datasets) {
    this.datasets = datasets;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public List<DcatProperty> getTitle() {
    return titles;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(List<DcatProperty> title) {
    this.titles = title;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public List<DcatProperty> getDescription() {
    return descriptions;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(List<DcatProperty> description) {
    this.descriptions = description;
  }

  /**
   * Gets the publisher.
   *
   * @return the publisher
   */
  public FoafAgent getPublisher() {
    return publisher;
  }

  /**
   * Sets the publisher.
   *
   * @param publisher the new publisher
   */
  public void setPublisher(FoafAgent publisher) {
    this.publisher = publisher;
  }

  /**
   * Gets the release date.
   *
   * @return the release date
   */
  public DcatProperty getReleaseDate() {
    return releaseDate;
  }

  /**
   * Sets the release date.
   *
   * @param releaseDate the new release date
   */
  public void setReleaseDate(DcatProperty releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Gets the update date.
   *
   * @return the update date
   */
  public DcatProperty getUpdateDate() {
    return updateDate;
  }

  /**
   * Sets the update date.
   *
   * @param updateDate the new update date
   */
  public void setUpdateDate(DcatProperty updateDate) {
    this.updateDate = updateDate;
  }

  /**
   * Gets the theme taxonomy.
   *
   * @return the theme taxonomy
   */
  public List<DcatProperty> getThemeTaxonomy() {
    return themeTaxonomy;
  }

  /**
   * Sets the theme taxonomy.
   *
   * @param themeTaxonomy the new theme taxonomy
   */
  public void setThemeTaxonomy(List<DcatProperty> themeTaxonomy) {
    this.themeTaxonomy = themeTaxonomy;
  }

  /**
   * Gets the language.
   *
   * @return the language
   */
  public List<DcatProperty> getLanguage() {
    return languages;
  }

  /**
   * Sets the language.
   *
   * @param language the new language
   */
  public void setLanguage(List<DcatProperty> language) {
    this.languages = language;
  }

  /**
   * Gets the homepage.
   *
   * @return the homepage
   */
  public DcatProperty getHomepage() {
    return homepage;
  }

  /**
   * Sets the homepage.
   *
   * @param homepage the new homepage
   */
  public void setHomepage(DcatProperty homepage) {
    this.homepage = homepage;
  }

  /**
   * Gets the license.
   *
   * @return the license
   */
  public DcatProperty getLicense() {
    return license;
  }

  /**
   * Sets the license.
   *
   * @param license the new license
   */
  public void setLicense(DcatProperty license) {
    this.license = license;
  }

  /**
   * Gets the rigths.
   *
   * @return the rigths
   */
  public DcatProperty getRigths() {
    return rigths;
  }

  /**
   * Sets the rigths.
   *
   * @param rigths the new rigths
   */
  public void setRigths(DcatProperty rigths) {
    this.rigths = rigths;
  }

  /**
   * Gets the spatial.
   *
   * @return the spatial
   */
  public List<DcatProperty> getSpatial() {
    return spatials;
  }

  /**
   * Sets the spatial.
   *
   * @param spatial the new spatial
   */
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCATCatalog [datasets=" + datasets + ", titles=" + titles + ", descriptions="
        + descriptions + ", publisher=" + publisher + ", releaseDate=" + releaseDate
        + ", updateDate=" + updateDate + ", themeTaxonomy=" + themeTaxonomy + ", languages="
        + languages + ", homepage=" + homepage + ", license=" + license + ", rigths=" + rigths
        + ", spatials=" + spatials + "]";
  }

}
