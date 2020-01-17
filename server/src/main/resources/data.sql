INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('camunda-admin', 1, 'camunda BPM Administrators', 'SYSTEM');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('users', 1, 'Users', 'WORKFLOW');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('reviewers', 1, 'Reviewers', 'WORKFLOW');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('editors', 1, 'Editors', 'WORKFLOW');

INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('anonymous', 1, 'Unknown', 'Anonymous', 'anonymous@unknown.com',
'{SHA-512}uEUKZR/KKyxZv+UB+1ECjp8XzOjm66APW1v+vU03sS/Hlr0uR7BELj32rRsesazStx8YjiBsjg9sg5+VnmM1NA==',
'ywTBluqNVYPXohFjafDUow==', null, null, null);

INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('systemAdmin', 1, 'System', 'Administrator', 'xmlws.megatravel@gmail.com',
'{SHA-512}Xy3jYn9hwSzWOv2vGzVZwHlx6uR525qn9ZdXHsWKcWNaIjZ8mEqpd1cVe6m3nejetpENeYOW7Im3CtizYKIHMw==',
'+vjpNnOsPi4VU7a1p/9heQ==', null, null, null);

INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('systemAdmin', 'camunda-admin');

INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (1, 'ROLE_ADMINISTRATOR');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (3, 'ROLE_REVIEWER');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (4, 'ROLE_EDITOR');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (5, 'ROLE_GUEST');

INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (1, 'xmlws.megatravel@gmail.com', b'1', 'System', 'Administrator', '$2y$10$tFH87.NgUuRxNSCwZiZE8uv/z7k/XBBTX1SA.Bj5YsJLQW3mxoRyS', null, 'systemAdmin', null);

INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (2, 'anonymous@unknown.com', b'1', 'Unknown', 'Anonymous', '$2y$10$jJCmyh4aqFCreKHnHelHEOBxYPY6yriJuc/W/R0KCE.dsgJPfv8vi', null, 'anonymous', null);

INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (1, 1);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (2, 5);

INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (1,'Acoustics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (2,'Aeronautics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (3,'Agronomy');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (4,'Anatomy');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (5,'Anthropology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (6,'Archaeology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (7,'Astronomy');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (8,'Astrophysics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (9,'Bacteriology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (10,'Biochemistry');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (11,'Biology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (12,'Botany');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (13,'Cardiology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (14,'Cartography');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (15,'Chemistry');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (16,'Cosmology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (17,'Crystallography');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (18,'Ecology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (19,'Embryology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (20,'Endocrinology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (21,'Entomology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (22,'Enzymology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (23,'Forestry');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (24,'Gelotology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (25,'Genetics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (26,'Geochemistry');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (27,'Geodesy');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (28,'Geography');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (29,'Geology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (30,'Geophysics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (31,'Hematology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (32,'Histology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (33,'Horology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (34,'Hydrology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (35,'Ichthyology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (36,'Immunology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (37,'Linguistics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (38,'Mechanics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (39,'Medicine');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (40,'Meteorology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (41,'Metrology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (42,'Microbiology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (43,'Mineralogy');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (44,'Mycology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (45,'Neurology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (46,'Nucleonics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (47,'Nutrition');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (48,'Oceanography');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (49,'Oncology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (50,'Optics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (51,'Paleontology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (52,'Pathology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (53,'Petrology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (54,'Pharmacology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (55,'Physics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (56,'Physiology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (57,'Psychology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (58,'Radiology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (59,'Robotics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (60,'Seismology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (61,'Spectroscopy');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (62,'Systematics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (63,'Thermodynamics');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (64,'Toxicology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (65,'Virology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (66,'Volcanology');
INSERT INTO scientific_center.SCIENTIFIC_AREA(id,name) VALUES (67,'Zoology');