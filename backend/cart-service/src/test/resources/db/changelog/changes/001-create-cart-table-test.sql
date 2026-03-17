-- Liquibase formatted sql
-- changeset envars:001

CREATE TABLE carts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE
);