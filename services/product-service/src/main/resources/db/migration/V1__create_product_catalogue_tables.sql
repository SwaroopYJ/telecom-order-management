
CREATE TABLE product_categories (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    code VARCHAR(50) NOT NULL,
                                    name VARCHAR(100) NOT NULL,
                                    description VARCHAR(500),
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP,

                                    PRIMARY KEY (id),
                                    CONSTRAINT uk_product_categories_code UNIQUE (code)
);

CREATE TABLE products (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          sku VARCHAR(50) NOT NULL,
                          name VARCHAR(150) NOT NULL,
                          description VARCHAR(1000),
                          category_id BIGINT NOT NULL,
                          status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          PRIMARY KEY (id),
                          CONSTRAINT uk_products_sku UNIQUE (sku),
                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id)
                                  REFERENCES product_categories(id),
                          CONSTRAINT chk_products_status
                              CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED')),

                          INDEX idx_products_category (category_id),
                          INDEX idx_products_status (status)
);

CREATE TABLE product_prices (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                product_id BIGINT NOT NULL,
                                amount DECIMAL(12, 2) NOT NULL,
                                currency CHAR(3) NOT NULL,
                                billing_frequency VARCHAR(30) NOT NULL,
                                valid_from DATETIME NOT NULL,
                                valid_to DATETIME,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                PRIMARY KEY (id),
                                CONSTRAINT fk_product_prices_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products(id),
                                CONSTRAINT chk_product_prices_amount
                                    CHECK (amount >= 0),
                                CONSTRAINT chk_product_prices_validity
                                    CHECK (valid_to IS NULL OR valid_to > valid_from),
                                CONSTRAINT chk_product_prices_frequency
                                    CHECK (billing_frequency IN ('ONE_TIME', 'MONTHLY', 'ANNUAL')),

                                INDEX idx_product_prices_product (product_id),
                                INDEX idx_product_prices_validity (valid_from, valid_to)
);

INSERT INTO product_categories (code, name, description)
VALUES
    ('BROADBAND', 'Broadband', 'Fixed broadband internet plans'),
    ('SIM_ONLY', 'SIM Only', 'SIM-only mobile subscription plans'),
    ('HANDSET', 'Handset', 'Mobile phones and devices'),
    ('EQUIPMENT', 'Equipment', 'Telecom accessories and equipment');
