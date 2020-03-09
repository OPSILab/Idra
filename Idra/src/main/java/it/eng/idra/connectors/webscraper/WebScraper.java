/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.idra.connectors.webscraper;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.plaf.synth.SynthScrollBarUI;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.webscraper.NavigationParameter;
import it.eng.idra.beans.webscraper.NavigationType;
import it.eng.idra.beans.webscraper.NavigationTypeNotValidException;
import it.eng.idra.beans.webscraper.PageNumberNotParseableException;
import it.eng.idra.beans.webscraper.PageSelector;
import it.eng.idra.beans.webscraper.SitemapNotValidException;
import it.eng.idra.beans.webscraper.WebScraperSelector;
import it.eng.idra.beans.webscraper.WebScraperSelectorNotFoundException;
import it.eng.idra.beans.webscraper.WebScraperSitemap;
import it.eng.idra.connectors.CKanConnector;
import it.eng.idra.utils.PropertyManager;

public class WebScraper {

	private static Logger logger = LogManager.getLogger(WebScraper.class);

	private static final int PAGINATION_RETRY_NUM;
	private static final int DATASET_TIMEOUT;
	private static final long COUNTDOWN_LATCH_TIMEOUT;
	private static final int WEB_SCRAPER_RANGE_SCALE_NUM;

