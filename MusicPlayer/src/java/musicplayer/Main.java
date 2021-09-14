/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 *
 * @author pc
 */
public class Main {

    @Resource(lookup = "shConnectionFactory")
    static ConnectionFactory cf;
    
    @Resource(lookup = "musicListQueue")
    static Queue musicListQueue;
    
    @Resource(lookup = "musicQueue")
    static Queue musicQueue;
    
    public static void main(String[] args) {
        AudioPlayer musicPlayer = new AudioPlayer();
        AudioListSender musicListSender = new AudioListSender();
        musicPlayer.start();
        musicListSender.start();
    }
    
}
