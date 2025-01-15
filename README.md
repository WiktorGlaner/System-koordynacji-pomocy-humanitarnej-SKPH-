# System-koordynacji-pomocy-humanitarnej-SKPH-

DB credentials:
backendIo\src\main\resources\application.properties

żeby działało najlepiej usunąć starą bazę i postawić na nowo
po uruchomieniu springa jak już się wygenerują tabele to trzeba te inserty
```sql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
INSERT INTO roles(name) VALUES('ROLE_VICTIM');
INSERT INTO roles(name) VALUES('ROLE_DONOR');
INSERT INTO roles(name) VALUES('ROLE_VOLUNTEER');
INSERT INTO roles(name) VALUES('ROLE_ORGANIZATION');
INSERT INTO roles(name) VALUES('ROLE_AUTHORITY');
```
