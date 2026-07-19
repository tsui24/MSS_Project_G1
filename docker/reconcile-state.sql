-- Repairs lifecycle inconsistencies between booking_db and room_db.
-- This script is idempotent: it is safe to run again after service interruption.

START TRANSACTION;

-- A housekeeping task must not remain active after its room has been assigned
-- to an IN_HOUSE reservation. Keep the row for audit instead of deleting it.
UPDATE room_db.housekeeping_tasks task
JOIN room_db.rooms room ON room.id = task.room_id
SET task.status = 'CANCELLED'
WHERE task.status IN ('PENDING', 'IN_PROGRESS')
  AND EXISTS (
      SELECT 1
      FROM booking_db.reservation_rooms assignment
      JOIN booking_db.reservations reservation ON reservation.id = assignment.reservation_id
      WHERE assignment.room_id = room.id
        AND reservation.booking_status = 'IN_HOUSE'
  );

-- An OCCUPIED room without an IN_HOUSE reservation is unsafe to keep occupied.
-- Mark it DIRTY so it must pass through housekeeping before becoming AVAILABLE.
UPDATE room_db.rooms room
SET room.status = 'DIRTY'
WHERE room.status = 'OCCUPIED'
  AND NOT EXISTS (
      SELECT 1
      FROM booking_db.reservation_rooms assignment
      JOIN booking_db.reservations reservation ON reservation.id = assignment.reservation_id
      WHERE assignment.room_id = room.id
        AND reservation.booking_status = 'IN_HOUSE'
  );

-- IN_HOUSE is the authoritative owner of physical occupancy.
UPDATE room_db.rooms room
SET room.status = 'OCCUPIED'
WHERE EXISTS (
    SELECT 1
    FROM booking_db.reservation_rooms assignment
    JOIN booking_db.reservations reservation ON reservation.id = assignment.reservation_id
    WHERE assignment.room_id = room.id
      AND reservation.booking_status = 'IN_HOUSE'
);

-- Reconcile active housekeeping rooms that are not occupied.
UPDATE room_db.rooms room
JOIN room_db.housekeeping_tasks task ON task.room_id = room.id
SET room.status = CASE
    WHEN task.task_type <> 'CLEANING' THEN 'MAINTENANCE'
    WHEN task.status = 'IN_PROGRESS' THEN 'CLEANING'
    ELSE 'DIRTY'
END
WHERE task.status IN ('PENDING', 'IN_PROGRESS')
  AND NOT EXISTS (
      SELECT 1
      FROM booking_db.reservation_rooms assignment
      JOIN booking_db.reservations reservation ON reservation.id = assignment.reservation_id
      WHERE assignment.room_id = room.id
        AND reservation.booking_status = 'IN_HOUSE'
  );

-- Occupant rows are authoritative for assignments created by the new check-in flow.
-- Legacy zero values with no occupant rows are intentionally left unresolved.
UPDATE booking_db.reservation_rooms assignment
JOIN (
    SELECT reservation_room_id, COUNT(*) AS occupant_count
    FROM booking_db.room_occupants
    GROUP BY reservation_room_id
) occupants ON occupants.reservation_room_id = assignment.id
SET assignment.guest_count = occupants.occupant_count
WHERE occupants.occupant_count > 0
  AND assignment.guest_count <> occupants.occupant_count;

COMMIT;

-- Verification: the first four result sets should be empty after reconciliation.
SELECT 'IN_HOUSE_ROOM_NOT_OCCUPIED' AS violation, reservation.id AS reservation_id,
       assignment.id AS reservation_room_id, room.id AS room_id, room.room_number, room.status
FROM booking_db.reservations reservation
JOIN booking_db.reservation_rooms assignment ON assignment.reservation_id = reservation.id
JOIN room_db.rooms room ON room.id = assignment.room_id
WHERE reservation.booking_status = 'IN_HOUSE' AND room.status <> 'OCCUPIED';

SELECT 'OCCUPIED_WITHOUT_IN_HOUSE' AS violation, room.id AS room_id, room.room_number, room.status
FROM room_db.rooms room
WHERE room.status = 'OCCUPIED'
  AND NOT EXISTS (
      SELECT 1 FROM booking_db.reservation_rooms assignment
      JOIN booking_db.reservations reservation ON reservation.id = assignment.reservation_id
      WHERE assignment.room_id = room.id AND reservation.booking_status = 'IN_HOUSE'
  );

SELECT 'ACTIVE_TASK_ON_OCCUPIED_ROOM' AS violation, task.id AS task_id,
       task.room_id, task.status AS task_status, room.status AS room_status
FROM room_db.housekeeping_tasks task
JOIN room_db.rooms room ON room.id = task.room_id
WHERE task.status IN ('PENDING', 'IN_PROGRESS') AND room.status = 'OCCUPIED';

SELECT 'OCCUPANT_COUNT_MISMATCH' AS violation, assignment.id AS reservation_room_id,
       assignment.guest_count, COUNT(occupant.id) AS occupant_count
FROM booking_db.reservation_rooms assignment
JOIN booking_db.room_occupants occupant ON occupant.reservation_room_id = assignment.id
GROUP BY assignment.id, assignment.guest_count
HAVING assignment.guest_count <> COUNT(occupant.id);

-- Informational legacy rows that cannot be inferred safely.
SELECT 'LEGACY_GUEST_COUNT_UNKNOWN' AS warning, assignment.id AS reservation_room_id,
       assignment.reservation_id, assignment.room_id
FROM booking_db.reservation_rooms assignment
LEFT JOIN booking_db.room_occupants occupant ON occupant.reservation_room_id = assignment.id
WHERE assignment.guest_count = 0
GROUP BY assignment.id, assignment.reservation_id, assignment.room_id
HAVING COUNT(occupant.id) = 0;
