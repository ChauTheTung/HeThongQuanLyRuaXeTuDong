# AutoWash Pro Database Design

## 1. User

Lưu thông tin tài khoản đăng nhập hệ thống.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| user_id | BIGINT | PK | ID người dùng |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Tên đăng nhập |
| password | VARCHAR(255) | NOT NULL | Mật khẩu đã mã hóa BCrypt |
| role | VARCHAR(20) | NOT NULL | ADMIN hoặc CUSTOMER |

### Relationships
- 1 User → 1 Customer

---

## 2. LoyaltyTier

Lưu thông tin hạng thành viên.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| tier_id | BIGINT | PK | ID hạng thành viên |
| tier_name | VARCHAR(50) | NOT NULL | Bronze, Silver, Gold, Platinum |
| min_points | INT | NOT NULL | Điểm tối thiểu để đạt hạng |
| booking_window_days | INT | NOT NULL | Số ngày được đặt lịch trước |

### Relationships
- 1 LoyaltyTier → N Customer
- 1 LoyaltyTier → N Promotion

---

## 3. Customer

Lưu thông tin khách hàng.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| customer_id | BIGINT | PK | ID khách hàng |
| user_id | BIGINT | FK → User(user_id) | Tài khoản |
| tier_id | BIGINT | FK → LoyaltyTier(tier_id) | Hạng thành viên |
| full_name | VARCHAR(100) | NOT NULL | Họ tên |
| phone | VARCHAR(20) | NOT NULL | Số điện thoại |
| email | VARCHAR(100) | UNIQUE | Email |
| address | VARCHAR(255) | | Địa chỉ |

### Relationships
- 1 Customer → N Vehicle
- 1 Customer → N Booking
- 1 Customer → N PointTransaction

---

## 4. Vehicle

Lưu thông tin xe của khách hàng.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| vehicle_id | BIGINT | PK | ID xe |
| customer_id | BIGINT | FK → Customer(customer_id) | Chủ sở hữu |
| license_plate | VARCHAR(20) | UNIQUE, NOT NULL | Biển số xe |
| brand | VARCHAR(50) | | Hãng xe |
| model | VARCHAR(50) | | Dòng xe |
| color | VARCHAR(30) | | Màu sắc |

### Relationships
- 1 Vehicle → N Booking

---

## 5. Promotion

Lưu thông tin chương trình khuyến mãi.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| promotion_id | BIGINT | PK | ID khuyến mãi |
| title | VARCHAR(100) | NOT NULL | Tên chương trình |
| discount_percent | DECIMAL(5,2) | NOT NULL | % giảm giá |
| start_date | DATE | NOT NULL | Ngày bắt đầu |
| end_date | DATE | NOT NULL | Ngày kết thúc |
| target_tier | BIGINT | FK → LoyaltyTier(tier_id) | Hạng áp dụng |

### Relationships
- 1 Promotion → N Booking

---

## 6. Booking

Lưu thông tin đặt lịch rửa xe.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| booking_id | BIGINT | PK | ID booking |
| customer_id | BIGINT | FK → Customer(customer_id) | Khách hàng |
| vehicle_id | BIGINT | FK → Vehicle(vehicle_id) | Xe được rửa |
| promotion_id | BIGINT | FK → Promotion(promotion_id) | Khuyến mãi áp dụng |
| booking_date | DATE | NOT NULL | Ngày đặt lịch |
| booking_time | TIME | NOT NULL | Giờ đặt lịch |
| status | VARCHAR(30) | NOT NULL | PENDING, CONFIRMED, COMPLETED, CANCELLED |
| wash_package | VARCHAR(100) | NOT NULL | Gói dịch vụ |
| total_amount | DECIMAL(12,2) | NOT NULL | Tổng tiền |

### Relationships
- N Booking → 1 Customer
- N Booking → 1 Vehicle
- N Booking → 1 Promotion
- 1 Booking → N WashHistory

---

## 7. WashHistory

Lưu lịch sử rửa xe.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| history_id | BIGINT | PK | ID lịch sử |
| booking_id | BIGINT | FK → Booking(booking_id) | Booking liên quan |
| wash_date | DATE | NOT NULL | Ngày rửa xe |
| earned_points | INT | NOT NULL | Điểm nhận được |
| amount | DECIMAL(12,2) | NOT NULL | Số tiền thanh toán |

### Relationships
- N WashHistory → 1 Booking

---

## 8. PointTransaction

Lưu lịch sử cộng/trừ điểm.

| Column | Type | Constraint | Description |
|----------|----------|----------|----------|
| transaction_id | BIGINT | PK | ID giao dịch điểm |
| customer_id | BIGINT | FK → Customer(customer_id) | Khách hàng |
| points | INT | NOT NULL | Số điểm thay đổi |
| transaction_date | TIMESTAMP | NOT NULL | Thời gian giao dịch |
| type | VARCHAR(20) | NOT NULL | EARN hoặc REDEEM |

### Relationships
- N PointTransaction → 1 Customer

---

# Entity Relationships Summary

## User
- User (1) ---- (1) Customer

## LoyaltyTier
- LoyaltyTier (1) ---- (N) Customer
- LoyaltyTier (1) ---- (N) Promotion

## Customer
- Customer (1) ---- (N) Vehicle
- Customer (1) ---- (N) Booking
- Customer (1) ---- (N) PointTransaction

## Vehicle
- Vehicle (1) ---- (N) Booking

## Promotion
- Promotion (1) ---- (N) Booking

## Booking
- Booking (1) ---- (N) WashHistory

---

# Database Statistics

| Table | Purpose |
|---------|---------|
| User | Authentication & Authorization |
| Customer | Customer Information |
| LoyaltyTier | Membership Tier Management |
| Vehicle | Vehicle Management |
| Promotion | Promotion Management |
| Booking | Booking Management |
| WashHistory | Wash History Tracking |
| PointTransaction | Loyalty Point Tracking |

Total Tables: **8**