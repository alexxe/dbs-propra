/*BEFUELLUNG DER DB mit BEISPIELEINTRAEGEN
  Relationen sollten stimmen, wenn ID immer ab 1 gezaehlt wird.*/

--Daten in Tabelle ORT eintragen
INSERT INTO Ort (Bezeichnung, Land) VALUES ('Dortmund','Deutschland');
INSERT INTO Ort (Bezeichnung, Land) VALUES ('Graefenhainichen','Deutschland');
INSERT INTO Ort (Bezeichnung, Land) VALUES ('Nuerburg','Deutschland');
INSERT INTO Ort (Bezeichnung, Land) VALUES ('Indio','USA');

--Daten in Tabelle GENRE eintragen
INSERT INTO Genre (Name) VALUES ('Rock');
INSERT INTO Genre (Name) VALUES ('Punkrock');
INSERT INTO Genre (Name) VALUES ('Pop');
INSERT INTO Genre (Name) VALUES ('Hiphop');
INSERT INTO Genre (Name) VALUES ('Blues');
INSERT INTO Genre (Name) VALUES ('Electropop');

--Daten in Tabelle FESTIVAL eintragen
INSERT INTO Festival (Bezeichnung, Bild, Datum, Ort_ID) VALUES ('Splash', readfile('bild.png'), '2020-07-10', 2);
INSERT INTO Festival (Bezeichnung, Bild, Datum, Ort_ID) VALUES ('Juicy Beats', readfile('bild.png'), '2020-07-24', 1);
INSERT INTO Festival (Bezeichnung, Bild, Datum, Ort_ID) VALUES ('Rock am Ring', readfile('bild.png'), '2019-06-05', 3);
INSERT INTO Festival (Bezeichnung, Bild, Datum, Ort_ID) VALUES ('Coachella', readfile('bild.png'), '2019-04-10', 4);

--Daten in Tabelle BUEHNE eintragen
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Backyard', 50, 400, 1);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Playground', 40, 350, 1);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Snipes Mainstage', 20, 900, 1);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Juicy Stage', 90, 2000, 2);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Konzerthaus Stage', 15, 400, 2);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Buehne', 300, 5000, 3);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Plattform', 100, 1200, 3);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Sonora', 400, 10000, 4);
INSERT INTO Buehne (Bezeichnung, Sitzplaetze, Stehplaetze, Festival_ID) VALUES ('Sahara', 500, 8000,4);

--Daten in Tabelle USER eintragen
--davon Kuenstler
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('mickjagger@roll.com', 'CBH6clint', 'Michael', 'Jagger');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('keithrichards@roll.com', 'Richyk1', 'Keith', 'Richards');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('kopplins@sdp.de', 'Pass4steve', 'Dag', 'Kopplin');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('koljah@ag.de', 'Kennwort2hulk', 'Kolja', 'Podkowik');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('pittner@deichkind.de', 'Tony7pw', 'Malte', 'Pittner');
--davon Besucher
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('maxmustermann@web.de', '0pwMustermann', 'Max', 'Mustermann');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('johndoe@gmx.de', 'J0hnnyboy', 'John', 'Doe');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('berndbeispiel@aol.de', 'Passwbernd1', 'Bernd', 'Beispiel');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('hans@gmail.com', 'Hans3n', 'Hans', 'Wurst');
--davon Veranstalter
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('alfredausgedacht@freenet.nl', 'Alfred0', 'Alfred', 'Ausgedacht');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('musterfraumel@mail.de', 'Abcdef123', 'Melanie', 'Musterfrau');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('thomas@chbr.com', 'Triplet3', 'Thomas', 'Tollpatsch');
INSERT INTO User (Mailadresse, Passwort, Vorname, Nachname) VALUES ('booking@bohlen.de', 'Diedah4', 'Dieter', 'Bohlen');

--Daten in Tabelle KUENSTLER eintragen
INSERT INTO Kuenstler (User_Mailadresse,Kuenstlername) VALUES ('mickjagger@roll.com', 'Mick Jagger');
INSERT INTO Kuenstler (User_Mailadresse) VALUES ('keithrichards@roll.com');
INSERT INTO Kuenstler (User_Mailadresse) VALUES ('kopplins@sdp.de');
INSERT INTO Kuenstler (User_Mailadresse, Kuenstlername) VALUES ('koljah@ag.de', 'Koljah');
INSERT INTO Kuenstler (User_Mailadresse) VALUES ('pittner@deichkind.de');

--Daten in Tabelle BESUCHER eintragen
INSERT INTO Besucher (User_Mailadresse,Geburtsdatum, Telefonnummer) VALUES ('berndbeispiel@aol.de', '1995-04-07','+4915134497618');
INSERT INTO Besucher (User_Mailadresse,Geburtsdatum) VALUES ('maxmustermann@web.de', '1984-03-16');
INSERT INTO Besucher (User_Mailadresse,Geburtsdatum) VALUES ('johndoe@gmx.de', '1993-10-29');
INSERT INTO Besucher (User_Mailadresse,Geburtsdatum, Telefonnummer) VALUES ('hans@gmail.com', '1983-02-09', '+491733569531');

