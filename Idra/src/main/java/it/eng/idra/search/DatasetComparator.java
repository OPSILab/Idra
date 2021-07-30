package it.eng.idra.search;

import it.eng.idra.beans.dcat.DcatDataset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public enum DatasetComparator implements Comparator<DcatDataset> {
  IDENTIFIER_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getId().compareTo(o2.getId());
    }
  },
  NODEID_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getNodeId().compareTo(o2.getNodeId());
    }
  },
  TITLE_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getTitle().getValue().compareTo(o2.getTitle().getValue());
    }
  },
  PUBLISHER_NAME_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getPublisher().getName().getValue()
          .compareTo(o2.getPublisher().getName().getValue());
    }
  },
  CONTACTPOINT_FN_SORT {
    public int compare(DcatDataset o1, DcatDataset o2) {
      return o1.getContactPoint().get(0).getFn().getValue()
          .compareTo(o2.getContactPoint().get(0).getFn().getValue());
    }
  },
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
