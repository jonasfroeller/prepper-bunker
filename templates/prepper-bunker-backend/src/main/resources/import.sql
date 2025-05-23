-- Lagerorte
CREATE TABLE storage_location (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
);

-- Munitionstypen
CREATE TABLE ammunition_type (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    caliber VARCHAR(255),
    type VARCHAR(255)
);

-- Waffen
CREATE TABLE weapon (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type VARCHAR(255),
    model VARCHAR(255),
    quantity INT,
    ammunition_type_id BIGINT,
    storage_location_id BIGINT,
    FOREIGN KEY (ammunition_type_id) REFERENCES ammunition_type(id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Munitionsbestände
CREATE TABLE ammunition_stock (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ammunition_type_id BIGINT,
    quantity INT,
    storage_location_id BIGINT,
    FOREIGN KEY (ammunition_type_id) REFERENCES ammunition_type(id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Getränke
CREATE TABLE drink (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type VARCHAR(255),
    quantity DOUBLE,
    expiration_date DATE,
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Essen
CREATE TABLE food (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type VARCHAR(255),
    quantity DOUBLE,
    expiration_date DATE,
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Medikamente
CREATE TABLE medication (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255),
    quantity INT,
    expiration_date DATE,
    purpose VARCHAR(255),
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Treibstofftypen
CREATE TABLE fuel_type (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255)
);

-- Treibstoff
CREATE TABLE fuel (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    fuel_type_id BIGINT,
    quantity DOUBLE,
    storage_location_id BIGINT,
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_type(id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Batterien
CREATE TABLE battery (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type VARCHAR(255),
    capacity DOUBLE,
    quantity INT,
    storage_location_id BIGINT,
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Generatoren
CREATE TABLE generator (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    type VARCHAR(255),
    power DOUBLE,
    fuel_type_id BIGINT,
    status VARCHAR(255),
    storage_location_id BIGINT,
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_type(id),
    FOREIGN KEY (storage_location_id) REFERENCES storage_location(id)
);

-- Beispieldaten einfügen
INSERT INTO storage_location (name, description) VALUES
    ('Waffenkammer', 'Lager für Waffen und Munition'),
    ('Vorratsraum', 'Lager für Essen und Getränke'),
    ('Notausgangslager', 'Lager nahe dem Notausgang für schnellen Zugriff'),
    ('Hygienevorrat', 'Raum für Hygieneartikel und Reinigungsmittel'),
    ('Küche', 'Bereich für Lebensmittelzubereitung'),
    ('Archivraum', 'Lager für Dokumente und Karten'),
    ('Trainingsraum', 'Bereich für Fitnessgeräte und Training'),
    ('Waffenarsenal', 'Spezialisierter Raum für Waffenlagerung'),
    ('Kinderbereich', 'Sicherer Bereich für Kinderbedarf'),
    ('Beobachtungsposten', 'Raum für Überwachungsausrüstung'),
    ('Recyclingstation', 'Bereich für Mülltrennung und Wiederverwertung'),
    ('Gartenraum', 'Innenbereich für Pflanzenzucht');

INSERT INTO ammunition_type (caliber, type) VALUES
    ('7.62mm', 'Gewehr'),
    ('9mm', 'Pistole'),
    ('10mm Auto', 'Pistole'),
    ('.44 Magnum', 'Revolver'),
    ('5.7x28mm', 'Pistole'),
    ('.300 Blackout', 'Sturmgewehr'),
    ('16 Gauge', 'Schrotflinte'),
    ('6.5 Creedmoor', 'Jagdgewehr'),
    ('4.6x30mm', 'Maschinenpistole'),
    ('.338 Lapua Magnum', 'Scharfschützengewehr'),
    ('.410 Bore', 'Schrotflinte'),
    ('7.62x51mm NATO', 'Sturmgewehr');

INSERT INTO weapon (type, model, quantity, ammunition_type_id, storage_location_id) VALUES
    ('Gewehr', 'AK-47', 5, 1, 1), -- 7.62mm
    ('Pistole', 'Glock 17', 10, 2, 1), -- 9mm
    ('Pistole', 'Glock 20', 4, 13, 11),  -- 10mm Auto
    ('Revolver', 'Ruger Redhawk', 3, 14, 11),  -- .44 Magnum
    ('Pistole', 'FN Five-seveN', 5, 15, 11),  -- 5.7x28mm
    ('Sturmgewehr', 'AAC Honey Badger', 2, 16, 11),  -- .300 Blackout
    ('Schrotflinte', 'Mossberg 500', 3, 17, 11),  -- 16 Gauge
    ('Jagdgewehr', 'Sako 85', 2, 18, 11),  -- 6.5 Creedmoor
    ('Maschinenpistole', 'HK MP7', 4, 19, 11),  -- 4.6x30mm
    ('Scharfschützengewehr', 'Accuracy International AWM', 1, 20, 11),  -- .338 Lapua Magnum
    ('Schrotflinte', 'Ithaca 37', 3, 21, 11),  -- .410 Bore
    ('Sturmgewehr', 'FN SCAR-H', 2, 22, 11);  -- 7.62x51mm NATO

INSERT INTO ammunition_stock (ammunition_type_id, quantity, storage_location_id) VALUES
    (1, 500, 1), -- 7.62mm
    (2, 1000, 1), -- 9mm
    (13, 300, 7),  -- 10mm Auto
    (14, 150, 7),  -- .44 Magnum
    (15, 400, 7),  -- 5.7x28mm
    (16, 200, 7),  -- .300 Blackout
    (17, 250, 7),  -- 16 Gauge
    (18, 100, 7),  -- 6.5 Creedmoor
    (19, 500, 7),  -- 4.6x30mm
    (20, 50, 7),  -- .338 Lapua Magnum
    (21, 200, 7),  -- .410 Bore
    (22, 300, 7);  -- 7.62x51mm NATO

INSERT INTO drink (type, quantity, expiration_date, storage_location_id) VALUES
    ('Wasser', 100.5, '2025-12-31', 2),
    ('Mineralwasser', 150.0, '2028-01-01', 6),
    ('Kakao', 20.0, '2025-12-31', 2),
    ('Fruchtsaft', 40.0, '2025-11-30', 2),
    ('Kräutertee', 15.0, '2026-03-31', 2),
    ('Elektrolytgetränk', 25.0, '2025-10-31', 2),
    ('Sojamilch', 30.0, '2025-09-30', 5),  -- Kühlraum
    ('Kombucha', 10.0, '2025-08-31', 2),
    ('Ingwertee', 12.0, '2026-02-28', 2),
    ('Proteinshake', 15.0, '2025-07-31', 2),
    ('Destilliertes Wasser', 100.0, '2029-12-31', 6);

INSERT INTO food (type, quantity, expiration_date, storage_location_id) VALUES
    ('Konserven', 50.0, '2026-06-30', 2),
    ('Haferflocken', 25.0, '2026-06-30', 2),
    ('Linsen', 20.0, '2027-03-31', 2),
    ('Konserven (Fisch)', 30.0, '2026-05-31', 2),
    ('Honig', 10.0, '2028-12-31', 2),
    ('Trockenmilch', 15.0, '2026-04-30', 2),
    ('Getreideflocken', 20.0, '2025-12-31', 2),
    ('Konserven (Suppe)', 25.0, '2026-02-28', 2),
    ('Olivenöl', 5.0, '2027-01-31', 2),
    ('Trockenbrot', 10.0, '2025-11-30', 2),
    ('Müsli', 15.0, '2025-10-31', 2);

INSERT INTO medication (name, quantity, expiration_date, purpose, storage_location_id) VALUES
    ('Paracetamol', 200, '2024-12-31', 'Schmerzmittel', 2),
    ('Paracetamol', 150, '2025-06-30', 'Schmerzmittel', 3),
    ('Antidepressiva', 30, '2025-03-31', 'Psychische Gesundheit', 3),
    ('Antidiarrhoika', 40, '2025-02-28', 'Verdauungsprobleme', 3),
    ('Steri-Strips', 50, '2026-12-31', 'Wundverschluss', 3),
    ('Augentropfen', 20, '2025-01-31', 'Augenpflege', 3),
    ('Hustenmittel', 30, '2025-04-30', 'Atemwegserkrankungen', 3),
    ('Adrenalin', 10, '2024-11-30', 'Allergische Reaktionen', 3),
    ('Betäubungsmittel', 15, '2025-05-31', 'Schwere Schmerzen', 3),
    ('Magnesiumtabletten', 100, '2026-06-30', 'Muskelkrämpfe', 3),
    ('Antiseptische Salbe', 25, '2025-12-31', 'Wundinfektionen', 3);

INSERT INTO fuel_type (name) VALUES
    ('Diesel'),
    ('Heizöl'),
    ('Kohle'),
    ('Holzpellets'),
    ('Lampenöl'),
    ('Aviation Fuel'),
    ('Gasoline 95'),
    ('Gasoline 98'),
    ('Diesel Winter'),
    ('Bioethanol'),
    ('Pflanzenöl');

INSERT INTO fuel (fuel_type_id, quantity, storage_location_id) VALUES (1, 200.0, 1);
INSERT INTO fuel (fuel_type_id, quantity, storage_location_id) VALUES
    (1, 200.0, 1), -- Strom
    (12, 80.0, 4),  -- Heizöl
    (13, 50.0, 4),  -- Kohle
    (14, 30.0, 4),  -- Holzpellets
    (15, 15.0, 4),  -- Lampenöl
    (16, 20.0, 4),  -- Aviation Fuel
    (17, 100.0, 4),  -- Gasoline 95
    (18, 90.0, 4),  -- Gasoline 98
    (19, 70.0, 4),  -- Diesel Winter
    (20, 25.0, 4),  -- Bioethanol
    (21, 10.0, 4);  -- Pflanzenöl

INSERT INTO battery (type, capacity, quantity, storage_location_id) VALUES
    ('AA', 1.5, 100, 1),
    ('AA', 1.5, 150, 1),
    ('CR123A', 3.0, 50, 1),
    ('LR44', 1.5, 100, 1),
    ('12V SLA', 12.0, 15, 1),
    ('Li-Ion 21700', 3.7, 30, 1),
    ('Alkaline D', 1.5, 40, 1),
    ('NiCd', 1.2, 60, 1),
    ('CR2', 3.0, 20, 1),
    ('Lead-Acid', 6.0, 10, 1),
    ('LR20', 1.5, 25, 1);

INSERT INTO generator (type, power, fuel_type_id, status, storage_location_id) VALUES
    ('Notstrom', 5.0, 1, 'funktionsfähig', 1), -- Strom
    ('Heizgenerator', 8.0, 12, 'funktionsfähig', 5),  -- Heizöl
    ('Kohlegenerator', 5.0, 13, 'in Wartung', 5),  -- Kohle
    ('Pelletgenerator', 4.0, 14, 'funktionsfähig', 5),  -- Holzpellets
    ('Lampenölgenerator', 1.5, 15, 'funktionsfähig', 5),  -- Lampenöl
    ('Flugzeugtreibstoffgenerator', 10.0, 16, 'in Wartung', 5),  -- Aviation Fuel
    ('Benzingenerator 95', 7.0, 17, 'funktionsfähig', 5),  -- Gasoline 95
    ('Benzingenerator 98', 7.5, 18, 'funktionsfähig', 5),  -- Gasoline 98
    ('Winterdieselgenerator', 6.0, 19, 'in Wartung', 5),  -- Diesel Winter
    ('Bioethanolgenerator', 3.5, 20, 'funktionsfähig', 5),  -- Bioethanol
    ('Pflanzenölgenerator', 2.0, 21, 'funktionsfähig', 5);  -- Pflanzenöl
