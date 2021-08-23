/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.search;

import it.eng.idra.beans.dcat.DcatDataset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc
/**
 * The Enum DatasetComparator.
 */
public enum DatasetComparator implements Comparator<DcatDataset> {

  /** The identifier sort. */
  IDENTIFIER_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getId().compareTo(o2.getId());
    }
  },

  /** The nodeid sort. */
  NODEID_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getNodeId().compareTo(o2.getNodeId());
    }
  },

  /** The title sort. */
  TITLE_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getTitle().getValue().compareTo(o2.getTitle().getValue());
    }
  },

  /** The publisher name sort. */
  PUBLISHER_NAME_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getPublisher().getName().getValue()
          .compareTo(o2.getPublisher().getName().getValue());
    }
  },

  /** The contactpoint fn sort. */
  CONTACTPOINT_FN_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getContactPoint().get(0).getFn().getValue()
          .compareTo(o2.getContactPoint().get(0).getFn().getValue());
    }
  },

  /** The contactpoint hasemail sort. */
  CONTACTPOINT_HASEMAIL_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getContactPoint().get(0).getHasEmail().getValue()
          .compareTo(o2.getContactPoint().get(0).getHasEmail().getValue());
    }
  },
  // LICENSETITLE_SORT {
  // public int compare(DCATDataset o1, DCATDataset o2) {
  // return o1.getLicense().getValue().compareTo(o2.getLicense().getValue());
  // }
  /** The issued sort. */
  // },
  ISSUED_SORT {

    /**
     * compare.
     */
    public int compare(DcatDataset o1, DcatDataset o2) {

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      GregorianCalendar date1 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
      GregorianCalendar date2 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

      try {
        date1.setTime(sdf.parse(o1.getReleaseDate().getValue()));
        date2.setTime(sdf.parse(o2.getReleaseDate().getValue()));
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return date1.compareTo(date2);
    }
  },

  /** The modified sort. */
  MODIFIED_SORT {
    /**
     * compare.
     */
    public int compare(DcatDataset o1, DcatDataset o2) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      GregorianCalendar date1 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
      GregorianCalendar date2 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

      try {
        date1.setTime(sdf.parse(o1.getUpdateDate().getValue()));
        date2.setTime(sdf.parse(o2.getUpdateDate().getValue()));
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return date1.compareTo(date2);
    }
  };

  /**
   * compare.
   *
   * @param other the other
   * @return the comparator
   */
  public static Comparator<DcatDataset> decending(final Comparator<DcatDataset> other) {
    return new Comparator<DcatDataset>() {
      public int compare(DcatDataset o1, DcatDataset o2) {
        return -1 * other.compare(o1, o2);
      }
    };
  }

  /**
   * compare.
   *
   * @param multipleOptions the multiple options
   * @return the comparator
   */
  public static Comparator<DcatDataset> getComparator(final DatasetComparator... multipleOptions) {
    return new Comparator<DcatDataset>() {
      public int compare(DcatDataset o1, DcatDataset o2) {
        for (DatasetComparator option : multipleOptions) {
          int result = option.compare(o1, o2);
          if (result != 0) {
            return result;
          }
        }
        return 0;
      }
    };
  }
}
