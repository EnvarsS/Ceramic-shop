-- Liquibase formatted SQL
-- changeset envars:005

ALTER TABLE auth_users ALTER COLUMN id RESTART WITH 11;