package it.eng.idra.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializer;

import it.eng.idra.beans.webscraper.WebScraperSitemap;

/**
 * Flexible deserializer for WebScraperSitemap that accepts either a JSON object
 * or a JSON string. If a string is provided, it will first attempt to parse the
 * string as a JSON object; if parsing fails, it will treat the string as a
 * startUrl-only sitemap.
 */
public class WebScraperSitemapDeserializer implements JsonDeserializer<WebScraperSitemap> {

  @Override
  public WebScraperSitemap deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {

    // Case 1: Proper JSON object
    if (json.isJsonObject()) {
      return context.deserialize(json.getAsJsonObject(), WebScraperSitemap.class);
    }

    // Case 2: Primitive string that may be JSON or a plain URL/id
    if (json.isJsonPrimitive()) {
      JsonPrimitive prim = json.getAsJsonPrimitive();
      if (prim.isString()) {
        String value = prim.getAsString();
        // Try to interpret the string value as a JSON structure
        try {
          JsonElement inner = JsonParser.parseString(value);
          if (inner != null && inner.isJsonObject()) {
            return context.deserialize(inner, WebScraperSitemap.class);
          }
        } catch (Exception ignore) {
          // fall through to startUrl-only sitemap
        }

        // Fallback: treat the string as startUrl
        WebScraperSitemap sitemap = new WebScraperSitemap();
        sitemap.setStartUrl(value);
        return sitemap;
      }
    }

    throw new JsonParseException("Invalid sitemap format: expected object or string");
  }
}
