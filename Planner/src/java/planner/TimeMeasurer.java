/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import measurer.DistanceMeasurer;
import static planner.Main.cf;
import static planner.Main.plannerReceiveTopic;
import static planner.Main.plannerSendTopic;

/**
 *
 * @author pc
 */
public class TimeMeasurer extends Thread {
    
    @Override
    public void run() {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(plannerSendTopic, "type='distanceSend' or type='distaneTaskSend'", false);
        JMSProducer producer = context.createProducer();
        
        Client client = ClientBuilder.newClient();
        
        while (true) {
            try {
                TextMessage message = (TextMessage) consumer.receive();
                
                if (message.getStringProperty("type").equals("distanceSend")) {                    
                    
                    String[] locations = (message.getText()).split(",");
                    System.out.println(locations[0] + "-" + locations[1]);
                    long distance = DistanceMeasurer.distance(locations[0], locations[1]);                                                                                                                                    
                    
                    TextMessage reply = context.createTextMessage();                    
                    reply.setStringProperty("type", "distanceReceive");                    
                    
                    if (distance < 0) {
                        reply.setIntProperty("status", 0);
                        reply.setText("Greska");
                    } else {
                        reply.setIntProperty("status", 1);
                        
                        long h = distance / 3600;
                        distance -= h * 3600; 
                        long m = distance / 60; // 12 32 20 45140
                        distance -= m * 60;
                        long s = distance;
                        reply.setText(h + "h " + m + "min " + s + "s");
                    }
                    
                    producer.send(plannerReceiveTopic, reply);
                } else {
                    // nadji trenutnu ili preth obavezu usera i vrati
                    String taskLocation = "";
                    String location = message.getText();                    
                    
                    long distance = DistanceMeasurer.distance(taskLocation, location);
                    
                    TextMessage reply = context.createTextMessage();                    
                    reply.setStringProperty("type", "distanceTaskReceive");
                    
                    if (distance < 0) {
                        reply.setIntProperty("status", 0);
                        reply.setText("Greska");
                    } else {
                        reply.setIntProperty("status", 1);
                        
                        long h = distance / 3600;
                        distance -= h * 3600; 
                        long m = distance / 60; // 12 32 20 45140
                        distance -= m * 60;
                        long s = distance;
                        reply.setText(h + "h " + m + "min " + s + "s");
                    }
                    
                    producer.send(plannerReceiveTopic, reply);
                }
            } catch (JMSException ex) {
                Logger.getLogger(TimeMeasurer.class.getName()).log(Level.SEVERE, null, ex);                
            }
        }
    }
    
}
