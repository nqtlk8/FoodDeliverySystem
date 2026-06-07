-- ==========================================
-- 1. KHỞI TẠO CÁC DATABASES
-- ==========================================
CREATE DATABASE user_db;
CREATE DATABASE voucher_db;
CREATE DATABASE order_db;
CREATE DATABASE dispatch_db;
CREATE DATABASE restaurant_db;

-- ==========================================
-- 2. TẠO SCHEMA VÀ MOCK DATA CHO VOUCHER_DB
-- ==========================================
-- Chuyển context sang database voucher_db
\c voucher_db;

-- Tạo bảng vouchers
CREATE TABLE IF NOT EXISTS vouchers (
    code VARCHAR(50) PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    min_spend DECIMAL(12,2) NOT NULL,
    discount_amount DECIMAL(12,2) NOT NULL,
    max_uses INT NOT NULL,
    current_uses INT NOT NULL DEFAULT 0
);

-- Tạo bảng voucher_usages
CREATE TABLE IF NOT EXISTS voucher_usages (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) UNIQUE NOT NULL,
    voucher_code VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_voucher FOREIGN KEY(voucher_code) REFERENCES vouchers(code)
);

-- Thêm Mock Data cho bảng vouchers
INSERT INTO vouchers (code, status, expiry_date, min_spend, discount_amount, max_uses, current_uses) 
VALUES 
-- 1. [HAPPY PATH] Voucher hợp lệ, dễ sử dụng
('GIAM20K', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30 days', 50000.00, 20000.00, 1000, 15),

-- 2. [MIN SPEND] Voucher hợp lệ, nhưng yêu cầu đơn hàng giá trị cao (để test lỗi ERR_PROMO_MIN_SPEND)
('GIAM50K', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '15 days', 200000.00, 50000.00, 500, 10),

-- 3. [FREESHIP] Voucher hợp lệ, thời hạn rất dài
('FREESHIP', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '60 days', 30000.00, 15000.00, 9999, 100),

-- 4. [EXPIRED] Voucher đã hết hạn ngày hôm qua (để test lỗi ERR_PROMO_EXPIRED)
('HETHAN', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '1 days', 0.00, 10000.00, 100, 5),

-- 5. [FULL USES] Voucher đã hết lượt sử dụng (để test lỗi race condition / hết lượt)
('HETLUOT', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30 days', 50000.00, 25000.00, 50, 50),

-- 6. [INACTIVE] Voucher bị khóa bởi Admin (để test lỗi không lấy được voucher)
('KHOA_MA', 'INACTIVE', CURRENT_TIMESTAMP + INTERVAL '30 days', 0.00, 5000.00, 100, 0);

-- Thêm Mock Data cho bảng voucher_usages (Giả lập một số đơn hàng đã dùng mã)
INSERT INTO voucher_usages (id, order_id, voucher_code, status) 
VALUES 
('uid-1111-2222', 'ORD-PREV-001', 'GIAM20K', 'DEDUCTED'),
('uid-3333-4444', 'ORD-PREV-002', 'GIAM50K', 'DEDUCTED'),
('uid-5555-6666', 'ORD-PREV-003', 'HETHAN', 'AVAILABLE'); -- AVAILABLE: đã từng dùng nhưng đơn bị hủy nên hoàn lại