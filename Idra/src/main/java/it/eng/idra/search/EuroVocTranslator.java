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
package it.eng.idra.search;

import it.eng.idra.beans.EuroVocLanguage;
import it.eng.idra.beans.exception.EuroVocTranslationNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class EuroVocTranslator.
 */
public class EuroVocTranslator {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(EuroVocTranslator.class);

  /** The emf. */
  private static EntityManagerFactory emf;

  /** The em. */
  private static EntityManager em;

  /**
   * Inits the.
   */
  public static void init() {
    try {
      emf = Persistence.createEntityManagerFactory("org.hibernate.jpa");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the eurovoc exact terms.
   *
   * @param term            the term
   * @param sourceLanguage  the source language
   * @param targetLanguages the target languages
   * @return the eurovoc exact terms
   */
  public static List<String> getEurovocExactTerms(String term, EuroVocLanguage sourceLanguage,
      List<EuroVocLanguage> targetLanguages) {

    logger.info("Get Eurovoc Exact Terms of " + term + " term");
    List<String> result = new ArrayList<String>();

    try {
      em = emf.createEntityManager();

      String whereClause = " WHERE " + sourceLanguage.name() + " LIKE '" + term + "'";

      String query = "SELECT "
          + targetLanguages.stream().filter(lang -> !lang.equals(sourceLanguage)).distinct()
              .map(EuroVocLanguage::name).collect(Collectors.joining(","))
          + " from eurovoc_terms" + whereClause;

      logger.info(query);

      Query q = em.createNativeQuery(query);

      if (targetLanguages.size() == 1) {
        @SuppressWarnings("unchecked")
        List<String> resultString = q.getResultList();
        if (resultString.size() != 0) {
          for (int i = 0; i < resultString.size(); i++) {
            String termString = resultString.get(i);
            if (!termString.contains("under translation")) {
              result.add(termString);
            }
          }
        }
      } else {
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = q.getResultList();
        if (resultList.size() != 0) {
          Object[] terms = resultList.get(0);
          for (int i = 0; i < terms.length; i++) {
            String termString = terms[i].toString();
            if (!termString.contains("under translation")) {
              result.add(termString);
            }
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      jpaClose();
    }
    return result;
  }

  /**
   * Gets the eurovoc exact terms.
   *
   * @param term the term
   * @return the eurovoc exact terms
   */
  public static List<String> getEurovocExactTerms(String term) {

    logger.info("Get Eurovoc Exact Terms of " + term + " term");
    List<String> result = new ArrayList<String>();
    String whereClause = "WHERE ";
    Iterator<EuroVocLanguage> it = Arrays.asList(EuroVocLanguage.values()).iterator();
    while (it.hasNext()) {
      whereClause += it.next().name() + " LIKE '" + term + "'" + (it.hasNext() ? " OR " : "");
    }

    try {
      em = emf.createEntityManager();
      Query q = em.createNativeQuery("SELECT * from eurovoc_terms " + whereClause);

      List<Object[]> resultList = q.getResultList();

      if (resultList.size() != 0) {
        Object[] terms = resultList.get(0);

        for (int i = 0; i < terms.length; i++) {
          String termString = terms[i].toString();
          if (!termString.contains("under translation")) {
            result.add(termString);
          } else {
            result.add(term);
          }
        }
      } else {
        result.add(term);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      jpaClose();
    }
    return result;
  }

  /**
   * Jpa close.
   */
  public static void jpaClose() {
    if (em != null) {
      if (em.isOpen()) {
        em.clear();
        em.close();
      }
      em = null;
    }
  }

  /**
   * Jpa finalize.
   */
  public static void jpaFinalize() {
    jpaClose();
    emf.close();
    emf = null;

  }

  /**
   * Replace euro voc terms.
   *
   * @param searchParameters the search parameters
   * @return the hash map
   * @throws EuroVocTranslationNotFoundException the euro voc translation not
   *                                             found exception
   */
  public static HashMap<String, Object> replaceEuroVocTerms(
      HashMap<String, Object> searchParameters) throws EuroVocTranslationNotFoundException {
    EuroVocLanguage sourceLanguage = null;
    List<EuroVocLanguage> targetLanguages = null;

    try {
      sourceLanguage = EuroVocLanguage.valueOf((String) searchParameters.remove("sourceLanguage"));
    } catch (NullPointerException e) {
      sourceLanguage = null;
    }

    try {
      targetLanguages = Arrays
          .asList(((String) searchParameters.remove("targetLanguages")).split(",")).stream()
          .map(item -> EuroVocLanguage.valueOf(item)).collect(Collectors.toList());

    } catch (NullPointerException e) {
      targetLanguages = Arrays.asList(EuroVocLanguage.values());
    }

    boolean selectedLanguages = (sourceLanguage != null)
        && StringUtils.isNotBlank(sourceLanguage.name()) && targetLanguages != null;

    String key;
    String value;
    boolean foundAsString = false;
    for (Entry<String, Object> e : searchParameters.entrySet()) {
      key = e.getKey();

      if (!key.equals("releaseDate") && !key.equals("updateDate") && !key.equals("sort")
          && !key.equals("rows") && !key.equals("start") && !key.equals("nodes")
          && !key.equals("nodeID") && !key.equals("live") && !key.equals("euroVoc")) {

        value = ((String) e.getValue()).replaceAll("\"", "").trim();

        String eurovocTerms = "";
        List<String> temp = Arrays.asList(value.split(","));
        Iterator<String> it = temp.iterator();

        if (selectedLanguages) {

          while (it.hasNext()) {

            String termEurovoc = getEurovocExactTerms(it.next(), sourceLanguage, targetLanguages)
                .stream()
                .map(item -> /* new String("(" + */item.trim()/*
                                                               * .replaceAll("\\s"," AND ") + ")")
                                                               */).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(termEurovoc)) {
              eurovocTerms += termEurovoc + (it.hasNext() ? "," : "");
              foundAsString = true;
            }

          }
        } else {

          while (it.hasNext()) {

            String termEurovoc = getEurovocExactTerms(it.next()).stream()
                .map(item -> /* new String("(" + */item.trim()/*
                                                               * .replaceAll("\\s"," AND ") + ")")
                                                               */).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(termEurovoc)) {
              eurovocTerms += termEurovoc + (it.hasNext() ? "," : "");
              foundAsString = true;
            }

          }

        }

        if (StringUtils.isNotBlank(eurovocTerms)) {
          e.setValue(eurovocTerms);
        }
      }
    }

    if (!foundAsString) {
      throw new EuroVocTranslationNotFoundException("Translation not found for provided input!");
    }

    return searchParameters;
  }

}
