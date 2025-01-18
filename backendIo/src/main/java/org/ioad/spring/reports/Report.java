package org.ioad.spring.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.services.IRequestService;
import org.ioad.spring.resource.models.Donation;
import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.services.ResourceService;
import org.ioad.spring.task.model.Task;
import org.ioad.spring.user.repository.UserInfoRepository;


import org.ioad.spring.task.repository.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
@Service
public class Report {
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private IRequestService requestService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserInfoRepository userInfoRepository;

    Date nowDate = new Date();
//    JasperPrint createTaxReport(long userId) throws JRException {
//      System.out.println(resourceService.getSumOfQuantityByTypeAndDonorId(ResourceType.FINANCIAL, userId));
//
//        Map<String, Object> parameters = new HashMap<>();
//        double kwotaa = resourceService.getSumOfQuantityByTypeAndDonorId(ResourceType.FINANCIAL, userId);
//        parameters.put("kwota", kwotaa);
//        parameters.put("Data", nowDate);
//        String filePath = "C:\\Users\\dawid\\Desktop\\studia-3rok\\io\\projekt2\\System-koordynacji-pomocy-humanitarnej-SKPH--master\\backendIo\\src\\main\\java\\org\\ioad\\spring\\reports\\TaxReport.jrxml";
//        JasperReport report = JasperCompileManager.compileReport(filePath);
//        JasperPrint print = JasperFillManager.fillReport(report, parameters);
//        return print;
//    }

//   public JasperPrint createReceiptReport(long userId) throws JRException {
//        List<Donation> donations = new ArrayList<>();
//        List<Donation> donations2 = new ArrayList<>();
//        donations2.addAll(resourceService.getByDonationDonorId(userId));
//        donations.add(donations2.get(0));
//        donations.addAll(resourceService.getByDonationDonorId(userId));
//       // donations.addAll(resourceService.getAllDonations());
//        JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(donations);
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("resourcesDataSet3", taskDataSource);
//        parameters.put("Data", nowDate);
//       String filePath = "DonationReport.jrxml";
//       InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filePath);
//
//       if (reportStream == null) {
//           throw new IllegalArgumentException("Plik raportu nie został znaleziony: " + filePath);
//       }
//       JasperReport report = JasperCompileManager.compileReport(filePath);
//       JasperPrint print = JasperFillManager.fillReport(report, parameters, taskDataSource);
//        return print;
//
//    }

    // RAPORT DOTACJI DANEGO DARCZYŃCY
public JasperPrint createReceiptReport(long userId) throws JRException {
    // Pobieranie danych
        List<Donation> donations = new ArrayList<>();
        List<Donation> donations2 = new ArrayList<>();
        donations2.addAll(resourceService.getByDonationDonorId(userId));
        donations.add(donations2.get(0));
        donations.addAll(resourceService.getByDonationDonorId(userId));

    // Utworzenie źródła danych dla raportu
    JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(donations);

    // Przygotowanie parametrów
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("resourcesDataSet3", taskDataSource);
    parameters.put("Data", nowDate);

    // Wczytanie pliku raportu jako zasobu
    String filePath = "reportsModels/DonationReport.jrxml";
    InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filePath);

    if (reportStream == null) {
        throw new IllegalArgumentException("Plik raportu nie został znaleziony: " + filePath);
    }

    // Kompilacja i wypełnienie raportu
    JasperReport report = JasperCompileManager.compileReport(reportStream);
    return JasperFillManager.fillReport(report, parameters, taskDataSource);
}


//    public JasperPrint createResourcesReport() throws JRException {
//        List<Resource> resources = new ArrayList<>();
//        resources.add(resourceService.getResourceById(1111));
//        resources.addAll(resourceService.getAllResources());
//        JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(resources);
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("resourcesDataSet3", taskDataSource);
//        parameters.put("Data", nowDate);
//        String filePath = "C:\\Users\\dawid\\Desktop\\studia-3rok\\io\\projekt2\\System-koordynacji-pomocy-humanitarnej-SKPH--master\\backendIo\\src\\main\\java\\org\\ioad\\spring\\reports\\ResourceReport.jrxml";
//        JasperReport report = JasperCompileManager.compileReport(filePath);
//        JasperPrint print = JasperFillManager.fillReport(report, parameters, taskDataSource);
//        return print;
//
//    }

    // RAPORT ZASOBÓW
