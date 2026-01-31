/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

/**
 *
 * @author asian
 */
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

//@PlanningEntity
public class optaPlannerEnroll {
    int studid;
    Timeslot timeslot;
    
    public optaPlannerEnroll() { }

    public optaPlannerEnroll(int studid, Timeslot timeslot) {
        this.studid = studid;
        this.timeslot = timeslot;
    }
    
    //@PlanningVariable
    public int getStudid() {
        return studid;
    }
    
    //@PlanningVariable
    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setStudid(int studid) {
        this.studid = studid;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }
    
    
}
