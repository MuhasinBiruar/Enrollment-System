/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class DummyPlanningEntity {
    //just to comply with variable requirement
    @PlanningVariable(valueRangeProviderRefs = "booleanRange")
    private Boolean assigned;

    public Boolean getAssigned() { return assigned; }
    public void setAssigned(Boolean assigned) { this.assigned = assigned; }
}