public JasperPrint createResourcesReport() throws JRException {
    List<Resource> resources = new ArrayList<>();
    resources.add(resourceService.getResourceById(1111));
    resources.addAll(resourceService.getAllResources());

    JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(resources);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("resourcesDataSet3", taskDataSource);
    parameters.put("Data", nowDate);

    String filePath = "reportsModels/ResourceReport.jrxml";
    InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filePath);

    if (reportStream == null) {
        throw new IllegalArgumentException("Plik raportu nie został znaleziony: " + filePath);
    }

    JasperReport report = JasperCompileManager.compileReport(reportStream);
    return JasperFillManager.fillReport(report, parameters, taskDataSource);
}

//    public JasperPrint createRequestReport() throws JRException {
//        List<Request> requests = new ArrayList<>();
//        requests.add(requestService.getRequestById(1L));
//        requests.addAll(requestService.getAllRequests());
//        JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(requests);
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("resourcesDataSet2", taskDataSource);
//        parameters.put("Data", nowDate);
//        String filePath = "C:\\Users\\dawid\\Desktop\\studia-3rok\\io\\projekt2\\System-koordynacji-pomocy-humanitarnej-SKPH--master\\backendIo\\src\\main\\java\\org\\ioad\\spring\\reports\\RequestReport.jrxml";
//        JasperReport report = JasperCompileManager.compileReport(filePath);
//        JasperPrint print = JasperFillManager.fillReport(report, parameters, taskDataSource);
//        return print;
//
//    }

    // RAPORT REQUEST
public JasperPrint createRequestReport() throws JRException {
    List<Request> requests = new ArrayList<>();
    requests.add(requestService.getRequestById(1L));
    requests.addAll(requestService.getAllRequests());

    JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(requests);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("resourcesDataSet2", taskDataSource);
    parameters.put("Data", nowDate);

    String filePath = "reportsModels/RequestReport.jrxml";
    InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filePath);

    if (reportStream == null) {
        throw new IllegalArgumentException("Plik raportu nie został znaleziony: " + filePath);
    }

    JasperReport report = JasperCompileManager.compileReport(reportStream);
    return JasperFillManager.fillReport(report, parameters, taskDataSource);
}

//    public JasperPrint createHelpActivitiesReport() throws JRException {
//        List<Task> tasks = new ArrayList<>();
//        List<Task> tasks2 = new ArrayList<>();
//        tasks2.addAll(taskRepo.findAll());
//        tasks.add(tasks2.get(0));
//        tasks.addAll(taskRepo.findAll());
//        JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(tasks);
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("resourcesDataSet", taskDataSource);
//        parameters.put("Data", nowDate);
//
//        String filePath = "C:\\Users\\dawid\\Desktop\\studia-3rok\\io\\projekt2\\System-koordynacji-pomocy-humanitarnej-SKPH--master\\backendIo\\src\\main\\java\\org\\ioad\\spring\\reports\\HelpActivitiesReport.jrxml";
//        JasperReport report = JasperCompileManager.compileReport(filePath);
//        JasperPrint print = JasperFillManager.fillReport(report, parameters, taskDataSource);
//        return print;
//
//
//    }

    // RAPORT AKTYWNOŚCI POMOCOWYCH
public JasperPrint createHelpActivitiesReport() throws JRException {
        List<Task> tasks = new ArrayList<>();
        List<Task> tasks2 = new ArrayList<>();
        tasks2.addAll(taskRepo.findAll());
        tasks.add(tasks2.get(0));
        tasks.addAll(taskRepo.findAll());

    JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(tasks);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("resourcesDataSet", taskDataSource);
    parameters.put("Data", nowDate);

    String filePath = "reportsModels/HelpActivitiesReport.jrxml";
    InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filePath);

    if (reportStream == null) {
        throw new IllegalArgumentException("Plik raportu nie został znaleziony: " + filePath);
    }

    JasperReport report = JasperCompileManager.compileReport(reportStream);
    return JasperFillManager.fillReport(report, parameters, taskDataSource);
}
}
