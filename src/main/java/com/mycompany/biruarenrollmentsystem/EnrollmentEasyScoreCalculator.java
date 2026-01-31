package com.mycompany.biruarenrollmentsystem;

import java.util.List;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public class EnrollmentEasyScoreCalculator implements EasyScoreCalculator<EnrollmentSchedule, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(EnrollmentSchedule schedule) {
        int hard = 0;
        int soft = 0;

        // Use the LIST YOU ACTUALLY PASS in your enrollment code:
        List<optaPlannerEnroll> rows = schedule.getEnrollmentList();
        if (rows == null || rows.isEmpty()) {
            // Nothing to check -> feasible 0/0
            return HardSoftScore.of(hard, soft);
        }

        // HARD: a student cannot have two classes that overlap
        final int n = rows.size();
        for (int i = 0; i < n; i++) {
            optaPlannerEnroll a = rows.get(i);
            for (int j = i + 1; j < n; j++) {
                optaPlannerEnroll b = rows.get(j);
                if (a.getStudid() == b.getStudid()) {
                    Timeslot ta = a.getTimeslot();
                    Timeslot tb = b.getTimeslot();
                    if (ta != null && tb != null && ta.overlaps(tb)) {
                        hard -= 1; // penalize each overlapping pair
                    }
                }
            }
        }

        // (optional soft preferences later)

        return HardSoftScore.of(hard, soft);
    }
}
