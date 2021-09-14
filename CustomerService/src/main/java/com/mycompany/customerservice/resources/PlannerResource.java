/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import entities.Korisnik;
import entities.Obaveza;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import plannerdata.TaskData;

/**
 *
 * @author pc
 */
@Path("planner")
public class PlannerResource {

    @Resource(lookup = "shConnectionFactory")
    private ConnectionFactory cf;
    
    @Resource(lookup = "plannerSendTopic")
    private Topic plannerSendTopic;
    
    @Resource(lookup = "plannerReceiveTopic")
    private Topic plannerReceiveTopic;
    
    @POST
    @Path("{date}/{time}/{dur}")
    public Response createTask(@PathParam("date") String date, @PathParam("time") String time,
                               @PathParam("dur") int dur, @QueryParam("dest") String dest,
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
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(plannerReceiveTopic, "type='createRecieve'", false);
            
            String[] d = date.split("-");
            String[] t = time.split(":");
            int year = Integer.parseInt(d[0]);
            int month = Integer.parseInt(d[1]);
            int day = Integer.parseInt(d[2]);
            int hours = Integer.parseInt(t[0]);
            int mins = Integer.parseInt(t[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hours, mins);
            Date dt = cal.getTime();
            
            TaskData task = new TaskData();
            task.setDur(dur);
            task.setUserId(user.getIdK());
            task.setDate(dt);
            if (dest != null) task.setDest(dest);
            
            ObjectMessage msg = context.createObjectMessage(task);
            msg.setStringProperty("type", "createSend");
            
            producer.send(plannerSendTopic, msg);
            
            TextMessage rpl = (TextMessage) consumer.receive();
            
            if (rpl.getText().equals("1")) {
                return Response.status(Response.Status.OK).entity("Uspesno").build();
            }
            else {
                if (rpl.getText().equals("2"))
                    return Response.status(Response.Status.CONFLICT).entity("Nemoguce obaviti obavezu").build();
                else return Response.status(Response.Status.CONFLICT).entity("Pogresan unos").build();
            }
            
        } catch (JMSException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DELETE
    @Path("{date}/{time}")
    public Response deleteTask(@PathParam("date") String date, @PathParam("time") String time,
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
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(plannerReceiveTopic, "type='deleteRecieve'", false);
            
            String[] d = date.split("-");
            String[] t = time.split(":");
            int year = Integer.parseInt(d[0]);
            int month = Integer.parseInt(d[1]);
            int day = Integer.parseInt(d[2]);
            int hours = Integer.parseInt(t[0]);
            int mins = Integer.parseInt(t[1]);
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hours, mins);
            Date dt = cal.getTime();
            
            TaskData task = new TaskData();
            task.setUserId(user.getIdK());
            task.setDate(dt);
            
            ObjectMessage msg = context.createObjectMessage(task);
            msg.setStringProperty("type", "deleteSend");
            
            producer.send(plannerSendTopic, msg);
            
            TextMessage rpl = (TextMessage) consumer.receive();
            
            if (rpl.getText().equals("1")) {
                return Response.status(Response.Status.OK).entity("Uspesno obrisano").build();
            }
            else return Response.status(Response.Status.OK).entity("Neuspesno brisanje").build();
        } catch (JMSException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    
    @GET
    @Path("list")
    public Response getTasks(@Context HttpHeaders httpHeaders) {
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
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(plannerReceiveTopic, "type='getRecieve'", false);
            
            TextMessage msg = context.createTextMessage(user.getIdK() + "");
            msg.setStringProperty("type", "getSend");
            producer.send(plannerSendTopic, msg);
            
            ObjectMessage rpl = (ObjectMessage) consumer.receive();
            ArrayList<Obaveza> tasks = (ArrayList<Obaveza>) rpl.getObject();
            
            return Response.status(Response.Status.OK).entity(new GenericEntity<List<Obaveza>>(tasks){}).build();
                       
        } catch (JMSException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @GET
    @Path("setalarm/{idO}")
    public Response setTaskAlarm(@PathParam("idO") int idO, @Context HttpHeaders httpHeaders) {
        
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
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(plannerReceiveTopic, "type='alarmRecieve'", false);
            
            TextMessage msg = context.createTextMessage(user.getIdK() + "");
            msg.setStringProperty("type", "alarmSend");
            producer.send(plannerSendTopic, msg);
            
            TextMessage rpl = (TextMessage) consumer.receive();
            
            if (rpl.getText().equals("1")) {
                return Response.status(Response.Status.OK).entity("Postavljen").build();
            }
            else return Response.status(Response.Status.CONFLICT).entity("Greska").build();
            
        } catch (JMSException ex) {
            Logger.getLogger(PlannerResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }
    
}