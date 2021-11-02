/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmclock;

import entities.Alarm;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class Main {
	
	// for creating destinations
    @Resource(lookup = "shConnectionFactory")
    static ConnectionFactory cf;
    
    @Resource(lookup = "alarmSendTopic")
    static Topic alarmSendTopic;
    
    @Resource(lookup = "alarmRecieveTopic")
    static Topic alarmRecieveTopic;
    
    public static void main(String[] args) {
        new AlarmSetter().start();
        new SongSetter().start();
        
        /*EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmClockPU");
        EntityManager em = emf.createEntityManager();
        
        TypedQuery<Alarm> tq = em.createQuery("select a from Alarm al", Alarm.class);
        List<Alarm> alarmList = tq.getResultList();
        if (alarmList != null) {
            for (Alarm a : alarmList) {
                if (a.getTip().equals("set")) {
                        new RegularAlarm(a.getIdA()).start();
                    }
                    else {
                        new PeriodicAlarm(a.getIdA()).start();
                    }
            }
        }*/
    }
    
}
