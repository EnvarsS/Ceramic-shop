-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO auth_users(id, username, password_hash, role_id) VALUES
(1, 'user1', '$2a$10$hash1', 1),
(2, 'user2', '$2a$10$hash2', 1),
(3, 'user3', '$2a$10$hash3', 1),
(4, 'user4', '$2a$10$hash4', 1),
(5, 'user5', '$2a$10$hash5', 1),
(6, 'user6', '$2a$10$hash6', 1),
(7, 'user7', '$2a$10$hash7', 1),
(8, 'user8', '$2a$10$hash8', 1),
(9, 'admin1', '$2a$10$adminhash1', 2),
(10,'admin2', '$2a$10$adminhash2', 2);