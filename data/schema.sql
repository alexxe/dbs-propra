/*PHASE 3 - TRISTAN MERTINS (2423481)*/
/*--PRAGMAS--*/
PRAGMA auto_vacuum = 1;
PRAGMA encoding ="UTF-8";
PRAGMA foreign_keys = 1;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = NORMAL;

/*ENTITAETEN*/
CREATE TABLE Ort (
    Bezeichnung   VARCHAR NOT NULL
                        COLLATE NOCASE
                        CHECK (length(Bezeichnung) > 0 AND
                                Bezeichnung NOT GLOB '*[^ -~]*')
                        PRIMARY KEY,
    Land        VARCHAR NOT NULL
                        CHECK   (length(Land) > 0 AND
                                  Land NOT GLOB '*[^ -~]*')
);

CREATE TABLE Genre (
    Name    VARCHAR NOT NULL
                    COLLATE NOCASE
                    CHECK   (length(Name) > 0 AND
                             Name NOT GLOB '*[^ -~]*')
                    PRIMARY KEY
);

CREATE TABLE Festival (
    ID              INTEGER NOT NULL
                            PRIMARY KEY,
    Bezeichnung     VARCHAR NOT NULL
                            CHECK   (length(Bezeichnung) > 0 AND
                                      Bezeichnung NOT GLOB '*[^ -~]*'),
    Bild            BLOB    NOT NULL
                            CHECK (length(Bild) > 0 AND HEX(Bild) GLOB '89504E470D0A1A0A*'),
    Datum           DATE    NOT NULL
                            CHECK (Datum IS date(Datum)),/* 'YYYY-MM-DD')),*/
    Ort_Bezeichnung    VARCHAR NOT NULL
                            REFERENCES Ort(Bezeichnung)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE
);

CREATE TABLE Buehne (
    ID              INTEGER NOT NULL
                            PRIMARY KEY,
    Bezeichnung         VARCHAR NOT NULL
                                COLLATE NOCASE
                                CHECK   (length(Bezeichnung) > 0 AND
                                        Bezeichnung NOT GLOB '*[^ -~]*'),
    Sitzplaetze         INTEGER NOT NULL
                                CHECK (Sitzplaetze >= 0),
    Stehplaetze         INTEGER NOT NULL
                                CHECK (Stehplaetze >= 0),
    Festival_ID         INTEGER NOT NULL
                                REFERENCES Festival(ID)
);

CREATE TABLE User (
    Mailadresse VARCHAR PRIMARY KEY
                        NOT NULL
                        COLLATE NOCASE
                        CHECK /*(length(Mailadresse) > 0 AND*/
                           (substr(Mailadresse,1,instr(Mailadresse,'@') - 1) NOT GLOB '*[^a-zA-Z0-9]*' AND
                                   substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),1,instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') - 1) NOT GLOB '*[^a-zA-Z0-9]*' AND
                                   substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') + 1) NOT GLOB '*[^a-zA-Z]*')/*)*/
                        CHECK   (length(substr(Mailadresse,1,instr(Mailadresse,'@') - 1))> 0 AND
                                   length(substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),1,instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') - 1)) > 0 AND
                                   length(substr(substr(Mailadresse,instr(Mailadresse,'@') + 1),instr(substr(Mailadresse,instr(Mailadresse,'@') + 1),'.') + 1)) > 0),
    Passwort    VARCHAR NOT NULL
                        CHECK   (length(Passwort) >= 6 AND
                                Passwort NOT GLOB '*[^ -~]*' AND
                                Passwort GLOB '*[A-Z]*' AND
                                Passwort GLOB '*[0-9*0-9]*'),
    Vorname     VARCHAR NOT NULL
                        CHECK   (length(Vorname) > 0 AND
                                   Vorname NOT GLOB '*[^A-Za-z]*' AND
                                   Vorname NOT GLOB '*[^ -~]*'),
    Nachname    VARCHAR NOT NULL
                        CHECK   (length(Nachname) > 0 AND
                                   Nachname NOT GLOB '*[^A-Za-z]*' AND
                                   Nachname NOT GLOB '*[^ -~]*')
);

CREATE TABLE Kuenstler (
    User_Mailadresse    VARCHAR NOT NULL
                                PRIMARY KEY
                                REFERENCES User(Mailadresse)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE,
    /*optionales Attribut*/
    Kuenstlername       VARCHAR CHECK   (length(Kuenstlername) > 0 AND
                                        Kuenstlername NOT GLOB '*[^ -~]*')
);

