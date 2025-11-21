package java.academic;

public class EligibilityCheck {
    private boolean isEligible = false;
    private final double CGPATarget = 2.0;
    private final int maxFailCourses = 3;

    public boolean checkCGPA(AcademicProfile profile){
        return profile.calculateCGPA() >= CGPATarget;
    }

    public boolean checkFailedCourseLimit(AcademicProfile profile){
        return profile.getTotalFailedCourse() <= maxFailCourses;
    }


    public boolean isEligible(){

        return isEligible;
    }

    public void setEligible(boolean eligible){

        isEligible = eligible;
    }
}