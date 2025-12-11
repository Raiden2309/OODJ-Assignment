package report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import data_access.DataAccess;
import domain.StudentPerformance;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateReportPDF
{
    private final Font heading = FontFactory.getFont(FontFactory.TIMES_BOLD, 16, BaseColor.BLACK);
    private final Font body = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
    private final Font column = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
    private final String pdfTitle = "Student Academic Performance Report";

    DataAccess data = new DataAccess();
    List<String[]> students = data.getStudents();

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

            for (String[] student : students)
            {
                if (student[0].equals(studentId))
                {
                    String name = student[1] + " " + student[2];
                    String major = student[3];
                    String year = student[4];
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

    public  void separateByYear(String studentId, Document doc)
    {
        List<String[]> enrolledCourses = data.getEnrolledCourses(new String[]{studentId});
        int displayedYear = 0;
        int displayedSemester = 0;

        for (String[] enrolledCourse : enrolledCourses)
        {
            int year = Integer.parseInt(enrolledCourse[2]);
            int semester = Integer.parseInt(enrolledCourse[3]);

            try
            {
                if (year != displayedYear)
                {
                    Paragraph yearHeader = new Paragraph("YEAR " + enrolledCourse[2], heading);
                    doc.add(yearHeader);
                    displayedYear = year;
                    displayedSemester = 0;
                }

                if (semester != displayedSemester)
                {
                    Paragraph semHeader = new Paragraph("SEMESTER " + enrolledCourse[3], body);
                    semHeader.setSpacingAfter(10);
                    doc.add(semHeader);
                    displayedSemester = semester;
                    generateTable(studentId, doc, displayedYear, displayedSemester);
                }
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e);
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
        DataAccess data = new DataAccess();
        StudentPerformance perf = new StudentPerformance(studentId);
        List<String[]> enrolledCourses = data.getEnrolledCourses(new String[]{studentId});
        int creditsBySemester = 0;
        double gpaBySemester = 0;

        for (String[] enrolledCourse : enrolledCourses)
        {
            if (Integer.parseInt(enrolledCourse[2]) == displayedYear && Integer.parseInt(enrolledCourse[3]) == displayedSemester)
            {
                for (String[] row : perf.getPerformance(data))
                {
                    if (row[0].equals(enrolledCourse[1]))
                    {
                        int creditHours = Integer.parseInt(row[2]);
                        double gradePoint = Double.parseDouble(row[4]);

                        creditsBySemester += creditHours;
                        gpaBySemester += gradePoint * creditHours;

                        for (String s : row)
                        {
                            PdfPCell cell = new PdfPCell();

                            cell.setPhrase(new Phrase(s, body));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            tab.addCell(cell);
                        }
                    }
                }
            }
        }
        addSummary(studentId, tab, creditsBySemester, gpaBySemester);
    }

    public void addSummary(String studentId, PdfPTable tab, int creditHours, double gpa)
    {
        DataAccess data = new DataAccess();
        StudentPerformance perf = new StudentPerformance(studentId);
        perf.getPerformance(data);

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