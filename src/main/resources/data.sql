-- Dữ liệu test cho Hệ Thống Quản Lý Rửa Xe Tự Động

-- Thêm Loyalty Tiers
INSERT INTO loyalty_tiers (name, min_points, max_points, description) VALUES
('Bronze', 0, 4999, 'Hạng đồng - Thành viên mới'),
('Silver', 5000, 9999, 'Hạng bạc - Thành viên thường xuyên'),
('Gold', 10000, 19999, 'Hạng vàng - Khách hàng VIP'),
('Platinum', 20000, NULL, 'Hạng bạch kim - Khách hàng cao cấp');

-- Thêm Users (Admin)
INSERT INTO users (username, password, email, full_name, role, is_active) VALUES
('admin', '$2a$10$VxOW9jJQ7VHVVeU8mOxUJ.MxLkn0AsDIQvVPzFoHvHJQ0OiM9Bi7S', 'admin@autowash.com', 'Quản Trị Viên', 'ADMIN', TRUE);

-- Thêm Users (Customers)
INSERT INTO users (username, password, email, full_name, role, is_active) VALUES
('customer1', '$2a$10$VxOW9jJQ7VHVVeU8mOxUJ.MxLkn0AsDIQvVPzFoHvHJQ0OiM9Bi7S', 'customer1@email.com', 'Nguyễn Văn A', 'CUSTOMER', TRUE),
('customer2', '$2a$10$VxOW9jJQ7VHVVeU8mOxUJ.MxLkn0AsDIQvVPzFoHvHJQ0OiM9Bi7S', 'customer2@email.com', 'Trần Thị B', 'CUSTOMER', TRUE),
('customer3', '$2a$10$VxOW9jJQ7VHVVeU8mOxUJ.MxLkn0AsDIQvVPzFoHvHJQ0OiM9Bi7S', 'customer3@email.com', 'Lê Văn C', 'CUSTOMER', TRUE);

-- Thêm Customers
INSERT INTO customers (user_id, full_name, phone_number, email, loyalty_points) VALUES
(2, 'Nguyễn Văn A', '0912345678', 'customer1@email.com', 2500),
(3, 'Trần Thị B', '0987654321', 'customer2@email.com', 8500),
(4, 'Lê Văn C', '0901234567', 'customer3@email.com', 15000);

-- Thêm Vehicles
INSERT INTO vehicles (customer_id, license_plate, brand, model, vehicle_type, color, notes) VALUES
(1, '29A-12345', 'Toyota', 'Camry', 'Sedan', 'Bạc', 'Xe mới'),
(1, '29A-12346', 'Honda', 'Civic', 'Sedan', 'Đen', NULL),
(2, '30B-54321', 'Hyundai', 'Elantra', 'Sedan', 'Trắng', 'Xe gia đình'),
(3, '31C-99999', 'Ford', 'Ranger', 'Pickup', 'Xanh', 'Xe công việc');

-- Thêm Bookings
INSERT INTO bookings (customer_id, vehicle_id, booking_time, status, total_price) VALUES
(1, 1, '2024-01-15 09:00:00', 'COMPLETED', 150000),
(1, 1, '2024-01-20 14:30:00', 'COMPLETED', 150000),
(2, 3, '2024-01-22 10:00:00', 'PENDING', 200000),
(2, 3, '2024-01-25 15:00:00', 'CANCELLED', 200000),
(3, 4, '2024-01-28 08:00:00', 'CONFIRMED', 250000),
(1, 2, '2024-02-01 11:00:00', 'PENDING', 180000);

-- Thêm Wash History
INSERT INTO wash_history (customer_id, vehicle_id, booking_id, service_type, price, performed_at) VALUES
(1, 1, 1, 'Basic Wash', 150000, '2024-01-15 09:30:00'),
(1, 1, 2, 'Premium Wash', 150000, '2024-01-20 14:45:00'),
(2, 3, NULL, 'Full Service', 200000, '2024-01-22 11:00:00'),
(3, 4, 5, 'Basic Wash', 250000, '2024-01-28 08:45:00');

-- Thêm Point Transactions
INSERT INTO point_transactions (customer_id, points, transaction_type, description) VALUES
(1, 250, 'EARNED', 'Từ booking #1'),
(1, 250, 'EARNED', 'Từ booking #2'),
(2, 400, 'EARNED', 'Từ booking #3'),
(3, -100, 'REDEEMED', 'Đổi voucher'),
(3, 500, 'EARNED', 'Từ booking #5');

-- Thêm Promotions
INSERT INTO promotions (title, discount_percent, start_date, end_date, description, is_active) VALUES
('Khuyến mãi Tết', 20.00, '2024-01-01', '2024-01-31', 'Giảm 20% cho tất cả dịch vụ', TRUE),
('Khách hàng VIP', 15.00, '2024-01-15', '2024-12-31', 'Giảm 15% cho khách hàng hạng Gold trở lên', TRUE),
('Lần đầu rửa', 30.00, '2024-01-01', '2024-12-31', 'Giảm 30% cho khách hàng mới', TRUE);
