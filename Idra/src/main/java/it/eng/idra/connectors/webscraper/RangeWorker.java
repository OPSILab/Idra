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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.webscraper.NavigationParameter;
import it.eng.idra.beans.webscraper.NavigationTypeNotValidException;
import it.eng.idra.utils.PropertyManager;

public class RangeWorker implements Runnable {

	private static final int RANGE_RETRY_NUM;
	private static final int JSOUP_THROTTLING;

	private List<Document> outputScraper;
	private CountDownLatch countDownLatch;
	private int roundNumber, rangeScale, rangeRest;
	private String startUrl;
	private NavigationParameter navParam;
	private Logger logger = LogManager.getLogger(RangeWorker.class);

	static {
		RANGE_RETRY_NUM = Integer.parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_RANGE_RETRY_NUM));
		JSOUP_THROTTLING = Integer.parseInt(PropertyManager.getProperty(IdraProperty.WEB_SCRAPER_GLOBAL_THROTTILING));

	}

	public RangeWorker(String startUrl, NavigationParameter navParam, int roundNumber, int rangeScale, int rangeRest,
			List<Document> outputScraper, CountDownLatch countDownLatch) {

		this.roundNumber = roundNumber;
		this.rangeScale = rangeScale;
		this.rangeRest = rangeRest;
		this.navParam = navParam;
		this.startUrl = startUrl;
		this.outputScraper = outputScraper;
		this.countDownLatch = countDownLatch;

	}

	@Override
	public void run() {

		List<Document> roundResult = new ArrayList<Document>();

		logger.info("Thread: " + Thread.currentThread().getId() + " ROUND: " + roundNumber + " Starting to retrieve "
				+ rangeScale + " documents");

		for (int j = 0; j < (rangeRest != 0 ? rangeRest : rangeScale); j++) {

			boolean retry = false;
			int retryNum = 1;

			/*
			 * Start trying to get the document, for max N attempts
			 */

			do {

				int finalParam = (Integer.parseInt(navParam.getStartValue()) + (roundNumber * rangeScale) + j);
				try {

					// logger.debug("Thread: " + Thread.currentThread().getId()
					// + " Round: " + roundNumber
					// + " - Getting document number: " + j + " with final
					// query/path parameter: "
					// + finalParam);

					/*
					 * AVOID DOS EFFECT TO ODMS SERVER
					 */
					try {
						TimeUnit.MILLISECONDS.sleep(JSOUP_THROTTLING);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Document doc = WebScraper.getDatasetDocumentByIncrement(startUrl, navParam,
							(roundNumber * rangeScale) + j);
					roundResult.add(doc);
					logger.debug("Thread: " + Thread.currentThread().getId() + " - Param: " + finalParam);
					retry = false;

					// logger.debug("Thread: " + Thread.currentThread().getId()
					// + " Got document with final param "
					// + (Integer.parseInt(navParam.getStartValue()) +
					// (roundNumber * rangeScale) + j));

				} catch (IOException | NavigationTypeNotValidException e) {
					logger.info("\nThread: " + Thread.currentThread().getId() + " Error: " + e.getMessage()
							+ " while retrieving documents with parameter: " + finalParam + "\nAttempt n: " + retryNum);
					retry = true;
					retryNum++;
					if (retryNum > RANGE_RETRY_NUM) {
						retry = false;
						e.printStackTrace();
					}
				}

			} while (retry);
		}

		logger.info("Thread: " + Thread.currentThread().getId() + " Adding " + roundResult.size()
				+ " retrieved document to shared total List");
		outputScraper.addAll(roundResult);
		countDownLatch.countDown();
		logger.debug("Thread: " + Thread.currentThread().getId() + " Decremented Latch to count: "
				+ countDownLatch.getCount());
		// System.out.println("____________________________________________________");
	}
}
