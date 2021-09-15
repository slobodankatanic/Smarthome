/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import alarmdata.AlarmData;
import entities.Korisnik;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("alarms")
public class AlarmResource {
    
    @Resource(lookup = "shConnectionFactory")
    private ConnectionFactory cf;
    
    @Resource(lookup = "alarmSendTopic")
    private Topic sendTopic;
    
    @Resource(lookup = "alarmRecieveTopic")
    private Topic recieveTopic;
    
    @POST
    @Path("set/{hours}/{mins}/{secs}")
    public Response setAlarm(@PathParam("hours") int hours, @PathParam("mins") int mins, @PathParam("secs") int secs,
                                @QueryParam("p") Integer p, @Context HttpHeaders httpHeaders) {
        
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        String username = null;
        String password = null;
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            username = stringTokenizer.nextToken();
            password = stringTokenizer.nextToken();
        }
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my_persistence_unit");
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Korisnik> q = 
                em.createQuery("select kor from Korisnik kor where kor.password = :password and kor.username = :username", Korisnik.class);
        q.setParameter("password", password);
        q.setParameter("username", username);
        
        Korisnik user = q.getSingleResult();
        
        em.close();
        emf.close();
        
        if (user == null) {
            return Response.status(Response.Status.CONFLICT).entity("Greska").build();
        }
        
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(recieveTopic, "type='set'");
        JMSProducer producer = context.createProducer();
        
        AlarmData ad = new AlarmData();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, mins);
        cal.set(Calendar.SECOND, secs);
        cal.set(Calendar.MILLISECOND, 0);
        Date d = cal.getTime();
        
        ad.addTime(d);
        ad.setUserId(user.getIdK());
        if (p != null && p == 1) {
            ad.setType("per");
            ad.setPeriod(hours * 60 * 60 + mins * 60 + secs);
        }
        else {
            ad.setType("set");
        }
        
        ObjectMessage msg = context.createObjectMessage(ad);
        try {
            msg.setStringProperty("type", "set");
            
        } catch (JMSException ex) {
            Logger.getLogger(AlarmResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
        producer.send(sendTopic, msg);
        System.out.println("Poslano");
        return Response.status(Response.Status.CREATED).entity("Uspesno kreiran alarm").build();
    }
    
       
    @POST
    @Path("{idA}/{idS}")
    public Response setRingtone(@PathParam("idA") int idA, @PathParam("idS") int idS,
                                @Context HttpHeaders httpHeaders) {
        
        try {
            List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
            String username = null;
            String password = null;
            if(authHeaderValues != null && authHeaderValues.size() > 0){
                String authHeaderValue = authHeaderValues.get(0);
                String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
                StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
                username = stringTokenizer.nextToken();
                password = stringTokenizer.nextToken();
            }
            
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("my_persistence_unit");
            EntityManager em = emf.createEntityManager();
            
            TypedQuery<Korisnik> q =
                    em.createQuery("select kor from Korisnik kor where kor.password = :password and kor.username = :username", Korisnik.class);
            q.setParameter("password", password);
            q.setParameter("username", username);
            
            Korisnik user = q.getSingleResult();
            
            em.close();
            emf.close();
            
            if (user == null) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            
            JMSContext context = cf.createContext();
            JMSConsumer consumer = context.createConsumer(recieveTopic, "type='setSong'");
            JMSProducer producer = context.createProducer();
            
            TextMessage msg = context.createTextMessage();
            msg.setText(idA + ":" + idS + ":" + user.getIdK());
            msg.setStringProperty("type", "setSong");
            
            producer.send(sendTopic, msg);
            
            TextMessage rpl = (TextMessage) consumer.receive();
            
            if (rpl.getText().equals("1")) {
                return Response.status(Response.Status.OK).entity("Postavljeno zvono").build();
            }
            else {
                return Response.status(Response.Status.CONFLICT).entity("Pogresan alarm ili pesma").build();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(AlarmResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska na serveru").build();
        }
        
    }
    
}
