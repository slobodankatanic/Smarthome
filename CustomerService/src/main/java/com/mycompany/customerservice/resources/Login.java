/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.customerservice.resources;

import entities.Korisnik;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author pc
 */

@Path("login")
public class Login {    
    
    @POST
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my_persistence_unit");
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Korisnik> query = em.createQuery("SELECT k FROM Korisnik k WHERE k.username = :username and k.password = :password", Korisnik.class);
        query.setParameter("username", username);
        query.setParameter("password", password); 
        
        List<Korisnik> users = query.getResultList();
        
        if (users.size() == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
