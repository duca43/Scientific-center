INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('camunda-admin', 1, 'camunda BPM Administrators', 'SYSTEM');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('users', 1, 'Users', 'WORKFLOW');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('reviewers', 1, 'Reviewers', 'WORKFLOW');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('editors', 1, 'Editors', 'WORKFLOW');
INSERT INTO scientific_center.act_id_group (ID_, REV_, NAME_, TYPE_) VALUES ('guests', 1, 'Guests', 'WORKFLOW');

INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('anonymous', 1, 'Unknown', 'Anonymous', 'anonymous@unknown.com',
'{SHA-512}uEUKZR/KKyxZv+UB+1ECjp8XzOjm66APW1v+vU03sS/Hlr0uR7BELj32rRsesazStx8YjiBsjg9sg5+VnmM1NA==',
'ywTBluqNVYPXohFjafDUow==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('systemAdmin', 1, 'System', 'Administrator', 'xmlws.megatravel@gmail.com',
'{SHA-512}Xy3jYn9hwSzWOv2vGzVZwHlx6uR525qn9ZdXHsWKcWNaIjZ8mEqpd1cVe6m3nejetpENeYOW7Im3CtizYKIHMw==',
'+vjpNnOsPi4VU7a1p/9heQ==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('firstEditor', 1, 'First', 'Editor', 'dmaksic@uns.ac.rs',
'{SHA-512}tU4W2Cm3tITisrKJ45unXt+AvYwuUz29if6isYIopd0s3/bVP89xc+QuGcA2NmdE0U8CniRsXNn1r58zQg3Csw==',
'NaUn54+3jUolGRcfjs0LmQ==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('reviewer1', 1, 'First', 'Reviewer', 'maxdux10@gmail.com',
'{SHA-512}LE+uD5CjAvadCzap5e48g3pIjCCA1n93OVVB3iJt9j7yI9u75Z0dsFJh3tlZuk3XL2rkMuyx/r2TEPMHLOlJXQ==',
'a55U3u/pqmJa1uuhxnWQGQ==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('reviewer2', 1, 'Second', 'Reviewer', 'maxdux10@gmail.com',
'{SHA-512}c6CTpTnxwFKkiz1/n0lblOG2R3iu2MT1xc/8PPk76qKRSNtXpWzoYHDJD9qgkUJZ0oU4JBmM0upCz55XxNNyAQ==',
'B4CIl08zMJr1N4XpssiSFQ==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('user1234', 1, 'User', '1234', 'maxdux10@gmail.com',
'{SHA-512}2rd0pkyZqanXkHwnCWmTWUhjb2IKSYn3GB0m3DCr3fTBv9anb57cWoQuOD949AqoNe3Tx8PTzPebU21g2ykfsg==',
'O2v/RS+ipOynqMA9ixIxdw==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('reviewer3', 1, 'Third', 'Reviewer', 'maxdux10@gmail.com',
'{SHA-512}Xl6H3kBc9bo0yJ6yjC92aE9WQ80Klite2Hry526lojH5ILPGaB9fAIEP85AVLV4HZeVS4yudXqnpggplgAMvqQ==',
'jqc6Schv4TJVlss13sZh5g==', null, null, null);
INSERT INTO scientific_center.act_id_user (ID_, REV_, FIRST_, LAST_, EMAIL_, PWD_, SALT_, LOCK_EXP_TIME_, ATTEMPTS_, PICTURE_ID_)
VALUES ('secondEditor', 1, 'Second', 'Editor', 'dmaksic@uns.ac.rs',
'{SHA-512}6uR17/vBMGxlwxw7Jv3uZqbH/dAH4onGHgqtAxDQ2CKV53sDRkO67f6Z0CWC2QttWzcFittKIwlS6IfKQN1nOg==',
'2U4/ongRZiO7ZMCCfWDqjg==', null, null, null);

INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('systemAdmin', 'camunda-admin');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('firstEditor', 'editors');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('reviewer1', 'reviewers');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('reviewer2', 'reviewers');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('reviewer1', 'users');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('reviewer2', 'users');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('anonymous', 'guests');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('user1234', 'users');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('reviewer3', 'reviewers');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('reviewer3', 'users');
INSERT INTO scientific_center.act_id_membership (USER_ID_, GROUP_ID_) VALUES ('secondEditor', 'editors');

INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (1, 'ROLE_ADMINISTRATOR');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (3, 'ROLE_REVIEWER');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (4, 'ROLE_EDITOR');
INSERT INTO scientific_center.AUTHORITY (id, name) VALUES (5, 'ROLE_GUEST');

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

INSERT INTO scientific_center.LOCATION (id, address, city, country, latitude, longitude) VALUES (1, 'Djurdja Brankovica, Novi Sad, Војводина, Serbia', 'Novi Sad', 'Serbia', 45.2606, 19.8373);

INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (1, 'xmlws.megatravel@gmail.com', b'1', 'System', 'Administrator', '$2y$10$tFH87.NgUuRxNSCwZiZE8uv/z7k/XBBTX1SA.Bj5YsJLQW3mxoRyS', null, 'systemAdmin', null);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (2, 'anonymous@unknown.com', b'1', 'Unknown', 'Anonymous', '$2y$10$jJCmyh4aqFCreKHnHelHEOBxYPY6yriJuc/W/R0KCE.dsgJPfv8vi', null, 'anonymous', null);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (3, 'dmaksic@uns.ac.rs', b'1', 'First', 'Editor', '$2a$10$KcARaIopTJuisP2gTu6ngeBhcpuz25eHmq3IE5yrbCYorZOlQepJ.', 'dipling', 'firstEditor', 1);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (4, 'maxdux10@gmail.com', b'1', 'First', 'Reviewer', '$2a$10$uijFAWkyAtlppALGiqh0DuSVGu.TcIaX140MA1XY1MjcRxq6Nia0W', 'dipling', 'reviewer1', 1);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (5, 'maxdux10@gmail.com', b'1', 'Second', 'Reviewer', '$2a$10$nDEhEwC1gDu3XvTJt3BeCOnHxD6K4TddcfJ5BdgaXT9CqUJ2/5v7y', 'dipling', 'reviewer2', 1);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (6, 'maxdux10@gmail.com', b'1', 'User', '1234', '$2a$10$HUUF4U0YW8fIkgmRbqIRT.PrxnMQX9/Qf9DPcg22JOPCjPv24cvgS', 'dipling', 'user1234', 1);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (7, 'maxdux10@gmail.com', b'1', 'Third', 'Reviewer', '$2a$10$PHW2MOmtMlo6XCy9rKQvVOeqwZsyDX28lwjyNXqdh3bZuWszGsX0q', 'dipling', 'reviewer3', 1);
INSERT INTO scientific_center.user (id, email, enabled, firstname, lastname, password, title, username, location_id)
VALUES (8, 'dmaksic@uns.ac.rs', b'1', 'Second', 'Editor', '$2a$10$K9pTkSprmk.wjWBC3COJue6uo1zyfKGG1kueGDP7FvLH2DhJ6OKay', 'dipling', 'secondEditor', 1);

INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (1, 1);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (4, 2);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (5, 2);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (4, 3);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (5, 3);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (3, 4);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (2, 5);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (6, 2);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (7, 2);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (7, 3);
INSERT INTO scientific_center.user_authorities(user_id, authorities_id) VALUES (8, 4);

INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 1);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 1);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 1);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 2);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 2);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 2);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 3);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 3);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 3);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 4);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 4);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 4);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 5);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 5);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 5);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 6);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 6);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 6);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 7);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 7);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 7);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 8);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 8);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 8);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 9);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (4, 9);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (5, 9);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 10);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 11);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 12);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 13);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 14);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (3, 15);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (6, 2);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (6, 4);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (6, 5);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 1);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 2);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 3);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 4);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 5);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 6);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 7);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 8);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (7, 10);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (8, 1);
INSERT INTO scientific_center.user_scientific_areas(user_id, scientific_areas_id) VALUES (8, 24);

INSERT INTO scientific_center.magazine (id, chosen_editors_and_reviewers, enabled, enabled_as_merchant, issn,membership_currency, membership_price, merchant_id, name, payment, requested_changes, main_editor_id)
VALUES (1, b'1', b'1', NULL, '12345678', NULL, NULL, NULL, 'Best magazine', 'READER', b'0', 3);
INSERT INTO scientific_center.magazine (id, chosen_editors_and_reviewers, enabled, enabled_as_merchant, issn,membership_currency, membership_price, merchant_id, name, payment, requested_changes, main_editor_id)
VALUES (2, b'1', b'1', NULL, '87654321', 'USD', 30, 'd1058caf-6874-4533-90f5-7202020dc78e', 'Second best magazine', 'AUTHOR', b'0', 8);

INSERT INTO scientific_center.magazine_editors (magazine_id, editors_id) VALUES (1, 8);
INSERT INTO scientific_center.magazine_editors (magazine_id, editors_id) VALUES (2, 3);

INSERT INTO scientific_center.magazine_reviewers (magazine_id, reviewers_id) VALUES (1, 4);
INSERT INTO scientific_center.magazine_reviewers (magazine_id, reviewers_id) VALUES (1, 5);
INSERT INTO scientific_center.magazine_reviewers (magazine_id, reviewers_id) VALUES (1, 7);
INSERT INTO scientific_center.magazine_reviewers (magazine_id, reviewers_id) VALUES (2, 4);
INSERT INTO scientific_center.magazine_reviewers (magazine_id, reviewers_id) VALUES (2, 5);
INSERT INTO scientific_center.magazine_reviewers (magazine_id, reviewers_id) VALUES (2, 7);

INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 1);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 2);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 3);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 4);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 5);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 6);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 7);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 8);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 9);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 10);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 24);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (1, 67);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 1);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 2);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 3);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 4);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 5);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 6);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 7);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 8);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 9);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 10);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 24);
INSERT INTO scientific_center.magazine_scientific_areas (magazine_id, scientific_areas_id) VALUES (2, 67);