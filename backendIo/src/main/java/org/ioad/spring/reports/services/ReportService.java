//package org.ioad.spring.reports;
//
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import org.ioad.spring.request.models.Request;
//import org.ioad.spring.request.services.IRequestService;
//import org.ioad.spring.resource.models.Resource;
//import org.ioad.spring.resource.services.ResourceService;
//import org.ioad.spring.task.model.Task;
//import org.ioad.spring.task.repository.TaskRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.ioad.spring.reports.Report;
//import java.io.ByteArrayOutputStream;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ReportService {
//
//    @Autowired
//    private TaskRepo taskRepo;
//    @Autowired
//    private IRequestService requestService;
//    @Autowired
//    private ResourceService resourceService;
//    @Autowired
//    private Report report4;
//    public byte[] generateReport() throws JRException {
//
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        JasperExportManager.exportReportToPdfStream(report4.createHelpActivitiesReport(), byteArrayOutputStream);
//        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
//
//        return pdfBytes; // Zwrócenie wygenerowanego PDF jako byte[]
//    }
//}
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
//            case "taxes":
//                jasperPrint = report.createTaxReport(userId);
//                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowy typ raportu: " + type);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}