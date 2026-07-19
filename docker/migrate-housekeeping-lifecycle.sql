-- Idempotent schema/data migration for persisted housekeeping lifecycle.
USE room_db;

DELIMITER $$
DROP PROCEDURE IF EXISTS add_housekeeping_column $$
CREATE PROCEDURE add_housekeeping_column(IN column_name_value VARCHAR(64), IN definition_value VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'room_db'
          AND table_name = 'housekeeping_tasks'
          AND column_name = column_name_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE room_db.housekeeping_tasks ADD COLUMN ', definition_value);
        PREPARE statement_value FROM @ddl;
        EXECUTE statement_value;
        DEALLOCATE PREPARE statement_value;
    END IF;
END $$
DELIMITER ;

CALL add_housekeeping_column('started_at', 'started_at DATETIME(6) NULL');
CALL add_housekeeping_column('completed_at', 'completed_at DATETIME(6) NULL');
CALL add_housekeeping_column('cancelled_at', 'cancelled_at DATETIME(6) NULL');
CALL add_housekeeping_column('completed_steps', 'completed_steps INT NOT NULL DEFAULT 0');
DROP PROCEDURE add_housekeeping_column;

UPDATE room_db.housekeeping_tasks
SET started_at = COALESCE(started_at, created_at)
WHERE status IN ('IN_PROGRESS', 'COMPLETED');

UPDATE room_db.housekeeping_tasks
SET completed_at = COALESCE(completed_at, created_at),
    completed_steps = CASE WHEN task_type = 'CLEANING' THEN 9 ELSE 3 END
WHERE status = 'COMPLETED';

UPDATE room_db.housekeeping_tasks
SET cancelled_at = COALESCE(cancelled_at, created_at)
WHERE status = 'CANCELLED';

SELECT id, room_id, task_type, status, completed_steps, started_at, completed_at, cancelled_at
FROM room_db.housekeeping_tasks
ORDER BY id;
