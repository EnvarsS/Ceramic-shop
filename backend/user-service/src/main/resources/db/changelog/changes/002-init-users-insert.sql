-- Liquibase formatter SQL
-- changeset envars:002

INSERT INTO users (id, email, username, password, role_id) VALUES
(1, 'user1@mail.com', 'user1', '$2a$10$hash1', 1),
(2, 'user2@mail.com', 'user2', '$2a$10$hash2', 1),
(3, 'user3@mail.com', 'user3', '$2a$10$hash3', 1),
(4, 'user4@mail.com', 'user4', '$2a$10$hash4', 1),
(5, 'user5@mail.com', 'user5', '$2a$10$hash5', 1),
(6, 'user6@mail.com', 'user6', '$2a$10$hash6', 1),
(7, 'user7@mail.com', 'user7', '$2a$10$hash7', 1),
(8, 'user8@mail.com', 'user8', '$2a$10$hash8', 1),
(9, 'admin1@mail.com', 'admin1', '$2a$10$adminhash1', 2),
(10,'admin2@mail.com', 'admin2', '$2a$10$adminhash2', 2);
