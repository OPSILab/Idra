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

package it.eng.idra.api.ckan;

import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.SkosConceptSubject;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.dcat.dump.DcatApDeserializer;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ckan.Dataset;
import org.ckan.Extra;
import org.ckan.Tag;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanUtils.
 */
public class CkanUtils {

  /** The CKA nto DCA tmap. */
  private static HashMap<String, String> CKANtoDCATmap = new HashMap<String, String>();

  /** The logger. */
  protected static Logger logger = LogManager.getLogger(CkanUtils.class);

  /**
   * Instantiates a new ckan utils.
   */
  public CkanUtils() {
    // TODO Auto-generated constructor stub
  }

  static {
    // Mapping of ckan's search parameter to Idra's

    CKANtoDCATmap.put("title", "title");
    CKANtoDCATmap.put("name", "identifier");
    CKANtoDCATmap.put("metadata_created", "releaseDate");
    CKANtoDCATmap.put("metadata_modified", "updateDate");
    CKANtoDCATmap.put("notes", "description");
    CKANtoDCATmap.put("url", "landingPage");
    // CKANtoDCATmap.put("contactPoint_fn", "contact_name");
    // CKANtoDCATmap.put("contactPoint_hasEmail", "contact_email");
    // CKANtoDCATmap.put("publisher_name", "publisher_name");
  }

  /**
   * To ckan dataset.
   *
   * @param dataset the dataset
   * @return the dataset
   */
  public static Dataset toCkanDataset(DcatDataset dataset) {
    Dataset d = new Dataset();

    d.setDownload_url(null);

    d.setId(dataset.getId());
    d.setIsopen(true);

    d.setLog_message(null);

    if (dataset.getPublisher() != null) {
      if (dataset.getPublisher().getName() != null && !dataset.getPublisher().getName().isEmpty()) {
        String names = dataset.getPublisher().getName().stream()
            .map(DcatProperty::getValue)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", ")); // Join names with commas
        d.setMaintainer(StringUtils.isNotBlank(names) ? names : null);
      }

      /*
       * if (dataset.getPublisher().getName() != null) {
       * d.setMaintainer(StringUtils.isNotBlank(dataset.getPublisher().getName().
       * getValue())
       * ? dataset.getPublisher().getName().getValue()
       * : null);
       * }
       */

