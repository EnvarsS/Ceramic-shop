-- Liquibase formatted sql
-- changeset envars:002

CREATE TABLE cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE
);