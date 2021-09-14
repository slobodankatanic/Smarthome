/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmclock;

import entities.Alarm;
import entities.Pesma;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author pc
 */
public class RegularAlarm extends Thread {
    
    private int idA;
    
    public RegularAlarm(int idA) {
        this.idA = idA;
    }
    
    public long timeDifference(Calendar calC, Calendar calA) {
        long timeC = calC.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 +
                    calC.get(Calendar.MINUTE) * 60 * 1000 + 
                    calC.get(Calendar.SECOND) * 1000;
        long timeA = calA.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 +
                    calA.get(Calendar.MINUTE) * 60 * 1000 +
                    calA.get(Calendar.SECOND) * 1000;       
        long midnight = 24 * 60 * 60 * 1000;
        
        long ret;
        if (timeA >= timeC) ret = timeA - timeC;
        else ret = timeA + midnight - timeC;
        
        return ret;
    }
    
    @Override
    public void run() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmClockPU");
        EntityManager em = emf.createEntityManager();
        Alarm alarm = em.find(Alarm.class, idA);
        //while(true) {
            try {
                //Thread.sleep(alarm.getPerioda() * 1000);
                Date alarmTime = alarm.getVreme();
                Date currTime = new Date();
                System.out.println(currTime.toString());
                
                Calendar calA = Calendar.getInstance();
                Calendar calC = Calendar.getInstance();
                calA.setTime(alarmTime);
                calC.setTime(currTime);
                
                long alarmLeft = timeDifference(calC, calA);
                System.out.println(alarmLeft);
                Thread.sleep(alarmLeft);
                
                alarm = em.find(Alarm.class, idA);
                Pesma song = em.find(Pesma.class, alarm.getIdP().getIdP());
                
                if (Desktop.isDesktopSupported()) {
                    
                    if (song != null) {
                        Desktop.getDesktop().browse(new URI(song.getUrl()));
                    }
                }   
                
                //Thread.sleep(60000);
                
            } catch (InterruptedException | IOException | URISyntaxException ex) {
                Logger.getLogger(PeriodicAlarm.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
    }
}
