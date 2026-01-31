/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

/**
 *
 * @author asian
 */



import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import java.util.Collections;
import java.util.List;

@PlanningSolution
public class EnrollmentSchedule {

    // This tells OptaPlanner where to find the data for the constraints.
    @ProblemFactCollectionProperty
    private List<optaPlannerEnroll> enrollmentList;

    // This is the property the error message is looking for.
    // It tells OptaPlanner which list contains the planning entities.
    @PlanningEntityCollectionProperty
    private List<DummyPlanningEntity> dummyEntityList;

    @PlanningScore
    private HardSoftScore score;

    public EnrollmentSchedule() {}

    public EnrollmentSchedule(List<optaPlannerEnroll> enrollmentList) {
        this.enrollmentList = enrollmentList;
        // You must initialize the dummy entity list.
        this.dummyEntityList = Collections.singletonList(new DummyPlanningEntity());
    }

    // This provides the possible values for the @PlanningVariable.
    @ValueRangeProvider(id = "booleanRange")
    public List<Boolean> getBooleanRange() {
        return Collections.singletonList(Boolean.TRUE);
    }
    
    // All Getters and Setters for the fields are required.
    public List<optaPlannerEnroll> getEnrollmentList() { return enrollmentList; }
    public void setEnrollmentList(List<optaPlannerEnroll> list) { this.enrollmentList = list; }
    public List<DummyPlanningEntity> getDummyEntityList() { return dummyEntityList; }
    public void setDummyEntityList(List<DummyPlanningEntity> list) { this.dummyEntityList = list; }
    public HardSoftScore getScore() { return score; }
    public void setScore(HardSoftScore score) { this.score = score; }
}
