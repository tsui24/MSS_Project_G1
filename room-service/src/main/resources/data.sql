-- Seed only an empty catalog so deleted room types do not reappear on restart.
-- VARCHAR keeps the database compatible when a new RoomStatus enum value is added in Java.
ALTER TABLE rooms MODIFY COLUMN status VARCHAR(20) NOT NULL;

-- Backfill floor from room number prefix when the column is new/null.
UPDATE rooms
SET floor = CAST(LEFT(room_number, 1) AS UNSIGNED)
WHERE floor IS NULL
  AND room_number REGEXP '^[0-9]';

-- Backfill lifecycle fields added to existing housekeeping tasks. Hibernate creates
-- the columns before this script runs; COALESCE keeps the migration idempotent.
UPDATE housekeeping_tasks
SET started_at = COALESCE(started_at, created_at)
WHERE status IN ('IN_PROGRESS', 'COMPLETED');

UPDATE housekeeping_tasks
SET completed_at = COALESCE(completed_at, created_at),
    completed_steps = CASE WHEN task_type = 'CLEANING' THEN 9 ELSE 3 END
WHERE status = 'COMPLETED';

UPDATE housekeeping_tasks
SET cancelled_at = COALESCE(cancelled_at, created_at)
WHERE status = 'CANCELLED';

INSERT INTO room_classes (class_name, standard_occupancy, max_occupancy, base_price, amenities)
SELECT seed.class_name, seed.standard_occupancy, seed.max_occupancy, seed.base_price, seed.amenities
FROM (
    SELECT 'Standard' AS class_name, 1 AS standard_occupancy, 2 AS max_occupancy, 800000.00 AS base_price,
           CAST('["Wi-Fi","Air conditioning","TV"]' AS JSON) AS amenities
    UNION ALL SELECT 'Deluxe', 2, 3, 1200000.00, CAST('["Wi-Fi","Air conditioning","Mini fridge","Bathtub"]' AS JSON)
    UNION ALL SELECT 'Suite', 2, 4, 2000000.00, CAST('["Wi-Fi","Living area","Kitchenette","Bathtub"]' AS JSON)
    UNION ALL SELECT 'Family', 3, 6, 2500000.00, CAST('["Wi-Fi","Extra beds","Air conditioning"]' AS JSON)
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM room_classes);

-- Backfill amenities JSON for existing room classes that still have NULL.
UPDATE room_classes
SET amenities = CAST('["Wi-Fi","Air conditioning","TV"]' AS JSON)
WHERE class_name = 'Standard' AND (amenities IS NULL OR amenities = CAST('null' AS JSON) OR JSON_LENGTH(amenities) = 0);

UPDATE room_classes
SET amenities = CAST('["Wi-Fi","Air conditioning","Mini fridge","Bathtub"]' AS JSON)
WHERE class_name = 'Deluxe' AND (amenities IS NULL OR amenities = CAST('null' AS JSON) OR JSON_LENGTH(amenities) = 0);

UPDATE room_classes
SET amenities = CAST('["Wi-Fi","Living area","Kitchenette","Bathtub"]' AS JSON)
WHERE class_name = 'Suite' AND (amenities IS NULL OR amenities = CAST('null' AS JSON) OR JSON_LENGTH(amenities) = 0);

UPDATE room_classes
SET amenities = CAST('["Wi-Fi","Extra beds","Air conditioning"]' AS JSON)
WHERE class_name = 'Family' AND (amenities IS NULL OR amenities = CAST('null' AS JSON) OR JSON_LENGTH(amenities) = 0);

-- Re-runnable local test inventory. Existing room numbers are preserved.
INSERT IGNORE INTO rooms (room_number, room_class_id, status, description, floor)
SELECT seed.room_number, rc.id, seed.status, seed.description, seed.floor
FROM (
    SELECT '101' AS room_number, 'Standard' AS class_name, 'AVAILABLE' AS status, 'Standard room on floor 1' AS description, 1 AS floor
    UNION ALL SELECT '102', 'Standard', 'AVAILABLE', 'Standard room on floor 1', 1
    UNION ALL SELECT '103', 'Standard', 'DIRTY', 'Standard room awaiting cleaning', 1
    UNION ALL SELECT '202', 'Suite', 'AVAILABLE', 'Suite room on floor 2', 2
    UNION ALL SELECT '203', 'Suite', 'AVAILABLE', 'Suite room on floor 2', 2
    UNION ALL SELECT '204', 'Suite', 'MAINTENANCE', 'Suite room under maintenance', 2
    UNION ALL SELECT '301', 'Family', 'AVAILABLE', 'Family room on floor 3', 3
    UNION ALL SELECT '302', 'Family', 'AVAILABLE', 'Family room on floor 3', 3
    UNION ALL SELECT '303', 'Family', 'OCCUPIED', 'Occupied family room for status testing', 3
    UNION ALL SELECT '401', 'SupFamily', 'AVAILABLE', 'Large family room on floor 4', 4
    UNION ALL SELECT '402', 'SupFamily', 'AVAILABLE', 'Large family room on floor 4', 4
    UNION ALL SELECT '403', 'SupFamily', 'DIRTY', 'Large family room awaiting cleaning', 4
) seed
JOIN room_classes rc ON rc.class_name = seed.class_name;

INSERT INTO hotel_services (service_name, category, unit_price, description, duration, availability)
SELECT seed.service_name, seed.category, seed.unit_price, seed.description, seed.duration, seed.availability
FROM (
    SELECT 'Aroma Massage' AS service_name, 'SPA' AS category, 450000.00 AS unit_price,
           'A relaxing full-body aroma massage.' AS description, 60 AS duration, TRUE AS availability
    UNION ALL SELECT 'Minibar Soft Drink', 'MINIBAR', 40000.00, 'Chilled soft drink delivered to your room.', NULL, TRUE
    UNION ALL SELECT 'Breakfast Set', 'F_AND_B', 180000.00, 'Hotel breakfast set for one guest.', NULL, TRUE
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM hotel_services);
