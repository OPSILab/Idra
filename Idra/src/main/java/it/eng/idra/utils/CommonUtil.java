/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.idra.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Ordering;

import it.eng.idra.beans.odms.ODMSCatalogue;

public class CommonUtil {

	private static Logger logger = LogManager.getLogger(CommonUtil.class);
	private static DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);
	private static String[] dateFormats = { "dd/MM/yyyy", "yyyy-MM-dd", "EEE MMM dd HH:mm:ss zzz yyyy",
			"EEEE dd MMMM yyyy", "dd MMMM yyyy", "yyyy-MM-dd'T'HH:mm:ss[XXX][X]"  };

	public static Ordering<ODMSCatalogue> idOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getId()-other.getId();
		}
	};
	
	public static Ordering<ODMSCatalogue> nameOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getName().compareTo(other.getName());
		}
	};
	
	public static Ordering<ODMSCatalogue> hostOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getHost().compareTo(other.getHost());
		}
	};
	
	public static Ordering<ODMSCatalogue> typeOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getNodeType().compareTo(other.getNodeType());
		}
	};
	
	public static Ordering<ODMSCatalogue> federationLevelOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getFederationLevel().compareTo(other.getFederationLevel());
		}
	};
	
	public static Ordering<ODMSCatalogue> stateOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getNodeState().compareTo(other.getNodeState());
		}
	};
	
	public static Ordering<ODMSCatalogue> isActiveOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.isActive().compareTo(other.isActive());
		}
	};
	
	public static Ordering<ODMSCatalogue> refreshPeriodOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getRefreshPeriod()-other.getRefreshPeriod();
		}
	};
	
	public static Ordering<ODMSCatalogue> datasetCountOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getDatasetCount()-other.getDatasetCount();
		}
	};

	public static Ordering<ODMSCatalogue> registerDateOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getRegisterDate().compareTo(other.getRegisterDate());
		}
	};
	
	public static Ordering<ODMSCatalogue> lastUpdateOrder = new Ordering<ODMSCatalogue>() {
		public int compare(ODMSCatalogue one, ODMSCatalogue other) {
			return one.getLastUpdateDate().compareTo(other.getLastUpdateDate());
		}
	};
	
	public static final Integer ROWSDEFAULT = 10, OFFSETDEFAULT = 0;
	
	private static Pattern emailPattern = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b");

	public static String encodePassword(String pwd) throws NoSuchAlgorithmException {
		// Esegue la codifica MD5
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(pwd.getBytes());

		byte byteData[] = md.digest();

		// Conversione della password codificata in Stringa
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++)
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

		return sb.toString();
	}

	public static String toUtcDate(String dateString) throws IllegalArgumentException {

		// SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		// String[] dateFormats = { "yyyy-MM-dd", "EEE MMM dd HH:mm:ss zzz yyyy" };
		// for (String dateFormat : dateFormats) {
		// try {
		// return out.format(new SimpleDateFormat(dateFormat,
		// Locale.US).parse(dateString));
		// } catch (ParseException ignore) {
		// }
		// }
		// throw new IllegalArgumentException("Invalid date: " + dateString);

		for (String dateFormat : dateFormats) {
			try {

				if (dateFormat.contains("H"))
					return dtFormatter.format(DateTimeFormatter.ofPattern(dateFormat, Locale.US).parse(dateString));
				else
					return dtFormatter
							.format(LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, Locale.US))
									.atStartOfDay().atZone(ZoneOffset.UTC));
				// .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.US)));
			} catch (DateTimeParseException ignore) {
			}

		}
		logger.error("Invalid date: " + dateString+" impossible to format, leaving the default");
		return dateString;
