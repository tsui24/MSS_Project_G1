INSERT IGNORE INTO notification_templates (type, title, body_template) VALUES
    ('BOOKING_CONFIRMATION', 'Booking confirmed', 'Your reservation has been confirmed. We look forward to hosting you!'),
    ('CHECKIN_REMINDER', 'Check-in reminder', 'This is a reminder that your check-in date is approaching.'),
    ('CHECKOUT_REMINDER', 'Check-out reminder', 'This is a reminder that your check-out date is approaching.'),
    ('INVOICE', 'Your invoice is ready', 'Your final invoice for this stay is now available.');
