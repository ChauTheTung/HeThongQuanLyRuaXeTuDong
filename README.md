# Hướng Dẫn Sử Dụng & Thiết Lập Hệ Thống AutoWash Pro

Chào mừng bạn đến với **AutoWash Pro** - Hệ thống Quản lý Rửa xe Tự động viết bằng Spring Boot (Java 17), Thymeleaf và MySQL.

Tài liệu này sẽ hướng dẫn chi tiết cách thiết lập cơ sở dữ liệu MySQL, cách cấu hình và khởi chạy ứng dụng từ đầu.

---

## 1. Yêu Cầu Hệ Thống
Trước khi bắt đầu, hãy đảm bảo máy tính của bạn đã cài đặt:
* **Java Development Kit (JDK)**: Phiên bản 17 trở lên.
* **Apache Maven**: Phiên bản 3.6 trở lên.
* **MySQL Server**: Phiên bản 8.0 trở lên.
* **Công cụ quản lý MySQL**: MySQL Workbench, DBeaver, HeidiSQL hoặc công cụ tương đương.

---

## 2. Hướng Dẫn Thiết Lập MySQL Database

Hệ thống sử dụng **Hibernate JPA** để tự động tạo cấu trúc bảng (Table schema) khi khởi chạy lần đầu tiên. Bạn chỉ cần làm theo các bước sau:

### Bước 2.1: Tạo Database mới
1. Mở phần mềm quản lý MySQL của bạn và kết nối vào máy chủ cục bộ (localhost).
2. Chạy câu lệnh SQL sau để tạo một cơ sở dữ liệu trống:
   ```sql
   CREATE DATABASE autowash_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

### Bước 2.2: Cấu hình kết nối Spring Boot
Mở file `src/main/resources/application.properties` và cấu hình tài khoản kết nối MySQL của bạn:
```properties
# URL kết nối đến database vừa tạo
spring.datasource.url=jdbc:mysql://localhost:3306/autowash_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# Tên tài khoản MySQL của bạn (thường mặc định là root)
spring.datasource.username=root

# Mật khẩu tài khoản MySQL trên máy bạn
spring.datasource.password=123456

# Chế độ tự động tạo/cập nhật bảng của Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 3. Cách Khởi Chạy Ứng Dụng

### Cách 1: Chạy trực tiếp từ IDE (Khuyên dùng)
1. Mở thư mục dự án `d:\HeThongQuanLyRuaXeTuDong` bằng công cụ lập trình của bạn (**IntelliJ IDEA**, **Eclipse** hoặc **VS Code**).
2. Đợi IDE tải và biên dịch xong các thư viện trong file `pom.xml`.
3. Tìm đến class `com.autowash.AutowashProApplication` (nằm trong thư mục `src/main/java/com/autowash/AutowashProApplication.java`).
4. Nhấp chuột phải vào file này và chọn **Run** (hoặc nhấn nút Run màu xanh).

### Cách 2: Chạy bằng Dòng lệnh (Terminal)
1. Mở ứng dụng Command Prompt hoặc PowerShell tại thư mục gốc của dự án (`d:\HeThongQuanLyRuaXeTuDong`).
2. Thực hiện lệnh sau để khởi chạy ứng dụng:
   ```bash
   mvn spring-boot:run
   ```

Khi console hiển thị dòng chữ:
```text
Tomcat started on port 8080 (http) with context path '/'
Started AutowashProApplication in ... seconds
```
Điều này nghĩa là hệ thống đã được chạy thành công trên cổng mạng `8080`.

---

## 4. Nạp Dữ Liệu Chạy Thử Nghiệm (Seeding Data)

Khi ứng dụng chạy lần đầu tiên, hệ thống đã tự động tạo sẵn cấu trúc bảng và chèn tài khoản **Admin mặc định** cùng cấu hình **Hạng thành viên (Loyalty Tiers)**.

Để nạp thêm các dữ liệu mẫu khác (Khách hàng thử nghiệm, Xe đã đăng ký, Lịch sử rửa xe, Ưu đãi khuyến mãi), bạn hãy làm như sau:
1. Mở phần mềm quản lý MySQL và chọn database `autowash_db`.
2. Copy đoạn lệnh SQL dưới đây và thực thi (Execute) trong database:

