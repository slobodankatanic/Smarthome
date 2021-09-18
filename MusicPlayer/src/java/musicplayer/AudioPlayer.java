/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import entities.Korisnik;
import entities.Pesma;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static musicplayer.Main.cf;
import static musicplayer.Main.musicQueue;

/**
 *
 * @author pc
 */
public class AudioPlayer extends Thread {
    
    public boolean playSong(int idS, int idK) {        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MusicPlayerPU");
        if (emf == null) {
            return false;
        }
        EntityManager em = emf.createEntityManager();    
        if (Desktop.isDesktopSupported()) {
            try {
                Pesma song = em.find(Pesma.class, idS);
                if (song == null) {
                    return false;
                }
                
                Desktop.getDesktop().browse(new URI(song.getUrl()));
                
                Korisnik user = em.find(Korisnik.class, idK);
                List<Pesma> songs = user.getPesmaList();
                boolean listened = false;
                if (songs != null)
                    for (Pesma p : songs) {                        
                        if (p.getIdP() == idS) {
                            listened = true;
                            break;
                        }
                    }
                
                if (!listened) {
                    Pesma s = em.find(Pesma.class, idS);
                    
                    em.getTransaction().begin();
                    
                    if (songs == null) {
                        songs = new ArrayList<Pesma>();
                    } 
                        
                    songs.add(s);                        
                    user.setPesmaList(songs);
                    
                    em.getTransaction().commit();
                }
                
                em.clear();
                em.close();
                emf.close();
                
                return true;
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } 
        else return false; 
    }
    
    @Override
    public void run() {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(musicQueue, "type='playSend'", false);
        JMSProducer producer = context.createProducer();
        
        while(true) {
            try {
                TextMessage msg = (TextMessage) consumer.receive();
                
                String parts[] = msg.getText().split(":");
                
                int idS = Integer.parseInt(parts[0]);
                int idK = Integer.parseInt(parts[1]);
                boolean status = playSong(idS, idK);
                
                String reply = status ? "1" : "0";
                TextMessage rplmsg = context.createTextMessage(reply);
                rplmsg.setStringProperty("type", "playRecieve");
                producer.send(musicQueue, rplmsg);
                
            } catch (JMSException ex) {
                Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
