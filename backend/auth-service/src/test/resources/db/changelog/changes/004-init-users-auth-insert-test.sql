-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO auth_users(id, username, password_hash, email, role_id) VALUES
(1, 'user1', '$2a$10$dJBTBAzG2t7nADaUlus.WOdtagVxrqAP1IVBkDXKMDr6JjBwb0d7a', 'user1@gmail.com', 1),
(2, 'user2', '$2a$10$mTGv3ZCK5jl4E6YtbGFgkOHmmNaVc.sBXHkqnvOiNGJF.lvSKFBSS', 'user2@gmail.com', 1),
(3, 'user3', '$2a$10$1hZkNpwSnlVEudR9RKIgw.MsQJj0MmowLUw1oqMczWhwLhM8l3FUq', 'user3@gmail.com', 1),
(4, 'user4', '$2a$10$5SJmK2CI3Ln6gTS45TPHaus0TjHCZZgEZAhPafVFKsjqnKvGlwsn.', 'user4@gmail.com', 1),
(5, 'user5', '$2a$10$Rcr25IHk92H7soP.X.aYMOMOSZkzy6t41TiK.2/t12BB7uKbyarGG', 'user5@gmail.com', 1),
(6, 'user6', '$2a$10$a/lSeW95gk1LEu3Sr1hFMOKloeVRYyYHHRf1YR9hgNMWywXnWY4x6', 'user6@gmail.com', 1),
(7, 'user7', '$2a$10$oDVdFTT5/WqBIOoIIJ/5M.AEXxIxnnhsB.DA/2GiD7D.19jP4CK2a', 'user7@gmail.com', 1),
(8, 'user8', '$2a$10$zPuW4QP/GG0zjvQ3IDc0veLrSUsx3a5xCvgZj.HEBW1Co9u1H14VK', 'user8@gmail.com', 1),
(9, 'admin1', '$2a$10$I62NjIL2Jwf.vzXhKN9n1.45e3ULEw7L/iysHY39HR.EU//wrhL9C', 'admin1@gmail.com', 2),
(10,'admin2', '$2a$10$MrLUh/9BUOJn0e0tzgJm9OcjR9GfDiQMfngl7KVeM82TmdJEyRW9u ', 'admin2@gmail.com', 2);