```sql
USE autowash_db;

-- 1. Thêm Customers mẫu
INSERT INTO customers (user_id, full_name, phone_number, email, loyalty_points) VALUES
(2, 'Nguyễn Văn A', '0912345678', 'customer1@email.com', 2500),
(3, 'Trần Thị B', '0987654321', 'customer2@email.com', 8500),
(4, 'Lê Văn C', '0901234567', 'customer3@email.com', 15000);

-- 2. Thêm Vehicles mẫu
INSERT INTO vehicles (customer_id, license_plate, brand, vehicle_type, color, notes) VALUES
(1, '29A-12345', 'Toyota', 'Sedan', 'Bạc', 'Xe mới'),
(1, '29A-12346', 'Honda', 'Sedan', 'Đen', NULL),
(2, '30B-54321', 'Hyundai', 'Sedan', 'Trắng', 'Xe gia đình'),
(3, '31C-99999', 'Ford', 'Pickup', 'Xanh', 'Xe công việc');

-- 3. Thêm Bookings mẫu
INSERT INTO bookings (customer_id, vehicle_id, booking_time, status, total_price) VALUES
(1, 1, '2024-01-15 09:00:00', 'COMPLETED', 150000),
(1, 1, '2024-01-20 14:30:00', 'COMPLETED', 150000),
(2, 3, '2024-01-22 10:00:00', 'PENDING', 200000),
(2, 3, '2024-01-25 15:00:00', 'CANCELLED', 200000),
(3, 4, '2024-01-28 08:00:00', 'CONFIRMED', 250000),
(1, 2, '2024-02-01 11:00:00', 'PENDING', 180000);

-- 4. Thêm Wash History mẫu
INSERT INTO wash_history (customer_id, vehicle_id, booking_id, service_type, price, performed_at) VALUES
(1, 1, 1, 'Basic Wash', 150000, '2024-01-15 09:30:00'),
(1, 1, 2, 'Premium Wash', 150000, '2024-01-20 14:45:00'),
(2, 3, NULL, 'Full Service', 200000, '2024-01-22 11:00:00'),
(3, 4, 5, 'Basic Wash', 250000, '2024-01-28 08:45:00');

-- 5. Thêm Point Transactions mẫu
INSERT INTO point_transactions (customer_id, points, transaction_type, description) VALUES
(1, 250, 'EARNED', 'Từ booking #1'),
(1, 250, 'EARNED', 'Từ booking #2'),
(2, 400, 'EARNED', 'Từ booking #3'),
(3, -100, 'REDEEMED', 'Đổi voucher'),
(3, 500, 'EARNED', 'Từ booking #5');

-- 6. Thêm Promotions mẫu
INSERT INTO promotions (name, discount_percent, start_date, end_date, description, active) VALUES
('Khuyến mãi Tết', 20.00, '2024-01-01 00:00:00', '2024-01-31 23:59:59', 'Giảm 20% cho tất cả dịch vụ', TRUE),
('Khách hàng VIP', 15.00, '2024-01-15 00:00:00', '2024-12-31 23:59:59', 'Giảm 15% cho khách hàng hạng Gold trở lên', TRUE),
('Lần đầu rửa', 30.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'Giảm 30% cho khách hàng mới', TRUE);
```

---

## 5. Hướng Dẫn Trải Nghiệm Hệ Thống

Sau khi cài đặt xong dữ liệu, hãy mở trình duyệt và truy cập `http://localhost:8080/`.

### 5.1 Đăng nhập dưới vai trò Admin
* **Đường dẫn**: `http://localhost:8080/login`
* **Username**: `admin`
* **Password**: `admin1234`
* **Chức năng chính**: 
  * Quản lý đặt chỗ (Bookings): xác nhận, cập nhật trạng thái đặt lịch.
  * Quản lý khách hàng (Customers): xem danh sách, chỉnh sửa hồ sơ khách hàng.
  * Thiết lập Loyalty Tiers & Promotions: cấu hình ưu đãi tích điểm và khuyến mãi giảm giá.
  * Xem báo cáo doanh thu & biểu đồ thống kê.

### 5.2 Trải nghiệm vai trò Khách hàng (Customer)
1. Truy cập trang đăng ký: `http://localhost:8080/register`
2. Điền thông tin cá nhân (Họ tên, Email, Số điện thoại và Mật khẩu) để tạo tài khoản mới.
3. Đăng nhập bằng tài khoản vừa tạo.
4. **Các tính năng khách hàng**:
  * **Xe của tôi**: Đăng ký biển số xe, hãng xe, loại xe (SUV/Sedan).
  * **Đặt lịch rửa xe**: Lên lịch rửa xe, chọn xe đã đăng ký, theo dõi trạng thái.
  * **Đổi điểm thưởng**: Kiểm tra số điểm tích lũy được sau mỗi lần rửa xe để đổi mã giảm giá.
  * **Xem khuyến mãi**: Theo dõi các chiến dịch khuyến mãi đang diễn ra.
