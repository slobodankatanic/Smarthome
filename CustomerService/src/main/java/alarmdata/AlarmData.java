/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarmdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author pc
 */
public class AlarmData implements Serializable {
    private int userId;
    private String type;
    private ArrayList<Date> timeList;
    private int period;
    
    public AlarmData() {
        timeList = new ArrayList<Date>();
    }
    
    public void setType(String c) {
        type = c;
    }
    
    public String getType() {
        return type;
    }
    
    public void addTime(Date time) {
        timeList.add(time);
    }
    
    public ArrayList<Date> getTimeList() {
        return timeList;
    } 
    
    public void setUserId(int id) {
        userId = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setPeriod(int p) {
        period = p;
    }
    
    public int getPeriod() {
        return period;
    }
	
}
