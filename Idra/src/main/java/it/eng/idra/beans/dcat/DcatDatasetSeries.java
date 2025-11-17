package it.eng.idra.beans.dcat;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONArray;
import com.google.gson.annotations.SerializedName;
import it.eng.idra.cache.CacheContentType;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import org.apache.solr.common.SolrDocument;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dcat_dataset_series")
public class DcatDatasetSeries implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The id. */
    private String datasetSeriesId;

    /** The node id. */
    @SerializedName(value = "nodeID")
    private transient String nodeId;

    /** The dataset id. */
    @SerializedName(value = "dataset_id")
    private String datasetId;

    /** The applicable legislation(s) related to the dataset series. */
    private List<DcatProperty> applicableLegislation;

    /** The contact point(s) for the dataset series. */
    private List<VcardOrganization> contactPoint;

    /** The description(s) of the dataset series. */
    private List<DcatDetails> description;

    /** The frequency of updates for the dataset series. */
    private DcatProperty frequency;

    /** The geographical coverage of the dataset series. */
    private List<DctLocation> geographicalCoverage;

    /** The modification date of the dataset series. */
    private DcatProperty modificationDate;

    /** The publisher of the dataset series. */
    private FoafAgent publisher;

    /** The release date of the dataset series. */
    private DcatProperty releaseDate;

    /** The temporal coverage of the dataset series. */
    private List<DctPeriodOfTime> temporalCoverage;

    /** The title(s) of the dataset series. */
    private List<DcatDetails> title;

    /**
     * Instantiates a new dcat dataset series.
     */
    public DcatDatasetSeries() {
    }

    /**
     * Instantiates a new dcat dataset series.
     *
     * @param applicableLegislation the applicable legislation
     * @param contactPoint          the contact point
     * @param description           the description
     * @param frequency             the frequency of updates
     * @param geographicalCoverage  the geographical coverage
     * @param modificationDate      the modification date
     * @param publisher             the publisher
     * @param releaseDate           the release date
     * @param temporalCoverage      the temporal coverage
     * @param title                 the title
     * @param nodeId                node id
     * @param datasetId             dataset id
     */
    public DcatDatasetSeries(List<String> applicableLegislation, List<VcardOrganization> contactPoint,
            List<DcatDetails> description, String frequency, List<DctLocation> geographicalCoverage,
            String modificationDate, FoafAgent publisher, String releaseDate,
            List<DctPeriodOfTime> temporalCoverage, List<DcatDetails> title, String nodeId,
            String datasetId) {

        setApplicableLegislation(applicableLegislation != null && !applicableLegislation.isEmpty()
                ? applicableLegislation.stream()
                        .map(item -> new DcatProperty(DCATAP.applicableLegislation, ELI.LegalResource, item))
                        .collect(Collectors.toList())
                : Arrays.asList(new DcatProperty(DCATAP.applicableLegislation, ELI.LegalResource, "")));
        setContactPoint(contactPoint);
        // setContactPoint(contactPoint.stream()
        // .map(cp -> new DcatProperty(DCAT.contactPoint.getURI(), RDFS.Resource, cp))
        // .collect(Collectors.toList()));
        setFrequency(new DcatProperty(DCTerms.accrualPeriodicity.getURI(), DCTerms.Frequency, frequency));
        // setGeographicalCoverage(geographicalCoverage.stream()
        // .map(gc -> new DcatProperty(DCTerms.spatial.getURI(), DCTerms.Location,
        // gc.toString()))
        // .collect(Collectors.toList()));
        setGeographicalCoverage(geographicalCoverage);
        setModificationDate(new DcatProperty(DCTerms.modified.getURI(), RDFS.Literal, modificationDate));
        setPublisher((publisher != null ? publisher
                : new FoafAgent(DCTerms.publisher.getURI(), "", null, "", "", "", "",
                        nodeId)));
        setReleaseDate(new DcatProperty(DCTerms.issued.getURI(), RDFS.Literal, releaseDate));
        setTemporalCoverage(temporalCoverage);
        // setTitle(title.stream()
        // .map(t -> new DcatProperty(DCTerms.title.getURI(), RDFS.Literal, t))
        // .collect(Collectors.toList()));
        setTitle(title);
        setDescription(description);
        // setDescription(description.stream()
        // .map(desc -> new DcatProperty(DCTerms.description.getURI(),
        // DCTerms.description, desc))
        // .collect(Collectors.toList()));
        setNodeId(nodeId);
        setDatasetId(datasetId);
    }

    // Getters and Setters
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "dataset_series_id")
    public String getDatasetSeriesId() {
        return datasetSeriesId;
    }

    public void setDatasetSeriesId(String datasetSeriesId) {
        this.datasetSeriesId = datasetSeriesId;
    }

    @Column(name = "nodeID")
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Column(name = "dataset_id")
    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ElementCollection
    @CollectionTable(name = "dcat_applicable_legislation", joinColumns = {
            @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "applicableLegislation")) })
    public List<DcatProperty> getApplicableLegislation() {
        return applicableLegislation;
    }

    public void setApplicableLegislation(List<DcatProperty> applicableLegislation) {
        this.applicableLegislation = applicableLegislation;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT), foreignKey =
    // @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    public List<VcardOrganization> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<VcardOrganization> contactPoint) {
        this.contactPoint = contactPoint;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT)
    @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    public List<DcatDetails> getDescription() {
        return description;
    }

    public void setDescription(List<DcatDetails> description) {
        this.description = description;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "frequency")) })
    public DcatProperty getFrequency() {
        return frequency;
    }

    public void setFrequency(DcatProperty frequency) {
        this.frequency = frequency;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id") // FK in dcat_location
    // @JoinColumns({ @JoinColumn(name = "geographicalCoverage_id",
    // referencedColumnName = "location_id") })
    // @JoinColumns({ @JoinColumn(name = "location_id", referencedColumnName =
    // "geographicalCoverage_id"),
    // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID"),
    // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id") })
    public List<DctLocation> getGeographicalCoverage() {
        return geographicalCoverage;
    }

    public void setGeographicalCoverage(List<DctLocation> geographicalCoverage) {
        this.geographicalCoverage = geographicalCoverage;
    }

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "modificationDate")) })
    public DcatProperty getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DcatProperty modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Gets the publisher.
     *
     * @return the publisher
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "publisher_id")
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

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "value", column = @Column(name = "releaseDate")) })
    public DcatProperty getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(DcatProperty releaseDate) {
        this.releaseDate = releaseDate;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id") // FK in dcat_periodoftime
    // @JoinColumns({ @JoinColumn(name = "temporalCoverage_id", referencedColumnName
    // = "periodOfTime_id") })
    // @JoinColumns({ @JoinColumn(name = "periodOfTime_id", referencedColumnName =
    // "temporalCoverage_id"),
    // @JoinColumn(name = "nodeID", referencedColumnName = "nodeID"),
    // @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id") })
    public List<DctPeriodOfTime> getTemporalCoverage() {
        return temporalCoverage;
    }

    public void setTemporalCoverage(List<DctPeriodOfTime> temporalCoverage) {
        this.temporalCoverage = temporalCoverage;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = { CascadeType.ALL })
    // @Fetch(FetchMode.SELECT)
    @JoinColumns({ @JoinColumn(name = "dataset_id", referencedColumnName = "dataset_id"),
            @JoinColumn(name = "nodeID", referencedColumnName = "nodeID") })
    public List<DcatDetails> getTitle() {
        return title;
    }

    public void setTitle(List<DcatDetails> title) {
        this.title = title;
    }

    public SolrInputDocument toDoc(CacheContentType contentType) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", this.datasetSeriesId);
        doc.addField("datasetIdentifier", this.datasetId);
        doc.addField("nodeID", this.nodeId);
        // doc.addField("content_type", contentType.toString());

        if (frequency != null)
            doc.addField("frequency", frequency.getValue());

        if (modificationDate != null)
            doc.addField("modificationDate", modificationDate.getValue());

        if (releaseDate != null)
            doc.addField("releaseDate", releaseDate.getValue());

        if (publisher != null && publisher.getName() != null)
            doc.addField("publisher", publisher.getName());

        if (applicableLegislation != null && !applicableLegislation.isEmpty())
            doc.addField("applicableLegislation", applicableLegislation.stream()
                    .map(DcatProperty::getValue).collect(Collectors.joining(";")));

        if (title != null && !title.isEmpty()) {
            try {
                doc.addField("title", GsonUtil.obj2Json(title, GsonUtil.detailsListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (description != null && !description.isEmpty()) {
            try {
                doc.addField("description", GsonUtil.obj2Json(description, GsonUtil.detailsListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (contactPoint != null && !contactPoint.isEmpty()) {
            try {
                doc.addField("contactPoint", GsonUtil.obj2Json(contactPoint, GsonUtil.vcardListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (geographicalCoverage != null && !geographicalCoverage.isEmpty()) {
            try {
                doc.addField("geographicalCoverage",
                        GsonUtil.obj2Json(geographicalCoverage, GsonUtil.locationListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        if (temporalCoverage != null && !temporalCoverage.isEmpty()) {
            try {
                doc.addField("temporalCoverage", GsonUtil.obj2Json(temporalCoverage, GsonUtil.periodOfTimeListType));
            } catch (GsonUtilException e) {
                e.printStackTrace();
            }
        }

        return doc;
    }

    public static DcatDatasetSeries docToDcatDatasetSeries(SolrDocument doc, String nodeId, String datasetId) {
        List<SolrDocument> childDocs = doc.getChildDocuments();
        List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();
        FoafAgent publisher = null;
        DctLocation spatialCoverage = null;
        DctPeriodOfTime temporalCoverage = null;
        List<DctLocation> geographicalCoverageList = new ArrayList<DctLocation>();
        List<DctPeriodOfTime> temporalCoverageList = new ArrayList<DctPeriodOfTime>();
        List<String> applicableLegislation = new ArrayList<>();

        String datasetSeriesId = doc.getFieldValue("id") != null ? doc.getFieldValue("id").toString() : null;
        String frequencyVal = doc.getFieldValue("frequency") != null ? doc.getFieldValue("frequency").toString() : null;
        String modDate = doc.getFieldValue("modificationDate") != null
                ? doc.getFieldValue("modificationDate").toString()
                : null;
        String relDate = doc.getFieldValue("releaseDate") != null ? doc.getFieldValue("releaseDate").toString() : null;
        String title = doc.getFieldValue("title") != null ? doc.getFieldValue("title").toString() : null;
        String description = doc.getFieldValue("description") != null ? doc.getFieldValue("description").toString()
                : null;
        List<DcatDetails> dcatDetailsList = List
                .of(new DcatDetails(null, null, datasetSeriesId, datasetId, nodeId, description, title, null));

        if (doc.getFieldValue("applicableLegislation") != null) {
            JSONArray jsonArray = new JSONArray(doc.getFieldValue("applicableLegislation").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                applicableLegislation.add(jsonArray.optString(i, ""));
            }
        }

        if (null != childDocs) {

            for (SolrDocument child : childDocs) {
                // if (child.containsKey("content_type") && child.getFieldValue("content_type")
                // .equals(CacheContentType.spatialCoverage.toString())) {
                spatialCoverage = DctLocation.docToDctLocation(child, DCTerms.spatial.getURI(),
                        nodeId);// , datasetId
                geographicalCoverageList = spatialCoverage != null ? List.of(spatialCoverage) : null;
                // }

                // if (child.containsKey("content_type") && child.getFieldValue("content_type")
                // .equals(CacheContentType.temporalCoverage.toString())) {
                temporalCoverage = DctPeriodOfTime.docToDctPeriodOfTime(child, DCTerms.temporal.getURI(),
                        nodeId);// , datasetId
                temporalCoverageList = temporalCoverage != null ? List.of(temporalCoverage) : null;

                // if (child.containsKey("content_type")
                // &&
                // child.getFieldValue("content_type").equals(CacheContentType.publisher.toString()))
                // {
                publisher = FoafAgent.docToFoafAgent(child, DCTerms.publisher.getURI(), nodeId);
                // }

                // if (child.containsKey("content_type") && child.getFieldValue("content_type")
                // .equals(CacheContentType.contactPoint.toString())) {
                VcardOrganization vcardOrganization = VcardOrganization
                        .docToVcardOrganization(child, DCAT.contactPoint.getURI(), nodeId);// datasetId
                contactPointList.add(vcardOrganization != null ? vcardOrganization : null);
                // }
            }
        }
        // }

        // FoafAgent pub = new FoafAgent(DCTerms.publisher.getURI(), publisherUri, null,
        // "", "", "", "", nodeId);

        DcatDatasetSeries series = new DcatDatasetSeries(
                applicableLegislation,
                contactPointList,
                dcatDetailsList != null ? dcatDetailsList : Collections.emptyList(),
                frequencyVal,
                geographicalCoverageList,
                modDate,
                publisher != null ? publisher
                        : new FoafAgent(DCTerms.publisher.getURI(), "", null, "", "", "", "", nodeId),
                relDate,
                temporalCoverageList,
                dcatDetailsList != null ? dcatDetailsList : Collections.emptyList(),
                nodeId,
                datasetId);

        series.setDatasetSeriesId(datasetSeriesId);
        return series;
    }

    @Override
    public String toString() {
        return "DcatDatasetSeries{" +
                "applicableLegislation=" + applicableLegislation +
                ", contactPoint=" + contactPoint +
                ", description=" + description +
                ", frequency=" + frequency +
                ", geographicalCoverage=" + geographicalCoverage +
                ", modificationDate=" + modificationDate +
                ", publisher=" + publisher +
                ", releaseDate=" + releaseDate +
                ", temporalCoverage=" + temporalCoverage +
                ", title=" + title +
                ']';
    }
}