CREATE TABLE Besucher (
    User_Mailadresse   VARCHAR  NOT NULL
                                PRIMARY KEY
                                REFERENCES User(Mailadresse)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE,
    Geburtsdatum        DATE    NOT NULL
                                CHECK (Geburtsdatum IS date(Geburtsdatum)),/* 'YYYY-MM-DD')),*/
    /*optionales Attribut*/
    Telefonnummer       VARCHAR CHECK(length(Telefonnummer) > 0 AND
                                      Telefonnummer GLOB '+49*' AND substr(Telefonnummer,4) NOT GLOB '*[^0-9]*')
);

CREATE TABLE Veranstalter (
    User_Mailadresse    VARCHAR NOT NULL
                                PRIMARY KEY
                                REFERENCES User(Mailadresse)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE,
    Name                VARCHAR NOT NULL
                                CHECK   (length(Name) > 0 AND
                                        Name NOT GLOB '*[^ -~]*')
);

CREATE TABLE Ticket (
    ID                          INTEGER NOT NULL
                                        PRIMARY KEY,
    Preis                       DOUBLE  NOT NULL
                                        CHECK(Preis >= 0 AND (Preis GLOB '*.[0-9][0-9]' OR Preis GLOB '*.[0-90-9]' )),
    Datum                       DATE    NOT NULL
                                        DEFAULT (datetime('now')),
                                        --CHECK (Datum IS datetime('now') AND Datum = strftime('%Y-%m-%d') AND datetime(Datum) IS Datum),
    VIP_Vermerk                 BOOLEAN NOT NULL
                                        CHECK (VIP_Vermerk in (true, false)),
    Festival_ID                 INTEGER NOT NULL
                                        REFERENCES Festival(ID)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE,
    Besucher_User_Mailadresse   VARCHAR NOT NULL
                                        REFERENCES Besucher(User_Mailadresse)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE,
    UNIQUE (Festival_ID, Besucher_User_Mailadresse)
);

CREATE TABLE Band (
    ID              INTEGER NOT NULL
                            PRIMARY KEY,
    Name            VARCHAR NOT NULL
                            CHECK (length(Name) > 0 AND
                                  Name NOT GLOB '*[^ -~]*'),
    Gruendungsjahr  INTEGER NOT NULL
                            CHECK (Gruendungsjahr >= 0)
);

CREATE TABLE Programmpunkt (
    ID                  INTEGER NOT NULL
                                PRIMARY KEY,
    Uhrzeit             TIME    NOT NULL
                                CHECK (Uhrzeit is time(Uhrzeit)),
    Dauer               INTEGER NOT NULL
                                CHECK  (Dauer in (15, 30, 45, 60, 75, 90, 120)),
    Buehne_ID           INTEGER NOT NULL
                                REFERENCES Buehne(ID)
                                ON UPDATE CASCADE
                                ON DELETE CASCADE,
    Band_ID             INTEGER NOT NULL
                                REFERENCES Band(ID)
);

/*BEZIEHUNGEN*/
CREATE TABLE hat(
   Band_ID                      INTEGER NOT NULL
                                        REFERENCES Band(ID),
   Kuenstler_User_Mailadresse   VARCHAR NOT NULL
                                        REFERENCES Kuenstler(User_Mailadresse)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE,
   PRIMARY KEY (Band_ID, Kuenstler_User_Mailadresse)
);

CREATE TABLE gehoert_zu(
    Genre_Name  VARCHAR NOT NULL
                        REFERENCES Genre(Name)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
    Band_ID     INTEGER NOT NULL
                        REFERENCES Band(ID),
    PRIMARY KEY (Genre_Name, Band_ID)
);

CREATE TABLE organisiert(
    Veranstalter_User_Mailadresse  VARCHAR NOT NULL
                                            REFERENCES Veranstalter(User_Mailadresse)
                                            ON UPDATE CASCADE
                                            ON DELETE CASCADE,
    Festival_ID                     INTEGER NOT NULL
                                            REFERENCES Festival(ID),
    PRIMARY KEY (Veranstalter_User_Mailadresse, Festival_ID)
);

CREATE TABLE kooperiert(
    Veranstalter_User_Mailadresse1  VARCHAR NOT NULL
                                            REFERENCES Veranstalter(User_Mailadresse)
                                            ON UPDATE CASCADE
                                            ON DELETE CASCADE,
    Veranstalter_User_Mailadresse2  VARCHAR NOT NULL
                                            REFERENCES Veranstalter(User_Mailadresse)
                                            ON UPDATE CASCADE
                                            ON DELETE CASCADE,
    PRIMARY KEY (Veranstalter_User_Mailadresse1, Veranstalter_User_Mailadresse2),
    CHECK (Veranstalter_User_Mailadresse1 != Veranstalter_User_Mailadresse2)
);