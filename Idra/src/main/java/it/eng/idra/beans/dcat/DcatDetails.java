package it.eng.idra.beans.dcat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import it.eng.idra.cache.CacheContentType;

@Entity
@Table(name = "dcat_details")
public class DcatDetails {

    /** The id. */
    private String id;

    /** The catalogue record id. */
    private String catalogueRecordId;

    /** The dataset series id. */
    private String datasetSeriesId;

    /** The dataset id. */
    private String datasetId;

    /** The node id. */
    private String nodeId;

    /** The description. */
    private String description;

    /** The title. */
    private String title;

    /** The language. */
    private String language;

    /**
     * Instantiates a new DcatDetails.
     */
    public DcatDetails() {
    }

    /**
     * Instantiates a new DcatDetails.
     *
     * @param id                the id
     * @param catalogueRecordId the catalogue record id
     * @param datasetSeriesId   the dataset series id
     * @param datasetId         the dataset id
     * @param nodeId            the node id
     * @param description       the description
     * @param title             the title
     * @param language          the language
     */
    public DcatDetails(String id, String catalogueRecordId, String datasetSeriesId, String datasetId, String nodeId,
            String description, String title, String language) {
        this.id = id;
        this.catalogueRecordId = catalogueRecordId;
        this.datasetSeriesId = datasetSeriesId;
        this.datasetId = datasetId;
        this.nodeId = nodeId;
        this.description = description;
        this.title = title;
        this.language = language;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "details_id")
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the catalogue record id.
     *
     * @return the catalogue record id
     */
    @Column(name = "catalogue_record_id")
    public String getCatalogueRecordId() {
        return catalogueRecordId;
    }

    /**
     * Sets the catalogue record id.
     *
     * @param catalogueRecordId the new catalogue record id
     */
    public void setCatalogueRecordId(String catalogueRecordId) {
        this.catalogueRecordId = catalogueRecordId;
    }

    /**
     * Gets the dataset series id.
     *
     * @return the dataset series id
     */
    @Column(name = "dataset_series_id")
    public String getDatasetSeriesId() {
        return datasetSeriesId;
    }

    /**
     * Sets the dataset series id.
     *
     * @param datasetSeriesId the new dataset series id
     */
    public void setDatasetSeriesId(String datasetSeriesId) {
        this.datasetSeriesId = datasetSeriesId;
    }

    @Column(name = "dataset_id")
    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    @Column(name = "nodeID")
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "language")
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public SolrInputDocument toDoc(CacheContentType contentType) {
        SolrInputDocument doc = new SolrInputDocument();
        if (id != null) {
            doc.addField("id", this.id);
        }
        if (catalogueRecordId != null) {
            doc.addField("catalogueRecordIdentifier", this.catalogueRecordId);
        }
        if (datasetSeriesId != null) {
            doc.addField("datasetSeriesIdentifier", this.datasetSeriesId);
        }
        if (datasetId != null) {
            doc.addField("datasetIdentifier", this.datasetId);
        }
        if (nodeId != null) {
            doc.addField("nodeID", this.nodeId);
        }
        if (description != null) {
            doc.addField("description", this.description);
        }
        if (title != null) {
            doc.addField("title", this.title);
        }
        if (language != null) {
            doc.addField("language", this.language);
        }
        return doc;
    }

    public static DcatDetails docToDcatDetails(SolrDocument doc) {
        String id = doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : null;
        String catalogueRecordId = doc.getFieldValue("catalogueRecordIdentifier") != null
                ? doc.getFieldValue("catalogueRecordIdentifier").toString()
                : null;
        String datasetSeriesId = doc.getFieldValue("datasetSeriesIdentifier") != null
                ? doc.getFieldValue("datasetSeriesIdentifier").toString()
                : null;
        String datasetId = doc.getFieldValue("datasetIdentifier") != null
                ? doc.getFieldValue("datasetIdentifier").toString()
                : null;
        String nodeId = doc.getFieldValue("nodeID") != null ? doc.getFieldValue("nodeID").toString() : null;
        String description = doc.getFieldValue("description") != null ? doc.getFieldValue("description").toString()
                : null;
        String title = doc.getFieldValue("title") != null ? doc.getFieldValue("title").toString() : null;
        String language = doc.getFieldValue("language") != null ? doc.getFieldValue("language").toString() : null;

        return new DcatDetails(
                id,
                catalogueRecordId,
                datasetSeriesId,
                datasetId,
                nodeId,
                description,
                title,
                language);
    }

    @Override
    public String toString() {
        return "DcatDetails [id=" + id + ", catalogueRecordIdentifier=" + catalogueRecordId
                + ", datasetSeriesIdentifier="
                + datasetSeriesId + ", datasetIdentifier=" + datasetId + ", nodeId=" + nodeId + ", description="
                + description
                + ", title=" + title + ", language=" + language + "]";
    }

}
