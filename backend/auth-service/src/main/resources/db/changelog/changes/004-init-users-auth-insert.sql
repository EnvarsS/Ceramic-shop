-- Liquibase formatted SQL
-- changeset envars:004

INSERT INTO auth_users(id, username, password_hash, email, role_id) VALUES
(1, 'user1', '$2a$10$WlAuNLaU1G9mjJwIad8lQuQFzbiS6OycfkqiMv/6fQed74/25o2ze', 'user1@gmail.com', 1),
(2, 'user2', '$2a$10$gcB9s7fh.Mj/BAmQrVUSXu0OJAk7k5Rt3AjBJFSZNVaO5lHw8lLpy', 'user2@gmail.com', 1),
(3, 'user3', '$2a$10$ITo1cWv7ImLIwMD.lO1ixuB8kFZzYcwnkxBTn3H8cqBHv0P1dfL9O', 'user3@gmail.com', 1),
(4, 'user4', '$2a$10$U01z/uzUcWVGeb7eaKe3hub2mwx1zNkdCPgGvyLkaLXIg8gQ54aUa', 'user4@gmail.com', 1),
(5, 'user5', '$2a$10$aNIOGEQsaOsX7bIj4GTHAOmVSwfl6PiV/G6VCVQv3mJ8fHMRt/JY2', 'user5@gmail.com', 1),
(6, 'user6', '$2a$10$B9bOyn0POhdwsDJmf9bnROHG9.gds/RLErK4PbRxMPYCP9mJnRcSi', 'user6@gmail.com', 1),
(7, 'user7', '$2a$10$xt1L7yLGaspWKMv.rXH85OBl6psl8RFpNdhhe7ljm8qyw0dfmMc5m', 'user7@gmail.com', 1),
(8, 'user8', '$2a$10$EKWJzLDfVSVi7o7PLr4TK.ss21g4dmn53GW/.7GKQaUtftA4SNZ3a', 'user8@gmail.com', 1),
(9, 'admin1', '$2a$10$B9YKaA4t9TytZ1.IrnIClOtAr9KuOrq59.EhXf/hn8IdBAA1e/cMC', 'admin1@gmail.com', 2),
(10,'admin2', '$2a$10$UwdqTDxpai3ApSVGgB.eMujUJ8TxeX4H6RezkmubJ32Gvz6/Y1ftq', 'admin2@gmail.com', 2);