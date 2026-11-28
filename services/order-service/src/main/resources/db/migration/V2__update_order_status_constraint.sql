
ALTER TABLE orders
DROP CHECK chk_orders_status;

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_status
        CHECK (
            status IN (
                       'PENDING',
                       'PROCESSING',
                       'CONFIRMED',
                       'CANCELLED',
                       'FAILED'
                )
            );
