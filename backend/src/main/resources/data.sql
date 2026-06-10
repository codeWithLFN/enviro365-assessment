-- Investors
INSERT INTO investors (id, firstname, lastname, email, password, date_of_birth)
VALUES (1, 'Lufuno', 'Mudzivhadi', 'lufuno@example.com', 'password123', '1990-05-10');

INSERT INTO investors (id, firstname, lastname, email, password, date_of_birth)
VALUES (2, 'Sarah', 'Nkosi', 'sarah@example.com', 'password123', '1955-03-22');

-- Investment Products
INSERT INTO investment_product (id, product_name, product_type, balance, investor_id)
VALUES (1, 'Retirement Annuity Fund', 'RETIREMENT', 50000.00, 1);

INSERT INTO investment_product (id, product_name, product_type, balance, investor_id)
VALUES (2, 'Tax Free Savings Account', 'SAVINGS', 15000.00, 1);

INSERT INTO investment_product (id, product_name, product_type, balance, investor_id)
VALUES (3, 'Retirement Preservation Fund', 'RETIREMENT', 120000.00, 2);