-- Seed booking history for customer@test.local (password: Test@123)
-- Idempotent: skips if booking codes already exist.

USE booking_db;

SET @customer_id := (
  SELECT u.id FROM auth_db.users u
  JOIN auth_db.roles r ON r.id = u.role_id
  WHERE u.username = 'customer@test.local' AND r.role_name = 'CUSTOMER'
  LIMIT 1
);

SET @room_101 := (SELECT id FROM room_db.rooms WHERE room_number = '101' LIMIT 1);
SET @room_102 := (SELECT id FROM room_db.rooms WHERE room_number = '102' LIMIT 1);
SET @room_202 := (SELECT id FROM room_db.rooms WHERE room_number = '202' LIMIT 1);
SET @room_301 := (SELECT id FROM room_db.rooms WHERE room_number = '301' LIMIT 1);

-- Fallback if seed rooms missing: pick any available rooms
SET @room_101 := IFNULL(@room_101, (SELECT id FROM room_db.rooms ORDER BY id LIMIT 1));
SET @room_102 := IFNULL(@room_102, (SELECT id FROM room_db.rooms ORDER BY id LIMIT 1 OFFSET 1));
SET @room_202 := IFNULL(@room_202, (SELECT id FROM room_db.rooms ORDER BY id LIMIT 1 OFFSET 2));
SET @room_301 := IFNULL(@room_301, (SELECT id FROM room_db.rooms ORDER BY id LIMIT 1 OFFSET 3));

-- 1) Past stay - CHECKED_OUT (history / finished tab)
INSERT INTO reservations (booking_code, customer_id, booking_status, checked_in_at, checked_out_at)
SELECT 'BK-HIST-001', @customer_id, 'CHECKED_OUT', '2026-07-01 14:00:00', '2026-07-03 12:00:00'
WHERE @customer_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE booking_code = 'BK-HIST-001');

INSERT INTO reservation_rooms (reservation_id, room_id, check_in_date, check_out_date, guest_count)
SELECT r.id, @room_101, '2026-07-01', '2026-07-03', 2
FROM reservations r
WHERE r.booking_code = 'BK-HIST-001'
  AND @room_101 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservation_rooms rr WHERE rr.reservation_id = r.id);

-- 2) Another past stay - CHECKED_OUT (Suite)
INSERT INTO reservations (booking_code, customer_id, booking_status, checked_in_at, checked_out_at)
SELECT 'BK-HIST-002', @customer_id, 'CHECKED_OUT', '2026-06-10 14:00:00', '2026-06-13 12:00:00'
WHERE @customer_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE booking_code = 'BK-HIST-002');

INSERT INTO reservation_rooms (reservation_id, room_id, check_in_date, check_out_date, guest_count)
SELECT r.id, @room_202, '2026-06-10', '2026-06-13', 2
FROM reservations r
WHERE r.booking_code = 'BK-HIST-002'
  AND @room_202 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservation_rooms rr WHERE rr.reservation_id = r.id);

-- 3) Upcoming - PENDING (cancellable in UI)
INSERT INTO reservations (booking_code, customer_id, booking_status, checked_in_at, checked_out_at)
SELECT 'BK-HIST-003', @customer_id, 'PENDING', NULL, NULL
WHERE @customer_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE booking_code = 'BK-HIST-003');

INSERT INTO reservation_rooms (reservation_id, room_id, check_in_date, check_out_date, guest_count)
SELECT r.id, @room_102, '2026-07-25', '2026-07-27', 1
FROM reservations r
WHERE r.booking_code = 'BK-HIST-003'
  AND @room_102 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservation_rooms rr WHERE rr.reservation_id = r.id);

-- 4) Currently staying - IN_HOUSE
INSERT INTO reservations (booking_code, customer_id, booking_status, checked_in_at, checked_out_at)
SELECT 'BK-HIST-004', @customer_id, 'IN_HOUSE', '2026-07-20 14:00:00', NULL
WHERE @customer_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE booking_code = 'BK-HIST-004');

INSERT INTO reservation_rooms (reservation_id, room_id, check_in_date, check_out_date, guest_count)
SELECT r.id, @room_301, '2026-07-20', '2026-07-23', 3
FROM reservations r
WHERE r.booking_code = 'BK-HIST-004'
  AND @room_301 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservation_rooms rr WHERE rr.reservation_id = r.id);

UPDATE room_db.rooms
SET status = 'OCCUPIED'
WHERE id = @room_301
  AND EXISTS (SELECT 1 FROM reservations WHERE booking_code = 'BK-HIST-004' AND booking_status = 'IN_HOUSE');

-- 5) Cancelled booking
INSERT INTO reservations (booking_code, customer_id, booking_status, checked_in_at, checked_out_at)
SELECT 'BK-HIST-005', @customer_id, 'CANCELLED', NULL, NULL
WHERE @customer_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservations WHERE booking_code = 'BK-HIST-005');

INSERT INTO reservation_rooms (reservation_id, room_id, check_in_date, check_out_date, guest_count)
SELECT r.id, @room_101, '2026-05-01', '2026-05-03', 1
FROM reservations r
WHERE r.booking_code = 'BK-HIST-005'
  AND @room_101 IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM reservation_rooms rr WHERE rr.reservation_id = r.id);

-- Occupants for IN_HOUSE reservation (receptionist detail views)
INSERT INTO room_occupants (reservation_room_id, guest_name, phone_number, identity_document, residence)
SELECT rr.id, 'Test Customer', '0901234567', 'CCCD-001122', 'Ho Chi Minh City'
FROM reservation_rooms rr
JOIN reservations r ON r.id = rr.reservation_id
WHERE r.booking_code = 'BK-HIST-004'
  AND NOT EXISTS (SELECT 1 FROM room_occupants o WHERE o.reservation_room_id = rr.id);

-- Summary
SELECT r.booking_code, r.customer_id, r.booking_status, rr.check_in_date, rr.check_out_date, rr.room_id, rm.room_number
FROM reservations r
LEFT JOIN reservation_rooms rr ON rr.reservation_id = r.id
LEFT JOIN room_db.rooms rm ON rm.id = rr.room_id
WHERE r.booking_code LIKE 'BK-HIST-%'
ORDER BY r.booking_code;
