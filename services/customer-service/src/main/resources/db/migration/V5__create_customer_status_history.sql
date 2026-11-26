CREATE TABLE customer_status_history (
                                         id BIGINT NOT NULL AUTO_INCREMENT,
                                         customer_id BIGINT NOT NULL,
                                         previous_status VARCHAR(20) NULL,
                                         new_status VARCHAR(20) NOT NULL,
                                         changed_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
                                         changed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                         CONSTRAINT pk_customer_status_history
                                             PRIMARY KEY (id),

                                         CONSTRAINT fk_customer_status_history_customer
                                             FOREIGN KEY (customer_id)
                                                 REFERENCES customers(id),

                                         INDEX idx_customer_status_history_customer_id (customer_id),
                                         INDEX idx_customer_status_history_changed_at (changed_at)
);