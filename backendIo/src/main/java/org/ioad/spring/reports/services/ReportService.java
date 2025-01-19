package org.ioad.spring.reports.services;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.ioad.spring.reports.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReportService {

    @Autowired
    private Report report;

    public byte[] generateReport(String type, long userId) throws JRException {
        JasperPrint jasperPrint;
        switch (type) {
            case "tasks":
                jasperPrint = report.createHelpActivitiesReport();
                break;
            case "applications":
                jasperPrint = report.createRequestReport();
                break;
            case "resources":
                jasperPrint = report.createResourcesReport();
                break;
            case "donations":
                jasperPrint = report.createReceiptReport(userId);
                break;
            case "taxes":
                jasperPrint = report.createTaxReport(userId);
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowy typ raportu: " + type);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}