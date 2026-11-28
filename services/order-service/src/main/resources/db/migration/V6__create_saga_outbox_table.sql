CREATE TABLE saga_outbox (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             event_id VARCHAR(100) NOT NULL,
                             aggregate_type VARCHAR(50) NOT NULL,
                             aggregate_id VARCHAR(100) NOT NULL,
                             event_type VARCHAR(100) NOT NULL,
                             routing_key VARCHAR(150) NOT NULL,
                             payload JSON NOT NULL,
                             status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                             attempts INT NOT NULL DEFAULT 0,
                             created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                             published_at DATETIME(6) NULL,
                             last_error TEXT NULL,

                             PRIMARY KEY (id),
                             CONSTRAINT uk_saga_outbox_event_id UNIQUE (event_id),
                             INDEX idx_saga_outbox_status_created (status, created_at),
                             INDEX idx_saga_outbox_aggregate (aggregate_type, aggregate_id)
);