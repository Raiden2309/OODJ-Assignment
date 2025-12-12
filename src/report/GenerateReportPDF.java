package report;

import academic.EnrolledCourse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import domain.Student;
import domain.StudentPerformance;
import service.EnrolledCourseDAO;
import service.StudentDAO;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class GenerateReportPDF
{
    private final Font heading = FontFactory.getFont(FontFactory.TIMES_BOLD, 16, BaseColor.BLACK);
    private final Font body = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
    private final Font column = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
    private final String pdfTitle = "Student Academic Performance Report";

    StudentDAO studentDAO = new StudentDAO();
    List<Student> students = studentDAO.loadAllStudents();

    EnrolledCourseDAO enrolledCourseDAO = new EnrolledCourseDAO();
    List<EnrolledCourse> studentEnrolments = enrolledCourseDAO.loadAllEnrolledCourses();

    int totalCreditHours = 0;
    double totalGpa = 0;

    public void createDocument(String studentId)
    {
        LocalDateTime currDateTime = LocalDateTime.now(ZoneId.of("GMT+8"));
        String docName = studentId + "_" + currDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf";
        final String FILE_PATH = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + docName;
        try
        {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(FILE_PATH));
            doc.addCreationDate();
            doc.addTitle(pdfTitle + " - " + studentId);

            doc.open();
            generateDocContents(doc, studentId);
            doc.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }

    public void generateDocContents(Document doc, String studentId)
    {
        final String[] student_info = {"Student Name", "Student ID", "Major", "Year"};
        try
        {
            Paragraph header = new Paragraph(pdfTitle.toUpperCase(), heading);
            header.setAlignment(Element.ALIGN_CENTER);
            doc.add(header);

            for (Student student : students)
            {
                if (student.getStudentId().equals(studentId))
                {
                    String name = student.getFullName();
                    String major = student.getMajor();
                    String year = student.getAcademicYear();
                    String[] info = {name, studentId, major, year};

                    for (int i = 0; i < student_info.length; i++)
                    {
                        Paragraph p = new Paragraph();

                        Chunk info_type = new Chunk(student_info[i] + ": ", column);
                        Chunk information = new Chunk(info[i] + "\n", body);

                        p.setSpacingBefore(i == 0 ? 18 : 0);
                        p.setSpacingAfter(i == student_info.length - 1 ? 12 : 5);

                        p.add(info_type);
                        p.add(information);
                        doc.add(p);
                    }
                    break;
                }
            }
            separateByYear(studentId, doc);
        }
        catch (Exception e)
        {
            System.out.println("Error:" + e);
        }
    }

    public void separateByYear(String studentId, Document doc)
    {
        int displayedYear = 0;
        int displayedSemester = 0;

        for (EnrolledCourse ec : studentEnrolments)
        {
            if (ec.getStudentID().equals(studentId)) {
                int year = ec.getYear();
                int semester = ec.getSemester();

                try {
                    if (year != displayedYear) {
                        Paragraph yearHeader = new Paragraph("YEAR " + year, heading);
                        doc.add(yearHeader);
                        displayedYear = year;
                        displayedSemester = 0;
                    }

                    if (semester != displayedSemester) {
                        Paragraph semHeader = new Paragraph("SEMESTER " + semester, body);
                        semHeader.setSpacingAfter(10);
                        doc.add(semHeader);
                        displayedSemester = semester;
                        generateTable(studentId, doc, displayedYear, displayedSemester);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            }
        }
    }

    public void generateTable(String studentId, Document doc, int displayedYear, int displayedSemester)
    {
        final String[] columnTitles = {
                "Course Code",
                "Course Title",
                "Credit Hours",
                "Grade",
                "Grade Point"
        };
        final int totalColumns = columnTitles.length;
        final float[] columnWidths = {1.1f, 3f, 1.1f, 0.75f, 1f};

        PdfPTable tab = new PdfPTable(totalColumns);

        try
        {
            for (String columnTitle : columnTitles) {
                PdfPCell cell = new PdfPCell();

                cell.setPhrase(new Phrase(columnTitle, column));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tab.addCell(cell);
            }
            addRows(studentId, tab, displayedYear, displayedSemester);

            tab.setWidthPercentage(100);
            tab.setWidths(columnWidths);
            doc.add(tab);
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }

    public void addRows(String studentId, PdfPTable tab, int displayedYear, int displayedSemester)
    {
        StudentPerformance perf = new StudentPerformance(studentId);
        int creditsBySemester = 0;
        double gpaBySemester = 0;

        for (EnrolledCourse ec : studentEnrolments)
        {
            if (ec.getYear() == displayedYear && ec.getSemester() == displayedSemester)
            {
                for (String[] record : perf.getPerformance())
                {
                    if (record[0].equals(ec.getCourseID()) && ec.getStudentID().equals(studentId))
                    {
                        int creditHours = Integer.parseInt(record[2]);
                        double gradePoint = Double.parseDouble(record[4]);

                        creditsBySemester += creditHours;
                        gpaBySemester += gradePoint * creditHours;

                        for (String r : record)
                        {
                            PdfPCell cell = new PdfPCell();

                            cell.setPhrase(new Phrase(r, body));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            tab.addCell(cell);
                        }
                    }
                }
            }
        }
        addSummary(tab, creditsBySemester, gpaBySemester);
    }

    public void addSummary(PdfPTable tab, int creditHours, double gpa)
    {
        final List<String> summaryTitles = Arrays.asList("Total Credit Hours", "GPA", "CGPA");
        final int totalSummary = summaryTitles.size();

        totalCreditHours += creditHours;
        totalGpa += gpa;

        double overallCgpa = 0;
        if (totalCreditHours > 0) {
            overallCgpa = totalGpa / totalCreditHours;
        }

        String cgpaBySemester = String.format("%.2f", gpa / creditHours);
        String totalCgpa = String.format("%.2f", overallCgpa);

        String[] results = {
                String.valueOf(creditHours),
                cgpaBySemester,
                totalCgpa
        };
        int cellsAdded = 0;

        for (String summary : summaryTitles)
        {
            for (int i = 0; i < totalSummary; i++)
            {
                PdfPCell cell = new PdfPCell(new Phrase(""));

                if (cellsAdded == 0 || cellsAdded == 3 || cellsAdded == 6)
                {
                    cell.setColspan(2);
                    cell.setBorder(PdfPCell.NO_BORDER);
                }
                else if (cellsAdded == 1 || cellsAdded == 4 || cellsAdded == 7)
                {
                    cell.setColspan(2);
                    cell.setPhrase(new Phrase(summary, column));
                    cell.setBorder(PdfPCell.BOX);
                }
                else
                {
                    cell.setColspan(1);
                    cell.setPhrase(new Phrase(results[summaryTitles.indexOf(summary)], body));
                    cell.setBorder(PdfPCell.BOX);
                }
                tab.addCell(cell);
                cellsAdded++;
            }
        }
    }
}