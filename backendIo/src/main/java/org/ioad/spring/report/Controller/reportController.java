package org.ioad.spring.report.Controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.ioad.spring.report.reports.resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class reportController {

    private String type;
    private String activity;

    public static void main(String[] args) throws JRException {


//        public void generateReport(String type, String activity) {
//
//        }
//
//        public void deleteReport(int id) {
//
//        }
//
//        public archive(int id) {
//
//        }

        SpringApplication.run(reportController.class, args);

        // Stworzenie i dodanie poszczególnych rekordów (resources)
        resource resource0 = new resource("null", "null", "null", "null", "null", "null", "null", "null", "null");
        resource resource1 = new resource("1", "Ciasto", "Lodz", "15.12.2024", "5", "kg", "1", "AVALIABLE", "Food");
        resource resource2 = new resource("2", "Chleb", "Warszawa", "7.12.2024", "1", "kg", "2", "EXPIRED", "Food");
        resource resource3 = new resource("3", "Woda", "Lodz", "25.12.2024", "20", "l", "3", "UNAVALIABLE", "Food");
        resource resource4 = new resource("4", "Slodycze", "Poznań", "25.12.2025", "4", "kg", "4", "AVALIABLE", "Food");

        List<resource> resourcesList = new ArrayList<>();

        resourcesList.add(resource0);
        resourcesList.add(resource1);
        resourcesList.add(resource2);
        resourcesList.add(resource3);
        resourcesList.add(resource4);

        // Stworzenie Źródłowej bazy danych która zostanie naniesiona na template (arkusz raportu)
        JRAbstractBeanDataSource resourceDataSource = new JRBeanCollectionDataSource(resourcesList);

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("resourcesDataSet", resourceDataSource);

        System.out.println("Katalog roboczy: " + System.getProperty("user.dir"));

        // Storzenie i wygenerowanie do pliku PDF raportu
        String filePath = "src\\main\\java\\org\\ioad\\spring\\report\\reports\\Test.jrxml";
        // Zamiana pliku jrxml na plik JasperReport
        JasperReport report = JasperCompileManager.compileReport(filePath);
        // Stworzenie raportu
        JasperPrint print = JasperFillManager.fillReport(report, parameters, resourceDataSource);
        // Miejsce docelowe i generowanie pliku PDF
        JasperExportManager.exportReportToPdfFile(print, "src\\main\\java\\org\\ioad\\spring\\report\\reports\\plikPDF.pdf");
        System.out.println("Raport wygenerowany do PDF");
    }
}
