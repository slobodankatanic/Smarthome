/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userdevice;

import java.util.List;
import java.util.Scanner;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        
        System.out.println("Izaberite opciju:\nMuzicki plejer:\n1.Pusti pesmu\n2. Dohvati odslusane pesme\n");
        System.out.println("Alarm:\n3. Navij obican alarm\n3. Navij periodican alarm\n6. Izaberi vreme alarma\n7. Postavi zvono alarmu\n");
        System.out.println("Planer:\n8. Napravi obavezu\n9. Izlistaj obaveze\n10. Postavi podsetnik\n11. Obrisi obavezu");
        
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
                    System.out.println(s);
                    
                    break;

                case 2:
                    response  = client
                    .target("http://localhost:8080/CustomerService/smarthome/music/list").request()
                    .header("Authorization", authorizationHeaderValue)
                    .get();
                    System.out.println(response.readEntity(String.class));
                    
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
                    System.out.println(responseObject);
                    
                    break;
            }
        }
    }
    
}
