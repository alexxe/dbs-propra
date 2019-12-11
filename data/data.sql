/*BEFUELLUNG der DB mit Beispieleintraegen:*/
/*Relationen sollten stimmen, wenn ID immer ab 1 gezaehlt wird.*/

/*Daten in Tabelle User einfuegen*/
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('alfredausgedacht@freenet.nl', 'Niederlande', 'alfredo');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('borisbeispiel@aol.de', 'Deutschland', 'passwortboris');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('maxmustermann@web.de', 'Deutschland', 'pwmustermann');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('johndoe@gmx.de', 'USA', 'passwortjohn');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('hans@gmail.com', 'Schweiz', 'Passwort1');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('tonystark@stark.com', 'USA', 'Passwort');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('steverogers@avengers.com', 'USA', 'captain1234');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('brucebanner@hulk.com', 'USA', 'bruceistcool');
INSERT INTO User (Mailadresse, Land, Passwort) VALUES ('clintbarton@hawkeye.com', 'USA', 'passhawk');

/*Daten in Tabelle Entwickler einfuegen*/
INSERT INTO Entwickler (User_Mailadresse, Homepage, Studioname) VALUES ('tonystark@stark.com', 'http://stark-industries.com', 'StarkIndustries');
INSERT INTO Entwickler (User_Mailadresse, Homepage, Studioname) VALUES ('steverogers@avengers.com', 'http://captainamaerica.com', 'CA-Productions');
INSERT INTO Entwickler (User_Mailadresse, Homepage, Studioname) VALUES ('brucebanner@hulk.com', 'http://hulk.org', 'ScienceRocks GmbH');
INSERT INTO Entwickler (User_Mailadresse, Homepage, Studioname) VALUES ('clintbarton@hawkeye.com', 'http://hawkeye.com', 'Hawkeye Corporation');

/*Daten in Tabelle Kunde einfuegen*/
INSERT INTO Kunde (User_Mailadresse, Benutzername, Vorname, Nachname) VALUES ('alfredausgedacht@freenet.nl', 'Alfred12', 'Alfred', 'Ausgedacht');
INSERT INTO Kunde (User_Mailadresse, Benutzername, Vorname, Nachname) VALUES ('borisbeispiel@aol.de', 'Boris00', 'Boris', 'Beispiel');
INSERT INTO Kunde (User_Mailadresse, Benutzername, Vorname, Nachname) VALUES ('maxmustermann@web.de', 'Maxiking', 'Max', 'Mustermann');
INSERT INTO Kunde (User_Mailadresse, Benutzername, Vorname, Nachname) VALUES ('johndoe@gmx.de', 'Johnniboy', 'John', 'Doe');
INSERT INTO Kunde (User_Mailadresse, Benutzername, Vorname, Nachname) VALUES ('hans@gmail.com', 'Hanzz', 'Hans', 'Wurst');

/*Daten in Tabelle Produkt einfuegen*/
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Shoot', readfile('bild.png'), 'Ego-Shooter mit atemberaubender Grafik', '2018-02-12', 'tonystark@stark.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Tower Defense', readfile('bild.png'), 'Simples Strategiespiel', '2017-07-01', 'clintbarton@hawkeye.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Tower Defense Part 2', readfile('bild.png'), 'Teil 2 des erfolgreichen Strategiespiels mit mehr Gewalt', '2019-07-01', 'clintbarton@hawkeye.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Ball Kicker 2018', readfile('bild.png'), 'Das Sportspiel fuer die ganze Familie', '2018-01-01', 'steverogers@avengers.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Questionmaster', readfile('bild.png'), 'Raetselspiel fuer Jung und Alt', '2016-12-10', 'brucebanner@hulk.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Ball Kicker 2019', readfile('bild.png'), 'Das neue Sportspiel fuer die ganze Familie', '2019-01-01', 'steverogers@avengers.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Nebenquest-Addon', readfile('bild.png'), 'Die lang erwarteten Nebenquests', '2018-04-03', 'tonystark@stark.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Skinpack1', readfile('bild.png'), 'Mehrere Skins', '2019-05-02', 'steverogers@avengers.com');
INSERT INTO Produkt (Name, Bild, Beschreibung, Datum, Entwickler_User_Mailadresse) VALUES ('Soundtrack Gefahr', readfile('bild.png'), 'Gefaehrliche Situationen werden ab sofort noch gruseliger', '2019-03-05', 'tonystark@stark.com');

/*Daten in Tabelle Spiel einfuegen*/
INSERT INTO Spiel (Produkt_ID, FSK) VALUES (1, 18);
INSERT INTO Spiel (Produkt_ID, FSK) VALUES (2, 12);
INSERT INTO Spiel (Produkt_ID, FSK) VALUES (3, 16);
INSERT INTO Spiel (Produkt_ID, FSK) VALUES (4, 0);
INSERT INTO Spiel (Produkt_ID, FSK) VALUES (5, 6);
INSERT INTO Spiel (Produkt_ID, FSK) VALUES (6, 0);

