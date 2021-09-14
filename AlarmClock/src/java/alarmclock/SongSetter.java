/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmclock;

import static alarmclock.Main.alarmRecieveTopic;
import static alarmclock.Main.alarmSendTopic;
import static alarmclock.Main.cf;
import entities.Alarm;
import entities.Pesma;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author pc
 */
public class SongSetter extends Thread {
    
    @Override 
    public void run() {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createSharedConsumer(alarmSendTopic, "sub1", "type='setSong'");
        JMSProducer producer = context.createProducer();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmClockPU");
        EntityManager em = emf.createEntityManager();
        
        while(true) {
            try {
                TextMessage msg = (TextMessage) consumer.receive();
                String[] parts = msg.getText().split(":");
                int idA = Integer.parseInt(parts[0]);
                int idS = Integer.parseInt(parts[1]);
                int idK = Integer.parseInt(parts[2]);
                
                String ret;
                
                Alarm alarm = em.find(Alarm.class, idA);
                if (alarm != null & alarm.getIdK().getIdK() == idK) {
                    Pesma song = em.find(Pesma.class, idS);
                    if (song != null) {
                        em.getTransaction().begin();
                        alarm.setIdP(song);
                        em.getTransaction().commit();
                        ret = "1";
                    }
                    else ret = "0";
                }
                else ret = "0";
                
                TextMessage rpl = context.createTextMessage(ret);
                rpl.setStringProperty("type", "setSong");
                producer.send(alarmRecieveTopic, rpl);
                
            } catch (JMSException ex) {
                Logger.getLogger(SongSetter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
