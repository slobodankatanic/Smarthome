/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import entities.Korisnik;
import entities.Pesma;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("music")
public class MusicResource {
    
    @Resource(lookup = "shConnectionFactory")
    private ConnectionFactory cf;
    
    @Resource(lookup = "musicQueue")
    private Queue musicQueue;
    
    @Resource(lookup = "musicListQueue")
    private Queue musicListQueue;
    
    @GET
    @Path("play/{idS}")
    public Response playSong(@PathParam("idS") int idS, @Context HttpHeaders httpHeaders) {
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
            return Response.status(Response.Status.CONFLICT).entity("Nisu uneti ispravni kredencijali.").build();
        }                
        
        JMSContext context = cf.createContext();        
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(musicQueue, "type='playRecieve'", false);
        
        try {            
            TextMessage msg = context.createTextMessage(idS + ":" + user.getIdK());
            msg.setStringProperty("type", "playSend");
            producer.send(musicQueue, msg);
            
            TextMessage rpl = (TextMessage) consumer.receive();
            String s = rpl.getText();
            
            if (s.equals("1")) {
                return Response.status(Response.Status.OK).entity("Pesma se pusta...").build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        } catch (JMSException ex) {
            Logger.getLogger(MusicResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GET
    @Path("list")
    public Response getSongList(@Context HttpHeaders httpHeaders) {
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
        JMSConsumer consumer = context.createConsumer(musicListQueue, "type='listRecieve'", false);
        
        TextMessage msg = context.createTextMessage(user.getIdK() + "");
        try {
            msg.setStringProperty("type", "listSend");
        } catch (JMSException ex) {
            //Logger.getLogger(MusicResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        producer.send(musicListQueue, msg);

        ObjectMessage rpl = (ObjectMessage) consumer.receive();
        List<Pesma> songs = null;
        try {
            songs = (List<Pesma>) rpl.getObject();
            for (Pesma p : songs) System.out.println(p.getNaziv());
        } catch (JMSException ex) {
            //Logger.getLogger(MusicResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        //List<String> l = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (Pesma p : songs) sb.append("\n" + p.getNaziv());
        //return Response.status(Response.Status.OK).entity(new GenericEntity<List<String>>(l){}).build();
        return Response.status(Response.Status.OK).entity(sb.toString()).build();
    }
    
    public static void main(String[] args) {
        
    }
}
