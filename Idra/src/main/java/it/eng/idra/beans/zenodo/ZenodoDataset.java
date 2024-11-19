package it.eng.idra.beans.zenodo;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ZenodoDataset {

    // Class to represent each hit
    public static class Hit {
        private Integer id; // Unique identifier(Deposition identifier) for the dataset
        private Metadata metadata; // Nested metadata object(A deposition metadata resource)
        private String created; // Date the dataset was created(Creation time of deposition) //Timestamp
        private String modified; // Last modification time of deposition //Timestamp
        private String doi; // DOI(Digital Object Identifier) of the dataset
        private String doi_url; // URL for the DOI(Persistent link to your published deposition)
        private String state; // State of the dataset (e.g., 'inprogress','done','error')
        private List<File> files; // A list of deposition files resources
        private boolean submitted;// True if the deposition has been published, False otherwise.
        private String title;// Title of deposition (automatically set from metadata). Defaults to empty
                             // string.
        // additional properties below
        private String conceptrecid;
        private String conceptdoi;
        private Link links;
        private String updated;
        private String recid;
        private Integer revision;
        private List<Owner> owners;
        private String status;
        private Stats stats;
        //

        // Getters and Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getConceptrecid() {
            return conceptrecid;
        }

        public void setConceptrecid(String conceptrecid) {
            this.conceptrecid = conceptrecid;
        }

        public String getConceptdoi() {
            return conceptdoi;
        }

        public void setConceptdoi(String conceptdoi) {
            this.conceptdoi = conceptdoi;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getRecid() {
            return recid;
        }

        public void setRecid(String recid) {
            this.recid = recid;
        }

        public Integer getRevision() {
            return revision;
        }

        public void setRevision(Integer revision) {
            this.revision = revision;
        }

        public List<Owner> getOwners() {
            return owners;
        }

        public void setOwners(List<Owner> owners) {
            this.owners = owners;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Stats getStats() {
            return stats;
        }

        public void setStats(Stats stats) {
            this.stats = stats;
        }

        public boolean isSubmitted() {
            return submitted;
        }

        public void setSubmitted(boolean submitted) {
            this.submitted = submitted;
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }

        public String getDoi() {
            return doi;
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public String getDoi_url() {
            return doi_url;
        }

        public void setDoi_url(String doi_url) {
            this.doi_url = doi_url;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public List<File> getFiles() {
            return files;
        }

        public void setFiles(List<File> files) {
            this.files = files;
        }

        public Link getLinks() {
            return links;
        }

        public void setLinks(Link links) {
            this.links = links;
        }
    }

    // Class to represent Metadata
    public static class Metadata {
        private String title; // Title of deposition
        private String description; // Abstract or description for deposition
        private List<Creator> creators; // List of the creators/authors of the deposition
        private String publication_date; // Date of publication
        private String access_right; // Controlled vocabulary: * open: Open Access * embargoed: Embargoed Access *
                                     // restricted: Restricted Access * closed: Closed Access
        private String upload_type; // Controlled vocabulary: * publication: Publication * poster: Poster *
                                    // presentation: Presentation * dataset: Dataset * image: Image * video:
                                    // Video/Audio * software: Software * lesson: Lesson * physicalobject: Physical
                                    // object * other: Other
        private String publication_type;// Controlled vocabulary: * annotationcollection: Annotation collection * book:
                                        // Book * section: Book section * conferencepaper: Conference paper *
                                        // datamanagementplan: Data management plan * article: Journal article * patent:
                                        // Patent * preprint: Preprint * deliverable: Project deliverable * milestone:
                                        // Project milestone * proposal: Proposal * report: Report *
                                        // softwaredocumentation: Software documentation * taxonomictreatment: Taxonomic
                                        // treatment * technicalnote: Technical note * thesis: Thesis * workingpaper:
                                        // Working paper * other: Other
        private String image_type;// Controlled vocabulary: * figure: Figure * plot: Plot * drawing: Drawing *
                                  // diagram: Diagram * photo: Photo * other: Other
        private Licence license;// The selected license applies to all files in this deposition
        private String embargo_date;// When the deposited files will be made automatically made publicly available
                                    // by the system. Defaults to current date.
        private String access_conditions;// Specify the conditions under which you grant users access to the files in
                                         // your upload
        private String imprint_publisher; // Publisher of a book/report/chapter
        private String language; // Specify the main language of the record
        private List<String> keywords;// Free form keywords for this deposition.
        private String version;// Version of the resource.
        private List<Date> dates;// List of date intervals
        private String doi;// Digital Object Identifier
        private List<String> references;// List of references
        private ResourceType resource_type;// Resource type
        private Meeting meeting;// Meeting
        private List<Grant> grants;// List of OpenAIRE-supported grants
        private List<Community> communities;// List of communities you wish the deposition to appear.
        private Relations relations;// Relations

        // Getters and Setters
        public String getDoi() {
            return doi;
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public List<String> getReferences() {
            return references;
        }

        public void setReferences(List<String> references) {
            this.references = references;
        }

        public Meeting getMeeting() {
            return meeting;
        }

        public void setMeeting(Meeting meeting) {
            this.meeting = meeting;
        }

        public List<Grant> getGrants() {
            return grants;
        }

        public void setGrants(List<Grant> grants) {
            this.grants = grants;
        }

        public List<Community> getCommunities() {
            return communities;
        }

        public void setCommunities(List<Community> communities) {
            this.communities = communities;
        }

        public Relations getRelations() {
            return relations;
        }

        public void setRelations(Relations relations) {
            this.relations = relations;
        }

        public List<Date> getDates() {
            return dates;
        }

        public void setDates(List<Date> dates) {
            this.dates = dates;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Creator> getCreators() {
            return creators;
        }

        public void setCreators(List<Creator> creators) {
            this.creators = creators;
        }

        public String getPublication_date() {
            return publication_date;
        }

        public void setPublication_date(String publication_date) {
            this.publication_date = publication_date;
        }

        public String getAccess_right() {
            return access_right;
        }

        public void setAccess_right(String access_right) {
            this.access_right = access_right;
        }

        public String getUpload_type() {
            return upload_type;
        }

        public void setUpload_type(String upload_type) {
            this.upload_type = upload_type;
        }

        public String getPublication_type() {
            return publication_type;
        }

        public void setPublication_type(String publication_type) {
            this.publication_type = publication_type;
        }

        public String getImage_type() {
            return image_type;
        }

        public void setImage_type(String image_type) {
            this.image_type = image_type;
        }

        public String getAccess_conditions() {
            return access_conditions;
        }

        public void setAccess_conditions(String access_conditions) {
            this.access_conditions = access_conditions;
        }

        public String getImprint_publisher() {
            return imprint_publisher;
        }

        public void setImprint_publisher(String imprint_publisher) {
            this.imprint_publisher = imprint_publisher;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Licence getLicense() {
            return license;
        }

        public void setLicense(Licence license) {
            this.license = license;
        }

        public String getEmbargo_date() {
            return embargo_date;
        }

        public void setEmbargo_date(String embargo_date) {
            this.embargo_date = embargo_date;
        }

        public ResourceType getResource_type() {
            return resource_type;
        }

        public void setResource_type(ResourceType resource_type) {
            this.resource_type = resource_type;
        }

    }

    // Class to represent Creator
    public static class Creator {
        private String name; // Name of the creator
        private String affiliation;// Affiliation of creator
        private String orcid;// ORCID identifier of creator

        // Getters and Setters
        public String getAffiliation() {
            return affiliation;
        }

        public void setAffiliation(String affiliation) {
            this.affiliation = affiliation;
        }

        public String getOrcid() {
            return orcid;
        }

        public void setOrcid(String orcid) {
            this.orcid = orcid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Class to represent Files
    public static class File {
        private String id; // Deposition file identifier
        @SerializedName("key")
        private String filename; // Name of file
        @SerializedName("size")
        private Long filesize; // Size of file in bytes
        private String checksum; // MD5 checksum of file
        private Link links;// Links of files

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Link getLinks() {
            return links;
        }

        public void setLinks(Link links) {
            this.links = links;
        }

        public Long getFilesize() {
            return filesize;
        }

        public void setFilesize(Long filesize) {
            this.filesize = filesize;
        }

    }

    // Class to represent Licences
    public static class Licence {
        private String id; // Identifier for the license.
        private String title; // The name of the license
        private String url; // URL of the license

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    // Class to represent Date
    public static class Date {
        private String start;// start date
        private String end;// end date
        private String type;// The interval’s type

        // Getters and Setters
        public String getStart() {
            return start;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

    }

    // Class to represent Link
    public static class Link {
        private String self; // resource url
        private String doi; // doi url

        // Getters and Setters
        public String getDoi() {
            return doi;
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }

    // Class to represent ResourceType
    public class ResourceType {
        private String title;
        private String type;
        private String subtype;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubtype() {
            return subtype;
        }

        public void setSubtype(String subtype) {
            this.subtype = subtype;
        }

    }

    // Class to represent Meeting
    public class Meeting {
        private String title;
        private String acronym;
        private String dates;
        private String place;
        private String url;
        private String session;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAcronym() {
            return acronym;
        }

        public void setAcronym(String acronym) {
            this.acronym = acronym;
        }

        public String getDates() {
            return dates;
        }

        public void setDates(String dates) {
            this.dates = dates;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }
    }

    // Class to represent Grant
    public class Grant {
        private String code;
        @SerializedName("internal_id")
        private String internalId;
        private Funder funder;
        private String title;
        private String acronym;
        private String program;
        private String url;

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getInternalId() {
            return internalId;
        }

        public void setInternalId(String internalId) {
            this.internalId = internalId;
        }

        public Funder getFunder() {
            return funder;
        }

        public void setFunder(Funder funder) {
            this.funder = funder;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAcronym() {
            return acronym;
        }

        public void setAcronym(String acronym) {
            this.acronym = acronym;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    // Class to represent Funder
    public class Funder {
        private String name;
        private String doi;
        private String acronym;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDoi() {
            return doi;
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public String getAcronym() {
            return acronym;
        }

        public void setAcronym(String acronym) {
            this.acronym = acronym;
        }

    }

    // Class to represent Community
    public class Community {
        private String id;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    // Class to represent Relations
    public class Relations {
        private List<Version> version;

        // Getters and Setters
        public List<Version> getVersion() {
            return version;
        }

        public void setVersion(List<Version> version) {
            this.version = version;
        }

    }

    // Class to represent Version
    public class Version {
        private Integer index;
        @SerializedName("is_last")
        private boolean isLast;
        private Parent parent;

        // Getters and Setters
        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public boolean isLast() {
            return isLast;
        }

        public void setLast(boolean isLast) {
            this.isLast = isLast;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }

    }

    // Class to represent Parent
    public class Parent {
        @SerializedName("pid_type")
        private String pidType;
        @SerializedName("pid_value")
        private String pidValue;

        // Getters and Setters
        public String getPidType() {
            return pidType;
        }

        public void setPidType(String pidType) {
            this.pidType = pidType;
        }

        public String getPidValue() {
            return pidValue;
        }

        public void setPidValue(String pidValue) {
            this.pidValue = pidValue;
        }

    }

    // Class to represent Owner
    public class Owner {
        private String id;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    // Class to represent Stats
    public class Stats {
        private Integer downloads;
        @SerializedName("unique_downloads")
        private Integer uniqueDownloads;
        private Integer views;
        @SerializedName("unique_views")
        private Integer uniqueViews;
        @SerializedName("version_downloads")
        private Integer versionDownloads;
        @SerializedName("version_unique_downloads")
        private Integer versionUniqueDownloads;
        @SerializedName("version_views")
        private Integer versionViews;
        @SerializedName("version_unique_views")
        private Integer versionUniqueViews;

        // Getters and Setters
        public Integer getDownloads() {
            return downloads;
        }

        public void setDownloads(Integer downloads) {
            this.downloads = downloads;
        }

        public Integer getUniqueDownloads() {
            return uniqueDownloads;
        }

        public void setUniqueDownloads(Integer uniqueDownloads) {
            this.uniqueDownloads = uniqueDownloads;
        }

        public Integer getViews() {
            return views;
        }

        public void setViews(Integer views) {
            this.views = views;
        }

        public Integer getUniqueViews() {
            return uniqueViews;
        }

        public void setUniqueViews(Integer uniqueViews) {
            this.uniqueViews = uniqueViews;
        }

        public Integer getVersionDownloads() {
            return versionDownloads;
        }

        public void setVersionDownloads(Integer versionDownloads) {
            this.versionDownloads = versionDownloads;
        }

        public Integer getVersionUniqueDownloads() {
            return versionUniqueDownloads;
        }

        public void setVersionUniqueDownloads(Integer versionUniqueDownloads) {
            this.versionUniqueDownloads = versionUniqueDownloads;
        }

        public Integer getVersionViews() {
            return versionViews;
        }

        public void setVersionViews(Integer versionViews) {
            this.versionViews = versionViews;
        }

        public Integer getVersionUniqueViews() {
            return versionUniqueViews;
        }

        public void setVersionUniqueViews(Integer versionUniqueViews) {
            this.versionUniqueViews = versionUniqueViews;
        }

    }

    // Class to represent Hits
    public static class Hits {
        private Long total;// total count of records
        private List<Hit> hits; // list of records

        // Getters and Setters

        public List<Hit> getHits() {
            return hits;
        }

        public void setHits(List<Hit> hits) {
            this.hits = hits;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }
    }

    // Class to represent Response class for API responses
    public static class Response {
        private Hits hits;

        // Getters and Setters
        public Hits getHits() {
            return hits;
        }

        public void setHits(Hits hits) {
            this.hits = hits;
        }

    }

}
