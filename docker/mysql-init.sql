CREATE DATABASE IF NOT EXISTS auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS room_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS billing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS review_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'hotel_user'@'%' IDENTIFIED BY 'hotel_pass';
GRANT ALL PRIVILEGES ON auth_db.* TO 'hotel_user'@'%';
GRANT ALL PRIVILEGES ON room_db.* TO 'hotel_user'@'%';
GRANT ALL PRIVILEGES ON booking_db.* TO 'hotel_user'@'%';
GRANT ALL PRIVILEGES ON billing_db.* TO 'hotel_user'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'hotel_user'@'%';
GRANT ALL PRIVILEGES ON review_db.* TO 'hotel_user'@'%';
FLUSH PRIVILEGES;
