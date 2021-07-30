package it.eng.idra.beans.opendatasoft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class InnerDatasetFieldDeserializer.
 */
public class InnerDatasetFieldDeserializer implements JsonDeserializer<InnerDatasetField> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
   * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
   */
  @Override
  public InnerDatasetField deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
      throws JsonParseException {
    // TODO Auto-generated method stub
    JSONObject j = new JSONObject(arg0.toString());
    String label = j.optString("label", "");
    String type = j.optString("type", "");
    String name = j.optString("name", "");
    String description = "";
    try {
      description = j.getString("description");
    } catch (Exception e) {
      JSONObject desc = j.optJSONObject("description");
      if (desc != null) {
        if (desc.has("en")) {
          description = desc.optString("en");
        }
      }
    }

    InnerDatasetFieldAnnotations annotations = arg2
        .deserialize(arg0.getAsJsonObject().get("annotations"), InnerDatasetFieldAnnotations.class);

    return new InnerDatasetField(label, type, annotations, name, description);
  }

}
