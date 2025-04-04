/* -- Lagerorte
CREATE TABLE storage_location
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255)
);

-- Munitionstypen
CREATE TABLE ammunition_type
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    caliber VARCHAR(255),
    type    VARCHAR(255)
);

-- Waffen
CREATE TABLE weapon
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type                VARCHAR(255),
    model               VARCHAR(255),
    quantity            INT,
    ammunition_type_id  BIGINT,
    storage_location_id BIGINT,
    FOREIGN KEY (ammunition_type_id) REFERENCES ammunition_type (id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Munitionsbestände
CREATE TABLE ammunition_stock
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ammunition_type_id  BIGINT,
    quantity            INT,
    storage_location_id BIGINT,
    FOREIGN KEY (ammunition_type_id) REFERENCES ammunition_type (id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Getränke
CREATE TABLE drink
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type                VARCHAR(255),
    quantity DOUBLE,
    expiration_date     DATE,
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Essen
CREATE TABLE food
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type                VARCHAR(255),
    quantity DOUBLE,
    expiration_date     DATE,
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Medikamente
CREATE TABLE medication
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255),
    quantity            INT,
    expiration_date     DATE,
    purpose             VARCHAR(255),
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Treibstofftypen
CREATE TABLE fuel_type
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255)
);

-- Treibstoff
CREATE TABLE fuel
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    fuel_type_id        BIGINT,
    quantity DOUBLE,
    storage_location_id BIGINT,
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_type (id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Batterien
CREATE TABLE battery
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type                VARCHAR(255),
    capacity DOUBLE,
    quantity            INT,
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
);

-- Generatoren
CREATE TABLE generator
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type                VARCHAR(255),
    power DOUBLE,
    fuel_type_id        BIGINT,
    status              VARCHAR(255),
    storage_location_id BIGINT,
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_type (id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location (id)
); */

-- Beispieldaten einfügen
INSERT INTO storage_location (id, name, description)
VALUES (1, 'Waffenkammer', 'Lager für Waffen und Munition'),
       (2, 'Vorratsraum', 'Lager für Essen und Getränke'),
       (3, 'Notausgangslager', 'Lager nahe dem Notausgang für schnellen Zugriff'),
       (4, 'Hygienevorrat', 'Raum für Hygieneartikel und Reinigungsmittel'),
       (5, 'Küche', 'Bereich für Lebensmittelzubereitung'),
       (6, 'Archivraum', 'Lager für Dokumente und Karten'),
       (7, 'Trainingsraum', 'Bereich für Fitnessgeräte und Training'),
       (8, 'Waffenarsenal', 'Spezialisierter Raum für Waffenlagerung'),
       (9, 'Kinderbereich', 'Sicherer Bereich für Kinderbedarf'),
       (10, 'Beobachtungsposten', 'Raum für Überwachungsausrüstung'),
       (11, 'Recyclingstation', 'Bereich für Mülltrennung und Wiederverwertung'),
       (12, 'Gartenraum', 'Innenbereich für Pflanzenzucht');

INSERT INTO ammunition_type (id, caliber, type)
VALUES (1, '7.62mm', 'Gewehr'),
       (2, '9mm', 'Pistole'),
       (3, '10mm Auto', 'Pistole'),
       (4, '.44 Magnum', 'Revolver'),
       (5, '5.7x28mm', 'Pistole'),
       (6, '.300 Blackout', 'Sturmgewehr'),
       (7, '16 Gauge', 'Schrotflinte'),
       (8, '6.5 Creedmoor', 'Jagdgewehr'),
       (9, '4.6x30mm', 'Maschinenpistole'),
       (10, '.338 Lapua Magnum', 'Scharfschützengewehr'),
       (11, '.410 Bore', 'Schrotflinte'),
       (12, '7.62x51mm NATO', 'Sturmgewehr');

INSERT INTO weapon (id, type, model, quantity, ammunition_type_id, storage_location_id)
VALUES (1, 'Gewehr', 'AK-47', 5, 1, 1),
       (2, 'Pistole', 'Glock 17', 10, 2, 11),
       (3, 'Pistole', 'Glock 20', 4, 3, 11),
       (4, 'Revolver', 'Ruger Redhawk', 3, 4, 11),
       (5, 'Pistole', 'FN Five-seveN', 5, 5, 11),
       (6, 'Sturmgewehr', 'AAC Honey Badger', 2, 6, 11),
       (7, 'Schrotflinte', 'Mossberg 500', 3, 7, 11),
       (8, 'Jagdgewehr', 'Sako 85', 2, 8, 11),
       (9, 'Maschinenpistole', 'HK MP7', 4, 9, 11),
       (10, 'Scharfschützengewehr', 'Accuracy International AWM', 1, 10, 11),
       (11, 'Schrotflinte', 'Ithaca 37', 3, 11, 11),
       (12, 'Sturmgewehr', 'FN SCAR-H', 2, 12, 11);

