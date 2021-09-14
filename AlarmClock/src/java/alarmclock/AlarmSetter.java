/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmclock;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import static alarmclock.Main.cf;
import static alarmclock.Main.alarmSendTopic;
import static alarmclock.Main.alarmRecieveTopic;
import alarmdata.AlarmData;
import entities.Alarm;
import entities.Korisnik;
import entities.Pesma;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author pc
 */

public class AlarmSetter extends Thread {
    
    @Override
    public void run() {       
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(alarmSendTopic, "type='set'");
        JMSProducer producer = context.createProducer();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmClockPU");
        EntityManager em = emf.createEntityManager();
        
        while(true) {
            try {
                ObjectMessage msg = (ObjectMessage) consumer.receive();
                AlarmData ad = (AlarmData) msg.getObject();
                Alarm newAlarm = new Alarm();
                
                Korisnik user = em.find(Korisnik.class, ad.getUserId());
                newAlarm.setIdK(user);

                Pesma song = em.find(Pesma.class, 1);
                newAlarm.setIdP(song);
                
                newAlarm.setTip("" + ad.getType());
                newAlarm.setVreme(ad.getTimeList().get(0));
                if (ad.getType().equals("per")) newAlarm.setPerioda(ad.getPeriod());
                
                em.getTransaction().begin();
                em.persist(newAlarm);
                em.getTransaction().commit();
                
                if (ad.getType().equals("set")) {
                    new RegularAlarm(newAlarm.getIdA()).start();
                }
                else {
                    new PeriodicAlarm(newAlarm.getIdA()).start();
                }
                
                
            } catch (JMSException ex) {
                em.close();
                emf.close();
                Logger.getLogger(AlarmSetter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
