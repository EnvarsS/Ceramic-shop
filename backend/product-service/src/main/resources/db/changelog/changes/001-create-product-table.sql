-- Liquibase formatted sql
-- changeset envars:001

CREATE TABLE products
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    quantity    INT            NOT NULL DEFAULT 0
);