--Daten in Tabelle VERANSTALTER eintragen
INSERT INTO Veranstalter (User_Mailadresse, Name) VALUES ('alfredausgedacht@freenet.nl', 'Aus-Music');
INSERT INTO Veranstalter (User_Mailadresse, Name) VALUES ('musterfraumel@mail.de', 'Delight');
INSERT INTO Veranstalter (User_Mailadresse, Name) VALUES ('thomas@chbr.com', 'Chartbreaker');
INSERT INTO Veranstalter (User_Mailadresse, Name) VALUES ('booking@bohlen.de', 'Bohlenholen');

--Daten in Tabelle TICKET eintragen
INSERT INTO Ticket (Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) VALUES ('249.95', '2020-07-10 12:30:00', true, 1, 'johndoe@gmx.de');
INSERT INTO Ticket (Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) VALUES ('190.20', '2020-07-24', true, 2, 'johndoe@gmx.de');
INSERT INTO Ticket (Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) VALUES ('90.20', '2020-07-24', false, 2, 'maxmustermann@web.de');
INSERT INTO Ticket (Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) VALUES ('90.20', '2020-07-24', false, 2, 'hans@gmail.com');
INSERT INTO Ticket (Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) VALUES ('52.00', '2019-06-05 15:00:00', false, 3, 'hans@gmail.com');
INSERT INTO Ticket (Preis, Datum, VIP_Vermerk, Festival_ID, Besucher_User_Mailadresse) VALUES ('312.30', '2019-04-10', false, 4, 'hans@gmail.com');

--Daten in Tabelle BAND eintragen
INSERT INTO Band (Name, Gruendungsjahr) VALUES ('Rolling Stones', 1962);
INSERT INTO Band (Name, Gruendungsjahr) VALUES ('SDP', 1999);
INSERT INTO Band (Name, Gruendungsjahr) VALUES ('Deichkind', 1997);
INSERT INTO Band (Name, Gruendungsjahr) VALUES ('Antilopengang', 2009);

--Daten in Tabelle PROGRAMMPUNKT eintragen
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('15:00:00', 90, 6, 1);
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('18:15:00', 15, 7, 1);
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('20:00:00', 120, 9, 1);
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('17:15:00', 60, 4, 2);
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('18:45:00', 45, 4, 3);
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('17:20:00', 75, 5, 4);
INSERT INTO Programmpunkt (Uhrzeit, Dauer, Buehne_ID, Band_ID) VALUES ('19:00:00', 30, 7, 2);

--Daten in Tabelle HAT eintragen
INSERT INTO hat (Band_ID, Kuenstler_User_Mailadresse) VALUES (1, 'mickjagger@roll.com');
INSERT INTO hat (Band_ID, Kuenstler_User_Mailadresse) VALUES (1, 'keithrichards@roll.com');
INSERT INTO hat (Band_ID, Kuenstler_User_Mailadresse) VALUES (2, 'kopplins@sdp.de');
INSERT INTO hat (Band_ID, Kuenstler_User_Mailadresse) VALUES (3, 'pittner@deichkind.de');
INSERT INTO hat (Band_ID, Kuenstler_User_Mailadresse) VALUES (4, 'koljah@ag.de');

--Daten in Tabelle GEHOERT_ZU eintragen
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (1, 1);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (5, 1);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (3, 2);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (4, 2);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (1, 2);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (4, 3);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (6, 3);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (4, 4);
INSERT INTO gehoert_zu (Genre_ID, Band_ID) VALUES (2, 4);

--Daten in Tabelle ORGANISIERT eintragen
INSERT INTO organisiert (Veranstalter_User_Mailadresse, Festival_ID) VALUES ('alfredausgedacht@freenet.nl', 1);
INSERT INTO organisiert (Veranstalter_User_Mailadresse, Festival_ID) VALUES ('musterfraumel@mail.de', 2);
INSERT INTO organisiert (Veranstalter_User_Mailadresse, Festival_ID) VALUES ('thomas@chbr.com', 3);
INSERT INTO organisiert (Veranstalter_User_Mailadresse, Festival_ID) VALUES ('thomas@chbr.com', 4);

--Daten in Tabelle KOOPERIERT eintragen
INSERT INTO kooperiert (Veranstalter_User_Mailadresse1, Veranstalter_User_Mailadresse2) VALUES ('musterfraumel@mail.de', 'thomas@chbr.com');
INSERT INTO kooperiert (Veranstalter_User_Mailadresse1, Veranstalter_User_Mailadresse2) VALUES ('booking@bohlen.de', 'thomas@chbr.com');

