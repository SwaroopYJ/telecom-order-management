
CREATE TABLE order_sagas (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             saga_id VARCHAR(36) NOT NULL,
                             order_id BIGINT NOT NULL,
                             saga_type VARCHAR(50) NOT NULL,
                             current_state VARCHAR(50) NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             last_completed_step VARCHAR(50),
                             retry_count INT NOT NULL DEFAULT 0,
                             next_retry_at DATETIME(6),
                             failure_reason VARCHAR(1000),
                             created_at DATETIME(6) NOT NULL,
                             updated_at DATETIME(6) NOT NULL,
                             version BIGINT NOT NULL DEFAULT 0,

                             PRIMARY KEY (id),
                             CONSTRAINT uk_order_sagas_saga_id UNIQUE (saga_id),

                             INDEX idx_order_sagas_order_id (order_id),
                             INDEX idx_order_sagas_status (status),
                             INDEX idx_order_sagas_next_retry_at (next_retry_at),

                             CONSTRAINT fk_order_sagas_order
                                 FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE saga_step_executions (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      saga_id BIGINT NOT NULL,
                                      step_name VARCHAR(50) NOT NULL,
                                      status VARCHAR(30) NOT NULL,
                                      command_id VARCHAR(100),
                                      attempt_number INT NOT NULL DEFAULT 1,
                                      started_at DATETIME(6),
                                      completed_at DATETIME(6),
                                      error_message VARCHAR(1000),
                                      created_at DATETIME(6) NOT NULL,

                                      PRIMARY KEY (id),

                                      INDEX idx_saga_steps_saga_id (saga_id),
                                      INDEX idx_saga_steps_status (status),

                                      CONSTRAINT fk_saga_steps_order_saga
                                          FOREIGN KEY (saga_id) REFERENCES order_sagas(id)
);

