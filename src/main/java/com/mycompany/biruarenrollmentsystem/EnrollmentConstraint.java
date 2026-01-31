/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

/**
 *
 * @author asian
 */

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;


public class EnrollmentConstraint implements ConstraintProvider{

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{ scheduleConflict(factory) };
    }
    
     private Constraint scheduleConflict(ConstraintFactory factory) {
        // Penalize each pair of enrollments for the same student that have overlapping times.
        return factory.forEachUniquePair(optaPlannerEnroll.class,
                Joiners.equal(optaPlannerEnroll::getStudid), // Find pairs of enrollments for the same student that have overlapping times
                Joiners.overlapping(e -> e.getTimeslot().getStartTime(), e -> e.getTimeslot().getEndTime())
        )//returns a builder
         .penalize(HardSoftScore.ONE_HARD, (enrollment1, enrollment2) -> 1).asConstraint("Schedule conflict");
    }
    
}