/*Daten in Tabelle Nachfolger_von einfuegen*/
INSERT INTO Nachfolger_von (Spiel_Produkt_ID1, Spiel_Produkt_ID2) VALUES (3, 2);
INSERT INTO Nachfolger_von (Spiel_Produkt_ID1, Spiel_Produkt_ID2) VALUES (6, 4);

/*Daten in Tabelle Kategorie einfuegen*/
INSERT INTO Kategorie (Kategorietitel) VALUES ('Addon');
INSERT INTO Kategorie (Kategorietitel) VALUES ('Skinpack');
INSERT INTO Kategorie (Kategorietitel) VALUES ('Soundtrack');

/*Daten in Tabelle Zusatzinhalt einfuegen*/
INSERT INTO Zusatzinhalt (Produkt_ID, Bezeichnung, Kategorie_ID, Spiel_Produkt_ID) VALUES (7, 'Nebenquests', 1, 1);
INSERT INTO Zusatzinhalt (Produkt_ID, Bezeichnung, Kategorie_ID, Spiel_Produkt_ID) VALUES (8, 'Neue Skins', 2, 6);
INSERT INTO Zusatzinhalt (Produkt_ID, Bezeichnung, Kategorie_ID, Spiel_Produkt_ID) VALUES (9, 'Gruseliger Soundtrack fuer Gefahrensitutationen im Game', 3, 1);

/*Daten in Tabelle Wunschliste einfuegen*/
INSERT INTO Wunschliste (Titel, Kunde_User_Mailadresse) VALUES ('Christmas', 'hans@gmail.com');
INSERT INTO Wunschliste (Titel, Kunde_User_Mailadresse) VALUES ('Weihnachten', 'johndoe@gmx.de');
INSERT INTO Wunschliste (Titel, Kunde_User_Mailadresse) VALUES ('Ostern', 'maxmustermann@web.de');
INSERT INTO Wunschliste (Titel, Kunde_User_Mailadresse) VALUES ('interessant', 'hans@gmail.com');
INSERT INTO Wunschliste (Titel, Kunde_User_Mailadresse) VALUES ('Sportspiele', 'hans@gmail.com');

/*Daten in Tabelle befreundet_mit einfuegen*/
INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('borisbeispiel@aol.de', 'alfredausgedacht@freenet.nl');
/*INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('alfredausgedacht@freenet.nl', 'borisbeispiel@aol.de');*/
INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('maxmustermann@web.de', 'hans@gmail.com');
/*INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('hans@gmail.com', 'maxmustermann@web.de');*/
INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('maxmustermann@web.de', 'borisbeispiel@aol.de');
/*INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('borisbeispiel@aol.de', 'maxmustermann@web.de');*/

/*Daten in Tabelle besitzt einfuegen*/
INSERT INTO besitzt (Kunde_User_Mailadresse, Produkt_ID, Gutscheincode) VALUES ('borisbeispiel@aol.de', 2, 'Gutschein1');
INSERT INTO besitzt (Kunde_User_Mailadresse, Produkt_ID, Gutscheincode) VALUES ('borisbeispiel@aol.de', 3, 'Gutschein2');
INSERT INTO besitzt (Kunde_User_Mailadresse, Produkt_ID, Gutscheincode) VALUES ('alfredausgedacht@freenet.nl', 1, 'Gutschein3');
INSERT INTO besitzt (Kunde_User_Mailadresse, Produkt_ID, Gutscheincode) VALUES ('johndoe@gmx.de', 2, 'Gutschein4');

/*Daten in Tabelle bewertet einfuegen*/
INSERT INTO bewertet (Kunde_User_Mailadresse, Produkt_ID, Schulnote, Bewertungstext) VALUES ('borisbeispiel@aol.de', 2, 1, 'Richtig gut gemacht.');
INSERT INTO bewertet (Kunde_User_Mailadresse, Produkt_ID, Schulnote, Bewertungstext) VALUES ('borisbeispiel@aol.de', 3, 4, 'Kommt leider nicht an seinen Vorgaenger ran.');
INSERT INTO bewertet (Kunde_User_Mailadresse, Produkt_ID, Schulnote) VALUES ('alfredausgedacht@freenet.nl', 1, 2);

/*Daten in Tabelle enthaelt einfuegen*/
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (1, 5);
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (2, 6);
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (3, 1);
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (4, 2);
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (4, 3);
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (5, 4);
INSERT INTO enthaelt (Wunschliste_ID, Produkt_ID) VALUES (5, 6);


/*TESTEN, OB TRIGGER UND CO GREIFEN
INSERT INTO befreundet_mit (Kunde_User_Mailadresse1, Kunde_User_Mailadresse2) VALUES ('borisbeispiel@aol.de', 'borisbeispiel@aol.de');
INSERT INTO Wunschliste (Titel, Kunde_User_Mailadresse) VALUES ('Kaufen', 'hans@gmail.com');
 */
