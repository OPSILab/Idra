package it.eng.idra.beans.dcat;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.google.gson.annotations.SerializedName;
import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.solr.common.SolrDocument;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "dcat_catalogue_record")
public class DcatCatalogueRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The Constant RDFClass. */
    private static final transient Resource RDFClass = DCAT.CatalogRecord;

    /** The id. */
    private String catalogueRecordId;

    /** The node id. */
    @SerializedName(value = "nodeID")
    private transient String nodeId;

    /** The application profile(s) of the catalogue record. */
    private List<DctStandard> applicationProfile;

    /** The change type (status) of the catalogue record. */
    private DcatProperty changeType;

    /** The description(s) of the catalogue record. */
    private List<DcatDetails> description;

    /** The language(s) used for metadata in the catalogue record. */
    private List<DcatDetails> language;

    /** The listing date of the catalogue record. */
    private DcatProperty listingDate;

    /** The modification date of the catalogue record. */
    private DcatProperty modificationDate;

    /** The primary topic (Dataset or Data Service) for the catalogue record. */
    private DcatProperty primaryTopic; // Can be Dataset or Data Service

    /** The source metadata of the catalogue record. */
    // private DcatCatalogueRecord sourceMetadata;

    /** The title(s) of the catalogue record. */
    private List<DcatDetails> title;

    /**
     * Default constructor.
     */
    public DcatCatalogueRecord() {
    }

    /**
     * Instantiates a new dcat catalogue record.
     *
     * @param applicationProfile the application profile(s)
     * @param changeType         the change type
     * @param description        the description(s)
     * @param language           the language(s)
     * @param listingDate        the listing date
     * @param modificationDate   the modification date
     * @param primaryTopic       the primary topic
     * @param title              the title(s)
     * @param nodeId             node id
     */
    public DcatCatalogueRecord(List<DctStandard> applicationProfile, String changeType,
            List<DcatDetails> description, List<DcatDetails> language, String listingDate,
            String modificationDate, String primaryTopic, List<DcatDetails> title, String nodeId) {

        // setApplicationProfile(applicationProfile.stream()
        // .map(ap -> new DcatProperty(DCTerms.conformsTo.getURI(), DCTerms.Standard,
        // ap))
        // .collect(Collectors.toList()));
        setApplicationProfile(applicationProfile);
        setChangeType(new DcatProperty(ResourceFactory.createProperty("https://www.w3.org/ns/legacy_adms#status"),
                SKOS.Concept, changeType));
        setListingDate(new DcatProperty(DCTerms.issued.getURI(), RDFS.Literal, listingDate));
        setModificationDate(new DcatProperty(DCTerms.modified.getURI(), RDFS.Literal, modificationDate));
        setPrimaryTopic(new DcatProperty(FOAF.primaryTopic, DCAT.Resource, primaryTopic));
        // setSourceMetadata(sourceMetadata);// http://purl.org/dc/terms/source
        // setTitle(title.stream()
        // .map(t -> new DcatProperty(DCTerms.title.getURI(), RDFS.Literal, t))
        // .collect(Collectors.toList()));
        setTitle(title);
        setDescription(description);
        // setDescription(description.stream()
        // .map(desc -> new DcatProperty(DCTerms.description.getURI(),
        // DCTerms.description, desc))
        // .collect(Collectors.toList()));
        setLanguage(language);
        // setLanguage(language.stream()
        // .map(lang -> new DcatProperty(DCTerms.language.getURI(),
        // DCTerms.LinguisticSystem, lang))
        // .collect(Collectors.toList()));
        setNodeId(nodeId);
    }

    // Getters and Setters
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "catalogue_record_id")
    public String getCatalogueRecordId() {
        return catalogueRecordId;
    }

    public void setCatalogueRecordId(String catalogueRecordId) {
        this.catalogueRecordId = catalogueRecordId;
    }

    @Column(name = "nodeID")
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT)
    @JoinColumns({ @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    public List<DctStandard> getApplicationProfile() {
        return applicationProfile;
    }

    public void setApplicationProfile(List<DctStandard> applicationProfile) {
        this.applicationProfile = applicationProfile;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "changeType")) })
    public DcatProperty getChangeType() {
        return changeType;
    }

    public void setChangeType(DcatProperty changeType) {
        this.changeType = changeType;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT)
    // @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName =
    // "dataset_id"),
    // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @JoinColumn(name = "catalogue_record_id", referencedColumnName = "catalogue_record_id")
    public List<DcatDetails> getDescription() {
        return description;
    }

    public void setDescription(List<DcatDetails> description) {
        this.description = description;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT)
    // @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName =
    // "dataset_id"),
    // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @JoinColumn(name = "catalogue_record_id", referencedColumnName = "catalogue_record_id")
    public List<DcatDetails> getLanguage() {
        return language;
    }

    public void setLanguage(List<DcatDetails> language) {
        this.language = language;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "listingDate")) })
    public DcatProperty getListingDate() {
        return listingDate;
    }

    public void setListingDate(DcatProperty listingDate) {
        this.listingDate = listingDate;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "modificationDate")) })
    public DcatProperty getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DcatProperty modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "primaryTopic")) })
    public DcatProperty getPrimaryTopic() {
        return primaryTopic;
    }

    public void setPrimaryTopic(DcatProperty primaryTopic) {
        this.primaryTopic = primaryTopic;
    }

    // public DcatCatalogueRecord getSourceMetadata() {
    // return sourceMetadata;
    // }

    // public void setSourceMetadata(DcatCatalogueRecord sourceMetadata) {
    // this.sourceMetadata = sourceMetadata;
    // }
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT)
    // @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName =
    // "dataset_id"),
    // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @JoinColumn(name = "catalogue_record_id", referencedColumnName = "catalogue_record_id")
    public List<DcatDetails> getTitle() {
        return title;
    }

    public void setTitle(List<DcatDetails> title) {
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
        if (this.catalogueRecordId != null)
            doc.addField("id", this.catalogueRecordId);

        if (this.nodeId != null)
            doc.addField("nodeID", this.nodeId);
        // doc.addField("content_type", contentType.toString());

        if (this.changeType != null)
            doc.addField("changeType", this.changeType.getValue());

        if (this.listingDate != null)
            doc.addField("listingDate", this.listingDate.getValue());

        if (this.modificationDate != null)
            doc.addField("modificationDate", this.modificationDate.getValue());

        if (this.primaryTopic != null)
            doc.addField("primaryTopic", this.primaryTopic.getValue());

        if (this.applicationProfile != null && !this.applicationProfile.isEmpty()) {
            try {
                doc.addField("applicationProfile", GsonUtil.obj2Json(applicationProfile, GsonUtil.stdListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (this.title != null && !this.title.isEmpty()) {
            try {
                doc.addField("title", GsonUtil.obj2Json(title, GsonUtil.detailsListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (this.description != null && !this.description.isEmpty()) {
            try {
                doc.addField("description", GsonUtil.obj2Json(description, GsonUtil.detailsListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (this.language != null && !this.language.isEmpty()) {
            try {
                doc.addField("language", GsonUtil.obj2Json(language, GsonUtil.detailsListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        return doc;
    }

    public static DcatCatalogueRecord docToDcatCatalogueRecord(SolrDocument doc, String datasetId) {
        String catalogueRecordId = doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : "";
        String nodeId = doc.getFieldValue("nodeID") != null ? doc.getFieldValue("nodeID").toString() : "";

        String changeTypeVal = doc.getFieldValue("changeType") != null ? doc.getFieldValue("changeType").toString()
                : "";
        String listingDateVal = doc.getFieldValue("listingDate") != null ? doc.getFieldValue("listingDate").toString()
                : "";
        String modificationDateVal = doc.getFieldValue("modificationDate") != null
                ? doc.getFieldValue("modificationDate").toString()
                : "";
        String primaryTopicVal = doc.getFieldValue("primaryTopic") != null
                ? doc.getFieldValue("primaryTopic").toString()
                : "";

        DctStandard applicationProfileVal = (DctStandard) doc.getFieldValue("applicationProfile");
        String titleVal = doc.getFieldValue("title") != null ? doc.getFieldValue("title").toString() : "";
        String languageVal = doc.getFieldValue("language") != null ? doc.getFieldValue("language").toString() : "";
        String descriptionVal = doc.getFieldValue("description") != null ? doc.getFieldValue("description").toString()
                : "";

        List<DctStandard> applicationProfile = applicationProfileVal != null ? List.of(applicationProfileVal)
                : List.of();
        List<DcatDetails> dcatDetailsList = List
                .of(new DcatDetails(null, catalogueRecordId, null, datasetId, nodeId, descriptionVal, titleVal,
                        languageVal));

        DcatCatalogueRecord record = new DcatCatalogueRecord(
                applicationProfile,
                changeTypeVal,
                dcatDetailsList != null ? dcatDetailsList : Collections.emptyList(),
                dcatDetailsList != null ? dcatDetailsList : Collections.emptyList(),
                listingDateVal,
                modificationDateVal,
                primaryTopicVal,
                dcatDetailsList != null ? dcatDetailsList : Collections.emptyList(),
                nodeId);
        record.setCatalogueRecordId(catalogueRecordId);
        return record;
    }

    // ", sourceMetadata=" + sourceMetadata +
    @Override
    public String toString() {
        return "DcatCatalogueRecord [" +
                "applicationProfile=" + applicationProfile +
                ", changeType=" + changeType +
                ", description=" + description +
                ", language=" + language +
                ", listingDate=" + listingDate +
                ", modificationDate=" + modificationDate +
                ", primaryTopic=" + primaryTopic +
                ", title=" + title +
                ']';
    }
}
