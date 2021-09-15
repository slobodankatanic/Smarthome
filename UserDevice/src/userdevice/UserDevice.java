/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userdevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.stream.JsonParser;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pc
 */
public class UserDevice {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Client client = ClientBuilder.newClient();
        //HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("username", "password");
        //Client client = ClientBuilder.newClient();
        //client.target("").request().post(Entity.);
        System.out.print("username: ");
        String username = in.nextLine();
        System.out.print("password: ");
        String password = in.nextLine();
        
        String usernameAndPassword = username + ":" + password;
        String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
        
        System.out.println("Izaberite opciju:\n\nMuzicki plejer:\n1. Pusti pesmu\n2. Dohvati odslusane pesme\n");
        System.out.println("Alarm:\n3. Navij obican alarm\n4. Navij periodican alarm\n5. Navij alarm u predefinisanom trenutku\n6. Postavi zvono alarmu\n");
        System.out.println("Planer:\n7. Napravi obavezu\n8. Izlistaj obaveze\n10. Postavi podsetnik\n11. Obrisi obavezu\n12. Dohvati rastojanje izmedju dve lokacije");
        
        String[] predefinedAlarms = { "00:00", "00:45", "01:30", "02:15", "03:00", "03:45",
                                      "04:30", "05:15", "06:00", "06:45", "07:30", "08:15", 
                                      "09:00", "09:45", "10:30", "11:15", "12:00", "12:45", 
                                      "13:30", "14:15", "15:00", "15:45", "16:30", "17:15", 
                                      "18:00", "18:45", "19:30", "20:15", "21:00", "21:45", 
                                      "22:30", "23:15" };
        
