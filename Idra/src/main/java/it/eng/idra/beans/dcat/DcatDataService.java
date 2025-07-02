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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.annotations.SerializedName;
import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "dcat_data_service")
public class DcatDataService implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The Constant RDFClass. */
    private static final transient Resource RDFClass = DCAT.DataService;

    /** The id. */
    private String dataServiceId;

    /** The node id. */
    @SerializedName(value = "nodeID")
    private transient String nodeId;

    /** The dataset id. */
    /*
     * @SerializedName(value = "dataset_id")
     * private String datasetId;
     */

    /** The applicable legislation. */
    private List<DcatProperty> applicableLegislation; // Legislation mandating the creation or management of the Data
                                                      // Service

    /** The contact point. */
    private List<VcardOrganization> contactPoint; // Contact information for comments about the Data Service

    /** The documentation. */
    private List<DcatProperty> documentation; // Additional information about the Data Service

    /** The endpoint description. */
    private List<DcatProperty> endpointDescription; // Description of services available via the endpoints

    /** The endpoint URL. */
    private List<DcatProperty> endpointURL; // Root location or primary endpoint of the service

    /** The hvd category. */
    private List<DcatProperty> HVDCategory; // HVD category to which this Data Service belongs

    /** The licence. */
    private DcatProperty licence; // Licence under which the Data Service is made available

    /** The rights. */
    private List<DcatProperty> rights; // Rights associated with the Data Service

    /** The serves dataset. */
    // private List<DcatDataset> servesDataset; // Datasets that this Data Service
    // can distribute
    private List<DcatProperty> servesDataset;

    /** The title. */
    private DcatProperty title;

    /**
     * Instantiates a new dcat data service.
     */
    public DcatDataService() {
    }

    /**
     * Instantiates a new dcat data service.
     *
     * @param applicableLegislation the applicable legislation
     * @param contactPoint          the contact point
     * @param documentation         the documentation
     * @param endpointDescription   the endpoint description
     * @param endpointURL           the endpoint URL
     * @param HVDCategory           the hvd category
     * @param licence               the licence
     * @param rights                the rights
     * @param servesDataset         the serves dataset
     * @param nodeId                node id
     * @param datasetId             dataset id
     */
    public DcatDataService(List<String> applicableLegislation, List<VcardOrganization> contactPoint,
            List<String> documentation, List<String> endpointDescription,
            List<String> endpointURL, List<String> HVDCategory, String licence,
            List<String> rights, List<String> servesDataset, String title, String nodeId) {// , String datasetId

        setApplicableLegislation(applicableLegislation != null && !applicableLegislation.isEmpty()
                ? applicableLegislation.stream()
                        .map(item -> new DcatProperty(DCATAP.applicableLegislation, ELI.LegalResource, item))
                        .collect(Collectors.toList())
                : Arrays.asList(new DcatProperty(DCATAP.applicableLegislation, ELI.LegalResource, "")));
        setContactPoint(contactPoint);
        // setContactPoint(contactPoint.stream()
        // .map(cp -> new DcatProperty(DCAT.contactPoint.getURI(), RDFS.Resource, cp))
        // .collect(Collectors.toList()));
        setDocumentation(
                documentation == null || documentation.isEmpty()
                        ? Arrays.asList(new DcatProperty(FOAF.Document.getURI(), FOAF.Document, ""))
                        : documentation.stream()
                                .map(doc -> new DcatProperty(FOAF.Document.getURI(), FOAF.Document, doc))
                                .collect(Collectors.toList()));
        setEndpointDescription(endpointDescription == null || endpointDescription.isEmpty()
                ? Arrays.asList(new DcatProperty(DCAT.endpointDescription, RDFS.Resource, ""))
                : endpointDescription.stream()
                        .map(ed -> new DcatProperty(DCAT.endpointDescription, RDFS.Resource, ed))
                        .collect(Collectors.toList()));
        setEndpointURL(endpointURL == null || endpointURL.isEmpty()
                ? Arrays.asList(new DcatProperty(DCAT.endpointURL, RDFS.Resource, ""))
                : endpointURL.stream()
                        .map(eu -> new DcatProperty(DCAT.endpointURL, RDFS.Resource, eu))
                        .collect(Collectors.toList()));
        setHVDCategory(HVDCategory != null && !HVDCategory.isEmpty()
                ? HVDCategory.stream()
                        .map(item -> new DcatProperty(DCATAP.hvdCategory, SKOS.Concept, item))
                        .collect(Collectors.toList())
                : Arrays.asList(new DcatProperty(DCATAP.hvdCategory, SKOS.Concept, "")));
        setLicence(new DcatProperty(DCTerms.license.getURI(), DCTerms.LicenseDocument, licence));
        setRights(rights == null || rights.isEmpty()
                ? Arrays.asList(new DcatProperty(DCTerms.accessRights.getURI(), DCTerms.RightsStatement, ""))
                : rights.stream()
                        .map(r -> new DcatProperty(DCTerms.accessRights.getURI(), DCTerms.RightsStatement, r))
                        .collect(Collectors.toList()));
        setServesDataset(servesDataset == null || servesDataset.isEmpty()
                ? Arrays.asList(new DcatProperty(DCAT.servesDataset, DCAT.Dataset, ""))
                : servesDataset.stream()
                        .map(r -> new DcatProperty(DCAT.servesDataset, DCAT.Dataset, r))
                        .collect(Collectors.toList()));
        setTitle(new DcatProperty(DCTerms.title, RDFS.Literal, title));
        setNodeId(nodeId);
        // setDatasetId(datasetId);
    }

    // Getters and setters
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "data_service_id")
    public String getDataServiceId() {
        return dataServiceId;
    }

    public void setDataServiceId(String dataServiceId) {
        this.dataServiceId = dataServiceId;
    }

    @Column(name = "nodeID")
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /*
     * @Column(name = "dataset_id")
     * public String getDatasetId() {
     * return datasetId;
     * }
     * 
     * public void setDatasetId(String datasetId) {
     * this.datasetId = datasetId;
     * }
     */

    /*
     * @LazyCollection(LazyCollectionOption.FALSE)
     * 
     * @ElementCollection
     * 
     * @CollectionTable(name = "dcat_applicable_legislation", joinColumns = {
     * // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
     * 
     * @JoinColumn(name = "data_service_id", referencedColumnName =
     * "data_service_id"),
     * 
     * @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
     * 
     * @AttributeOverrides({ @AttributeOverride(name = "value", column
     * = @Column(name = "applicableLegislation")) })
     */
    @Transient
    public List<DcatProperty> getApplicableLegislation() {
        return applicableLegislation;
    }

    public void setApplicableLegislation(List<DcatProperty> applicableLegislation) {
        this.applicableLegislation = applicableLegislation;
    }

    /*
     * @LazyCollection(LazyCollectionOption.FALSE)
     * 
     * @OneToMany(cascade = { CascadeType.ALL })
     * // @Fetch(FetchMode.SELECT), foreignKey =
     * // @ForeignKey(ConstraintMode.NO_CONSTRAINT)
     * // @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName =
     * // "dataset_id"),
     * 
     * @JoinColumns({ @JoinColumn(name = "data_service_id", referencedColumnName =
     * "data_service_id"),
     * 
     * @JoinColumn(name = "nodeID", referencedColumnName = "nodeID")
     * })
     */
    @Transient
    public List<VcardOrganization> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<VcardOrganization> contactPoint) {
        this.contactPoint = contactPoint;
    }

    /*
     * @LazyCollection(LazyCollectionOption.FALSE)
     * 
     * @ElementCollection
     * 
     * @CollectionTable(name = "dcat_documentation", joinColumns = {
     * 
     * @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
     * 
     * @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
     * 
     * @AttributeOverrides({
     * 
     * @AttributeOverride(name = "value", column = @Column(name = "documentation",
     * columnDefinition = "LONGTEXT")) })
     */
    @Transient
    public List<DcatProperty> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<DcatProperty> documentation) {
        this.documentation = documentation;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "dcat_endpoint", joinColumns = {
            // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "data_service_id", referencedColumnName = "data_service_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "description")) })
    public List<DcatProperty> getEndpointDescription() {
        return endpointDescription;
    }

    public void setEndpointDescription(List<DcatProperty> endpointDescription) {
        this.endpointDescription = endpointDescription;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "dcat_endpoint", joinColumns = {
            // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "data_service_id", referencedColumnName = "data_service_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "url", columnDefinition = "LONGTEXT")) })
    public List<DcatProperty> getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(List<DcatProperty> endpointURL) {
        this.endpointURL = endpointURL;
    }

    /*
     * @LazyCollection(LazyCollectionOption.FALSE)
     * 
     * @ElementCollection
     * 
     * @CollectionTable(name = "dcat_hvd_category", joinColumns = {
     * 
     * @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
     * 
     * @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
     * 
     * @AttributeOverrides({ @AttributeOverride(name = "value", column
     * = @Column(name = "HVDCategory")) })
     */
    @Transient
    public List<DcatProperty> getHVDCategory() {
        return HVDCategory;
    }

    public void setHVDCategory(List<DcatProperty> HVDCategory) {
        this.HVDCategory = HVDCategory;
    }

    /*
     * @Embedded
     * 
     * @AttributeOverrides({ @AttributeOverride(name = "value", column
     * = @Column(name = "license")) })
     */
    @Transient
    public DcatProperty getLicence() {
        return licence;
    }

    public void setLicence(DcatProperty licence) {
        this.licence = licence;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "dcat_rights", joinColumns = {
            // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "data_service_id", referencedColumnName = "data_service_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "rights")) })
    public List<DcatProperty> getRights() {
        return rights;
    }

    public void setRights(List<DcatProperty> rights) {
        this.rights = rights;
    }

    /*
     * @LazyCollection(LazyCollectionOption.FALSE)
     * 
     * @OneToMany(cascade = { CascadeType.ALL })
     * // @Fetch(FetchMode.SELECT)
     * 
     * @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName =
     * "dataset_id"),
     * 
     * @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
     * public List<DcatProperty> getServesDataset() {
     * return servesDataset;
     * }
     */

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "dcat_serves_dataset", joinColumns = {
            // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "data_service_id", referencedColumnName = "data_service_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "servesDataset")) })
    public List<DcatProperty> getServesDataset() {
        return servesDataset;
    }

    public void setServesDataset(List<DcatProperty> servesDataset) {
        this.servesDataset = servesDataset;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "title")) })
    public DcatProperty getTitle() {
        return title;
    }

    public void setTitle(DcatProperty title) {
        this.title = title;
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

    public SolrInputDocument toDoc(CacheContentType contentType) {
        SolrInputDocument doc = new SolrInputDocument();
        if (this.dataServiceId != null)
            doc.addField("id", this.dataServiceId);
        if (this.nodeId != null)
            doc.addField("nodeID", this.nodeId);
        // if (this.datasetId != null)
        // doc.addField("datasetIdentifier", this.datasetId);
        // doc.addField("content_type", contentType.toString());

        // new
        if (applicableLegislation != null && !applicableLegislation.isEmpty()) {
            doc.addField("applicableLegislation", applicableLegislation.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));
        }

        if (contactPoint != null && !contactPoint.isEmpty()) {
            try {
                doc.addField("contactPoint", GsonUtil.obj2Json(contactPoint, GsonUtil.vcardListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }
        // if (contactPoint != null && !contactPoint.isEmpty()) {
        // contactPoint.stream().filter(item -> item != null)
        // .forEach(item ->
        // doc.addChildDocument(item.toDoc(CacheContentType.contactPoint)));
        // }

        if (this.documentation != null && !this.documentation.isEmpty())
            doc.addField("documentation", documentation.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));

        if (this.endpointDescription != null && !this.endpointDescription.isEmpty())
            doc.addField("endpointDescription", endpointDescription.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));

        if (this.endpointURL != null && !this.endpointURL.isEmpty())
            doc.addField("endpointURL", endpointURL.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));

        if (HVDCategory != null && !HVDCategory.isEmpty()) {
            doc.addField("HVDCategory", HVDCategory.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));
        }

        if (this.licence != null)
            doc.addField("license", this.licence.getValue());

        if (this.title != null)
            doc.addField("title", this.title.getValue());

        if (this.rights != null && !this.rights.isEmpty())
            doc.addField("rights", rights.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));

        if (this.servesDataset != null && !this.servesDataset.isEmpty()) {
            doc.addField("servesDataset", servesDataset.stream().filter(item -> item != null)
                    .map(item -> item.getValue()).collect(Collectors.toList()));
        }
        return doc;
    }

    public static DcatDataService docToDcatDataService(SolrDocument doc) {
        String dataServiceId = doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : null;
        String nodeId = doc.getFieldValue("nodeID") != null ? doc.getFieldValue("nodeID").toString() : null;

        String licenseVal = doc.getFieldValue("license") != null ? doc.getFieldValue("license").toString() : "";
        String titleVal = doc.getFieldValue("title") != null ? doc.getFieldValue("title").toString() : "";

        DcatDataService service = new DcatDataService(
                (ArrayList<String>) doc.getFieldValue("applicableLegislation"), // applicableLegislation
                null, // contactPoint
                (ArrayList<String>) doc.getFieldValue("documentation"), // documentation
                (ArrayList<String>) doc.getFieldValue("endpointDescription"), // endpointDescription
                (ArrayList<String>) doc.getFieldValue("endpointURL"), // endpoint URL
                (ArrayList<String>) doc.getFieldValue("HVDCategory"), // HVD Category
                licenseVal, // licence
                (ArrayList<String>) doc.getFieldValue("rights"), // rights
                (ArrayList<String>) doc.getFieldValue("servesDataset"), // servesDataset
                titleVal, // title
                nodeId);// datasetId

        service.setDataServiceId(dataServiceId);
        return service;
    }

    public static List<DcatDataService> jsonArrayToDcatDataService(JSONArray array, String nodeId) {
        List<DcatDataService> result = new ArrayList<DcatDataService>();

        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);
            result.add(
                    new DcatDataService(
                            (obj.has("applicableLegislation")) ? obj.getJSONArray("applicableLegislation").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            null,
                            (obj.has("documentation")) ? obj.getJSONArray("documentation").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            (obj.has("endpointDescription")) ? obj.getJSONArray("endpointDescription").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            (obj.has("endpointURL")) ? obj.getJSONArray("endpointURL").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            (obj.has("HVDCategory")) ? obj.getJSONArray("HVDCategory").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            obj.optString("licence"),
                            (obj.has("rights")) ? obj.getJSONArray("rights").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            (obj.has("servesDataset")) ? obj.getJSONArray("servesDataset").toList()
                                    .stream().map(item -> ((String) item)).collect(Collectors.toList())
                                    : Arrays.asList(""),
                            obj.optString("title"),
                            nodeId));
        }

        return result;
    }

    @Override
    public String toString() {
        return "DcatDataService [applicableLegislation=" + applicableLegislation + ", contactPoint=" + contactPoint
                + ", documentation=" + documentation + ", endpointDescription=" + endpointDescription
                + ", endpointURL=" + endpointURL + ", HVDCategory=" + HVDCategory + ", licence=" + licence
                + ", rights=" + rights + ", servesDataset=" + servesDataset + "]";
    }

}
