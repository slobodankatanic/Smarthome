/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import entities.Korisnik;
import entities.Pesma;
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
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import static musicplayer.Main.cf;
import static musicplayer.Main.musicListQueue;

/**
 *
 * @author pc
 */
public class AudioListSender extends Thread {
    
    public ArrayList<Pesma> getSongs(int idK) {
        EntityManager em = Persistence.createEntityManagerFactory("MusicPlayerPU").createEntityManager();    
        Korisnik user = em.find(Korisnik.class, idK);
        ArrayList<Pesma> songs = new ArrayList<>();
        
        if (user != null) {
            List<Pesma> songList = user.getPesmaList();
            for (Pesma p : songList) songs.add(p);
        }
        return songs;
    }
    
    @Override
    public void run() {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(musicListQueue, "type='listSend'", false);
        JMSProducer producer = context.createProducer();
        
        while(true) {
            try {
                TextMessage msg = (TextMessage) consumer.receive();
                int idK = Integer.parseInt(msg.getText());
                
                ArrayList<Pesma> songs = getSongs(idK);
                
                ObjectMessage rpl = context.createObjectMessage();
                rpl.setObject(songs);
                rpl.setStringProperty("type", "listRecieve");
                producer.send(musicListQueue, rpl);
                
            } catch (JMSException ex) {
                Logger.getLogger(AudioListSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
