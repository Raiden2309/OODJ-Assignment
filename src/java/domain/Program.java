package java.domain;

public class Program
{
    private String programId;
    private String name;
    private String level;

    public Program(String programId, String name, String level)
    {
        this.programId = programId;
        this.name = name;
        this.level = level;
    }

    public String getProgramId() {
        return programId;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }
}

