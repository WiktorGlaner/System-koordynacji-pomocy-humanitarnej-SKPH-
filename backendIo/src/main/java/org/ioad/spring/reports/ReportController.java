//package org.ioad.spring.reports;
//
//import net.sf.jasperreports.engine.JRException;
//import org.ioad.spring.task.model.Task;
//import org.ioad.spring.task.repository.TaskRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/report")
//public class ReportController {
//
//    @Autowired
//    private TaskRepo taskRepo;
//    @Autowired
//    private ReportService reportService;
//
//    @CrossOrigin(origins = "http://localhost:5173")  // Zezwól na CORS tylko dla tego endpointu
//    @GetMapping("/generate")
//    public ResponseEntity<byte[]> generateReport() {
//        try {
//            byte[] pdfBytes = reportService.generateReport();
//
//            // Wysyłanie pliku PDF jako odpowiedź
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
//                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
//                    .body(pdfBytes);
//        } catch (JRException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Błąd podczas generowania raportu".getBytes());
//        }
//    }
//}
package org.ioad.spring.reports;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @CrossOrigin(origins = "*")  // Zezwól na CORS tylko dla tego endpointu
    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport(@RequestParam("type") String type, @RequestParam Long userId) {
        try {
            System.out.println(userId);
            // Przekazanie parametru "type" do serwisu, aby wybrał odpowiednią metodę generowania raportu
            byte[] pdfBytes = reportService.generateReport(type, userId);

            // Wysyłanie pliku PDF jako odpowiedź
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdfBytes);
        } catch (JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Błąd podczas generowania raportu".getBytes());
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/preview", produces = "application/pdf")
    public ResponseEntity<byte[]> previewReport(@RequestParam("type") String type, @RequestParam Long userId) {
        try {
            System.out.println(userId);
            byte[] pdfBytes = reportService.generateReport(type, userId);

            // Zwracamy plik PDF do wyświetlenia w przeglądarce (bez wymuszania pobierania)
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdfBytes);
        } catch (JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
