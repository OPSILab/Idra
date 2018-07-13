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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import it.eng.idra.beans.odms.ODMSCatalogue;

public class DCATCatalog {

	private transient static final Resource RDFClass = DCAT.Catalog;

	private List<DCATDataset> datasets;

	private List<DCATProperty> titles;

	private List<DCATProperty> descriptions;

	private FOAFAgent publisher;

	private DCATProperty releaseDate;

	private DCATProperty updateDate;

	private List<DCATProperty> themeTaxonomy;

	private List<DCATProperty> languages;

	private DCATProperty homepage;

	private DCATProperty license;

	private DCATProperty rigths;

	private List<DCATProperty> spatials;

	public DCATCatalog(ODMSCatalogue node, List<DCATDataset> datasets, List<String> titles, List<String> descriptions,
			FOAFAgent publisher, String releaseDate, String updateDate, List<String> themeTaxonomy,
			List<String> languages, String homepage, String license, String rigths, List<String> spatials) {

		super();
		this.datasets = datasets;
		this.titles = titles.stream().map(title -> new DCATProperty("dct:title", RDFS.Literal.getURI(), title))
				.collect(Collectors.toList());
		this.descriptions = descriptions.stream()
				.map(description -> new DCATProperty("dct:description", RDFS.Literal.getURI(), description))
				.collect(Collectors.toList());
		this.publisher = (publisher != null ? publisher
				: new FOAFAgent(DCTerms.publisher.getURI(), "", "", "", "", "", "", String.valueOf(node.getId())));
		this.releaseDate = new DCATProperty("dct:releaseDate", RDFS.Literal.getURI(), releaseDate);
		this.updateDate = new DCATProperty("dct:updateDate", RDFS.Literal.getURI(), updateDate);
		this.themeTaxonomy = themeTaxonomy.stream()
				.map(theme -> new DCATProperty("dcat:themeTaxonomy", SKOS.ConceptScheme.getURI(), theme))
				.collect(Collectors.toList());
		this.languages = languages.stream()
				.map(language -> new DCATProperty("dct:language", DCTerms.LinguisticSystem.getURI(), language))
				.collect(Collectors.toList());
		this.homepage = new DCATProperty("foaf:homepage", FOAF.Document.getURI(), homepage);
		this.license = new DCATProperty("dct:license", DCTerms.LicenseDocument.getURI(), license);
		this.rigths = new DCATProperty("dct:rights", DCTerms.RightsStatement.getURI(), rigths);
		this.spatials = spatials.stream()
				.map(spatial -> new DCATProperty("dct:spatial", DCTerms.Location.getURI(), spatial))
				.collect(Collectors.toList());

	}

	public List<DCATDataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DCATDataset> datasets) {
		this.datasets = datasets;
	}

	public List<DCATProperty> getTitle() {
		return titles;
	}

	public void setTitle(List<DCATProperty> title) {
		this.titles = title;
	}

	public List<DCATProperty> getDescription() {
		return descriptions;
	}

	public void setDescription(List<DCATProperty> description) {
		this.descriptions = description;
	}

	public FOAFAgent getPublisher() {
		return publisher;
	}

	public void setPublisher(FOAFAgent publisher) {
		this.publisher = publisher;
	}

	public DCATProperty getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(DCATProperty releaseDate) {
		this.releaseDate = releaseDate;
	}

	public DCATProperty getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(DCATProperty updateDate) {
		this.updateDate = updateDate;
	}

	public List<DCATProperty> getThemeTaxonomy() {
		return themeTaxonomy;
	}

	public void setThemeTaxonomy(List<DCATProperty> themeTaxonomy) {
		this.themeTaxonomy = themeTaxonomy;
	}

	public List<DCATProperty> getLanguage() {
		return languages;
	}

	public void setLanguage(List<DCATProperty> language) {
		this.languages = language;
	}

	public DCATProperty getHomepage() {
		return homepage;
	}

	public void setHomepage(DCATProperty homepage) {
		this.homepage = homepage;
	}

	public DCATProperty getLicense() {
		return license;
	}

	public void setLicense(DCATProperty license) {
		this.license = license;
	}

	public DCATProperty getRigths() {
		return rigths;
	}

	public void setRigths(DCATProperty rigths) {
		this.rigths = rigths;
	}

	public List<DCATProperty> getSpatial() {
		return spatials;
	}

	public void setSpatial(List<DCATProperty> spatial) {
		this.spatials = spatial;
	}

	public static Resource getRDFClass() {
		return RDFClass;
	}

	@Override
	public String toString() {
		return "DCATCatalog [datasets=" + datasets + ", titles=" + titles + ", descriptions=" + descriptions
				+ ", publisher=" + publisher + ", releaseDate=" + releaseDate + ", updateDate=" + updateDate
				+ ", themeTaxonomy=" + themeTaxonomy + ", languages=" + languages + ", homepage=" + homepage
				+ ", license=" + license + ", rigths=" + rigths + ", spatials=" + spatials + "]";
	}

}
