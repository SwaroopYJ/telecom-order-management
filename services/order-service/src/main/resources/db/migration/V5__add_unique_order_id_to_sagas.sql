
ALTER TABLE order_sagas
    ADD CONSTRAINT uk_order_sagas_order_id UNIQUE (order_id);

