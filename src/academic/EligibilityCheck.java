package academic;

// This class encapsulates the business logic for checking a student's eligibility
public class EligibilityCheck {

    private boolean isEligible;

    // Constants for business rules
    private static final double MIN_CGPA = 2.00;
    private static final int MAX_FAILED_COURSES = 3;

    /**
     * Checks if the student's CGPA meets the minimum requirement.
     * @param profile The student's academic profile.
     * @return true if CGPA >= MIN_CGPA, false otherwise.
     */
    public boolean checkCGPA(AcademicProfile profile) {
        double studentCGPA = profile.calculateCGPA();
        return studentCGPA >= MIN_CGPA;
    }

    public boolean checkFailedCourseLimit(AcademicProfile profile) {
        int failedCourses = profile.getTotalFailedCourse();
        return failedCourses <= MAX_FAILED_COURSES;
    }

    public double getMinCgpa() {
        return MIN_CGPA;
    }

    public int getMaxFailedCourses()
    {
        return MAX_FAILED_COURSES;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }
}