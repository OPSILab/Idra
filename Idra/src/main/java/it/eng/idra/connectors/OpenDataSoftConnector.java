package it.eng.idra.connectors;

import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLocation;
import it.eng.idra.beans.dcat.DCTPeriodOfTime;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSConceptSubject;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.beans.opendatasoft.Dataset;
import it.eng.idra.beans.opendatasoft.DatasetDTO;
import it.eng.idra.beans.opendatasoft.DatasourceDTO;
import it.eng.idra.beans.opendatasoft.InnerDatasetMetaDefault;
import it.eng.idra.beans.opendatasoft.Link;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.Gson;

public class OpenDataSoftConnector implements IODMSConnector {

	private static final String datasetsPath = "/api/v2/catalog/datasets";
	
	private String nodeID;
	private ODMSCatalogue node;
	private static Logger logger = LogManager.getLogger(OpenDataSoftConnector.class);
	
	public OpenDataSoftConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
	}
	
	/**
	 * Live search is not available on current OpenDataSoft APIs
	 */
	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
		ArrayList<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		return resultDatasets;
	}

	/**
	 * Live search is not available on current OpenDataSoft APIs
	 */
	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
		return 0;
	}

	@Override
	public int countDatasets() throws Exception {
		String sJson = sendGetRequest(node.getHost().concat(datasetsPath).concat("?rows=0"));
		JSONObject jJson = new JSONObject(sJson);
		
		int count = jJson.getInt("total_count");
		if (count == 0)
			throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");
		return count;
	}

	public DCATDistribution distributionToDCAT(Link datasource, String license, ODMSCatalogue node){
		String format = datasource.getRel();
		
		if("self".equalsIgnoreCase(format))
			return null;
		
		String href = datasource.getHref();
		
		DCATDistribution dcat_distrib = new DCATDistribution(nodeID);
		dcat_distrib.setDownloadURL(href);
		dcat_distrib.setAccessURL(href);
		
		dcat_distrib.setMediaType(format);
		dcat_distrib.setFormat(format);
		dcat_distrib.setTitle(format);
		
		dcat_distrib.setLicense_name(license);
		
		return dcat_distrib;
	}
	
	@Override
	public DCATDataset datasetToDCAT(Object dataset, ODMSCatalogue node)
			throws Exception {
		
		Dataset ds = (Dataset)dataset;
		InnerDatasetMetaDefault metadata = ds.getDataset().getMetas().get_default();
		
		String title = metadata.getTitle();
		String description = metadata.getDescription();
		
		List<SKOSConceptTheme> datasetTheme = extractConceptList(DCAT.theme.getURI(), metadata.getTheme(), SKOSConceptTheme.class);
		
		Optional<String> publisherName = Optional.ofNullable(metadata.getPublisher());
		Optional<String> publisherUri= Optional.empty();
		Optional<String> publisherMbox= Optional.empty();
		Optional<String> publisherHomepage= Optional.empty();
		Optional<String> publisherType= Optional.empty();
		Optional<String> publisherIdentifier = Optional.empty();
		
		FOAFAgent publisher = new FOAFAgent(DCTerms.publisher.getURI(), publisherUri.orElse(null), publisherName.orElse(null), publisherMbox.orElse(null), publisherHomepage.orElse(null), publisherType.orElse(null), publisherIdentifier.orElse(null), nodeID);
		
		List<String> keywords = metadata.getKeyword();
		String license = metadata.getLicense();
		
		
		List<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();
		for(Link link : ds.getLinks()){
			
			if("exports".equals(link.getRel())){
				DatasourceDTO datasourcesDto = new Gson().fromJson(sendGetRequest(link.getHref()), DatasourceDTO.class);
				datasourcesDto.getLinks().forEach(l -> {
					Optional<DCATDistribution> opt_dcat_distrib = Optional.ofNullable(distributionToDCAT(l, license, node));
					if(opt_dcat_distrib.isPresent())
						distributionList.add(opt_dcat_distrib.get());
				});
				
				break;
			}
			
		}
		
		String identifier = ds.getDataset().getDataset_id();
		List<String> otherIdentifier = new ArrayList<String>();
					 otherIdentifier.add(identifier);
					 
		String landingPage = node.getHost().concat("/explore/dataset/").concat(identifier);
		
		//Get update Date
		String updateDate = CommonUtil.fixBadUTCDate(metadata.getModified());
		
		//Extract languages
		List<String> languages = new ArrayList<String>();
		Optional<String> lang = Optional.ofNullable(metadata.getLanguage());
		if(lang.isPresent())
			languages.add(lang.get());
		
		
		List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
		String accessRights = null;
		List<DCTStandard> conformsTo = new ArrayList<DCTStandard>();
		List<String> documentation = new ArrayList<String>();
		String frequency = null;
		List<String> hasVersion = new ArrayList<String>();
		List<String> isVersionOf = new ArrayList<String>();
		
		List<String> provenance = new ArrayList<String>();
		String releaseDate = null;
		
		List<String> sample = new ArrayList<String>();
		List<String> source = new ArrayList<String>();
		DCTLocation spatialCoverage = null;
		DCTPeriodOfTime temporalCoverage = null;
		String type = null;
		String version = null;
		List<String> versionNotes = new ArrayList<String>();
		FOAFAgent rightsHolder = null;
		List<SKOSConceptSubject> subjectList = new ArrayList<SKOSConceptSubject>();
		List<String> relatedResources = new ArrayList<String>();;

		DCATDataset mapped = new DCATDataset(nodeID, identifier, title, description, distributionList, datasetTheme, publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
				hasVersion, isVersionOf, landingPage, languages, provenance, releaseDate, updateDate, otherIdentifier, sample, source, spatialCoverage, temporalCoverage, 
				type, version, versionNotes, rightsHolder, publisher, subjectList, relatedResources);
		
		return mapped;
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws Exception {
		String sJson = sendGetRequest(node.getHost().concat(datasetsPath).concat(datasetId));
		Dataset dataset = new Gson().fromJson(sJson, Dataset.class);
		return datasetToDCAT(dataset, node);
	}

	@Override
	public List<DCATDataset> getAllDatasets() throws Exception {
		
		/*
		 *
		 * Get all the datasets in the ODMS.
		 * The OpenDataSoft portals allows only to retrieve 100 rows per request. So here a pagination mechanism is necessary.
		 *
		 */
		
		Integer startIndex = 0;
		Integer totDatasets = 0;
		boolean repeat = false;
		Set<DCATDataset> out = new HashSet<DCATDataset>();
		
		do{
			String url = node.getHost().concat(datasetsPath).concat("?rows=100&start=").concat(String.valueOf(startIndex));
			logger.debug(url);
			String sJson = sendGetRequest(url);
			DatasetDTO datasets = new Gson().fromJson(sJson, DatasetDTO.class);
	
			totDatasets = datasets.getTotal_count();
			
			if(startIndex==0) //Print info only at first iteration
				logger.info(totDatasets + " total datasets found");
			
			Integer currentDatasetNumber = datasets.getDatasets().size();
			logger.debug("Took " + currentDatasetNumber + " datasets");
			datasets.getDatasets().stream().parallel().forEach( d-> {
				try { out.add(datasetToDCAT(d, node)); } 
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			startIndex += currentDatasetNumber;
			logger.info(out.size() + " evaluated");
			
			repeat = totDatasets>startIndex;
			logger.debug("repeat: "+repeat);
		}
		while(repeat);
		
		return new ArrayList<DCATDataset>(out);
	}
	

	private <T extends SKOSConcept> List<T> extractConceptList(String propertyUri, List<String> concepts,Class<T> type) {
		List<T> result = new ArrayList<T>();

		
		if(concepts!=null) {
			for (String label : concepts) {
				try { result.add(type.getDeclaredConstructor(SKOSConcept.class).newInstance(new SKOSConcept(propertyUri, "", Arrays.asList(new SKOSPrefLabel("", label, nodeID)), nodeID))); } 
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	private String sendGetRequest(String urlString) throws Exception {
		try {
			RestClient client = new RestClientImpl();
			HttpResponse response = client.sendGetRequest(urlString, new HashMap<String,String>());
			return client.getHttpResponseBody(response);
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDate) throws Exception {
		ArrayList<DCATDataset> newDatasets = (ArrayList<DCATDataset>) getAllDatasets();

		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();

		ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
		ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

		int deleted = 0, added = 0;//, changed = 0;

		/// Find added datasets
		SetView<DCATDataset> diff = Sets.difference(newSets, oldSets);
		logger.info("New Packages: " + diff.size());
		for (DCATDataset d : diff) {
			syncrhoResult.addToAddedList(d);
			added++;
		}

		// Find removed datasets
		SetView<DCATDataset> diff1 = Sets.difference(oldSets, newSets);
		logger.info("Deleted Packages: " + diff1.size());
		for (DCATDataset d : diff1) {
			syncrhoResult.addToDeletedList(d);
			deleted++;
		}

		// Find updated datasets
		SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
		logger.fatal("Changed Packages: " + intersection.size());

		GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		int exception = 0;
		for (DCATDataset d : intersection) {
			try {
				int oldIndex = oldDatasets.indexOf(d);
				int newIndex = newDatasets.indexOf(d);
				oldDate.setTime(ISO.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
				newDate.setTime(ISO.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

				if (newDate.after(oldDate)) {
					syncrhoResult.addToChangedList(d);
//					changed++;
				}
			} catch (Exception ex) {
				exception++;
				if (exception % 1000 == 0) {
					ex.printStackTrace();
				}
			}
		}
		logger.info("Changed " + syncrhoResult.getChangedDatasets().size());
		logger.info("Added " + syncrhoResult.getAddedDatasets().size());
		logger.info("Deleted " + syncrhoResult.getDeletedDatasets().size());
		logger.info("Expected new dataset count: " + (node.getDatasetCount() - deleted + added));

		return syncrhoResult;
	}
	
}
