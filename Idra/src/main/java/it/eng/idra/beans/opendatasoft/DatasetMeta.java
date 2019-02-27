package it.eng.idra.beans.opendatasoft;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class DatasetMeta {
	
	@SerializedName("default")
	private InnerDatasetMetaDefault _default;
	private Map<String, Object> dcat;
	private Map<String, Object> inspire;
	private Map<String, Object> custom;
	
	public InnerDatasetMetaDefault get_default() {
		return _default;
	}
	public void set_default(InnerDatasetMetaDefault _default) {
		this._default = _default;
	}
	public Map<String, Object> getDcat() {
		return dcat;
	}
	public void setDcat(Map<String, Object> dcat) {
		this.dcat = dcat;
	}
	public Map<String, Object> getInspire() {
		return inspire;
	}
	public void setInspire(Map<String, Object> inspire) {
		this.inspire = inspire;
	}
	public Map<String, Object> getCustom() {
		return custom;
	}
	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
	}

}
