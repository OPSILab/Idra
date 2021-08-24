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

package it.eng.idra.utils;

import com.google.common.collect.Ordering;
import it.eng.idra.beans.odms.OdmsCatalogue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
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
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonUtil.
 */
public class CommonUtil {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CommonUtil.class);

  /** The dt formatter. */
  private static DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
      .withZone(ZoneOffset.UTC);

  /** The date formats. */
  private static String[] dateFormats = { "yyyy", "dd/MM/yyyy", "yyyy-MM-dd",
      "EEE MMM dd HH:mm:ss zzz yyyy", "EEEE dd MMMM yyyy", "dd MMMM yyyy",
      "yyyy-MM-dd'T'HH:mm:ss[XXX][X]", "EEEE, dd MMMM yyyy" };

  /** The id order. */
  public static Ordering<OdmsCatalogue> idOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getId() - other.getId();
    }
  };

  /** The name order. */
  public static Ordering<OdmsCatalogue> nameOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getName().compareTo(other.getName());
    }
  };

  /** The host order. */
  public static Ordering<OdmsCatalogue> hostOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getHost().compareTo(other.getHost());
    }
  };

  /** The type order. */
  public static Ordering<OdmsCatalogue> typeOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getNodeType().compareTo(other.getNodeType());
    }
  };

  /** The federation level order. */
  public static Ordering<OdmsCatalogue> federationLevelOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getFederationLevel().compareTo(other.getFederationLevel());
    }
  };

  /** The state order. */
  public static Ordering<OdmsCatalogue> stateOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getNodeState().compareTo(other.getNodeState());
    }
  };

  /** The is active order. */
  public static Ordering<OdmsCatalogue> isActiveOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.isActive().compareTo(other.isActive());
    }
  };

  /** The refresh period order. */
  public static Ordering<OdmsCatalogue> refreshPeriodOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getRefreshPeriod() - other.getRefreshPeriod();
    }
  };

  /** The dataset count order. */
  public static Ordering<OdmsCatalogue> datasetCountOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getDatasetCount() - other.getDatasetCount();
    }
  };

  /** The register date order. */
  public static Ordering<OdmsCatalogue> registerDateOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getRegisterDate().compareTo(other.getRegisterDate());
    }
  };

  /** The last update order. */
  public static Ordering<OdmsCatalogue> lastUpdateOrder = new Ordering<OdmsCatalogue>() {
    public int compare(OdmsCatalogue one, OdmsCatalogue other) {
      return one.getLastUpdateDate().compareTo(other.getLastUpdateDate());
    }
  };

  /** The Constant ROWSDEFAULT. */
  public static final Integer ROWSDEFAULT = 10;

  /** The Constant OFFSETDEFAULT. */
  public static final Integer OFFSETDEFAULT = 0;

  /** The email pattern. */
  private static Pattern emailPattern = Pattern
      .compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b");

  /**
   * Encode password.
   *
   * @param pwd the pwd
   * @return the string
   * @throws NoSuchAlgorithmException the no such algorithm exception
   */
  public static String encodePassword(String pwd) throws NoSuchAlgorithmException {
    // Esegue la codifica MD5
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(pwd.getBytes());

    byte[] byteData = md.digest();

    // Conversione della password codificata in Stringa
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
  }

  /**
   * To utc date.
   *
   * @param dateString the date string
   * @return the string
   * @throws IllegalArgumentException the illegal argument exception
   */
  public static String toUtcDate(String dateString) throws IllegalArgumentException {
    for (String dateFormat : dateFormats) {
      try {

        if (dateFormat.contains("H")) {
          return dtFormatter
              .format(DateTimeFormatter.ofPattern(dateFormat, Locale.US).parse(dateString));
        } else {
          return dtFormatter.format(
              LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, Locale.US))
                  .atStartOfDay().atZone(ZoneOffset.UTC));
        }
      } catch (DateTimeParseException ignore) {
        logger.debug(ignore.getLocalizedMessage());
      }

    }
    logger.error("Invalid date: " + dateString + " impossible to format, leaving the default");
    return dateString;
  }

  /**
   * From local to utc date.
   *
   * @param originalDateString the original date string
   * @param locale             the locale
   * @return the string
   */
  public static String fromLocalToUtcDate(String originalDateString, Locale locale) {

    if (StringUtils.isNotBlank(originalDateString)) {
      String dateString = originalDateString.toLowerCase();

      if (locale == null) {
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale l : locales) {
          for (String dateFormat : dateFormats) {
            try {

              if (dateFormat.contains("H")) {
                return dtFormatter
                    .format(DateTimeFormatter.ofPattern(dateFormat, l).parse(dateString));
              } else {
                return dtFormatter
                    .format(LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, l))
                        .atStartOfDay().atZone(ZoneOffset.UTC));
              }

            } catch (DateTimeParseException ignore) {
              logger.debug(ignore.getLocalizedMessage());
            }
          }
        }

      } else {

        for (String dateFormat : dateFormats) {
          try {

            if (dateFormat.contains("H")) {
              return dtFormatter
                  .format(DateTimeFormatter.ofPattern(dateFormat, locale).parse(dateString));
            } else {
              return dtFormatter.format(
                  LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat, locale))
                      .atStartOfDay().atZone(ZoneOffset.UTC));
            }
          } catch (DateTimeParseException ignore) {
            logger.debug(ignore.getLocalizedMessage());
          }

        }
      }

    }

    throw new IllegalArgumentException("Invalid date: " + originalDateString);

  }

  /**
   * Format date.
   *
   * @param dt the dt
   * @return the string
   */
  public static String formatDate(ZonedDateTime dt) {
    return dtFormatter.format(dt.truncatedTo(ChronoUnit.SECONDS));

  }

  /**
   * Parses the date.
   *
   * @param dateString the date string
   * @return the zoned date time
   */
  public static ZonedDateTime parseDate(String dateString) {
    return ZonedDateTime.from(dtFormatter.parse(dateString));
  }

  /**
   * Fix bad UTC date.
   *
   * @param date the date
   * @return the string
   */
  public static String fixBadUtcDate(String date) {

    Pattern pattern1 = Pattern
        .compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{6})$");
    if (pattern1.matcher(date).find()) {
      return date.substring(0, date.length() - 7) + "Z";
    }

    Pattern pattern2 = Pattern
        .compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})$");
    if (pattern2.matcher(date).find()) {
      return date.substring(0, date.length() - 4) + "Z";
    }

    Pattern pattern3 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2})$");
    if (pattern3.matcher(date).find()) {
      return date + "Z";
    }

    Pattern pattern4 = Pattern.compile(
        "([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}" + ":[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2})$");
    if (pattern4.matcher(date).find()) {
      return date.substring(0, date.length() - 6) + "Z";
    }
    Pattern pattern5 = Pattern
        .compile("([0-9]{4}-[0-9]{2}-[0-9]{2}" + "T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}Z)$");
    if (pattern5.matcher(date).find()) {
      return date.substring(0, date.length() - 5) + "Z";
    }

    Pattern pattern6 = Pattern
        .compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2})(.[0-9]*)?$");
    Matcher m6 = pattern6.matcher(date);
    if (m6.find()) {
      return m6.group(1) + "Z";
    }
    Pattern pattern7 = Pattern
        .compile("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2})(.*)?$");
    Matcher m7 = pattern7.matcher(date);
    if (m7.find()) {
      return m7.group(1) + "Z";
    }

    return date;
  }

  /**
   * Extract format from file extension.
   *
   * @param downloadUrl the download URL
   * @return the string
   */
  public static String extractFormatFromFileExtension(String downloadUrl) {

    Matcher matcher = Pattern.compile("\\w+\\.(\\w\\w\\w(\\w)?)$").matcher(downloadUrl);
    String result = null;

    return (matcher.find() && (result = matcher.group(1)) != null) ? result : "";

  }

  /**
   * Extract frequency from uri.
   *
   * @param uri the uri
   * @return the string
   */
  public static String extractFrequencyFromUri(String uri) {

    Matcher matcher = Pattern.compile("http:\\/\\/publications\\.europa\\."
        + "eu\\/resource\\/authority\\/frequency(\\/|#)(\\w*)").matcher(uri);
    String result = null;

    return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

  }

  /**
   * Check if is email.
   *
   * @param emailValue the email value
   * @return true, if successful
   */
  public static boolean checkIfIsEmail(String emailValue) {

    return emailPattern.matcher(emailValue).find();
  }

  /**
   * Extract seo identifier.
   *
   * @param title              the title
   * @param internalIdentifier the internal identifier
   * @param nodeId             the node ID
   * @return the string
   */
  public static String extractSeoIdentifier(String title, String internalIdentifier,
      String nodeId) {
    // String title1 =
    // unaccent(title).toLowerCase().replaceAll("[^\\p{L}\\p{Z}\\p{N}]","");
    // String title1 = unaccent(title).toLowerCase().replaceAll("[^\\w\\s]","");
    // String title1 = unaccent(title.toLowerCase())
    // .replaceAll("[_+-.,!@#$%^*():?=\\\\;&amp;\\/|&gt;&lt;&quot;']","");
    String title1 = StringUtils.normalizeSpace(
        unaccent(title.trim().toLowerCase()).replaceAll("[^\\p{L}\\p{Z}\\p{N}]", ""));

    // In order to support this features for non ASCII characters, the
    // internalIdentifier is used
    // since it can be a problem with solr server
    boolean foundMatch = false;
    Pattern regex = Pattern.compile("\\p{L}");
    Matcher regexMatcher = regex.matcher(title1);
    foundMatch = regexMatcher.find();
    if (!foundMatch) {
      return internalIdentifier + "-" + nodeId;
    }

    String seoId = "";
    logger.debug("Old: " + title);
    logger.debug("New: " + title1);
    String[] arr = title1.split(" ");
    if (arr.length > 4) {
      seoId = String.join("-", Arrays.copyOfRange(arr, 0, 4));
    } else {
      seoId = String.join("-", arr);
    }
    String[] arr1 = internalIdentifier.split("-");
    logger.debug("seoID: " + seoId + "-" + arr1[arr1.length - 2] + "-" + nodeId);
    return seoId + "-" + arr1[arr1.length - 1] + "-" + nodeId;
  }

  // https://stackoverflow.com/questions/3322152/is-there-a-way-to-get-rid-of-accents-and-convert-a-whole-string-to-regular-lette

  /**
   * Unaccent.
   *
   * @param src the src
   * @return the string
   */
  public static String unaccent(String src) {
    return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    // .replaceAll("[^\\p{M}]", ""); -> it works also for japanese char but solr
    // doesn't return the proper dataset
  }

  /**
   * Store file.
   *
   * @param filePath the file path
   * @param fileName the file name
   * @param content  the content
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void storeFile(String filePath, String fileName, String content)
      throws IOException {
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

  /**
   * Delete file.
   *
   * @param filePath the file path
   */
  public static void deleteFile(String filePath) {
    logger.info("Deleting file: " + filePath);
    try {
      File file = new File(filePath);
      if (file.delete()) {
        logger.info(file.getName() + " is deleted!");
      } else {
        logger.info("Delete operation is failed.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * From millis to utc date.
   *
   * @param time the time
   * @return the string
   */
  public static String fromMillisToUtcDate(Long time) {
    return dtFormatter.format(new Date(time * 1000).toInstant());
  }

  /**
   * Encrypt.
   *
   * @param text the text
   * @return the string
   */
  public static String encrypt(String text) {
    String encr = "";
    try {
      String key = "key1234567";
      Key aesKey = new SecretKeySpec(Arrays.copyOf(key.getBytes("UTF-8"), 16), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      byte[] encrypted = cipher.doFinal(text.getBytes());

      encr = Base64.getEncoder().encodeToString(encrypted);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return encr;
  }

  /**
   * Decrypt.
   *
   * @param encr the encr
   * @return the string
   */
  public static String decrypt(String encr) {
    String decrypted = "";
    try {
      String key = "key1234567";
      Key aesKey = new SecretKeySpec(Arrays.copyOf(key.getBytes("UTF-8"), 16), "AES");
      Cipher cipher = Cipher.getInstance("AES");

      cipher.init(Cipher.DECRYPT_MODE, aesKey);
      byte[] decode = Base64.getDecoder().decode(encr);
      decrypted = new String(cipher.doFinal(decode));

    } catch (Exception e) {
      e.printStackTrace();
    }
    return decrypted;
  }

}
