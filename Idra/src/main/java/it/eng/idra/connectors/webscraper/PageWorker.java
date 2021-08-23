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
package it.eng.idra.connectors.webscraper;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.webscraper.NavigationParameter;
import it.eng.idra.beans.webscraper.NavigationType;
import it.eng.idra.beans.webscraper.NavigationTypeNotValidException;
import it.eng.idra.beans.webscraper.UrlNotParseableException;
import it.eng.idra.utils.PropertyManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// TODO: Auto-generated Javadoc
/**
 * The Class PageWorker.
 */
public class PageWorker implements Runnable {

  /** The Constant PAGE_RETRY_NUM. */
  private static final int PAGE_RETRY_NUM;

  /** The Constant DATASET_RETRY_NUM. */
  private static final int DATASET_RETRY_NUM;

  /** The Constant PAGE_TIMEOUT. */
  private static final int PAGE_TIMEOUT;

  /** The Constant DATASET_TIMEOUT. */
  private static final int DATASET_TIMEOUT;

  /** The Constant JSOUP_THROTTLING. */
  private static final int JSOUP_THROTTLING;

  /** The output scraper. */
  private List<Document> outputScraper;

  /** The count down latch. */
  private CountDownLatch countDownLatch;

  /** The page number. */
  private int pageNumber;

  /** The start url. */
  private String startUrl;

  /** The nav param. */
  private NavigationParameter navParam;

  /** The logger. */
  private Logger logger = LogManager.getLogger(PageWorker.class);