INSERT INTO ammunition_stock (id, ammunition_type_id, quantity, storage_location_id)
VALUES (1, 1, 500, 1),
       (2, 2, 1000, 1),
       (3, 3, 300, 7),
       (4, 4, 150, 7),
       (5, 5, 400, 7),
       (6, 6, 200, 7),
       (7, 7, 250, 7),
       (8, 8, 100, 7),
       (9, 9, 500, 7),
       (10, 10, 50, 7),
       (11, 11, 200, 7),
       (12, 12, 300, 7);

INSERT INTO drink (id, type, quantity, expiration_date, storage_location_id)
VALUES (1, 'Wasser', 100.5, '2025-12-31', 2),
       (2, 'Mineralwasser', 150.0, '2028-01-01', 6),
       (3, 'Kakao', 20.0, '2025-12-31', 2),
       (4, 'Fruchtsaft', 40.0, '2025-11-30', 2),
       (5, 'Kräutertee', 15.0, '2026-03-31', 2),
       (6, 'Elektrolytgetränk', 25.0, '2025-10-31', 2),
       (7, 'Sojamilch', 30.0, '2025-09-30', 5), -- Kühlraum
       (8, 'Kombucha', 10.0, '2025-08-31', 2),
       (9, 'Ingwertee', 12.0, '2026-02-28', 2),
       (10, 'Proteinshake', 15.0, '2025-07-31', 2),
       (11, 'Destilliertes Wasser', 100.0, '2029-12-31', 6);

INSERT INTO food (id, type, quantity, expiration_date, storage_location_id)
VALUES (1, 'Konserven', 50.0, '2026-06-30', 2),
       (2, 'Haferflocken', 25.0, '2026-06-30', 2),
       (3, 'Linsen', 20.0, '2027-03-31', 2),
       (4, 'Konserven (Fisch)', 30.0, '2026-05-31', 2),
       (5, 'Honig', 10.0, '2028-12-31', 2),
       (6, 'Trockenmilch', 15.0, '2026-04-30', 2),
       (7, 'Getreideflocken', 20.0, '2025-12-31', 2),
       (8, 'Konserven (Suppe)', 25.0, '2026-02-28', 2),
       (9, 'Olivenöl', 5.0, '2027-01-31', 2),
       (10, 'Trockenbrot', 10.0, '2025-11-30', 2),
       (11, 'Müsli', 15.0, '2025-10-31', 2);

INSERT INTO medication (id, name, quantity, expiration_date, purpose, storage_location_id)
VALUES (1, 'Paracetamol', 200, '2024-12-31', 'Schmerzmittel', 2),
       (2, 'Paracetamol', 150, '2025-06-30', 'Schmerzmittel', 3),
       (3, 'Antidepressiva', 30, '2025-03-31', 'Psychische Gesundheit', 3),
       (4, 'Antidiarrhoika', 40, '2025-02-28', 'Verdauungsprobleme', 3),
       (5, 'Wundverschlussstreifen', 50, '2026-12-31', 'Wundverschluss', 3),
       (6, 'Augentropfen', 20, '2025-01-31', 'Augenpflege', 3),
       (7, 'Hustenmittel', 30, '2025-04-30', 'Atemwegserkrankungen', 3),
       (8, 'Adrenalin', 10, '2024-11-30', 'Allergische Reaktionen', 3),
       (9, 'Betäubungsmittel', 15, '2025-05-31', 'Schwere Schmerzen', 3),
       (10, 'Magnesiumtabletten', 100, '2026-06-30', 'Muskelkrämpfe', 3),
       (11, 'Antiseptische Salbe', 25, '2025-12-31', 'Wundinfektionen', 3);

INSERT INTO fuel_type (id, name)
VALUES (1, 'Strom'),          -- ID 1
       (2, 'Benzin'),         -- ID 2
       (3, 'Diesel'),         -- ID 3
       (4, 'Propangas'),      -- ID 4
       (5, 'Erdgas'),         -- ID 5
       (6, 'Kerosin'),        -- ID 6
       (7, 'Feuerholz'),      -- ID 7
       (8, 'Wasserstoff'),    -- ID 8
       (9, 'Solarenergie'),   -- ID 9
       (10, 'Windenergie'),   -- ID 10
       (11, 'Batterieenergie'), -- ID 11
       (12, 'Heizöl'),        -- ID 12
       (13, 'Kohle'),         -- ID 13
       (14, 'Holzpellets'),   -- ID 14
       (15, 'Lampenöl'),      -- ID 15
       (16, 'Flugzeugkraftstoff'), -- ID 16
       (17, 'Benzin 95'),   -- ID 17
       (18, 'Benzin 98'),   -- ID 18
       (19, 'Winterdiesel'), -- ID 19
       (20, 'Bioethanol'),    -- ID 20
       (21, 'Pflanzenöl'); -- ID 21

