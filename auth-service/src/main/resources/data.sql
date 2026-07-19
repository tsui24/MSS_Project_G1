INSERT IGNORE INTO roles (role_name, description) VALUES
    ('ADMIN', 'Full system administrator'),
    ('RECEPTIONIST', 'Front-desk staff handling check-in/out and bookings'),
    ('HOUSEKEEPING', 'Housekeeping staff managing room cleanliness status'),
    ('CUSTOMER', 'Guest/customer account');

-- Local test accounts. All four accounts use the password: Test@123
-- INSERT IGNORE keeps existing accounts and their passwords unchanged.
INSERT IGNORE INTO users (username, password_hash, full_name, role_id)
SELECT 'admin@test.local', '$2a$10$.AO0646.cQ.RW2yMgob16OOikyPRIzjdvvdw3b6PYOZTM/v2OVi6i',
       'Test Administrator', id
FROM roles WHERE role_name = 'ADMIN';

INSERT IGNORE INTO users (username, password_hash, full_name, role_id)
SELECT 'receptionist@test.local', '$2a$10$.AO0646.cQ.RW2yMgob16OOikyPRIzjdvvdw3b6PYOZTM/v2OVi6i',
       'Test Receptionist', id
FROM roles WHERE role_name = 'RECEPTIONIST';

INSERT IGNORE INTO users (username, password_hash, full_name, role_id)
SELECT 'housekeeping@test.local', '$2a$10$.AO0646.cQ.RW2yMgob16OOikyPRIzjdvvdw3b6PYOZTM/v2OVi6i',
       'Test Housekeeper', id
FROM roles WHERE role_name = 'HOUSEKEEPING';

INSERT IGNORE INTO users (username, password_hash, full_name, role_id)
SELECT 'customer@test.local', '$2a$10$.AO0646.cQ.RW2yMgob16OOikyPRIzjdvvdw3b6PYOZTM/v2OVi6i',
       'Test Customer', id
FROM roles WHERE role_name = 'CUSTOMER';

-- Priority room-turnover window between standard check-out (12:00) and check-in (14:00).
INSERT IGNORE INTO shifts (name, start_time, end_time)
VALUES ('Housekeeping turnover', '12:00:00', '14:00:00');

INSERT IGNORE INTO shifts (name, start_time, end_time) VALUES
    ('Ca sáng', '06:00:00', '14:00:00'),
    ('Ca chiều', '14:00:00', '22:00:00'),
    ('Ca đêm', '22:00:00', '06:00:00');