  static {
    PAGE_RETRY_NUM = Integer
        .parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_PAGE_RETRY_NUM));
    DATASET_RETRY_NUM = Integer
        .parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_DATASET_RETRY_NUM));
    PAGE_TIMEOUT = Integer
        .parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_PAGE_TIMEOUT));
    DATASET_TIMEOUT = Integer
        .parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_DATASET_TIMEOUT));
    JSOUP_THROTTLING = Integer
        .parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_GLOBAL_THROTTILING));

  }

  /**
   * Instantiates a new page worker.
   *
   * @param startUrl       the start url
   * @param navParam       the nav param
   * @param pageNumber     the page number
   * @param outputScraper  the output scraper
   * @param countDownLatch the count down latch
   */
  public PageWorker(String startUrl, NavigationParameter navParam, int pageNumber,
      List<Document> outputScraper, CountDownLatch countDownLatch) {
    this.outputScraper = outputScraper;
    this.countDownLatch = countDownLatch;
    this.navParam = navParam;
    this.pageNumber = pageNumber;
    this.startUrl = startUrl;

  }

  /**
   * Extract base url from start url.
   *
   * @return the string
   * @throws UrlNotParseableException the url not parseable exception
   */
  private String extractBaseUrlFromStartUrl() throws UrlNotParseableException {
    String result = null;
    Pattern p = Pattern.compile("((?:http|https):\\/\\/(?:[\\w-]+)(?:\\.[\\w-]+)+)"
        + "(?:[\\w.,@?^=%&amp;:\\/~+#-]*[\\w@?^=%&amp;\\/~+#-])?");

    Matcher matcher = p.matcher(startUrl);

    if (matcher.find() && (result = matcher.group(1)) != null) {
      return result;
    } else {
      throw new UrlNotParseableException(
          "It was not " + "possible to extract the Base Url from Start Url");
    }
  }

  /**
   * Builds the page url.
   *
   * @param startUrl  the start url
   * @param param     the param
   * @param pageValue the page value
   * @return the string
   * @throws NavigationTypeNotValidException the navigation type not valid
   *                                         exception
   */
  private static String buildPageUrl(String startUrl, NavigationParameter param, int pageValue)
      throws NavigationTypeNotValidException {

    Integer pageMultiplier = param.getDatasetsPerPage();
    pageMultiplier = (pageMultiplier == null || pageMultiplier < 1) ? 1 : pageMultiplier;

    if (param.getType().equals(NavigationType.PATH_PAGE)) {
      return startUrl + (startUrl.endsWith("/") ? "" : "/") + param.getName() + "/"
          + String.valueOf(pageValue * pageMultiplier);
    } else if (param.getType().equals(NavigationType.QUERY_PAGE)) {
      return startUrl + (startUrl.contains("?") ? "&" : "?") + param.getName() + "="
          + String.valueOf(pageValue * pageMultiplier);
    } else {
      throw new NavigationTypeNotValidException("The input navigation Type is not valid");
    }
  }

  /**
   * Gets the page document.
   *
   * @return the page document
   * @throws IOException                     Signals that an I/O exception has
   *                                         occurred.
   * @throws NavigationTypeNotValidException the navigation type not valid
   *                                         exception
   */
  private Document getPageDocument() throws IOException, NavigationTypeNotValidException {

    int retryNum = 1;
    boolean retry = false;
    do {

      try {
        String pageUrl = buildPageUrl(startUrl, navParam, pageNumber);
        logger.debug("--------------- PAGE URL: " + pageUrl + "---------------------------");
        return Jsoup.connect(pageUrl).timeout(PAGE_TIMEOUT).get();

      } catch (IOException | NavigationTypeNotValidException e) {
        logger.info("\nThread: " + Thread.currentThread().getId() + " Error: " + e.getMessage()
            + " retrieving Page Document: " + pageNumber + " - Attempt n: " + retryNum);
        retry = true;
        retryNum++;
        if (retryNum > PAGE_RETRY_NUM) {
          retry = false;
          throw e;
        }
      }

    } while (retry);

    throw new IOException("It was reached the max connection attempts for Page Document retrieval");
  }

  /**
   * Gets the dataset document.
   *
   * @param linkElement the link element
   * @return the dataset document
   * @throws IOException              Signals that an I/O exception has occurred.
   * @throws UrlNotParseableException the url not parseable exception
   */
  private Document getDatasetDocument(Element linkElement)
      throws IOException, UrlNotParseableException {

    int retryNum = 1;
    boolean retry = false;
    String link = null;
    do {

      try {
        link = linkElement.attr("href");

        // Handle relative path links
        if (link.startsWith("/")) {
          link = extractBaseUrlFromStartUrl() + link;
        }

        return Jsoup.connect(link).timeout(DATASET_TIMEOUT).get();
      } catch (IOException e) {
        logger.info("\nThread: " + Thread.currentThread().getId() + "Page: " + pageNumber
            + " Error: " + e.getMessage() + " retrieving Dataset Document: " + link
            + " - Attempt n: " + retryNum);
        retry = true;
        retryNum++;
        if (retryNum == DATASET_RETRY_NUM) {
          retry = false;
          throw e;
        }
      }

    } while (retry);

    throw new IOException(
        "It was reached the " + "max connection attempts for Dataset Document retrieval");

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {

    List<Document> pageResult = new ArrayList<Document>();

    logger.info("Thread: " + Thread.currentThread().getId()
        + " Starting to retrieve Page Document: " + pageNumber);

    /*
     * Start trying to get the Page document, for max N attempts
     */
    try {
      final Document pageDocument = getPageDocument();

      /*
       * If the Selector for extracting datasets links is present Start to retrieve
       * Datasets document from extracted links
       */
      navParam.getPageSelectors().stream().forEach(selector -> {
        if ("datasetLink".equalsIgnoreCase(selector.getName())) {
          List<Element> itemElements = pageDocument.select(selector.getSelector());

          if (itemElements != null && !itemElements.isEmpty()) {
            logger.info("Thread: " + Thread.currentThread().getId() + " Page Document: "
                + pageNumber + " Starting to retrieve datasets documents "
                + "from Datasets List with size: " + itemElements.size());

            for (Element e : itemElements) {
              try {
                try {
                  TimeUnit.MILLISECONDS.sleep(JSOUP_THROTTLING);
                } catch (InterruptedException e2) {
                  e2.printStackTrace();
                }
                pageResult.add(getDatasetDocument(e));
              } catch (IOException | UrlNotParseableException e1) {
                e1.printStackTrace();
                continue;
              }
            }
          }
        }
      });

      logger.info("Thread: " + Thread.currentThread().getId() + " Adding " + pageResult.size()
          + " retrieved documents to shared total List");
      outputScraper.addAll(pageResult);
      countDownLatch.countDown();
      logger.debug("Thread: " + Thread.currentThread().getId() + " Decremented Latch to count: "
          + countDownLatch.getCount());
      System.out.println("____________________________________________________");

    } catch (IOException | NavigationTypeNotValidException e1) {
      e1.printStackTrace();
      countDownLatch.countDown();
      return;
    }
  }

}
