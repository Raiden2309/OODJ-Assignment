package service;
import model.StudentPerformance;

public class EligibilityCheck
{
    public boolean[] isEligible(StudentPerformance performance)
    {
        final int maxFailedCourses = 3;
        final double minCGPA = 2.0;
        final boolean conditionOneMet = performance.getFailedCourses() > maxFailedCourses;
        final boolean conditionTwoMet = performance.getCgpa() < minCGPA;
        final boolean anyConditionMet = conditionOneMet || conditionTwoMet;

        if (anyConditionMet)
        {
            return new boolean[]{true, conditionOneMet, conditionTwoMet};
        }
        else
        {
            return new boolean[]{false, conditionOneMet, conditionTwoMet};
        }
    }
}
