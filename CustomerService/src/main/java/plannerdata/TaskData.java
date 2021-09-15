/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plannerdata;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author pc
 */
public class TaskData implements Serializable {    
    private int UserId;
    private Date date;
    private String dest;
    private Date dur;

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Date getDur() {
        return dur;
    }

    public void setDur(Date dur) {
        this.dur = dur;
    }
    
    
}
