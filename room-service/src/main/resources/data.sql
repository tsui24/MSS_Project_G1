-- Seed only an empty catalog so deleted room types do not reappear on restart.
-- VARCHAR keeps the database compatible when a new RoomStatus enum value is added in Java.
ALTER TABLE rooms MODIFY COLUMN status VARCHAR(20) NOT NULL;

INSERT INTO room_classes (class_name, standard_occupancy, max_occupancy, base_price)
SELECT seed.class_name, seed.standard_occupancy, seed.max_occupancy, seed.base_price
FROM (
    SELECT 'Standard' AS class_name, 1 AS standard_occupancy, 2 AS max_occupancy, 800000.00 AS base_price
    UNION ALL SELECT 'Deluxe', 2, 3, 1200000.00
    UNION ALL SELECT 'Suite', 2, 4, 2000000.00
    UNION ALL SELECT 'Family', 3, 6, 2500000.00
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM room_classes);

-- Re-runnable local test inventory. Existing room numbers are preserved.
INSERT IGNORE INTO rooms (room_number, room_class_id, status, description)
SELECT seed.room_number, rc.id, seed.status, seed.description
FROM (
    SELECT '101' AS room_number, 'Standard' AS class_name, 'AVAILABLE' AS status, 'Standard room on floor 1' AS description
    UNION ALL SELECT '102', 'Standard', 'AVAILABLE', 'Standard room on floor 1'
    UNION ALL SELECT '103', 'Standard', 'DIRTY', 'Standard room awaiting cleaning'
    UNION ALL SELECT '202', 'Suite', 'AVAILABLE', 'Suite room on floor 2'
    UNION ALL SELECT '203', 'Suite', 'AVAILABLE', 'Suite room on floor 2'
    UNION ALL SELECT '204', 'Suite', 'MAINTENANCE', 'Suite room under maintenance'
    UNION ALL SELECT '301', 'Family', 'AVAILABLE', 'Family room on floor 3'
    UNION ALL SELECT '302', 'Family', 'AVAILABLE', 'Family room on floor 3'
    UNION ALL SELECT '303', 'Family', 'OCCUPIED', 'Occupied family room for status testing'
    UNION ALL SELECT '401', 'SupFamily', 'AVAILABLE', 'Large family room on floor 4'
    UNION ALL SELECT '402', 'SupFamily', 'AVAILABLE', 'Large family room on floor 4'
    UNION ALL SELECT '403', 'SupFamily', 'DIRTY', 'Large family room awaiting cleaning'
) seed
JOIN room_classes rc ON rc.class_name = seed.class_name;

INSERT INTO hotel_services (service_name, category, unit_price, description)
SELECT seed.service_name, seed.category, seed.unit_price, seed.description
FROM (
    SELECT 'Aroma Massage' AS service_name, 'SPA' AS category, 450000.00 AS unit_price,
           'A relaxing full-body aroma massage.' AS description
    UNION ALL SELECT 'Minibar Soft Drink', 'MINIBAR', 40000.00, 'Chilled soft drink delivered to your room.'
    UNION ALL SELECT 'Breakfast Set', 'F_AND_B', 180000.00, 'Hotel breakfast set for one guest.'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM hotel_services);
