INSERT INTO client (id, first_name, last_name, birthday, sex, inn) VALUES
    (1, 'John', 'Allerdyce', '2002-07-11', 'MALE', '00123948475'),
    (2, 'Raven', 'Darkhölme', '1996-02-23', 'MALE', '00123256875'),
    (3, 'Bobby', 'Drake', '2000-02-18', 'MALE', '00123986475'),
    (4, 'Piotr', 'Rasputin', '1982-09-09', 'MALE', '0013464475'),
    (5, 'Kurt', 'Wagner', '1989-01-11', 'MALE', '00126355475'),
    (6, 'Marie', 'Rogue', '1986-06-21', 'FEMALE', '0012236434');

INSERT INTO orders (id, client_id, order_date, order_state, amount, currency, confirmed) VALUES
    (1, 1, '2017-07-11', 'NEW', 34567, 'USD', FALSE),
    (2, 1, '2017-02-23', 'PROCESSING', 870000, 'UAH', TRUE),
    (3, 2, '2017-02-18', 'COMPLETED', 57000, 'UAH', TRUE),
    (4, 3, '2017-09-09', 'PROCESSING', 23000, 'USD', TRUE),
    (5, 4, '2017-01-11', 'COMPLETED', 1000, 'EUR', TRUE),
    (6, 5, '2017-09-09', 'NEW', 55000, 'UAH', FALSE),
    (7, 5, '2017-10-05', 'COMPLETED', 34900, 'USD', TRUE),
    (8, 5, '2017-09-28', 'NEW', 18000, 'EUR', FALSE),
    (9, 6, '2017-09-30', 'NEW', 3500, 'USD', FALSE);

