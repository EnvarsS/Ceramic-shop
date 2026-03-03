-- Liquibase formatter SQL
-- changeset envars:002

INSERT INTO users (id, email, username, auth_id, address, birth_date) VALUES
(1, 'user1@gmail.com', 'user1', 1, 'Berlin, Alexanderplatz 1', '1998-03-15'),
(2, 'user2@gmail.com', 'user2', 2, 'Munich, Marienplatz 5', '1997-07-21'),
(3, 'user3@gmail.com', 'user3', 3, 'Hamburg, HafenCity 12', '1999-11-02'),
(4, 'user4@gmail.com', 'user4', 4, 'Cologne, Domkloster 4', '1996-01-30'),
(5, 'user5@gmail.com', 'user5', 5, 'Frankfurt, Zeil 45', '2000-06-18'),
(6, 'user6@gmail.com', 'user6', 6, 'Stuttgart, Königstrasse 9', '1995-09-09'),
(7, 'user7@gmail.com', 'user7', 7, 'Düsseldorf, Medienhafen 3', '1998-12-25'),
(8, 'user8@gmail.com', 'user8', 8, 'Leipzig, Markt 7', '1997-04-11'),
(9, 'admin1@gmail.com', 'admin1', 9, 'Berlin, Unter den Linden 77', '1990-05-05'),
(10,'admin2@gmail.com', 'admin2', 10, 'Munich, Maximilianstrasse 20', '1988-08-14');
