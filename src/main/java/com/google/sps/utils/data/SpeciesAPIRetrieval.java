package com.google.sps.utils.data;

import com.google.gson.Gson;
import com.google.sps.utils.data.Species;
import com.google.sps.utils.data.Taxonomy;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;

public class SpeciesAPIRetrieval {

  // Retrieves JSON response from given url with the species parameter
  public static String getJSON(String base, String species) throws IOException, InterruptedException {
    String uri = base + encode(species);
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(uri))
      .build();
    
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  // Encode a string value using `UTF-8` encoding scheme
  private static String encode(String url) {
    try {
      return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  /**
   * Will be used to convert API JSON to a Map for easier access
   * @return converted map of JSON, or null if incorrect JSON format
   */
  public static Map convertJSONToMap(String jsonString) {
    Gson gson = new Gson();
    Map map = gson.fromJson(jsonString, Map.class);

    try {
      if (map.get("matchType").equals("EXACT")) {
        return map;
      }
    } catch (NullPointerException e) {
      return null;
    }

    return null;
  }

  /**
   * Updates Species with fields from converted JSON map
   * @param species: species to add fields to
   * @param apiMap: map of the JSON returned from API call to this species
   */
  public static void addAPISpeciesInfo(Species species, Map apiMap) {
    if (apiMap == null) {
      System.out.println("No results found in GBIF API for '" + species.getBinomialName() + "'.");
      return;
    }

    // Update DataCollection.speciesMap with values from apiMap
    String kingdom = apiMap.get("kingdom").toString();
    String phylum = apiMap.get("phylum").toString();
    String class_t = apiMap.get("class").toString();
    String order = apiMap.get("order").toString();
    String family = apiMap.get("family").toString();
    String genus = apiMap.get("genus").toString();
    Taxonomy taxonomy = new Taxonomy(kingdom, phylum, class_t, order, family, genus);
    
    if (species != null) {
      species.setTaxonomy(taxonomy);
    }
    return;
  }  
}