--Liquibase formatted SQL
-- changeset envars:001

CREATE ROLES(
       id BIGSERIAl PRIMARY KEY,
       name VARCHAR(50) NOT NULL UNIQUE
)