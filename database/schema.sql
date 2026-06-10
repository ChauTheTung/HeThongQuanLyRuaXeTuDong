-- 1. Tạo bảng Users (Lưu tài khoản đăng nhập hệ thống)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'CUSTOMER' hoặc 'ADMIN'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tạo bảng Customers (Thông tin hồ sơ khách hàng & điểm thưởng)
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    current_points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Tạo bảng Vehicles (Thông tin phương tiện - Phần việc cốt lõi của bạn)
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,              -- Liên kết đến khách hàng chủ sở hữu
    license_plate VARCHAR(20) NOT NULL UNIQUE, -- Biển số xe (đảm bảo duy nhất toàn hệ thống)
    vehicle_type VARCHAR(50) NOT NULL,         -- Loại xe (Xe ga, xe số, côn tay...)
    brand VARCHAR(50),                         -- Hãng sản xuất (Honda, Yamaha, Vespa...)
    color VARCHAR(30),                         -- Màu sắc xe
    notes TEXT,                                -- Ghi chú (Xe trầy xước, dán decal...)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);