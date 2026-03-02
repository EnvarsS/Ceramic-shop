-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO auth_users(id, username, password_hash, email, role_id) VALUES
(1, 'user1', 'u1password1', 'user1@gmail.com', 1),
(2, 'user2', 'u2password2', 'user2@gmail.com', 1),
(3, 'user3', 'u3password3', 'user3@gmail.com', 1),
(4, 'user4', 'u4password4', 'user4@gmail.com', 1),
(5, 'user5', 'u5password5', 'user5@gmail.com', 1),
(6, 'user6', 'u6password6', 'user6@gmail.com', 1),
(7, 'user7', 'u7password7', 'user7@gmail.com', 1),
(8, 'user8', 'u8password8', 'user8@gmail.com', 1),
(9, 'admin1', 'a1password1', 'admin1@gmail.com', 2),
(10,'admin2', 'a2password2', 'admin2@gmail.com', 2);