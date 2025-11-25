package domain;

public enum GradingScheme
{
    A_PLUS("A+", 4.0, 80.0),
    A("A", 3.7, 75.0),
    B_PLUS("B+", 3.3, 70.0),
    B("B", 3.0, 65.0),
    C_PLUS("C+", 2.7, 60.0),
    C("C", 2.3, 55.0),
    C_MINUS("C-", 2.0, 50.0),
    D("D", 1.7, 40.0),
    F_PLUS("F+", 1.3, 30.0),
    F("F", 1.0, 20.0),
    F_MINUS("F-", 0.0, 0.0);

    private final String grade;
    private final double gpa;
    private final double minScore;

    GradingScheme(String grade, double gpa, double minScore)
    {
        this.grade = grade;
        this.gpa = gpa;
        this.minScore = minScore;
    }

    public String getGrade()
    {
        return grade;
    }

    public double getGpa()
    {
        return gpa;
    }

    public double getMinScore()
    {
        return minScore;
    }
}

