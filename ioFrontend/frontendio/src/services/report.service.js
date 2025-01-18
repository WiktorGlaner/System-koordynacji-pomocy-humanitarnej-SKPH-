// import axios from "axios";
// import authHeader from "./auth-header";
//
// const API_URL = "http://localhost:8080/";
//
// class ReportService {
//     // Metoda do generowania i pobierania raportu
//     generateReport(reportType) {
//         return axios.get(API_URL + "report/generate?type=" + reportType , {
//             headers: authHeader(),
//             responseType: "arraybuffer",  // Oczekujemy odpowiedzi w postaci pliku (np. PDF)
//         })
//             .then(response => {
//                 // Tworzymy Blob z otrzymanych danych i otwieramy go w nowym oknie przeglądarki
//                 const blob = new Blob([response.data], { type: "application/pdf" });
//                 const url = window.URL.createObjectURL(blob);
//                 const link = document.createElement("a");
//                 link.href = url;
//                 link.download = "report.pdf";  // Nazwa pliku, który ma być pobrany
//                 link.click();  // Symulujemy kliknięcie w link, aby rozpocząć pobieranie
//                 window.URL.revokeObjectURL(url);  // Usuwamy URL po pobraniu pliku
//             })
//             .catch(error => {
//                 console.error("Błąd podczas pobierania raportu:", error);
//                 alert("Błąd podczas generowania raportu.");
//             });
//     }
// }
//
// export default new ReportService();
import axios from "axios";
import authHeader from "./auth-header";

const API_URL = "http://localhost:8080/";

class ReportService {
    // Pobierz PDF do pobrania
    generateReport(reportType, userId) {
        return axios.get(`${API_URL}report/generate`, {
            headers: authHeader(),
            responseType: "arraybuffer", // Pobieramy raport jako plik
            params: { type: reportType,
                      userId: userId,},
        })
            .then((response) => {
                const blob = new Blob([response.data], { type: "application/pdf" });
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement("a");
                link.href = url;
                link.download = "report.pdf";
                link.click();
                window.URL.revokeObjectURL(url); // Usuwamy URL po pobraniu
            })
            .catch((error) => {
                console.error("Błąd podczas pobierania raportu:", error);
                throw error;
            });
    }

    // Podgląd PDF w iframe
    previewReport(reportType, userId) {
        return axios.get(`${API_URL}report/preview`, {
            headers: authHeader(),
            responseType: "blob", // Odbieramy raport jako blob
            params: { type: reportType,
                userId: userId,},
        })
            .then((response) => {
                const blob = new Blob([response.data], { type: "application/pdf" });
                return window.URL.createObjectURL(blob); // Zwracamy URL do wyświetlenia w iframe
            })
            .catch((error) => {
                console.error("Błąd podczas podglądu raportu:", error);
                throw error;
            });
    }
}

export default new ReportService();