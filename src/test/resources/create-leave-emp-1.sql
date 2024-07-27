INSERT INTO employees (id, first_name, last_name, email, available_leave_days, password)
    VALUES (1, 'John Doe', 'Samir', 'john@samir.com', 25, 'test');
INSERT INTO leaves (start_date, end_date, state, employee_id) VALUES ('2024-08-01', '2024-08-10', 'APPROVED', 1);