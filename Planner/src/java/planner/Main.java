/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;

/**
 *
 * @author pc
 */
public class Main {
    @Resource(lookup = "shConnectionFactory")
    static ConnectionFactory cf;
    
    @Resource(lookup = "plannerSendTopic")
    static Topic plannerSendTopic;
    
    @Resource(lookup = "plannerReceiveTopic")
    static Topic plannerReceiveTopic;
   
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
