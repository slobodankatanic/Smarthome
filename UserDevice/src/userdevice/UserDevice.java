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
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
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
        
        boolean done = false;
        
        String username = null;
        String password = null;
        
        while (!done) {
            System.out.print("username: ");
            username = in.nextLine();
            System.out.print("password: ");
            password = in.nextLine();
                                    
            Client clientAuth = ClientBuilder.newClient();
            WebTarget target = clientAuth.target("http://localhost:8080/CustomerService/smarthome/login");
            Builder request = target.request();  
            
            Form form = new Form();
            form.param("username", username);
            form.param("password", password);
            Response responseAuth = request.post(Entity.form(form), Response.class);
            
            if (responseAuth.getStatus() == 200) {
                done = true;
            } else {
                System.out.println("Pogresni kredencijali.");
            }
        }                        
        
        String usernameAndPassword = username + ":" + password;
        String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
        
        System.out.println("\nIzaberite opciju:\n\nMuzicki plejer:\n1. Pusti pesmu\n2. Dohvati odslusane pesme\n");
        System.out.println("Alarm:\n3. Navij obican alarm\n4. Navij periodican alarm\n5. Navij alarm u predefinisanom trenutku\n6. Postavi zvono alarmu\n");
        System.out.println("Planer:\n7. Napravi obavezu\n8. Izlistaj obaveze\n9. Izmeni obavezu\n10. Obrisi obavezu\n11. Postavi podsetnik\n12. Dohvati rastojanje izmedju dve lokacije\n13. Dohvati rastojanje izmedju od trenutne lokacije\n");
        
        String[] predefinedAlarms = { "00:00:00", "00:45:00", "01:30:00", "02:15:00", "03:00:00", "03:45:00",
                                      "04:30:00:00", "05:15:00", "06:00:00", "06:45:00", "07:30:00", "08:15:00", 
                                      "09:00:00", "09:45:00", "10:30:00", "11:15:00", "12:00:00", "12:45:00", 
                                      "13:30:00", "14:15:00", "15:00:00", "15:45:00", "16:30:00", "17:15:00", 
                                      "18:00:00", "18:45:00", "19:30:00", "20:15:00", "21:00:00", "21:45:00", 
                                      "22:30:00", "23:15:00" };
        
        while(true) {
            int num = in.nextInt();
            Response response = null;
            String responseObject = null;
            String date = null;
            String time = null;
            int hours, minutes, seconds;
            Client client = ClientBuilder.newClient();
            switch (num) {
                case 1: 
                    // prikazi svee pesme, preko servisa zatrazi listu pesama
                    System.out.print("Unesite identifikator pesme: ");
                    
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
                    Scanner alarmIn = new Scanner(System.in);
                    
                    System.out.print("Unesite datum alarma u formatu godina-mesec-dan : ");
                    date = alarmIn.nextLine();
                    System.out.print("Unesite vreme alarma u formatu sati:minuti:sekunde : ");
                    time = alarmIn.nextLine();                    
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")
                    .path("set/{time}")
                    .queryParam("date", date)                    
                    .resolveTemplate("time", time)                    
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println(responseObject);
                    
                    break;
                case 4:                    
                    Scanner alarmScanner = new Scanner(System.in);
                    
                    System.out.print("Unesite trajanje periode u formatu sati:minuti:sekunde : ");
                    time = alarmScanner.nextLine();                                        
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")
                    .path("set/{time}")
                    .queryParam("p", 1)
                    .resolveTemplate("time", time)
                    .request()
                    .header("Authorization", authorizationHeaderValue)
                    .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 5:                    
                    System.out.println("Izaberite jedan od ponudjenih trenutaka:");
                    
                    int number = 0;
                    
                    StringBuilder sb1 = new StringBuilder();                    
                    for (String alarm : predefinedAlarms) {
                        sb1.append(number + ". " + alarm + "\n");
                        number++;
                    }                                        
                    
                    System.out.println(sb1.toString());
                    
                    number = in.nextInt();
                    
                    String alarm = predefinedAlarms[number - 1];                                        
                    
                    responseObject = new String();
                    
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/alarms/")                    
                    .path("set/{time}")                    
                    .resolveTemplate("time", alarm)                    
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
                    
                    System.out.print("Unesite datum pocetka obaveze u formatu godina-mesec-dan : ");
                    date = inTask.nextLine();
                    
                    System.out.print("Unesite vreme pocetka obaveze u formatu sati:minuti : ");
                    time = inTask.nextLine();
                    
                    System.out.print("Unesite trajanje obaveze u formatu sati:minuti : ");
                    String duration = inTask.nextLine();
                    
                    System.out.print("Unesite destinaciju obaveze (prazan unos ukoliko se obavlja kod Vase kuce): ");
                    String destination = inTask.nextLine();
                    
                    responseObject = new String();
                    
                    if (destination.length() > 0) {
                        response  = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")                        
                        .path("{date}/{time}/{dur}")
                        .queryParam("dest", destination)
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
                case 9:
                    response = client
                    .target("http://localhost:8080/CustomerService/smarthome/planner/list").request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();
                    
                    String[] tasksDataList = response.readEntity(String.class).split(",");
                    
                    StringBuilder sbList = new StringBuilder();
                    for (String task : tasksDataList) {
                        sbList.append("\n" + task);
                    }
                    
                    System.out.println("\nVase obaveze:\n" + sbList.toString() + "\n");
                    
                    Scanner inEdit = new Scanner(System.in);                                        
                    
                    System.out.print("Unesite identifikator obaveze: ");
                    int idO = in.nextInt();
                    
                    System.out.print("\nUnesite novi datum u formatu godina-mesec-dan (prazan unos za zadrzavanje stare vrednosti): ");
                    String newDate = inEdit.nextLine();
                    
                    System.out.print("Unesite novo vreme u formatu sati:minuti (prazan unos za zadrzavanje stare vrednosti): ");
                    String newTime = inEdit.nextLine();
                    
                    System.out.print("Unesite novo trajanje u formatu sati:minuti (prazan unos za zadrzavanje stare vrednosti): ");
                    String newDuration = inEdit.nextLine();
                    
                    System.out.print("Unesite novu destinaciju (prazan unos za zadrzavanje stare vrednosti): ");
                    String newDestination = inEdit.nextLine();
                                        
                    WebTarget webTarget = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")                        
                        .path("edit/{idO}")                        
                        .resolveTemplate("idO", idO);
                        //.request()
                        //.header("Authorization", authorizationHeaderValue)
                        //.post(Entity.text(responseObject));                                        
                    
                    if (newDate.length() > 0) {
                        webTarget = webTarget.queryParam("date", newDate);
                    }
                    
                    if (newTime.length() > 0) {
                        webTarget = webTarget.queryParam("time", newTime);
                    }
                    
                    if (newDuration.length() > 0) {
                        webTarget = webTarget.queryParam("duration", newDuration);
                    }
                    
                    if (newDestination.length() > 0) {
                        webTarget = webTarget.queryParam("destination", newDestination);
                    }
                    
                    responseObject = new String();
                    
                    response = webTarget.request()
                        .header("Authorization", authorizationHeaderValue)
                        .post(Entity.text(responseObject));                                        
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
              
                    break;
                case 10:
                    response = client
                    .target("http://localhost:8080/CustomerService/smarthome/planner/list").request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();
                    
                    tasksData = response.readEntity(String.class).split(",");
                    
                    sb = new StringBuilder();
                    for (String task : tasksData) {
                        sb.append("\n" + task);
                    }
                    
                    System.out.println("\nVase obaveze:\n" + sb.toString() + "\n");
                    
                    inEdit = new Scanner(System.in);                                        
                    
                    System.out.print("Unesite identifikator obaveze: ");
                    idO = inEdit.nextInt();
                    
                    responseObject = new String();
                    
                    response = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")                        
                        .path("{idO}")                   
                        .resolveTemplate("idO", idO)
                        .request()
                        .header("Authorization", authorizationHeaderValue)
                        .delete();
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 11:
                    response = client
                    .target("http://localhost:8080/CustomerService/smarthome/planner/list").request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();
                    
                    tasksData = response.readEntity(String.class).split(",");
                    
                    sb = new StringBuilder();
                    for (String task : tasksData) {
                        sb.append("\n" + task);
                    }
                    
                    System.out.println("\nVase obaveze:\n" + sb.toString() + "\n");
                    
                    inEdit = new Scanner(System.in);                                        
                    
                    System.out.print("Unesite identifikator obaveze: ");
                    idO = inEdit.nextInt();
                    
                    responseObject = new String();
                    
                    response = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")                        
                        .path("setalarm/{idO}")                   
                        .resolveTemplate("idO", idO)
                        .request()
                        .header("Authorization", authorizationHeaderValue)
                        .post(Entity.text(responseObject));
                    
                    responseObject = response.readEntity(String.class);
                    System.out.println("\n" + responseObject);
                    
                    break;
                case 12:
                    Scanner inDest = new Scanner(System.in);
                    
                    System.out.print("Unesite naziv prve lokacije: ");
                    String location1 = inDest.nextLine();
                    
                    System.out.print("Unesite naziv druge lokacije: ");
                    String location2 = inDest.nextLine();
                    
                    response = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")                    
                        .path("distance/{location1}/{location2}")
                        .resolveTemplate("location1", location1)
                        .resolveTemplate("location2", location2)
                        .request()                    
                        .header("Authorization", authorizationHeaderValue)
                        .get();                                        
                    
                    System.out.println(response.readEntity(String.class));                                                            
                    
                    break;
                case 13:
                    Scanner inLoc = new Scanner(System.in);
                    
                    System.out.print("Unesite naziv lokacije: ");
                    String location = inLoc.nextLine();                                        
                    
                    response = client
                        .target("http://localhost:8080/CustomerService/smarthome/planner/")                    
                        .path("distanceTask/{location}")
                        .resolveTemplate("location", location)                  
                        .request()                    
                        .header("Authorization", authorizationHeaderValue)
                        .get();                                        
                    
                    System.out.println(response.readEntity(String.class));                                                            
                    
                    break;
            }
        }
    }
    
}