	static {
		PAGINATION_RETRY_NUM = Integer
				.parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_PAGINATION_RETRY_NUM));
		DATASET_TIMEOUT = Integer.parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_DATASET_TIMEOUT));
		COUNTDOWN_LATCH_TIMEOUT = Long.parseLong(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_GLOBAL_TIMEOUT));
		WEB_SCRAPER_RANGE_SCALE_NUM = Integer
				.parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_RANGE_SCALE_NUM));
	}

	private WebScraper() {
	}

	/**
	 * Get the Document corresponding to the dataset page
	 * 
	 * @param conf
	 * @param incrementValue
	 * @return
	 * @throws IOException
	 * @throws NavigationTypeNotValidException
	 */
	public static Document getDatasetDocumentByIncrement(WebScraperSitemap conf, int incrementValue)
			throws IOException, NavigationTypeNotValidException {

		return Jsoup.connect(buildRangeUrl(conf.getStartUrl(), conf.getNavigationParameter(), incrementValue))
				.timeout(DATASET_TIMEOUT).get();

	}

	public static Document getDatasetDocumentByIncrement(String startUrl, NavigationParameter navParam,
			int incrementValue) throws IOException, NavigationTypeNotValidException {
		String finalUrl = buildRangeUrl(startUrl, navParam, incrementValue);
		// System.out.println(finalUrl);

		return Jsoup.connect(finalUrl).timeout(DATASET_TIMEOUT).get();

	}

	/**
	 * Get the Document list of all corresponding dataset pages
	 * 
	 * @param navParam
	 * @return
	 * @throws InterruptedException
	 * @throws PageNumberNotParseableException
	 * @throws SitemapNotValidException
	 */
	public static List<Document> getDatasetsDocument(WebScraperSitemap sitemap)
			throws InterruptedException, PageNumberNotParseableException, SitemapNotValidException {
		NavigationParameter navParam = sitemap.getNavigationParameter();

		validateSitemap(sitemap);

		switch (navParam.getType()) {
		case QUERY_RANGE:
		case PATH_RANGE:
			return getDatasetsDocumentByRange(sitemap.getStartUrl(), navParam);

		case PATH_PAGE:
		case QUERY_PAGE:
			return getDatasetsDocumentByPage(sitemap.getStartUrl(), navParam);

		default:
			return null;
		}

	}

	/**
	 * Validate the input Sitemap
	 * 
	 * @param sitemap
	 * @return
	 * @throws SitemapNotValidException
	 */
	private static void validateSitemap(WebScraperSitemap sitemap) throws SitemapNotValidException {
		NavigationParameter navParam = sitemap.getNavigationParameter();

		if (navParam != null) {

			try {
				NavigationType.valueOf(navParam.getType().toString());
			} catch (IllegalArgumentException | NullPointerException e) {
				throw new SitemapNotValidException("The input Sitemap is not valid: The Navigation Type is not valid");
			}

			if (StringUtils.isBlank(navParam.getName()))
				throw new SitemapNotValidException(
						"The input Sitemap is not valid: The Navigation Param Name is not valid");

			if (navParam.getPagesNumber() == null || StringUtils.isBlank(navParam.getPagesNumber().toString())) {
				if (navParam.getPageSelectors() == null || navParam.getPageSelectors().size() != 2)
					throw new SitemapNotValidException(
							"The input Sitemap is not valid: Both PagesNumber and PagesSelectors are empty.");
			}

		} else
			throw new SitemapNotValidException("The input Sitemap is not valid: The Navigation Type is empty");

	}

	/**
	 * Get all datasets, by navigating the site between a range of Query/Path
	 * parameter values
	 * 
	 * @param startValue
	 * @param endValue
	 * @throws InterruptedException
	 */
	private static List<Document> getDatasetsDocumentByRange(String startUrl, NavigationParameter navParam)
			throws InterruptedException {

		List<Document> rangeResult = Collections.synchronizedList(new ArrayList<Document>());
		Integer rangeScale = WEB_SCRAPER_RANGE_SCALE_NUM, skipped = 0;
		Integer startValue = Integer.parseInt(navParam.getStartValue());
		Integer endValue = Integer.parseInt(navParam.getEndValue());

		// Break the whole range in a thread pool, each of them collects
		// sequentially a rangeScale number of Documents
		Integer threadNumber = calculateRangeThreadNumber(startValue, endValue, rangeScale);

		// Create a Runnable list of threadNumber size and an associated
		// CountDownLatch

		logger.info("Starting Web Scraper RANGE retrieval: " + threadNumber + " threads");
		CountDownLatch rangeLatch = new CountDownLatch(threadNumber);

		for (int i = 0; i < threadNumber; i++) {

			// The last thread of the pool is launched with the correct range
			// rest (calculated with: end-start MOD rangeScale )
			if (i == threadNumber - 1)
				new Thread(new RangeWorker(startUrl, navParam, i, rangeScale,
						calculateRangeRest(startValue, endValue, rangeScale), rangeResult, rangeLatch)).start();
			else
				// All the other threads are launched with range rest 0
				new Thread(new RangeWorker(startUrl, navParam, i, rangeScale, 0, rangeResult, rangeLatch)).start();
		}

		logger.info("Waiting for threads...");
		rangeLatch.await(COUNTDOWN_LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		// skipped = (startValue-endValue) - rangeResult.size();
		// if (skipped > 0)
		logger.info("All threads returned\n Returned documents: " + rangeResult.size() + " - Expected: "
				+ (endValue - startValue));
		// rangeResult.stream().forEach(d ->
		// System.out.println(d.select("div#notices div:nth-of-type(10)")));
		return rangeResult;

	}

	private static List<Document> getDatasetsDocumentByPage(String startUrl, NavigationParameter navParam)
			throws PageNumberNotParseableException, InterruptedException {

		List<Document> pageResult = Collections.synchronizedList(new ArrayList<Document>());
		List<PageSelector> pageSelectors = navParam.getPageSelectors();
		Integer startPageValue = Integer.parseInt(navParam.getStartValue());
		Integer threadNumber = null, skipped = 0;

		/*
		 * ****************** PAGES NUMBER RETRIEVAL ************************Try to
		 * retrieve the pagesNumber by using automatic or manual mode Resulting pages
		 * number will corresponds to the number of launched threads - Automatic: Use
		 * the "lastPage" pageSelector - Manual: Use the pagesNumber field of
		 * navigationParameter
		 *
		 */

		try {
			if ((threadNumber = navParam.getPagesNumber()) == null || threadNumber == 0)
				threadNumber = calculatePageThreadNumber(startUrl, navParam, pageSelectors);

		} catch (PageNumberNotParseableException | IllegalArgumentException e) {
			throw new PageNumberNotParseableException(e.getMessage());
		}

		// Create a Runnable list of threadNumber size and an associated
		// CountDownLatch

		/*
		 * Analizza le pagine utilizzando i pageSelectors, da essi prende gli url dei
		 * risultati e chiama lo scraping del dataset singolo
		 */

		logger.info("Starting Web Scraper PAGE retrieval: " + threadNumber + " Threads");

		CountDownLatch pageLatch = new CountDownLatch(threadNumber);
		for (int i = startPageValue; i < threadNumber; i++) {
			new Thread(new PageWorker(startUrl, navParam, i, pageResult, pageLatch)).start();
		}

		logger.info("Waiting for threads...");
		pageLatch.await(COUNTDOWN_LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
		logger.info("All threads returned - Returned documents: " + pageResult.size());

		return pageResult;

	}

	/**
	 * Calculate the Thread Number for Pagination. Use the last page Number, by
	 * extracting it either from the first Page Document or explicit field of input
	 * NavigationParameter. In addition, if datasetsPerPage is present, sclaes the
	 * last page Number with it (becacuse here we have an offset pagination
	 * navigation)
	 * 
	 * @param startUrl
	 * @param navParam
	 * @param pageSelectors
	 * @return
	 * @return
	 * @throws PageNumberNotParseableException
	 */
	private static Integer calculatePageThreadNumber(String startUrl, NavigationParameter navParam,
			List<PageSelector> pageSelectors) throws PageNumberNotParseableException {

		int retryNum = PAGINATION_RETRY_NUM, pagesNumber;
		boolean retry = false;

		/* *************** AUTOMATIC MODE **************/
		try {
			List<? extends WebScraperSelector> bases = pageSelectors;
			PageSelector selector = (PageSelector) getSelectorByName(bases, "lastPage");
			Integer startValue = Integer.parseInt(navParam.getStartValue());
			do {

				try {
					Document firstPageDocument = Jsoup.connect(startUrl).get();

					if (firstPageDocument != null) {

						/* **** Extract the pages number from the Last Page Link **********/
						String lastPageLink = firstPageDocument.select(selector.getSelector()).attr("href");
						pagesNumber = (startValue == 0 ? 1 : 0)
								+ parseLastPageValueFromUrl(navParam.getName(), lastPageLink);

						/*
						 * **** If there is a datasetsPerPage navParam > 1, the retrieved pagesNumber
						 * from last page represents actually the offset, to be scaled with the
						 * datasetsPerPage
						 */

						Integer pageMultiplier = navParam.getDatasetsPerPage();
						pageMultiplier = (pageMultiplier == null || pageMultiplier < 1) ? 1 : pageMultiplier;

						int pagesQuot = Math.floorDiv(pagesNumber, pageMultiplier);
						int pagesMod = Math.floorMod(pagesNumber, pageMultiplier);
						return pagesMod == 0 ? pagesQuot : pagesQuot + 1;
					}
				} catch (IOException | PageNumberNotParseableException e) {
					logger.info("\nThread: " + Thread.currentThread().getId() + " Error: " + e.getMessage()
							+ " while retrieving the pagination from first Page Document\nAttempt n: " + retryNum);
					retry = true;
					retryNum--;
					if (retryNum == 0) {
						retry = false;
						throw e;
					}
				}

			} while (retry);

			throw new IOException("It was reached the max connection attempts for Dataset Document retrieval");

		} catch (Exception e) {

			/* *************** MANUAL MODE **********************/
			if (navParam.getPagesNumber() == null)
				throw new PageNumberNotParseableException(
						"It was not possible to get the last Page Value neither with Automatic nor Manual mode");
			else
				return navParam.getPagesNumber();
		}
	}

	public static WebScraperSelector getSelectorByName(List<? extends WebScraperSelector> bases, String selectorName)
			throws WebScraperSelectorNotFoundException {
		try {
			return bases.stream().filter(item -> selectorName.equalsIgnoreCase(item.getName()))
					.collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			throw new WebScraperSelectorNotFoundException(
					"Web Scraper Selector with name " + selectorName + " was not found - Error: " + e.getMessage());
		}

	}

	public static List<WebScraperSelector> getSelectorsByName(List<WebScraperSelector> selectors, String selectorName)
			throws WebScraperSelectorNotFoundException {
		try {
			return selectors.stream().filter(item -> selectorName.equalsIgnoreCase(item.getName()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new WebScraperSelectorNotFoundException(
					"Web Scraper Selectors with name " + selectorName + " were not found - Error: " + e.getMessage());
		}

	}

	/* ********** PAGE UTILITY METHODS *****************************/
	private static Integer parseLastPageValueFromUrl(String navParamName, String url)
			throws PageNumberNotParseableException {

		Pattern patterns[] = { Pattern.compile("(" + navParamName + "=\\w*\\b)"),
				Pattern.compile("javascript:\\w*\\(([^)]+)\\);*") };

		for (Pattern p : patterns) {

			Matcher matcher = p.matcher(url);
			String result = null;
			if (matcher.find()) {
				if ((result = matcher.group(1)) != null)

					if (result.split("=").length > 1) {
						try {
							return Integer.parseInt(result.split("=")[1]);
						} catch (IllegalArgumentException e) {
							throw new PageNumberNotParseableException(e.getMessage());
						}
					}

				try {
					return Integer.parseInt(result);
				} catch (IllegalArgumentException e) {
					throw new PageNumberNotParseableException(e.getMessage());
				}

			}
		}
		throw new PageNumberNotParseableException("It is not possible to extract the Page number from passed URL!");
	}

	/* ********** RANGE UTILITY METHODS *****************************/
	private static int calculateRangeThreadNumber(int startValue, int endValue, int scale) {
		int range = endValue - startValue;
		int quot = Math.floorDiv(range, scale);
		int mod = Math.floorMod(range, scale);
		return mod == 0 ? quot : quot + 1;

	}

	private static int calculateRangeRest(int startValue, int endValue, int scale) {
		int range = endValue - startValue;
		return Math.floorMod(range, scale);

	}

	private static String buildRangeUrl(String startUrl, NavigationParameter param, int incrementValue)
			throws NavigationTypeNotValidException {

		if (param.getType().equals(NavigationType.PATH_RANGE))
			return startUrl + "/" + param.getName() + "/"
					+ String.valueOf(Integer.parseInt(param.getStartValue()) + incrementValue);
		else if (param.getType().equals(NavigationType.QUERY_RANGE))
			return startUrl + "?" + param.getName() + "="
					+ String.valueOf(Integer.parseInt(param.getStartValue()) + incrementValue);
		else
			throw new NavigationTypeNotValidException("The input navigation Type is not valid");
	}

}
