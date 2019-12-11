/*PHASE 3*/

/*Pragmas (gehen auch mehr?)
PRAGMA automatic_index = 1;
PRAGMA case_sensitive_like = 0;
PRAGMA defer_foreign_keys = 0;
PRAGMA ignore_check_constraints = 0;
PRAGMA query_only = 0;
PRAGMA recursive_triggers = 1;
PRAGMA reverse_unordered_selects = 0;
PRAGMA secure_delete = 0;
*/
/*--PRAGMAS--*/
PRAGMA auto_vacuum = 1;
PRAGMA encoding ="UTF-8";
PRAGMA foreign_keys = 1;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;

/*ERZEUGUNG*/
CREATE TABLE User (
    Mailadresse VARCHAR PRIMARY KEY
                        NOT NULL
                        COLLATE NOCASE
                        CHECK   (substr(Mailadresse,1,instr(Mailadresse,'@') - 1) NOT GLOB '*[^a-zA-Z0-9]*' AND
                                substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),1,instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') - 1) NOT GLOB '*[^a-zA-Z0-9]*' AND
                                substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') + 1) NOT GLOB '*[^a-zA-Z]*')
                        CHECK   (length(substr(Mailadresse,1,instr(Mailadresse,'@') - 1))> 0 AND
                                length(substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),1,instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') - 1)) > 0 AND
                                length(substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') + 1)) > 0),
    Land        VARCHAR NOT NULL
                        CHECK   (length(Land) > 0 AND
                                Land NOT GLOB '*[^ -~]*'),
    Passwort    VARCHAR NOT NULL
                        CHECK   (length(Passwort) >= 6 AND
                                Passwort NOT GLOB '*[^ -~]*')
);

CREATE TABLE Entwickler (
    User_Mailadresse    VARCHAR NOT NULL
                                PRIMARY KEY
                                REFERENCES User(Mailadresse)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE,
    Homepage            VARCHAR NOT NULL
                                COLLATE NOCASE
                                CHECK   (length(Homepage) > 0 AND
                                        Homepage NOT GLOB '*[^ -~]*' AND
                                        Homepage GLOB 'http://*' ),
    Studioname          VARCHAR NOT NULL
                                CHECK   (length(Studioname) > 0 AND
                                        Studioname NOT GLOB '*[^ -~]*')
);

CREATE TABLE Kunde (
    User_Mailadresse    VARCHAR NOT NULL
                                PRIMARY KEY
                                REFERENCES User(Mailadresse)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE,
    Benutzername        VARCHAR NOT NULL
                                UNIQUE
                                COLLATE NOCASE
                                CHECK   (length(Benutzername) > 0 AND
                                        Benutzername NOT GLOB '*[^ -~]*'),
    Vorname             VARCHAR NOT NULL
                                CHECK   (length(Vorname) > 0 AND
                                        Vorname NOT GLOB '*[^A-Za-z]*' AND
                                        Vorname NOT GLOB '*[^ -~]*'),
    Nachname            VARCHAR NOT NULL
                                CHECK   (length(Nachname) > 0 AND
                                        Nachname NOT GLOB '*[^A-Za-z]*' AND
                                        Nachname NOT GLOB '*[^ -~]*')
);

CREATE TABLE Produkt (
    ID                          INTEGER NOT NULL
                                        PRIMARY KEY,
    Name                        VARCHAR NOT NULL
                                        CHECK   (length(Name) > 0 AND
                                                Name NOT GLOB '*[^ -~]*'),
    Bild                        BLOB    NOT NULL
                                        CHECK (length(Bild) > 0),
    Beschreibung                TEXT    NOT NULL
                                        CHECK (length(Beschreibung) > 0 AND
                                                Beschreibung NOT GLOB '*[^ -~]*'),
    Datum                       DATE    NOT NULL
                                        CHECK (Datum IS date(Datum)),/* 'YYYY-MM-DD')),*/
    Entwickler_User_Mailadresse VARCHAR NOT NULL
                                        REFERENCES Entwickler(User_Mailadresse)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE
);

CREATE TABLE Spiel (
    Produkt_ID  INTEGER NOT NULL
                        REFERENCES Produkt(ID)
                        PRIMARY KEY,
    FSK         INTEGER NOT NULL
                        CHECK   (FSK = 0 OR
                                FSK = 6 OR
                                FSK = 12 OR
                                FSK = 16 OR
                                FSK = 18)
);

CREATE TABLE Nachfolger_von (
    Spiel_Produkt_ID1   INTEGER NOT NULL
                                REFERENCES Spiel(Produkt_ID)
                                PRIMARY KEY,
    Spiel_Produkt_ID2   INTEGER NOT NULL
                                REFERENCES Spiel(Produkt_ID)
);

CREATE TABLE Kategorie (
    ID              INTEGER NOT NULL
                            PRIMARY KEY,
    Kategorietitel  VARCHAR NOT NULL
                            CHECK   (Kategorietitel = 'Soundtrack' OR
                                    Kategorietitel = 'Addon' OR
                                    Kategorietitel = 'Skinpack')
                            COLLATE NOCASE
);

CREATE TABLE Zusatzinhalt (
    Produkt_ID          INTEGER NOT NULL
                                REFERENCES Produkt(ID)
                                PRIMARY KEY,
    Bezeichnung         VARCHAR NOT NULL
                                CHECK   (length(Bezeichnung) > 0 AND
                                        Bezeichnung NOT GLOB '*[^ -~]*'),
    Kategorie_ID        INTEGER NOT NULL
                                REFERENCES Kategorie(ID),
    Spiel_Produkt_ID    INTEGER NOT NULL
                                REFERENCES Spiel(Produkt_ID)
);

CREATE TABLE Wunschliste (
    ID                      INTEGER NOT NULL
                                    PRIMARY KEY,
    Titel                   VARCHAR NOT NULL
                                    CHECK   (length(Titel) > 0 AND
                                            Titel NOT GLOB '*[^ -~]*'),
    Erstellungsdatum        DATE    NOT NULL
                                    DEFAULT (date ('now')) check (Erstellungsdatum IS date('now')),
    Kunde_User_Mailadresse  VARCHAR NOT NULL
                                    REFERENCES Kunde (User_Mailadresse)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE
);
CREATE TRIGGER maxZahlWunschlisten BEFORE INSERT ON Wunschliste
    WHEN ((SELECT count(Wunschliste.Kunde_User_Mailadresse) FROM Wunschliste WHERE Wunschliste.Kunde_User_Mailadresse = New.Kunde_User_Mailadresse) >= 3)
    BEGIN
    SELECT RAISE (ABORT, 'Die maximale Anzahl an Wunschlisten ist auf 3 beschraenkt.');
END;

CREATE TABLE befreundet_mit (
    Kunde_User_Mailadresse1 VARCHAR NOT NULL
                                    REFERENCES Kunde (User_Mailadresse)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE,
    Kunde_User_Mailadresse2 VARCHAR NOT NULL
                                    REFERENCES Kunde (User_Mailadresse)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE,
    PRIMARY KEY (
        Kunde_User_Mailadresse1,
        Kunde_User_Mailadresse2
    ),
    CHECK (Kunde_User_Mailadresse1 != Kunde_User_Mailadresse2)
);

CREATE TABLE besitzt (
    Kunde_User_Mailadresse  VARCHAR NOT NULL
                                    REFERENCES Kunde(User_Mailadresse)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE,
    Produkt_ID              INTEGER NOT NULL
                                    REFERENCES Produkt(ID),
    Gutscheincode           VARCHAR NOT NULL
                                    UNIQUE
                                    CHECK (length(Gutscheincode) = 10 AND
                                           Gutscheincode GLOB '*[A-Z]*' AND
                                           Gutscheincode GLOB '*[0-9]*' AND
                                           Gutscheincode NOT GLOB '*[^ -~]*'),
    PRIMARY KEY (
        Kunde_User_Mailadresse,
        Produkt_ID
    )
);

CREATE TABLE bewertet (
    Kunde_User_Mailadresse  VARCHAR NOT NULL
                                    REFERENCES Kunde(User_Mailadresse)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE,
    Produkt_ID              INTEGER NOT NULL
                                    REFERENCES Produkt(ID),
    Schulnote               INTEGER NOT NULL
                                    CHECK (Schulnote >= 1 AND Schulnote <= 6),
    /*optionales Attribut*/
    Bewertungstext          TEXT    CHECK (length(Bewertungstext) > 0 AND
                                           Bewertungstext NOT GLOB '*[^ -~]*'),
    PRIMARY KEY (
        Kunde_User_Mailadresse,
        Produkt_ID
    )
);
CREATE TRIGGER BewertungNurNachKauf BEFORE INSERT ON bewertet
    WHEN (New.Produkt_ID NOT IN(SELECT Produkt_ID FROM besitzt WHERE Kunde_User_Mailadresse = New.Kunde_User_Mailadresse))
BEGIN
    SELECT RAISE (ABORT, 'Es ist ein Fehler bei der Bewertung unterlaufen. Das Spiel muss zuvor vom Kunden gekauft werden.');
END;

CREATE TABLE enthaelt (
    Wunschliste_ID  INTEGER NOT NULL
                            REFERENCES Wunschliste(ID),
    Produkt_ID      INTEGER NOT NULL
                            REFERENCES Produkt(ID),
    PRIMARY KEY (
        Wunschliste_ID,
        Produkt_ID
    )
);
