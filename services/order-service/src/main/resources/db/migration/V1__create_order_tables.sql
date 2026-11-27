
CREATE TABLE orders (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        order_number VARCHAR(30) NOT NULL,
                        account_number VARCHAR(50) NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        total_amount DECIMAL(12, 2) NOT NULL,
                        currency CHAR(3) NOT NULL,
                        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),

                        PRIMARY KEY (id),
                        CONSTRAINT uk_orders_order_number UNIQUE (order_number),
                        CONSTRAINT chk_orders_status
                            CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED')),
                        CONSTRAINT chk_orders_total_amount CHECK (total_amount >= 0),

                        INDEX idx_orders_account_number (account_number),
                        INDEX idx_orders_status (status),
                        INDEX idx_orders_created_at (created_at)
);

CREATE TABLE order_items (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             product_sku VARCHAR(50) NOT NULL,
                             product_name VARCHAR(150) NOT NULL,
                             unit_price DECIMAL(12, 2) NOT NULL,
                             currency CHAR(3) NOT NULL,
                             billing_frequency VARCHAR(20) NOT NULL,
                             quantity INT NOT NULL,
                             line_total DECIMAL(12, 2) NOT NULL,
                             created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                             PRIMARY KEY (id),
                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id) REFERENCES orders(id),
                             CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
                             CONSTRAINT chk_order_items_unit_price CHECK (unit_price >= 0),
                             CONSTRAINT chk_order_items_line_total CHECK (line_total >= 0),

                             INDEX idx_order_items_order_id (order_id),
                             INDEX idx_order_items_product_id (product_id)
);
