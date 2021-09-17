/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import entities.Korisnik;
import entities.Obaveza;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import measurer.DistanceMeasurer;
import static planner.Main.cf;
import static planner.Main.plannerSendTopic;
import plannerdata.TaskData;
import static planner.Main.plannerReceiveTopic;

/**
 *
 * @author pc
 */
public class TaskManipulator extends Thread {
    
    private boolean canAddTask(TaskData task, List<Obaveza> obavezaList) {
        Obaveza before = null;
        Obaveza after = null;
        
        long max = 0;
        long min = 0;
        
        for (Obaveza o : obavezaList) {
            if (o.getPocetak().getTime() == task.getDate().getTime()) {
                return false;
            }            
            if (o.getPocetak().getTime() < task.getDate().getTime() &&
                o.getPocetak().getTime() > max) {
                before = o;
                max = o.getPocetak().getTime();
            }
        }
        
        for (Obaveza o : obavezaList) {
            if (o.getPocetak().getTime() == task.getDate().getTime()) {
                return false;
            }
            if (o.getPocetak().getTime() > task.getDate().getTime()) {
                if (after == null || o.getPocetak().getTime() < min) {
                    after = o;
                    min = o.getPocetak().getTime();
                }
            }
        }                        
                        
        long dBefore = -1;
        long dAfter = -1;
        
        if (before != null) {
            dBefore = DistanceMeasurer.distance(before.getLocation(), task.getDest()) * 1000;
            if (dBefore < 0) {
                return false;
            }
            dBefore += before.getPocetak().getTime() + before.getTrajanje() * 60 * 1000;
            if (dBefore > task.getDate().getTime()) return false;
        }
        
        if (after != null) {
            dAfter = DistanceMeasurer.distance(task.getDest(), after.getLocation()) * 1000;
            if (dAfter < 0) {
                return false;
            }
            dAfter += task.getDate().getTime() + task.getMinutesDuration() * 60 * 1000;
            if (dAfter > after.getPocetak().getTime()) return false;
        }
        
        return true;
    }
    
    private int createTask(TaskData task) {        
        // Date date = new Date();
        // if (date.getTime() > task.getDate().getTime()) return -1;
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        EntityManager em = emf.createEntityManager();
        
        Korisnik user = em.find(Korisnik.class, task.getUserId());
        List<Obaveza> obavezaList = user.getObavezaList();
        
        if (task.getDest() == null) {
            task.setDest(user.getLocation());
        }
        
        /*
        Obaveza before = null;
        Obaveza after = null;
        
        long max = 0;
        long min = 0;
        
        for (Obaveza o : obavezaList) {
            if (o.getPocetak().getTime() == task.getDate().getTime()) {
                return -2;
            }            
            if (o.getPocetak().getTime() < task.getDate().getTime() &&
                o.getPocetak().getTime() > max) {
                before = o;
                max = o.getPocetak().getTime();
            }
        }
        
        for (Obaveza o : obavezaList) {
            if (o.getPocetak().getTime() == task.getDate().getTime()) {
                return -2;
            }
            if (o.getPocetak().getTime() > task.getDate().getTime()) {
                if (after == null || o.getPocetak().getTime() < min) {
                    after = o;
                    min = o.getPocetak().getTime();
                }
            }
        }
                
        String dest;
        if (task.getDest() != null) dest = task.getDest();
        else dest = user.getLocation();                
        
                
        long dBefore = -1;
        long dAfter = -1;
        
        if (before != null) {
            dBefore = DistanceMeasurer.distance(before.getLocation(), dest) * 1000;
            if (dBefore < 0) {
                return -3;
            }
            dBefore += before.getPocetak().getTime() + before.getTrajanje() * 60 * 1000;
            if (dBefore > task.getDate().getTime()) return -1;
        }
        
        if (after != null) {
            dAfter = DistanceMeasurer.distance(dest, after.getLocation()) * 1000;
            if (dAfter < 0) {
                return -3;
            }
            dAfter += task.getDate().getTime() + task.getMinutesDuration() * 60 * 1000;
            if (dAfter > after.getPocetak().getTime()) return -1;
        }        
        */
        
        boolean status = canAddTask(task, obavezaList);
        
        if (status) {
            Obaveza ob = new Obaveza();
            
            ob.setIdK(user);
            ob.setLocation(task.getDest());
            ob.setPocetak(task.getDate());
            ob.setTrajanje(task.getMinutesDuration());

            em.getTransaction().begin();
            em.persist(ob);
            em.getTransaction().commit();

            em.close();
            emf.close();

            return 1;
            
        } else {
            return -1;
        }                
    }
    
    public boolean deleteTask(int idT, int idK) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        EntityManager em = emf.createEntityManager();
        
        Obaveza task = em.find(Obaveza.class, idT);
        
