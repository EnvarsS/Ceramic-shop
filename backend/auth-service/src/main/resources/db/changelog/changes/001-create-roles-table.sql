--Liquibase formatted SQL
-- changeset envars:001

CREATE TABLE roles(
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(50) NOT NULL UNIQUE
)