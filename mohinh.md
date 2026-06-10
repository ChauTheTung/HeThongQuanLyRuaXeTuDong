# Functional Decomposition Diagram (FDD)

# AutoWash Pro System

## 0. AutoWash Pro

### 1. Authentication Management

#### 1.1 Registration

* Create Customer Account

#### 1.2 Login

* Authenticate User
* Generate JWT Token

#### 1.3 Authorization

* ADMIN Authorization
* CUSTOMER Authorization

---

### 2. Customer Management

#### 2.1 Profile Management

* View Profile
* Update Profile

#### 2.2 Loyalty Information

* View Points
* View Membership Tier

#### 2.3 Wash History Inquiry

* View Wash History

---

### 3. Vehicle Management

#### 3.1 Vehicle Registration

* Add Vehicle

#### 3.2 Vehicle Maintenance

* Update Vehicle Information
* Delete Vehicle

#### 3.3 Vehicle Information

* View Vehicle List

---

### 4. Booking Management

#### 4.1 Booking Creation

* Create Booking

#### 4.2 Booking Monitoring

* View Booking Status

#### 4.3 Booking Validation

* Validate Booking Window
* Priority Booking Processing

---

### 5. Wash History Management

#### 5.1 History Recording

* Record Wash History

#### 5.2 History Inquiry

* View Wash History

---

### 6. Loyalty Management

#### 6.1 Point Management

* Earn Points
* Redeem Points

#### 6.2 Membership Management

* Track Service Usage
* Upgrade Membership Tier
* Downgrade Membership Tier

#### 6.3 Point Expiration

* Expire Points After 12 Months

---

### 7. Promotion Management

#### 7.1 Promotion Administration

* Create Promotion

#### 7.2 Tier-Based Promotion

* Promotion By Membership Tier

#### 7.3 Promotion Processing

* Auto Apply Promotion During Checkout

---

### 8. Dashboard Management

#### 8.1 Revenue Statistics

* View Revenue Summary

#### 8.2 Booking Statistics

* View Booking Statistics

#### 8.3 Customer Statistics

* View Customer Statistics

---

### 9. Scheduler Management

#### 9.1 Tier Review Scheduler

* Monthly Tier Review

#### 9.2 Point Expiration Scheduler

* Automatic Point Expiration

---

### 10. Error Handling

#### 10.1 Exception Management

* Global Exception Handling

#### 10.2 Error Pages

* Error 403
* Error 404
* Error 500

---

# Functional Decomposition Tree

```text
AutoWash Pro
│
├── Authentication Management
│   ├── Registration
│   ├── Login
│   └── Authorization
│
├── Customer Management
│   ├── Profile Management
│   ├── Loyalty Information
│   └── Wash History Inquiry
│
├── Vehicle Management
│   ├── Vehicle Registration
│   ├── Vehicle Maintenance
│   └── Vehicle Information
│
├── Booking Management
│   ├── Booking Creation
│   ├── Booking Monitoring
│   └── Booking Validation
│
├── Wash History Management
│
├── Loyalty Management
│   ├── Point Management
│   ├── Membership Management
│   └── Point Expiration
│
├── Promotion Management
│
├── Dashboard Management
│
├── Scheduler Management
│
└── Error Handling
```
