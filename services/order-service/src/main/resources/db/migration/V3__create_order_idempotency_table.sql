
CREATE TABLE order_idempotency (
                                   idempotency_key VARCHAR(100) NOT NULL,
                                   request_hash CHAR(64) NOT NULL,
                                   order_id BIGINT NULL,
                                   created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                   PRIMARY KEY (idempotency_key),

                                   CONSTRAINT fk_order_idempotency_order
                                       FOREIGN KEY (order_id) REFERENCES orders(id),

                                   INDEX idx_order_idempotency_order_id (order_id)
);