//		throw new IllegalArgumentException("Invalid date: " + dateString);
	}

	public static String fromLocalToUtcDate(String originalDateString, Locale locale) {

		if (StringUtils.isNotBlank(originalDateString)) {
			String dateString = originalDateString.toUpperCase();

			if (locale == null) {
				Locale[] locales = Locale.getAvailableLocales();

				for (Locale l : locales) {
					for (String dateFormat : dateFormats) {
						try {

							if (dateFormat.contains("H"))
								return dtFormatter.format(DateTimeFormatter.ofPattern(dateFormat, l).parse(dateString));
							else
								return dtFormatter
										.format(LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, l))
												.atStartOfDay().atZone(ZoneOffset.UTC));

						} catch (DateTimeParseException ignore) {
						}
					}
				}

			} else {

				for (String dateFormat : dateFormats) {
					try {

						if (dateFormat.contains("H"))
							return dtFormatter
									.format(DateTimeFormatter.ofPattern(dateFormat, locale).parse(dateString));
						else
							return dtFormatter
									.format(LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, locale))
											.atStartOfDay().atZone(ZoneOffset.UTC));
						// .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.US)));
					} catch (DateTimeParseException ignore) {
					}

				}
			}

		}

		throw new IllegalArgumentException("Invalid date: " + originalDateString);

	}

	public static String formatDate(ZonedDateTime dt) {
		return dtFormatter.format(dt.truncatedTo(ChronoUnit.SECONDS));

	}

	public static ZonedDateTime parseDate(String dateString) {
		return ZonedDateTime.from(dtFormatter.parse(dateString));
	}

	public static String fixBadUTCDate(String date) {

		Pattern pattern1 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{6})$");
		Pattern pattern2 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})$");
		Pattern pattern3 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2})$");
		Pattern pattern4 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2})$");
		Pattern pattern5 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}Z)$");
		
		if(pattern1.matcher(date).find())
			return date.substring(0, date.length() - 7) + "Z";
		
		if(pattern2.matcher(date).find())
			return date.substring(0, date.length() - 4) + "Z";
			
		if(pattern3.matcher(date).find())
			return date + "Z";
		
		if(pattern4.matcher(date).find())
			return date.substring(0, date.length() - 6) + "Z";
		
		if(pattern5.matcher(date).find())
			return date.substring(0, date.length() - 5) + "Z";
		
		return date;
	}

	public static String extractFormatFromFileExtension(String downloadURL) {

		Matcher matcher = Pattern.compile("\\w+\\.(\\w\\w\\w(\\w)?)$").matcher(downloadURL);
		String result = null;

		return (matcher.find() && (result = matcher.group(1)) != null) ? result : "";

	}

	public static String extractFrequencyFromURI(String uri) {

		Matcher matcher = Pattern
				.compile("http:\\/\\/publications\\.europa\\.eu\\/resource\\/authority\\/frequency(\\/|#)(\\w*)")
				.matcher(uri);
		String result = null;

		return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

	}

	public static boolean checkIfIsEmail(String emailValue) {

		return emailPattern.matcher(emailValue).find();
	}

	public static String extractSeoIdentifier(String title, String internalIdentifier,String nodeID) {
//		String title1 = unaccent(title).toLowerCase().replaceAll("[^\\p{L}\\p{Z}\\p{N}]","");
//		String title1 = unaccent(title).toLowerCase().replaceAll("[^\\w\\s]","");
//		String title1 = unaccent(title.toLowerCase()).replaceAll("[_+-.,!@#$%^*():?=\\\\;&amp;\\/|&gt;&lt;&quot;']","");
		String title1 = StringUtils.normalizeSpace(unaccent(title.trim().toLowerCase()).replaceAll("[^\\p{L}\\p{Z}\\p{N}]",""));
		
		//In order to support this features for non ASCII characters, the internalIdentifier is used
		//since it can be a problem with solr server
		boolean foundMatch = false;
		Pattern regex = Pattern.compile("\\p{L}");
		Matcher regexMatcher = regex.matcher(title1);
		foundMatch = regexMatcher.find();
		if(!foundMatch) {
			return internalIdentifier+"-"+nodeID;
		}
		
		String seoId="";
		logger.debug("Old: "+title);
		logger.debug("New: "+title1);
		String arr[] = title1.split(" ");
		if(arr.length>4) {
			seoId = String.join("-",Arrays.copyOfRange(arr, 0, 4));
		}else {
			seoId = String.join("-",arr);
		}
		String arr1 [] = internalIdentifier.split("-");
		logger.debug("seoID: "+seoId+"-"+arr1[arr1.length-2]+"-"+nodeID);
		return seoId+"-"+arr1[arr1.length-1]+"-"+nodeID;
	}
	
	
	//https://stackoverflow.com/questions/3322152/is-there-a-way-to-get-rid-of-accents-and-convert-a-whole-string-to-regular-lette
	
	public static String unaccent(String src) {
		return Normalizer
				.normalize(src, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
				//.replaceAll("[^\\p{M}]", ""); -> it works also for japanese char but solr doesn't return the proper dataset
	}
	
	public static void storeFile(String filePath,String fileName,String content) throws IOException {
		FileWriter out = null;
		logger.info("Writing model to file: " + filePath + fileName);

		Instant tick = Instant.now();
		try {
			out = new FileWriter(filePath + fileName);
			out.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
			Instant tock = Instant.now();
			logger.info("File writing completed in: " + Duration.between(tick, tock).toString());
		}
	}
	
	public static void deleteFile(String filePath){
		logger.info("Deleting file: " + filePath);
		try{
    		File file = new File(filePath);
    		if(file.delete()){
    			logger.info(file.getName() + " is deleted!");
    		}else{
    			logger.info("Delete operation is failed.");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	public static String fromMillisToUtcDate(Long time) {
		return dtFormatter.format(new Date(time*1000).toInstant());
	}
}
