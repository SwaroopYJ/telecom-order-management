ALTER TABLE customers
    RENAME COLUMN customer_number TO account_number;

ALTER TABLE customers
    RENAME INDEX uk_customers_customer_number
    TO uk_customers_account_number;