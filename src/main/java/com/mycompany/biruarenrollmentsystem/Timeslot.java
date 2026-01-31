/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

/**
 *
 * @author asian
 */
import java.time.LocalTime;
import java.util.Objects;

public class Timeslot {
    String day;
    LocalTime startTime;
    LocalTime endTime;

    public Timeslot(String day, LocalTime startTime, LocalTime endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public boolean overlaps(Timeslot other){
        return Objects.equals(this.day, other.day) &&
                this.startTime.isBefore(other.endTime) &&
                other.startTime.isBefore(this.endTime);
    }

    public String getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
    
    
}