        if (task != null && task.getIdK().getIdK() == idK) {
            em.getTransaction().begin();
            em.remove(task);
            em.getTransaction().commit();            
            
            return true;
        } else {
            return false;
        }        
    }
    
    public ArrayList<Obaveza> getTasks(int idK) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        EntityManager em = emf.createEntityManager();
        
        Korisnik user = em.find(Korisnik.class, idK);

        ArrayList<Obaveza> tasks = new ArrayList<>();
        List<Obaveza> userTasks = user.getObavezaList();
        
        for (Obaveza task : userTasks) {
            tasks.add(task);
        }
        
        em.close();
        emf.close();
        
        return tasks;
    }
    
    public int editTask(TaskData taskData) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        EntityManager em = emf.createEntityManager();
        
        Obaveza task = em.find(Obaveza.class, taskData.getTaslId());                
        
        if (taskData.getDest() == null) {
            taskData.setDest(task.getLocation());            
        }
        
        if (taskData.getDate() == null) {
            taskData.setDate(task.getPocetak());            
        }
        
        if (taskData.getMinutesDuration() < 0) {
            taskData.setMinutesDuration(task.getTrajanje());            
        }
        
        Korisnik user = em.find(Korisnik.class, taskData.getUserId());
        
        List<Obaveza> userTasks = user.getObavezaList();
        
        for (int i = 0; i < userTasks.size(); i++) {
            if (userTasks.get(i).getIdO() == task.getIdO()) {
                userTasks.remove(i);
                break;
            }
        }
        
        boolean status = canAddTask(taskData, userTasks);
        
        if (status) {
            em.getTransaction().begin();            
            
            task.setLocation(taskData.getDest());
            task.setPocetak(taskData.getDate());
            task.setTrajanje(taskData.getMinutesDuration());            
            
            em.getTransaction().commit();
        }
        
        em.close();
        emf.close();
        
        return status ? 1 : -1;
    }
    
    private boolean setAlarm(int idO, int idK) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlannerPU");
        EntityManager em = emf.createEntityManager();
        
        Korisnik user = em.find(Korisnik.class, idK);
        List<Obaveza> obavezaList = user.getObavezaList();
        
        em.close();
        emf.close();
        
        return false;
    }
    
    @Override
    public void run() {
        JMSContext context = cf.createContext();
        JMSConsumer consumer = context.createConsumer(plannerSendTopic, "type='createSend' or type='deleteSend' or type='getSend' or type='editSend' or type='alarmSend'", false);
        JMSProducer producer = context.createProducer();
        
        while(true) {
            Message msg = consumer.receive();
            
            try {
                if (msg instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) msg;

                    if (textMessage.getStringProperty("type").equals("getSend")) {
                        ArrayList<Obaveza> tasks = getTasks(Integer.parseInt(textMessage.getText()));
                        
                        ObjectMessage rpl = context.createObjectMessage();
                        rpl.setObject(tasks);
                        rpl.setStringProperty("type", "getRecieve");
                        producer.send(plannerReceiveTopic, rpl);
                    } else if (textMessage.getStringProperty("type").equals("deleteSend")) {
                        String[] ids = textMessage.getText().split(",");
                        
                        int idT = Integer.parseInt(ids[0]);
                        int idK = Integer.parseInt(ids[1]);
                        
                        boolean deleted = deleteTask(idT, idK);
                        
                        String ret = "0";                        
                        if (deleted) {
                            ret = "1";
                        }
                        
                        TextMessage reply = context.createTextMessage(ret);
                        reply.setStringProperty("type", "deleteRecieve");
                        producer.send(plannerReceiveTopic, reply);
                    } else {
                        String[] ids = textMessage.getText().split(",");
                        
                        int idT = Integer.parseInt(ids[0]);
                        int idK = Integer.parseInt(ids[1]);
                        
                        boolean status = setAlarm(idT, idK);
                    }
                } else {
                    ObjectMessage objectMessage = (ObjectMessage) msg;
                    TaskData taskData = (TaskData) objectMessage.getObject();
                    int status = 0;
                    String ret = "2";                    
                    
                    if (objectMessage.getStringProperty("type").equals("createSend")) {                                                
                        status = createTask(taskData);                        
                        if (status == 1) {
                            ret = "1";
                        }
                        
                        TextMessage reply = context.createTextMessage(ret);
                        reply.setStringProperty("type", "createRecieve");
                        producer.send(plannerReceiveTopic, reply);                        
                        
                    } else if (objectMessage.getStringProperty("type").equals("editSend")) {
                        status = editTask(taskData);
                        if (status == 1) {
                            ret = "1";
                        }
                        
                        TextMessage reply = context.createTextMessage(ret);
                        reply.setStringProperty("type", "editRecieve");
                        producer.send(plannerReceiveTopic, reply);
                    }
                }
            } catch (Exception e) {
                
            }                                    
        }
    }
}