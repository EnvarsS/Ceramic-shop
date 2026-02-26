-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO auth_users(id, username, password_hash, role_id) VALUES
(1, 'user1', 'u1password1', 1),
(2, 'user2', 'u2password2', 1),
(3, 'user3', 'u3password3', 1),
(4, 'user4', 'u4password4', 1),
(5, 'user5', 'u5password5', 1),
(6, 'user6', 'u6password6', 1),
(7, 'user7', 'u7password7', 1),
(8, 'user8', 'u8password8', 1),
(9, 'admin1', 'a1password1', 2),
(10,'admin2', 'a2password2', 2);