package service;

import report.Report;

public class PDFExporter {
    private Object iTextPDFInstance = new Object();

    public boolean exportReportToPDF(Report report, String filePath){
        System.err.println("Exporting report "+ report.getReportID() + " to "+ filePath);
        return true;
    }
}