package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetMeta.
 */
public class DatasetMeta {

  /** The default meta. */
  @SerializedName("default")
  private InnerDatasetMetaDefault defaultMeta;

  /** The dcat. */
  private Map<String, Object> dcat;

  /** The inspire. */
  private Map<String, Object> inspire;

  /** The custom. */
  private Map<String, Object> custom;

  /**
   * Gets the default.
   *
   * @return the default
   */
  public InnerDatasetMetaDefault get_default() {
    return defaultMeta;
  }

  /**
   * Sets the default.
   *
   * @param defaultMeta the new default
   */
  public void set_default(InnerDatasetMetaDefault defaultMeta) {
    this.defaultMeta = defaultMeta;
  }

  /**
   * Gets the dcat.
   *
   * @return the dcat
   */
  public Map<String, Object> getDcat() {
    return dcat;
  }

  /**
   * Sets the dcat.
   *
   * @param dcat the dcat
   */
  public void setDcat(Map<String, Object> dcat) {
    this.dcat = dcat;
  }

  /**
   * Gets the inspire.
   *
   * @return the inspire
   */
  public Map<String, Object> getInspire() {
    return inspire;
  }

  /**
   * Sets the inspire.
   *
   * @param inspire the inspire
   */
  public void setInspire(Map<String, Object> inspire) {
    this.inspire = inspire;
  }

  /**
   * Gets the custom.
   *
   * @return the custom
   */
  public Map<String, Object> getCustom() {
    return custom;
  }

  /**
   * Sets the custom.
   *
   * @param custom the custom
   */
  public void setCustom(Map<String, Object> custom) {
    this.custom = custom;
  }

}