INSERT INTO fuel (id, fuel_type_id, quantity, storage_location_id)
VALUES (1, 1, 200.0, 1),  -- Strom
       (2, 12, 80.0, 4),  -- Heizöl
       (3, 13, 50.0, 4),  -- Kohle
       (4, 14, 30.0, 4),  -- Holzpellets
       (5, 15, 15.0, 4),  -- Lampenöl
       (6, 16, 20.0, 4),  -- Flugzeugkraftstoff
       (7, 17, 100.0, 4), -- Benzin 95
       (8, 18, 90.0, 4),  -- Benzin 98
       (9, 19, 70.0, 4),  -- Winterdiesel
       (10, 20, 25.0, 4), -- Bioethanol
       (11, 21, 10.0, 4); -- Pflanzenöl

INSERT INTO battery (id, type, capacity, quantity, storage_location_id)
VALUES (1, 'AA', 1.5, 100, 1),
       (2, 'AA', 1.5, 150, 1),
       (3, 'CR123A', 3.0, 50, 1),
       (4, 'LR44', 1.5, 100, 1),
       (5, '12V SLA', 12.0, 15, 1),
       (6, 'Li-Ion 21700', 3.7, 30, 1),
       (7, 'Alkaline D', 1.5, 40, 1),
       (8, 'NiCd', 1.2, 60, 1),
       (9, 'CR2', 3.0, 20, 1),
       (10, 'Blei-Säure', 6.0, 10, 1),
       (11, 'LR20', 1.5, 25, 1);

INSERT INTO generator (id, type, power, fuel_type_id, status, storage_location_id)
VALUES (1, 'Notstromgenerator', 5.0, 1, 'funktionsfähig', 1),                  -- Strom
       (2, 'Heizgenerator', 8.0, 12, 'funktionsfähig', 5),            -- Heizöl
       (3, 'Kohlegenerator', 5.0, 13, 'in Wartung', 5),               -- Kohle
       (4, 'Pelletgenerator', 4.0, 14, 'funktionsfähig', 5),          -- Holzpellets
       (5, 'Lampenölgenerator', 1.5, 15, 'funktionsfähig', 5),        -- Lampenöl
       (6, 'Flugzeugkraftstoffgenerator', 10.0, 16, 'in Wartung', 5), -- Flugzeugkraftstoff
       (7, 'Benzingenerator 95', 7.0, 17, 'funktionsfähig', 5),       -- Benzin 95
       (8, 'Benzingenerator 98', 7.5, 18, 'funktionsfähig', 5),       -- Benzin 98
       (9, 'Winterdieselgenerator', 6.0, 19, 'in Wartung', 5),        -- Winterdiesel
       (10, 'Bioethanolgenerator', 3.5, 20, 'funktionsfähig', 5),     -- Bioethanol
       (11, 'Pflanzenölgenerator', 2.0, 21, 'funktionsfähig', 5); -- Pflanzenöl

-- Adjust sequences to start AFTER the max ID inserted for each table
-- Find the MAX(id) from your inserts for each table and add 1

-- Example: If max storage_location id inserted was 12:
ALTER SEQUENCE storage_location_SEQ RESTART WITH 13; 
-- Example: If max ammunition_type id inserted was 12:
ALTER SEQUENCE ammunition_type_SEQ RESTART WITH 13;
-- Example: If max weapon id inserted was 12:
ALTER SEQUENCE weapon_SEQ RESTART WITH 13;
-- Example: If max ammunition_stock id inserted was 12:
ALTER SEQUENCE ammunition_stock_SEQ RESTART WITH 13; 
-- Example: If max drink id inserted was 11:
ALTER SEQUENCE drink_SEQ RESTART WITH 12; 
-- Example: If max food id inserted was 11:
ALTER SEQUENCE food_SEQ RESTART WITH 12;
-- Example: If max medication id inserted was 11:
ALTER SEQUENCE medication_SEQ RESTART WITH 12;
-- Example: If max fuel_type id inserted was 21:
ALTER SEQUENCE fuel_type_SEQ RESTART WITH 22; 
-- Example: If max fuel id inserted was 11:
ALTER SEQUENCE fuel_SEQ RESTART WITH 12;
-- Example: If max battery id inserted was 11:
ALTER SEQUENCE battery_SEQ RESTART WITH 12;
-- Example: If max generator id inserted was 11:
ALTER SEQUENCE generator_SEQ RESTART WITH 12; 
