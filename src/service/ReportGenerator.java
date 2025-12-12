package service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * - Reads student data from CSV
 * - Calculates grades and CGPA
 * - Generates PDF report
 * INPUTS: Student ID (the String)
 * OUTPUTS: PDF file in /data/reports/ folder
 */
public class ReportGenerator {

    // File paths for CSV data
    private static final String STUDENT_INFO_FILE = "data/student_information.csv";
    private static final String ACADEMIC_RECORDS_FILE = "data/academic_records.csv";
    private static final String COURSE_INFO_FILE = "data/course_assessment_information.csv";


    // READ STUDENT DATA
    // Return String Array
    public String[] getStudentInfo(String studentID) {

        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_INFO_FILE))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(studentID)) {
                    // Create the result array
                    String id = data[0];
                    String name = data[1] + " " + data[2];  // FirstName + LastName
                    String major = data[3];
                    String year = data[4];
                    String email = data[5];

                    return new String[]{id, name, major, year, email};
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading student info: " + e.getMessage());
        }

        // Return null if student not found
        return null;
    }


    // READ ACADEMIC RECORDS
    public List<String[]> getStudentCourses(String studentID) {
        // Read from data/academic_records.csv
        // Read from data/course_assessment_information.csv
        // Combine data for each course
        // Return list of: [CourseID, CourseName, Credits, Grade]

        List<String[]> courses = new ArrayList<>();

        try (BufferedReader recordsReader = new BufferedReader(new FileReader(ACADEMIC_RECORDS_FILE))) {
            String line;

            recordsReader.readLine();

            while ((line = recordsReader.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(studentID)) {
                    String courseID = data[1];
                    String grade = data[3];

                    String[] courseDetails = getCourseDetails(courseID);

                    if (courseDetails != null) {
                        String[] courseRecord = new String[4];
                        courseRecord[0] = courseID;
                        courseRecord[1] = courseDetails[0];  // Course Name
                        courseRecord[2] = courseDetails[1];  // Credits
                        courseRecord[3] = grade;

                        courses.add(courseRecord);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading academic records: " + e.getMessage());
        }

        return courses;
    }

    private String[] getCourseDetails(String courseID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSE_INFO_FILE))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(courseID)) {
                    String courseName = data[1];
                    String credits = data[2];

                    return new String[]{courseName, credits};
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading course info: " + e.getMessage());
        }

        return null;
    }


    // CALCULATE GRADES
    public double gradeToPoint(String letterGrade) {
        if (letterGrade.equals("A+")) return 4.0;
        if (letterGrade.equals("A")) return 3.7;
        if (letterGrade.equals("A-")) return 3.5;
        if (letterGrade.equals("B+")) return 3.3;
        if (letterGrade.equals("B")) return 3.0;
        if (letterGrade.equals("B-")) return 2.7;
        if (letterGrade.equals("C+")) return 2.7;
        if (letterGrade.equals("C")) return 2.3;
        if (letterGrade.equals("C-")) return 2.0;
        if (letterGrade.equals("D+")) return 1.7;
        if (letterGrade.equals("D")) return 1.7;
        if (letterGrade.equals("D-")) return 1.3;
        if (letterGrade.equals("F+")) return 1.3;
        if (letterGrade.equals("F")) return 1.0;
        if (letterGrade.equals("F-")) return 0.0;

        return 0.0;
    }


    public double calculateCGPA(List<String[]> courses) {

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (String[] course : courses) {
            // course[2] is Credits, course[3] is Grade
            int credits = Integer.parseInt(course[2]);
            String grade = course[3];
            double gradePoint = gradeToPoint(grade);

            totalPoints = totalPoints + (gradePoint * credits);
            totalCredits = totalCredits + credits;
        }

        if (totalCredits > 0) {
            return totalPoints / totalCredits;
        } else {
            return 0.0;
        }
    }


    // GENERATE PDF
    public void generatePDF(String studentID) {
        String[] studentInfo = getStudentInfo(studentID);

        if (studentInfo == null) {
            System.out.println("Student not found: " + studentID);
            return;
        }

        List<String[]> courses = getStudentCourses(studentID);

        double cgpa = calculateCGPA(courses);

        try {
            String fileName = studentID + "_Report.pdf";
            String reportFolder = "data" + File.separator + "report";

            File folder = new File(reportFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = reportFolder + File.separator + fileName;

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            document.open();

            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph header = new Paragraph("Student Academic Report", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph("\n"));

            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            document.add(new Paragraph("Student ID: " + studentInfo[0], normalFont));
            document.add(new Paragraph("Name: " + studentInfo[1], normalFont));
            document.add(new Paragraph("Major: " + studentInfo[2], normalFont));
            document.add(new Paragraph("Year: " + studentInfo[3], normalFont));
            document.add(new Paragraph("Email: " + studentInfo[4], normalFont));

            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(4);  // 4 columns
            table.setWidthPercentage(100);

            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Course ID", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Course Name", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Credits", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Grade", boldFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            for (String[] course : courses) {
                for (String value : course) {
                    cell = new PdfPCell(new Phrase(value, normalFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total Courses: " + courses.size(), boldFont));
            document.add(new Paragraph(String.format("CGPA: %.2f", cgpa), boldFont));

            document.close();

            System.out.println("PDF generated successfully: " + filePath);

        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }


    // MAIN METHOD FOR TESTING
//    public static void main(String[] args) {
//        ReportGenerator generator = new ReportGenerator();
//
//        // Test with a student ID
//        String testStudentID = "S001";
//
//        // Test getting student info
//        String[] info = generator.getStudentInfo(testStudentID);
//        if (info != null) {
//            System.out.println("Student Info: " + info[0] + ", " + info[1] + ", " + info[2] + ", " + info[3]);
//        }
//
//        // Test getting courses
//        List<String[]> courses = generator.getStudentCourses(testStudentID);
//        System.out.println("Number of courses: " + courses.size());
//
//        // Test CGPA calculation
//        double cgpa = generator.calculateCGPA(courses);
//        System.out.println("CGPA: " + cgpa);
//
//        // Generate PDF
//        generator.generatePDF(testStudentID);
}