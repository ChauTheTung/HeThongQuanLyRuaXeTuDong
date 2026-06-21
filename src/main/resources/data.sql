-- Dữ liệu test cho Hệ Thống Quản Lý Rửa Xe Tự Động

-- Thêm Loyalty Tiers
INSERT INTO loyalty_tiers (name, min_points, max_points, discount_percent) VALUES
('Bronze', 0, 499, 0.0),
('Silver', 500, 1199, 5.0),
('Gold', 1200, 19999, 10.0),
('Platinum', 20000, NULL, 15.0);

-- Thêm Users (Admin)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$VxOW9jJQ7VHVVeU8mOxUJ.MxLkn0AsDIQvVPzFoHvHJQ0OiM9Bi7S', 'ROLE_ADMIN');

-- Thêm Dịch Vụ và Giá
INSERT INTO service_pricing (service_name, vehicle_type, price, description) VALUES
('Rửa xe tiêu chuẩn', 'Sedan', 150000, 'Rửa ngoài và hút bụi nội thất'),
('Rửa xe cao cấp', 'Sedan', 250000, 'Rửa ngoài, hút bụi, phủ bóng'),
('Rửa xe tiêu chuẩn', 'SUV', 200000, 'Rửa ngoài và hút bụi nội thất'),
('Rửa xe tiêu chuẩn', 'Pickup', 200000, 'Rửa ngoài và hút bụi nội thất'),
('Rửa xe tiêu chuẩn', 'Xe máy', 50000, 'Rửa bọt tuyết');

-- Thêm Promotions
INSERT INTO promotions (name, discount_percent, start_date, end_date, description, active) VALUES
('Khuyến mãi Tết', 20.00, '2024-01-01 00:00:00', '2024-01-31 23:59:59', 'Giảm 20% cho tất cả dịch vụ', TRUE),
('Khách hàng VIP', 15.00, '2024-01-15 00:00:00', '2024-12-31 23:59:59', 'Giảm 15% cho khách hàng hạng Gold trở lên', TRUE),
('Lần đầu rửa', 30.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Giảm 30% cho khách hàng mới', TRUE);
