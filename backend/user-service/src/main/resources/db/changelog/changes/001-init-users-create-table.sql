-- Liquibase formatted SQL
-- changeset envars:001
CREATE TABLE users(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    username   VARCHAR(255) NOT NULL UNIQUE,
    auth_id    BIGINT       NOT NULL,
    address    VARCHAR(255),
    birth_date DATE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