      if (dataset.getPublisher().getMbox() != null) {
        d.setMaintainer_email((StringUtils.isNotBlank(dataset.getPublisher().getMbox().getValue())
            ? dataset.getPublisher().getMbox().getValue()
            : null));
      }
    }

    if (dataset.getReleaseDate() != null) {
      try {
        d.setMetadata_created(StringUtils.isNotBlank(dataset.getReleaseDate().getValue())
            ? toCkanDate(dataset.getReleaseDate().getValue())
            : null);
      } catch (ParseException e) {
        d.setMetadata_created(null);
      }
    }

    if (dataset.getUpdateDate() != null) {
      try {
        d.setMetadata_modified(StringUtils.isNotBlank(dataset.getUpdateDate().getValue())
            ? toCkanDate(dataset.getUpdateDate().getValue())
            : null);
      } catch (ParseException e) {
        d.setMetadata_modified(null);
      }
    }

    d.setName(dataset.getId());
    d.setNotes(dataset.getDescription().getValue());
    d.setNum_resources(dataset.getDistributions().size());
    d.setNum_tags(dataset.getKeywords().size());

    d.setOrganization(null);
    d.setOwner_org(null);
    // d.setPriv(false);

    // Check
    d.setRelationships_as_object(null);
    d.setRelationships_as_subject(null);

    if (dataset.getDistributions().size() > 0) {

      Optional<DctLicenseDocument> lic = dataset.getDistributions().stream()
          .map(x -> x.getLicense()).findFirst();

      if (lic.isPresent()) {
        if (lic.get().getType() != null) {
          d.setLicense_id(StringUtils.isNotBlank(lic.get().getType().getValue())
              ? lic.get().getType().getValue()
              : null);
        }
        if (lic.get().getName() != null) {
          d.setLicense_title(StringUtils.isNotBlank(lic.get().getName().getValue())
              ? lic.get().getName().getValue()
              : null);
        }
        if (lic.get().getUri() != null) {
          d.setLicense_url(StringUtils.isNotBlank(lic.get().getUri()) ? lic.get().getUri() : null);
          d.setLicense_id(
              getLicenseId(StringUtils.isNotBlank(lic.get().getUri()) ? lic.get().getUri() : null));
        }
      }
    }

    List<org.ckan.Resource> resources = new ArrayList<org.ckan.Resource>();
    int positionDistro = 0;
    for (DcatDistribution distro : dataset.getDistributions()) {
      org.ckan.Resource r = new org.ckan.Resource();
      r.setId(distro.getId());
      r.setPackage_id(dataset.getId());
      r.setRevision_id(null);

      if (distro.getTitle() != null) {
        r.setName(
            StringUtils.isNotBlank(distro.getTitle().getValue()) ? distro.getTitle().getValue()
                : null);
      }

      if (distro.getDescription() != null) {
        r.setDescription(StringUtils.isNotBlank(distro.getDescription().getValue())
            ? distro.getDescription().getValue()
            : null);
      }

      if (distro.getFormat() != null) {
        r.setFormat(
            StringUtils.isNotBlank(distro.getFormat().getValue()) ? distro.getFormat().getValue()
                : null);
      }

      if (distro.getMediaType() != null) {
        r.setMimetype(StringUtils.isNotBlank(distro.getMediaType().getValue())
            ? distro.getMediaType().getValue()
            : null);
      }

      r.setMimetype_inner(null);

      if (distro.getChecksum() != null) {
        if (distro.getChecksum().getChecksumValue() != null) {
          r.setHash(StringUtils.isNotBlank(distro.getChecksum().getChecksumValue().getValue())
              ? distro.getChecksum().getChecksumValue().getValue()
              : null);
        }
      }

      if (distro.getByteSize() != null) {
        r.setSize(StringUtils.isNotBlank(distro.getByteSize().getValue())
            ? Integer.parseInt(distro.getByteSize().getValue())
            : 0);
      }

      // Devo vedere tutti i connettori, non solo il ckan
      r.setCache_last_updated(null);
      r.setCache_url(null);
      r.setDatastore_active(false);

      if (distro.getReleaseDate() != null) {
        try {
          r.setCreated(StringUtils.isNotBlank(distro.getReleaseDate().getValue())
              ? toCkanDate(distro.getReleaseDate().getValue())
              : null);
        } catch (ParseException e) {
          r.setCreated(null);
        }
      }

      if (distro.getUpdateDate() != null) {
        try {
          r.setLast_modified(StringUtils.isNotBlank(distro.getUpdateDate().getValue())
              ? toCkanDate(distro.getUpdateDate().getValue())
              : null);
        } catch (ParseException e) {
          r.setLast_modified(null);
        }
      }

      r.setPosition(positionDistro++);

      r.setResource_type(null);
      if (distro.getDownloadUrl() != null) {
        r.setUrl(StringUtils.isNotBlank(distro.getDownloadUrl().getValue())
            ? distro.getDownloadUrl().getValue()
            : null);
      }

      r.setUrl_type(null);

      if (distro.getStatus() != null && distro.getStatus().getPrefLabel() != null
          && distro.getStatus().getPrefLabel().size() != 0) {
        r.setState(StringUtils.isNotBlank(distro.getStatus().getPrefLabel().get(0).getValue())
            ? distro.getStatus().getPrefLabel().get(0).getValue()
            : null);
      }

      resources.add(r);
    }
    d.setResources(resources);

    d.setRevision_id(null);

    d.setTag_string(null);

    List<Tag> tags = new ArrayList<Tag>();
    for (String key : dataset.getKeywords()) {
      Tag t = new Tag();
      t.setName(key);
      t.setDisplayName(key);
      tags.add(t);
    }
    d.setTags(tags);

    if (dataset.getTitle() != null) {
      d.setTitle(
          StringUtils.isNotBlank(dataset.getTitle().getValue()) ? dataset.getTitle().getValue()
              : null);
    }

    if (dataset.getType() != null) {
      d.setType(StringUtils.isNotBlank(dataset.getType().getValue()) ? dataset.getType().getValue()
          : null);
    }

    if (dataset.getVersion() != null) {
      d.setVersion(
          StringUtils.isNotBlank(dataset.getVersion().getValue()) ? dataset.getVersion().getValue()
              : null);
    }

    if (dataset.getLandingPage() != null) {
      d.setUrl(StringUtils.isNotBlank(dataset.getLandingPage().getValue())
          ? dataset.getLandingPage().getValue()
          : null);
    }

    // TODO group
    List<Extra> extras = new ArrayList<Extra>();
    if (dataset.getAccessRights() != null) {
      if (StringUtils.isNotBlank(dataset.getAccessRights().getValue())) {
        extras.add(new Extra("access_rights", dataset.getAccessRights().getValue()));
      }
    }

    if (dataset.getFrequency() != null) {
      if (StringUtils.isNotBlank(dataset.getFrequency().getValue())) {
        extras.add(new Extra("frequency", dataset.getFrequency().getValue()));
      }
    }

    if (dataset.getIdentifier() != null) {
      if (StringUtils.isNotBlank(dataset.getIdentifier().getValue())) {
        extras.add(new Extra("identifier", dataset.getIdentifier().getValue()));
      }
    }

    if (dataset.getCreator() != null) {
      /*
       * if (dataset.getCreator().getName() != null
       * && StringUtils.isNotBlank(dataset.getCreator().getName().getValue())) {
       * extras.add(new Extra("creator_name",
       * dataset.getCreator().getName().getValue()));
       * }
       */

      if (dataset.getCreator().getName() != null
          && !dataset.getCreator().getName().isEmpty()) {

        String creatorNames = dataset.getCreator().getName().stream()
            .map(DcatProperty::getValue)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotBlank(creatorNames)) {
          extras.add(new Extra("creator_name", creatorNames));
        }
      }

      if (dataset.getCreator().getMbox() != null
          && StringUtils.isNotBlank(dataset.getCreator().getMbox().getValue())) {
        extras.add(new Extra("creator_email", dataset.getCreator().getMbox().getValue()));
      }

      if (dataset.getCreator().getIdentifier() != null
          && StringUtils.isNotBlank(dataset.getCreator().getIdentifier().getValue())) {
        extras
            .add(new Extra("creator_identifier", dataset.getCreator().getIdentifier().getValue()));
      }

      if (dataset.getCreator().getHomepage() != null
          && StringUtils.isNotBlank(dataset.getCreator().getHomepage().getValue())) {
        extras.add(new Extra("creator_url", dataset.getCreator().getHomepage().getValue()));
      }

      if (dataset.getCreator().getType() != null
          && StringUtils.isNotBlank(dataset.getCreator().getType().getValue())) {
        extras.add(new Extra("creator_type", dataset.getCreator().getType().getValue()));
      }

      if (dataset.getCreator().getResourceUri() != null
          && StringUtils.isNotBlank(dataset.getCreator().getResourceUri())) {
        extras.add(new Extra("creator_uri", dataset.getCreator().getResourceUri()));
      }
    }

    if (dataset.getRightsHolder() != null) {
      /*
       * if (dataset.getRightsHolder().getName() != null
       * && StringUtils.isNotBlank(dataset.getRightsHolder().getName().getValue())) {
       * extras.add(new Extra("holder_name",
       * dataset.getRightsHolder().getName().getValue()));
       * }
       */
      if (dataset.getRightsHolder().getName() != null
          && !dataset.getRightsHolder().getName().isEmpty()) {

        String holderNames = dataset.getRightsHolder().getName().stream()
            .map(DcatProperty::getValue)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(", "));

        if (StringUtils.isNotBlank(holderNames)) {
          extras.add(new Extra("holder_name", holderNames));
        }
      }

      if (dataset.getRightsHolder().getMbox() != null
          && StringUtils.isNotBlank(dataset.getRightsHolder().getMbox().getValue())) {
        extras.add(new Extra("holder_email", dataset.getRightsHolder().getMbox().getValue()));
      }

      if (dataset.getRightsHolder().getIdentifier() != null
          && StringUtils.isNotBlank(dataset.getRightsHolder().getIdentifier().getValue())) {
        extras.add(
            new Extra("holder_identifier", dataset.getRightsHolder().getIdentifier().getValue()));
      }

      if (dataset.getRightsHolder().getHomepage() != null
          && StringUtils.isNotBlank(dataset.getRightsHolder().getHomepage().getValue())) {
        extras.add(new Extra("holder_url", dataset.getRightsHolder().getHomepage().getValue()));
      }

      if (dataset.getRightsHolder().getType() != null
          && StringUtils.isNotBlank(dataset.getRightsHolder().getType().getValue())) {
        extras.add(new Extra("holder_type", dataset.getRightsHolder().getType().getValue()));
      }

      if (dataset.getRightsHolder().getResourceUri() != null
          && StringUtils.isNotBlank(dataset.getRightsHolder().getResourceUri())) {
        extras.add(new Extra("holder_uri", dataset.getRightsHolder().getResourceUri()));
      }
    }
    for (DctLocation sc : dataset.getSpatialCoverage()) {
      if (sc != null) {
        if (sc.getGeographicalIdentifier() != null && StringUtils
            .isNotBlank(sc.getGeographicalIdentifier().getValue())) {
          extras.add(new Extra("geographical_identifier",
              sc.getGeographicalIdentifier().getValue()));
        }

        if (sc.getGeographicalName() != null && StringUtils
            .isNotBlank(sc.getGeographicalName().getValue())) {
          extras.add(new Extra("geographical_name",
              sc.getGeographicalName().getValue()));
        }

        if (sc.getGeometry() != null
            && StringUtils.isNotBlank(sc.getGeometry().getValue())) {
          extras.add(new Extra("geometry", sc.getGeometry().getValue()));
        }
      }
    }
    for (DctPeriodOfTime tc : dataset.getTemporalCoverage()) {
      if (tc != null) {
        if (tc.getEndDate() != null
            && StringUtils.isNotBlank(tc.getEndDate().getValue())) {
          extras
              .add(new Extra("temporal_end", tc.getEndDate().getValue()));
        }

        if (tc.getStartDate() != null
            && StringUtils.isNotBlank(tc.getStartDate().getValue())) {
          extras.add(
              new Extra("temporal_start", tc.getStartDate().getValue()));
        }
      }
    }

    // Lista
    if (dataset.getDocumentation() != null && dataset.getDocumentation().size() > 0) {
      List<String> strTmp = dataset.getDocumentation().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("documentation", strTmp.toString()));
      }
    }

    if (dataset.getHasVersion() != null && dataset.getHasVersion().size() > 0) {
      List<String> strTmp = dataset.getHasVersion().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("has_version", strTmp.toString()));
      }
    }

    if (dataset.getIsVersionOf() != null && dataset.getIsVersionOf().size() > 0) {
      List<String> strTmp = dataset.getIsVersionOf().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("is_version_of", strTmp.toString()));
      }
    }

    if (dataset.getLanguage() != null && dataset.getLanguage().size() > 0) {
      List<String> strTmp = dataset.getLanguage().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("language", strTmp.toString()));
      }
    }

    if (dataset.getOtherIdentifier() != null && dataset.getOtherIdentifier().size() > 0) {
      List<String> strTmp = dataset.getOtherIdentifier().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("alternate_identifier", strTmp.toString()));
      }
    }

    if (dataset.getRelatedResource() != null && dataset.getRelatedResource().size() > 0) {
      List<String> strTmp = dataset.getRelatedResource().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("related_resource", strTmp.toString()));
      }
    }

    if (dataset.getSample() != null && dataset.getSample().size() > 0) {
      List<String> strTmp = dataset.getSample().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("sample", strTmp.toString()));
      }
    }

    if (dataset.getSource() != null && dataset.getSource().size() > 0) {
      List<String> strTmp = dataset.getSource().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("source", strTmp.toString()));
      }
    }

    if (dataset.getVersionNotes() != null && dataset.getVersionNotes().size() > 0) {
      List<String> strTmp = dataset.getVersionNotes().stream()
          .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
          .collect(Collectors.toList());
      if (!strTmp.isEmpty()) {
        extras.add(new Extra("version_notes", strTmp.toString()));
      }
    }

    JSONArray arContactPoint = new JSONArray();
    for (VcardOrganization o : dataset.getContactPoint()) {
      if (StringUtils.isNotBlank(o.getFn().getValue())
          || StringUtils.isNotBlank(o.getHasEmail().getValue())
          || StringUtils.isNotBlank(o.getHasTelephoneType().getValue())
          || StringUtils.isNotBlank(o.getHasTelephoneValue().getValue())
          || StringUtils.isNotBlank(o.getHasUrl().getValue())) {
        JSONObject tmpObj = new JSONObject();
        tmpObj.put("fn",
            StringUtils.isNotBlank(o.getFn().getValue()) ? o.getFn().getValue() : null);
        tmpObj.put("has_email",
            StringUtils.isNotBlank(o.getHasEmail().getValue()) ? o.getHasEmail().getValue() : null);
        tmpObj.put("has_telephone_type",
            StringUtils.isNotBlank(o.getHasTelephoneType().getValue())
                ? o.getHasTelephoneType().getValue()
                : null);
        tmpObj.put("has_telephone_value",
            StringUtils.isNotBlank(o.getHasTelephoneValue().getValue())
                ? o.getHasTelephoneValue().getValue()
                : null);
        tmpObj.put("has_url",
            StringUtils.isNotBlank(o.getHasUrl().getValue()) ? o.getHasUrl().getValue() : null);

        arContactPoint.put(tmpObj);
      }
    }

    if (arContactPoint.length() > 0) {
      extras.add(new Extra("contact_point",
          arContactPoint.toString().replaceAll("\\\\\"", "'").replaceAll("\"", "")));
    }

    JSONArray arConformsTo = new JSONArray();
    for (DctStandard s : dataset.getConformsTo()) {
      if (StringUtils.isNotBlank(s.getIdentifier().getValue())
          || StringUtils.isNotBlank(s.getDescription().getValue())
          || StringUtils.isNotBlank(s.getTitle().getValue())) {
        JSONObject tmpObj = new JSONObject();
        tmpObj.put("identifier",
            StringUtils.isNotBlank(s.getIdentifier().getValue()) ? s.getIdentifier().getValue()
                : null);
        tmpObj.put("description",
            StringUtils.isNotBlank(s.getDescription().getValue()) ? s.getDescription().getValue()
                : null);
        tmpObj.put("title",
            StringUtils.isNotBlank(s.getTitle().getValue()) ? s.getTitle().getValue() : null);
        if (s.getReferenceDocumentation() != null && !s.getReferenceDocumentation().isEmpty()) {
          tmpObj.put("reference_documentation",
              s.getReferenceDocumentation().stream()
                  .filter(x -> StringUtils.isNotBlank(x.getValue())).map(x -> x.getValue())
                  .collect(Collectors.toList()).toString());
        }

        arConformsTo.put(tmpObj);
      }
    }

    if (arConformsTo.length() > 0) {
      extras.add(new Extra("conforms_to",
          arConformsTo.toString().replaceAll("\\\\\"", "'").replaceAll("\"", "")));
    }

    for (SkosConceptTheme t : dataset.getTheme()) {
      List<SkosPrefLabel> labelTmp = t.getPrefLabel().stream()
          .filter(
              x -> StringUtils.isNotBlank(x.getLanguage()) || StringUtils.isNotBlank(x.getValue()))
          .collect(Collectors.toList());
      if (!labelTmp.isEmpty()) {
        JSONArray ar = new JSONArray();
        for (SkosPrefLabel p : labelTmp) {
          JSONObject tmpObj = new JSONObject();
          tmpObj.put("language", p.getLanguage());
          tmpObj.put("value", p.getValue());
          ar.put(tmpObj);
        }
        extras
            .add(new Extra("theme", ar.toString().replaceAll("\\\\\"", "'").replaceAll("\"", "")));
      }
    }

    for (SkosConceptSubject t : dataset.getSubject()) {
      List<SkosPrefLabel> labelTmp = t.getPrefLabel().stream()
          .filter(
              x -> StringUtils.isNotBlank(x.getLanguage()) || StringUtils.isNotBlank(x.getValue()))
          .collect(Collectors.toList());
      if (!labelTmp.isEmpty()) {
        JSONArray ar = new JSONArray();
        for (SkosPrefLabel p : labelTmp) {
          JSONObject tmpObj = new JSONObject();
          tmpObj.put("language", p.getLanguage());
          tmpObj.put("value", p.getValue());
          ar.put(tmpObj);
        }
        extras.add(
            new Extra("subject", ar.toString().replaceAll("\\\\\"", "'").replaceAll("\"", "")));
      }
    }

    d.setExtras(extras);

    d.setGroups(null);

    return d;

  }

  /**
   * Gets the license id.
   *
   * @param url the url
   * @return the license id
   */
  public static String getLicenseId(String url) {
    /*
     * 
     * id = "notspecified" id = "other-open" id = "other-pd" id = "other-at" id =
     * "other-nc" id = "other-closed"
     */
    if (url == null) {
      return null;
    } else {
      if (url.contains("creativecommons.org/licenses/by-sa")
          || url.contains("opendefinition.org/licenses/cc-by-sa")) {
        return "cc-by-sa";
      } else if (url.contains("creativecommons.org/licenses/by-nc")) {
        return "cc-nc";
      } else if (url.contains("creativecommons.org/licenses/by")
          || url.contains("opendefinition.org/licenses/cc-by")) {
        return "cc-by";
      } else if (url.contains("opendatacommons.org/licenses/pddl")
          || url.contains("https://opendefinition.org/licenses/odc-pddl")) {
        return "odc-pddl";
      } else if (url.contains("opendatacommons.org/licenses/odbl")
          || url.contains("https://opendefinition.org/licenses/odc-odbl")) {
        return "odc-odbl";
      } else if (url.contains("opendatacommons.org/licenses/by")
          || url.contains("https://opendefinition.org/licenses/odc-by")) {
        return "odc-by";
      } else if (url.contains("creativecommons.org/publicdomain/zero")
          || url.contains("https://opendefinition.org/licenses/cc-zero")) {
        return "cc-zero";
      } else if (url.contains("www.gnu.org/licenses/fdl")
          || url.contains("https://opendefinition.org/licenses/gfdl")) {
        return "gfdl";
      } else if (url.contains("reference.data.gov.uk/id/open-government-licence")) {
        return "uk-ogl";
      } else {
        return "notspecified";
      }
    }
  }

  /**
   * To ckan search result.
   *
   * @param res the res
   * @return the ckan search result
   */
  public static CkanSearchResult toCkanSearchResult(SearchResult res) {
    CkanSearchResult result = new CkanSearchResult();
    result.setCount(res.getCount());
    result.setResults(
        res.getResults().stream().map(x -> toCkanDataset(x)).collect(Collectors.toList()));
    return result;

  }

  /**
   * To ckan date.
   *
   * @param date the date
   * @return the string
   * @throws ParseException the parse exception
   */
  public static String toCkanDate(String date) throws ParseException {
    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return new Timestamp(iso.parse(date).getTime()).toString().replace(" ", "T") + "00000";
  }

  /**
   * Manage query.
   *
   * @param query     the query
   * @param separator the separator
   * @return the string
   */
  public static String manageQuery(String query, String separator) {
    List<String> tmpList = Arrays.asList(query.split(separator));
    List<String> outlist = new ArrayList<String>();
    if (!"*:*".equals(query)) {
      for (String tmp : tmpList) {
        int cnt = 1;
        for (String k : CKANtoDCATmap.keySet()) {
          Pattern regex = Pattern.compile("(" + k + ")\\s*?:");
          Matcher regexMatcher = regex.matcher(tmp);
          if (regexMatcher.find()) {
            outlist.add(tmp.replaceFirst("(" + k + ")\\s*?:", CKANtoDCATmap.get(k) + ":"));
            break;
          } else {
            if (cnt == CKANtoDCATmap.size()) {
              outlist.add(tmp);
            }
          }
          cnt++;
        }
      }
    }

    return String.join(separator, outlist);
  }

  /**
   * Manage sort.
   *
   * @param sort the sort
   * @return the string
   */
  public static String manageSort(String sort) {
    List<String> tmpList = Arrays.asList(sort.split(","));
    List<String> outlist = new ArrayList<String>();
    for (String tmp : tmpList) {
      int cnt = 1;
      for (String k : CKANtoDCATmap.keySet()) {
        Pattern regex = Pattern.compile("(" + k + ")");
        Matcher regexMatcher = regex.matcher(tmp);
        if (regexMatcher.find()) {
          outlist.add(tmp.replaceFirst("(" + k + ")", CKANtoDCATmap.get(k)));
          break;
        } else {
          if (cnt == CKANtoDCATmap.size()) {
            outlist.add(tmp);
          }
        }
        cnt++;
      }
    }

    return String.join(",", outlist);
  }

}
