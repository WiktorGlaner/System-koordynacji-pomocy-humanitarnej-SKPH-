package org.ioad.spring.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.services.IRequestService;
import org.ioad.spring.resource.models.Donation;
import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.models.ResourceType;
import org.ioad.spring.resource.services.ResourceService;
import org.ioad.spring.security.postgresql.AuthService;
import org.ioad.spring.security.postgresql.IAuthService;
import org.ioad.spring.security.postgresql.models.User;
import org.ioad.spring.task.model.Task;
import org.ioad.spring.user.models.UserInfo;
import org.ioad.spring.user.repository.UserInfoRepository;


import org.ioad.spring.task.repository.TaskRepo;
import org.ioad.spring.user.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class Report {
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private IRequestService requestService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

//    Date nowDate = new Date();
    public JasperPrint createTaxReport(long userId) throws JRException {
        Date nowDate = new Date();
        double kwota = resourceService.getSumOfQuantityByTypeAndDonorId(ResourceType.FINANCIAL, userId);
        User user = authService.getUserById(userId);
        String username = user.getUsername();
        Optional<UserInfo> optionalUserInfo = userService.getUser(username);
        UserInfo userInfo = optionalUserInfo.get();
        List<User> users = new ArrayList<>();
        users.add(authService.getUserById(userId));


        // Utworzenie źródła danych dla raportu
        JRBeanCollectionDataSource taskDataSource = new JRBeanCollectionDataSource(users);

        // Przygotowanie parametrów
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("resourcesDataSet3", taskDataSource);
        parameters.put("Data", nowDate);
        parameters.put("kwota", kwota);
        parameters.put("name" , userInfo.getName());
        parameters.put("surname" , userInfo.getSurname());
        parameters.put("pesel" , userInfo.getPesel());


        // Wczytanie pliku raportu jako zasobu
        String filePath = "reportsModels/TaxReport.jrxml";
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream(filePath);

        if (reportStream == null) {
            throw new IllegalArgumentException("Plik raportu nie został znaleziony: " + filePath);
        }

        // Kompilacja i wypełnienie raportu
        JasperReport report = JasperCompileManager.compileReport(reportStream);
        return JasperFillManager.fillReport(report, parameters, taskDataSource);
    }



    // RAPORT DOTACJI DANEGO DARCZYŃCY
public JasperPrint createReceiptReport(long userId) throws JRException {
    // Pobieranie danych
    Date nowDate = new Date();

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




    // RAPORT ZASOBÓW
public JasperPrint createResourcesReport() throws JRException {
    Date nowDate = new Date();

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



    // RAPORT REQUEST
public JasperPrint createRequestReport() throws JRException {
    Date nowDate = new Date();

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



    // RAPORT AKTYWNOŚCI POMOCOWYCH
public JasperPrint createHelpActivitiesReport() throws JRException {
    Date nowDate = new Date();

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
