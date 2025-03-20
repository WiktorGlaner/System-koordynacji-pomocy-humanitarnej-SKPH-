**PL**
# System-koordynacji-pomocy-humanitarnej-SKPH-
Ostateczna wersja programu znajduje się w branchu master.
System jest dostępny w języku polskim i angielskim.

**Dokumentacja**  
Dokumentacja składa się z dokumentacji technicznej oraz podręcznika użytkownika.
[📄 Pobierz dokumentacje](System%20koordynacji%20pomocy%20humanitarnej.pdf)


**EN**
# System for Humanitarian Aid Coordination
Finished project is located on master branch.
System supports polish and english language.

**Documentation**  
Documentation consists of technical documentation and a user guide.
[📄 Download documentation](System%20koordynacji%20pomocy%20humanitarnej.pdf) (*polish language only*)

**PL**
# Wymagania systemu:
- Java 21
- Maven
- Vue.js 3
- Spring Boot
- JavaScript
- Node.js, npm
- baza danych PostgreSQL o nazwie: testdbb

username oraz hasło bazy danych należy podmienić w pliku 'application.properties'
inserty ról użytkowników do bazy danych znajdują się w pliku README.md w branchu master

**Uruchomienie systemu:**
1. Włączenie backendu - plik 'TaskBackendApplication':
   
![backend](backend.png)

2. Włączenie frontendu - komenda 'npm run dev' będąc w folderze 'ioFrontend\frontendio'

**EN**

# System Requirements:
- Java 21
- Maven
- Vue.js 3
- Spring Boot
- JavaScript
- Node.js, npm
- PostgreSQL database named: testdbb
The database username and password must be updated in the 'application.properties' file.
User role inserts for the database can be found in the README.md file in the master branch.

**System Startup:**

1. Start the backend - file 'TaskBackendApplication':

![backend](backend.png)

2. Start the frontend - run the command 'npm run dev' while in the folder 'ioFrontend\frontendio'