        while(true) {
            int num = in.nextInt();
            Response response = null;
            String responseObject = null;
            int hours, minutes, seconds;
            switch (num) {
                case 1: 
                    // prikazi svee pesme, preko servisa zatrazi listu pesama
                    System.out.print("Unesite id pesme: ");
                    
                    num = in.nextInt();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/music/")
                    .path("play/{idS}")
                    .resolveTemplate("idS", num)
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();
                    
                    String s = response.readEntity(String.class);
                    System.out.println("\n" + s);
                    
                    break;

                case 2:
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/music/list").request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();                                        
                    
                    System.out.println("\n" + response.readEntity(String.class));
                    
                    break;
                case 3:                    
                    System.out.print("Unesite sate: ");
                    hours = in.nextInt();
                    System.out.print("Unesite minute: ");
                    minutes = in.nextInt();
                    System.out.print("Unesite sekunde: ");
                    seconds = in.nextInt();
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")
                    .path("set/{hours}/{mins}/{secs}")
                    .resolveTemplate("hours", hours)
                    .resolveTemplate("mins", minutes)
                    .resolveTemplate("secs", seconds)                    
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println(responseObject);
                    
                    break;
                case 4:                    
                    System.out.print("Unesite sate za periodu: ");
                    hours = in.nextInt();
                    System.out.print("Unesite minute za periodu: ");
                    minutes = in.nextInt();
                    System.out.print("Unesite sekunde za periodu: ");
                    seconds = in.nextInt();
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")
                    .path("set/{hours}/{mins}/{secs}")
                    .queryParam("p", 1)
                    .resolveTemplate("hours", hours)
                    .resolveTemplate("mins", minutes)
                    .resolveTemplate("secs", seconds)                    
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 5:                    
                    System.out.println("Izaberite jedan od ponudjenih trenutaka:");
                    
                    int number = 1;
                    
                    StringBuilder sb1 = new StringBuilder();                    
                    for (String alarm : predefinedAlarms) {
                        sb1.append(number + ". " + alarm + "\n");
                        number++;
                    }                                        
                    
                    System.out.println(sb1.toString());
                    
                    number = in.nextInt();
                    
                    String[] alarm = predefinedAlarms[number - 1].split(":");
                    
                    hours = Integer.parseInt(alarm[0]); 
                    minutes = Integer.parseInt(alarm[1]); 
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")
                    .path("set/{hours}/{mins}/{secs}")                    
                    .resolveTemplate("hours", hours)
                    .resolveTemplate("mins", minutes)
                    .resolveTemplate("secs", 0)                    
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 6:
                    int alarmId;
                    
                    System.out.print("Izaberte alarm za koji menjate zvono: ");
                    alarmId = in.nextInt();
                    
                    int songId;
                    
                    System.out.print("Izaberte pesmu koju postavljate: ");
                    songId = in.nextInt();                                        
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")
                    .path("{idA}/{idS}")                    
                    .resolveTemplate("idA", alarmId)
                    .resolveTemplate("idS", songId)                    
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 7:
                    Scanner inTask = new Scanner(System.in);
                    
                    System.out.print("Unesite datum pocetka obaveze u formatu godina-mesec-dan: ");
                    String date = inTask.nextLine();
                    
                    System.out.print("Unesite vreme pocetka obaveze u formatu sati:minuti: ");
                    String time = inTask.nextLine();
                    
                    System.out.print("Unesite trajanje obaveze u formatu sati:minuti: ");
                    String duration = inTask.nextLine();
                    
                    System.out.print("Unesite destinaciju obaveze (prazan unos ukoliko se obavlja kod Vase kuce): ");
                    String destination = inTask.nextLine();
                    
                    responseObject = new String();
                    
                    if (destination.length() > 0) {
                        response  = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")
                        .queryParam("dest", destination)
                        .path("{date}/{time}/{dur}")
                        .resolveTemplate("date", date)
                        .resolveTemplate("time", time)
                        .resolveTemplate("dur", duration)
                        .request()
                        .header("Authorization", authorizationHeaderValue)
                        .post(Entity.text(responseObject));
                    } else {
                        response  = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")
                        .path("{date}/{time}/{dur}")
                        .resolveTemplate("date", date)
                        .resolveTemplate("time", time)                    
                        .resolveTemplate("dur", duration)
                        .request()
                        .header("Authorization", authorizationHeaderValue)
                        .post(Entity.text(responseObject));
                    }                                        
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 8:
                    response = client
                    .target("http://localhost:8080/CustomerService/smarthome/planner/list").request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();
                    
                    String[] tasksData = response.readEntity(String.class).split(",");
                    
                    StringBuilder sb = new StringBuilder();
                    for (String task : tasksData) {
                        sb.append("\n" + task);
                    }
                    
                    System.out.println("\nVase obaveze:\n" + sb.toString());
                    // System.out.println("\n" + response.readEntity(String.class));                    
                    
                    break;
                case 0:
                    response = client
                    .target("https://geocode.search.hereapi.com/v1/geocode")
                    // .queryParam("at", "44.805764622277486,20.476015871578806")
                    .queryParam("q", "Splav Tag Beograd")
                    .queryParam("limit", 1)
                    .queryParam("apiKey", "DSitrL2u9LkOmaI7v2mzO9KYJlX08lcAwPFqCQD13YE")
                    .request()                    
                    .get();
                    
                    String r = response.readEntity(String.class);
                    System.out.println(r);                                  
                    
                    try {
                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(r);
                        
                        JSONArray msg = (JSONArray) json.get("items");
                        JSONObject jo = (JSONObject) msg.get(0);
                        JSONObject pos = (JSONObject) jo.get("position");
                        
                        double lng = (double) pos.get("lng");
                        double lat = (double) pos.get("lat");
                        
                        System.out.println(lng + ", " + lat);
                    } catch (ParseException ex) {
                        Logger.getLogger(UserDevice.class.getName()).log(Level.SEVERE, null, ex);
                    }                                                                              
                    break;
                case 12:
                    Scanner inDest = new Scanner(System.in);
                    
                    System.out.println("Uesite naziv prve lokacije: ");
                    String location1 = inDest.nextLine();
                    
                    System.out.println("Uesite naziv druge lokacije: ");
                    String location2 = inDest.nextLine();
                    
                    response = client
                    .target("http://localhost:8080/CustomerService/smarthome/planner/")                    
                    .path("distance/{location1}/{location2}")
                    .resolveTemplate("location1", location1)
                    .resolveTemplate("location2", location2)
                    .request()                    
                    .get();
                    
                    System.out.println("Vreme potrebno da se stigne od jedne lokacije na drugu: " + 
                            response.readEntity(String.class));
                    
                    break;
            }
        }
    }
    
}
