ALTER TABLE saga_outbox
    ADD COLUMN processing_started_at DATETIME(6) NULL;

CREATE INDEX idx_saga_outbox_processing
    ON saga_outbox (status, processing_started_at);