-- Liquibase formatted SQL
-- changeset envars:003

ALTER TABLE products ADD FULLTEXT INDEX ft_products_search (name, description);