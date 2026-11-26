CREATE TABLE customers (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           customer_number VARCHAR(30) NOT NULL,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           phone_number VARCHAR(20) NOT NULL,
                           date_of_birth DATE,
                           status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,

                           PRIMARY KEY (id),
                           CONSTRAINT uk_customers_customer_number UNIQUE (customer_number),
                           CONSTRAINT uk_customers_email UNIQUE (email),
                           CONSTRAINT uk_customers_phone UNIQUE (phone_number),
                           CONSTRAINT chk_customers_status
                               CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);