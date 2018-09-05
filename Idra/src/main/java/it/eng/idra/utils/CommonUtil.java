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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
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

		Matcher matcher = Pattern.compile(
				"([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9][0-9][0-9][0-9])$")
				.matcher(date);
		if (matcher.find())
			date = date.substring(0, date.length() - 7) + "Z";
		else {
			matcher = Pattern.compile(
					"([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9])$")
					.matcher(date);
			if (matcher.find())
				date = date.substring(0, date.length() - 4) + "Z";
			else {
				matcher = Pattern
						.compile("([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9])$")
						.matcher(date);
				if (matcher.find())
					date = date + "Z";
			}
		}

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

}
