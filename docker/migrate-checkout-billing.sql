-- Idempotent migration for receptionist checkout, folio charge reconciliation and actual stay timestamps.
USE booking_db;

DELIMITER $$
DROP PROCEDURE IF EXISTS add_checkout_column $$
CREATE PROCEDURE add_checkout_column(IN table_value VARCHAR(64), IN column_value VARCHAR(64), IN definition_value VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = table_value AND column_name = column_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', table_value, '` ADD COLUMN ', definition_value);
        PREPARE statement_value FROM @ddl;
        EXECUTE statement_value;
        DEALLOCATE PREPARE statement_value;
    END IF;
END $$
DELIMITER ;

CALL add_checkout_column('reservations', 'checked_in_at', 'checked_in_at DATETIME(6) NULL');
CALL add_checkout_column('reservations', 'checked_out_at', 'checked_out_at DATETIME(6) NULL');
DROP PROCEDURE add_checkout_column;

UPDATE booking_db.reservations r
JOIN (SELECT reservation_id, MIN(check_in_date) check_in_date FROM booking_db.reservation_rooms GROUP BY reservation_id) rr
  ON rr.reservation_id = r.id
SET r.checked_in_at = TIMESTAMP(rr.check_in_date, '14:00:00')
WHERE r.checked_in_at IS NULL AND r.booking_status IN ('IN_HOUSE', 'CHECKED_OUT');

UPDATE booking_db.reservations r
JOIN (SELECT reservation_id, MAX(check_out_date) check_out_date FROM booking_db.reservation_rooms GROUP BY reservation_id) rr
  ON rr.reservation_id = r.id
SET r.checked_out_at = TIMESTAMP(rr.check_out_date, '12:00:00')
WHERE r.checked_out_at IS NULL AND r.booking_status = 'CHECKED_OUT';

USE billing_db;
ALTER TABLE folio_items
    MODIFY COLUMN item_type ENUM(
        'ROOM_CHARGE', 'MINIBAR', 'SERVICE', 'DAMAGE', 'TIME_SURCHARGE', 'OTHER'
    ) NOT NULL;

DELIMITER $$
DROP PROCEDURE IF EXISTS add_folio_item_column $$
CREATE PROCEDURE add_folio_item_column(IN column_value VARCHAR(64), IN definition_value VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'folio_items' AND column_name = column_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE folio_items ADD COLUMN ', definition_value);
        PREPARE statement_value FROM @ddl;
        EXECUTE statement_value;
        DEALLOCATE PREPARE statement_value;
    END IF;
END $$
DELIMITER ;

CALL add_folio_item_column('description', 'description VARCHAR(255) NULL');
CALL add_folio_item_column('reference_key', 'reference_key VARCHAR(100) NULL');
DROP PROCEDURE add_folio_item_column;

DELIMITER $$
DROP PROCEDURE IF EXISTS add_folio_reference_index $$
CREATE PROCEDURE add_folio_reference_index()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE() AND table_name = 'folio_items' AND index_name = 'uk_folio_item_reference'
    ) THEN
        ALTER TABLE folio_items ADD CONSTRAINT uk_folio_item_reference UNIQUE (folio_id, reference_key);
    END IF;
END $$
DELIMITER ;
CALL add_folio_reference_index();
DROP PROCEDURE add_folio_reference_index;

USE room_db;
DELIMITER $$
DROP PROCEDURE IF EXISTS add_damage_reservation_column $$
CREATE PROCEDURE add_damage_reservation_column()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'damage_reports' AND column_name = 'reservation_id'
    ) THEN
        ALTER TABLE damage_reports ADD COLUMN reservation_id BIGINT NULL;
    END IF;
END $$
DELIMITER ;
CALL add_damage_reservation_column();
DROP PROCEDURE add_damage_reservation_column;
