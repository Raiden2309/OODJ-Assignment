package academic;

public class EligibilityCheck {
    private boolean isEligible = false;
    private final double CGPATarget = 2.0;
    private final int maxFailCourses = 3;

    public boolean checkCPGA(AcademicProfile profile){
        return profile.calculateCGPA() >= CGPATarget;
    }

    public boolean checkFailedCourseLimit(AcademicProfile profile){
        return profile.getTotalFailedCourse() <= maxFailCourses;
    }

    public Enrolment confirmRegistration(String studentID, CourseRecoveryPlan plan){
        if (isEligible){
            return new Enrolment(studentID, plan);
        }
        return null;
    }

    public boolean isEligible(){
        return isEligible;
    }

    public void setEligible(boolean eligible){
        isEligible = eligible;
    }
}