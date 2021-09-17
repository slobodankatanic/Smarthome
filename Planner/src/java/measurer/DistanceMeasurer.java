/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package measurer;

import entities.Korisnik;
import entities.Obaveza;
import static java.lang.Math.toRadians;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pc
 */
public class DistanceMeasurer {    
            
    public static long distance(String location1, String location2) {
        try {
            System.out.println(location1 + ", " + location2);
            
            Client client = ClientBuilder.newClient();
            
            String jsonLocation1 = client
                    .target("https://geocode.search.hereapi.com/v1/geocode")
                    .queryParam("q", location1)
                    .queryParam("limit", 1)
                    .queryParam("apiKey", "DSitrL2u9LkOmaI7v2mzO9KYJlX08lcAwPFqCQD13YE")
                    .request()
                    .get(String.class);
            
            String jsonLocation2 = client
                    .target("https://geocode.search.hereapi.com/v1/geocode")
                    .queryParam("q", location2)
                    .queryParam("limit", 1)
                    .queryParam("apiKey", "DSitrL2u9LkOmaI7v2mzO9KYJlX08lcAwPFqCQD13YE")
                    .request()
                    .get(String.class);
            
            System.out.println(jsonLocation1);
            System.out.println(jsonLocation2);
            
            //if (responseLocation1 == null || responseLocation2 == null) {
                //return -1;
            //}
            
            // String jsonLocation1 = responseLocation1.readEntity(String.class);
            // String jsonLocation2 = responseLocation1.readEntity(String.class);
            
            if (jsonLocation1 == null || jsonLocation1.length() == 0 ||
                jsonLocation2 == null || jsonLocation2.length() == 0) {
                return -1;
            }
            
            JSONParser parser = new JSONParser();
            
            JSONObject jsonObject1 = (JSONObject) parser.parse(jsonLocation1);
            JSONObject jsonObject2 = (JSONObject) parser.parse(jsonLocation2);
            
            if (jsonObject1 == null || jsonObject2 == null) {
                return -1;
            }
            
            if (!jsonObject1.containsKey("items") || !jsonObject2.containsKey("items")) {
                return -1;
            }
            
            JSONArray items1 = (JSONArray) jsonObject1.get("items");
            JSONArray items2 = (JSONArray) jsonObject2.get("items");
            
            if (items1.size() == 0 || items1.size() == 0) {
                return -1;
            }
            
            JSONObject itemsObject1 = (JSONObject) items1.get(0);
            JSONObject itemsObject2 = (JSONObject) items2.get(0);
            
            if (!itemsObject1.containsKey("position") || !itemsObject2.containsKey("position")) {
                return -1;
            }
            
            JSONObject position1 = (JSONObject) itemsObject1.get("position");
            JSONObject position2 = (JSONObject) itemsObject2.get("position");
            
            if (!position1.containsKey("lng") || !position1.containsKey("lat") ||
                !position2.containsKey("lng") || !position2.containsKey("lat")) {
                return -1;
            }
            
            double lat1 = (double) position1.get("lat");
            double lng1 = (double) position1.get("lng");            
            
            double lat2 = (double) position2.get("lat");
            double lng2 = (double) position2.get("lng");                                                                                    
            
            Response responseDuration = client
                .target("https://router.hereapi.com/v8/routes")
                .queryParam("origin", lat1 + "," + lng1)
                .queryParam("destination", lat2 + "," + lng2)
                .queryParam("return", "summary,typicalDuration")
                .queryParam("transportMode", "car")                    
                .queryParam("apiKey", "DSitrL2u9LkOmaI7v2mzO9KYJlX08lcAwPFqCQD13YE")
                .request()
                .get();                                
            
            if (responseDuration == null) {
                return -1;
            }
            
            String durationJsonString = responseDuration.readEntity(String.class);
            
            System.out.println(durationJsonString);
            
            if (durationJsonString == null || durationJsonString.length() == 0) {
                return -1;
            }                        
            
            JSONObject durationJsonObject = (JSONObject) parser.parse(durationJsonString);
            
            if (durationJsonObject == null || !durationJsonObject.containsKey("routes")) {
                return -1;
            }
            
            JSONArray routesArray = (JSONArray) durationJsonObject.get("routes");
            
            if (routesArray.size() == 0) {
                return -1;
            }
            
            JSONObject routesObject = (JSONObject) routesArray.get(0);
            
            if (!routesObject.containsKey("sections")) {
                return -1;
            }
            
            JSONArray sectionsArray = (JSONArray) routesObject.get("sections");
            
            if (sectionsArray.size() == 0) {
                return -1;
            }
            
            JSONObject sectionsObject = (JSONObject) sectionsArray.get(0);
            
            if (!sectionsObject.containsKey("summary")) {
                return -1;
            }
            
            JSONObject summaryObject = (JSONObject) sectionsObject.get("summary");
            
            if (!summaryObject.containsKey("typicalDuration")) {
                return -1;
            }
                                    
            return (long) summaryObject.get("typicalDuration");
            
        } catch (ParseException ex) {
            Logger.getLogger(DistanceMeasurer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
}
