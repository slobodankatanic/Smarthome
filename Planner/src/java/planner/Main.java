/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 *
 * @author pc
 */
public class Main {
    @Resource(lookup = "shConnectionFactory")
    static ConnectionFactory cf;
    
    @Resource(lookup = "plannerSendTopic")
    static Topic plannerSendTopic;
    
    @Resource(lookup = "plannerReceiveTopic")
    static Topic plannerReceiveTopic;
    
    public static void main(String[] args) {        
        new TaskManipulator().start();
        new TimeMeasurer().start();                       
    }
    
}
