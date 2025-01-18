use db;

-- Insert 60 users (15 each for admin, driver, assistant, customer)
INSERT INTO users (full_name, phone_number, email, password_hash, gender, address, date_of_birth, user_role) VALUES 
-- 15 Admins
('Nguyễn Văn An', '0912345678', 'an.nguyen@example.com', 'hash1', 'male', 'Hà Nội', '1990-01-15', 'admin'),
('Trần Thị Bình', '0912345679', 'binh.tran@example.com', 'hash2', 'female', 'Hồ Chí Minh', '1991-02-16', 'admin'),
('Lê Văn Cường', '0912345680', 'cuong.le@example.com', 'hash3', 'male', 'Đà Nẵng', '1992-03-17', 'admin'),
('Phạm Thị Dung', '0912345681', 'dung.pham@example.com', 'hash4', 'female', 'Hải Phòng', '1993-04-18', 'admin'),
('Hoàng Văn Em', '0912345682', 'em.hoang@example.com', 'hash5', 'male', 'Cần Thơ', '1994-05-19', 'admin'),
('Ngô Thị Phương', '0912345683', 'phuong.ngo@example.com', 'hash6', 'female', 'Huế', '1995-06-20', 'admin'),
('Đỗ Văn Giang', '0912345684', 'giang.do@example.com', 'hash7', 'male', 'Quảng Ninh', '1996-07-21', 'admin'),
('Vũ Thị Hương', '0912345685', 'huong.vu@example.com', 'hash8', 'female', 'Nghệ An', '1997-08-22', 'admin'),
('Đặng Văn Sơn', '0912345686', 'son.dang@example.com', 'hash9', 'male', 'Thanh Hóa', '1998-09-23', 'admin'),
('Bùi Thị Kim', '0912345687', 'kim.bui@example.com', 'hash10', 'female', 'Hà Tĩnh', '1999-10-24', 'admin'),
('Lý Văn Long', '0912345688', 'long.ly@example.com', 'hash11', 'male', 'Quảng Bình', '2000-11-25', 'admin'),
('Mai Thị Ngọc', '0912345689', 'ngoc.mai@example.com', 'hash12', 'female', 'Quảng Trị', '1989-12-26', 'admin'),
('Hồ Văn Phú', '0912345690', 'phu.ho@example.com', 'hash13', 'male', 'Bình Định', '1988-01-27', 'admin'),
('Trịnh Thị Quỳnh', '0912345691', 'quynh.trinh@example.com', 'hash14', 'female', 'Phú Yên', '1987-02-28', 'admin'),
('Đinh Văn Rồng', '0912345692', 'rong.dinh@example.com', 'hash15', 'male', 'Khánh Hòa', '1986-03-29', 'admin'),

-- 15 Drivers
('Lê Thị Sen', '0923456781', 'sen.le@example.com', 'hash16', 'female', 'Hồ Chí Minh', '1995-05-20', 'driver'),
('Nguyễn Văn Tâm', '0923456782', 'tam.nguyen@example.com', 'hash17', 'male', 'Hà Nội', '1994-06-21', 'driver'),
('Trần Thị Uyên', '0923456783', 'uyen.tran@example.com', 'hash18', 'female', 'Đà Nẵng', '1993-07-22', 'driver'),
('Phạm Văn Vĩnh', '0923456784', 'vinh.pham@example.com', 'hash19', 'male', 'Hải Phòng', '1992-08-23', 'driver'),
('Hoàng Thị Xuân', '0923456785', 'xuan.hoang@example.com', 'hash20', 'female', 'Cần Thơ', '1991-09-24', 'driver'),
('Đỗ Văn Yến', '0923456786', 'yen.do@example.com', 'hash21', 'male', 'Huế', '1990-10-25', 'driver'),
('Vũ Thị Zung', '0923456787', 'zung.vu@example.com', 'hash22', 'female', 'Quảng Ninh', '1989-11-26', 'driver'),
('Đặng Văn Anh', '0923456788', 'anh.dang@example.com', 'hash23', 'male', 'Nghệ An', '1988-12-27', 'driver'),
('Bùi Thị Bảo', '0923456789', 'bao.bui@example.com', 'hash24', 'female', 'Thanh Hóa', '1987-01-28', 'driver'),
('Lý Văn Cao', '0923456790', 'cao.ly@example.com', 'hash25', 'male', 'Hà Tĩnh', '1986-02-28', 'driver'),
('Mai Thị Diệu', '0923456791', 'dieu.mai@example.com', 'hash26', 'female', 'Quảng Bình', '1985-03-29', 'driver'),
('Hồ Văn Em', '0923456792', 'em.ho@example.com', 'hash27', 'male', 'Quảng Trị', '1984-04-30', 'driver'),
('Trịnh Thị Phương', '0923456793', 'phuong.trinh@example.com', 'hash28', 'female', 'Bình Định', '1983-05-31', 'driver'),
('Đinh Văn Giang', '0923456794', 'giang.dinh@example.com', 'hash29', 'male', 'Phú Yên', '1982-06-01', 'driver'),
('Lê Thị Hương', '0923456795', 'huong.le@example.com', 'hash30', 'female', 'Khánh Hòa', '1981-07-02', 'driver'),

-- 15 Assistants
('Nguyễn Văn Sơn', '0934567812', 'son.nguyen@example.com', 'hash31', 'male', 'Đà Nẵng', '1988-11-10', 'assistant'),
('Trần Thị Kim', '0934567813', 'kim.tran@example.com', 'hash32', 'female', 'Hà Nội', '1989-12-11', 'assistant'),
('Phạm Văn Long', '0934567814', 'long.pham@example.com', 'hash33', 'male', 'Hồ Chí Minh', '1990-01-12', 'assistant'),
('Hoàng Thị Ngọc', '0934567815', 'ngoc.hoang@example.com', 'hash34', 'female', 'Hải Phòng', '1991-02-13', 'assistant'),
('Đỗ Văn Phú', '0934567816', 'phu.do@example.com', 'hash35', 'male', 'Cần Thơ', '1992-03-14', 'assistant'),
('Vũ Thị Quỳnh', '0934567817', 'quynh.vu@example.com', 'hash36', 'female', 'Huế', '1993-04-15', 'assistant'),
('Đặng Văn Rồng', '0934567818', 'rong.dang@example.com', 'hash37', 'male', 'Quảng Ninh', '1994-05-16', 'assistant'),
('Bùi Thị Sen', '0934567819', 'sen.bui@example.com', 'hash38', 'female', 'Nghệ An', '1995-06-17', 'assistant'),
('Lý Văn Tâm', '0934567820', 'tam.ly@example.com', 'hash39', 'male', 'Thanh Hóa', '1996-07-18', 'assistant'),
('Mai Thị Uyên', '0934567821', 'uyen.mai@example.com', 'hash40', 'female', 'Hà Tĩnh', '1997-08-19', 'assistant'),
('Hồ Văn Vĩnh', '0934567822', 'vinh.ho@example.com', 'hash41', 'male', 'Quảng Bình', '1998-09-20', 'assistant'),
('Trịnh Thị Xuân', '0934567823', 'xuan.trinh@example.com', 'hash42', 'female', 'Quảng Trị', '1999-10-21', 'assistant'),
('Đinh Văn Yến', '0934567824', 'yen.dinh@example.com', 'hash43', 'male', 'Bình Định', '2000-11-22', 'assistant'),
('Lê Thị Zung', '0934567825', 'zung.le@example.com', 'hash44', 'female', 'Phú Yên', '1987-12-23', 'assistant'),
('Nguyễn Văn Anh', '0934567826', 'anh.nguyen2@example.com', 'hash45', 'male', 'Khánh Hòa', '1986-01-24', 'assistant'),

-- 15 Customers
('Trần Thị Bảo', '0945678123', 'bao.tran@example.com', 'hash46', 'female', 'Hải Phòng', '1992-07-25', 'customer'),
('Phạm Văn Cao', '0945678124', 'cao.pham@example.com', 'hash47', 'male', 'Hà Nội', '1993-08-26', 'customer'),
('Hoàng Thị Diệu', '0945678125', 'dieu.hoang@example.com', 'hash48', 'female', 'Hồ Chí Minh', '1994-09-27', 'customer'),
('Đỗ Văn Em', '0945678126', 'em.do@example.com', 'hash49', 'male', 'Đà Nẵng', '1995-10-28', 'customer'),
('Vũ Thị Phương', '0945678127', 'phuong.vu@example.com', 'hash50', 'female', 'Cần Thơ', '1996-11-29', 'customer'),
('Đặng Văn Giang', '0945678128', 'giang.dang@example.com', 'hash51', 'male', 'Huế', '1997-12-30', 'customer'),
('Bùi Thị Hương', '0945678129', 'huong.bui@example.com', 'hash52', 'female', 'Quảng Ninh', '1998-01-31', 'customer'),
('Lý Văn Sơn', '0945678130', 'son.ly@example.com', 'hash53', 'male', 'Nghệ An', '1999-02-01', 'customer'),
('Mai Thị Kim', '0945678131', 'kim.mai@example.com', 'hash54', 'female', 'Thanh Hóa', '2000-03-02', 'customer'),
('Hồ Văn Long', '0945678132', 'long.ho@example.com', 'hash55', 'male', 'Hà Tĩnh', '1985-04-03', 'customer'),
('Trịnh Thị Ngọc', '0945678133', 'ngoc.trinh@example.com', 'hash56', 'female', 'Quảng Bình', '1986-05-04', 'customer'),
('Đinh Văn Phú', '0945678134', 'phu.dinh@example.com', 'hash57', 'male', 'Quảng Trị', '1987-06-05', 'customer'),
('Lê Thị Quỳnh', '0945678135', 'quynh.le@example.com', 'hash58', 'female', 'Bình Định', '1988-07-06', 'customer'),
('Nguyễn Văn Rồng', '0945678136', 'rong.nguyen@example.com', 'hash59', 'male', 'Phú Yên', '1989-08-07', 'customer'),
('Trần Thị Sen', '0945678137', 'sen.tran@example.com', 'hash60', 'female', 'Khánh Hòa', '1990-09-08', 'customer');

-- Insert 15 admins
INSERT INTO admins (user_id) VALUES 
(1), (2), (3), (4), (5), (6), (7), (8), (9), (10), (11), (12), (13), (14), (15);

-- Insert 15 customers
INSERT INTO customers (user_id) VALUES 
(46), (47), (48), (49), (50), (51), (52), (53), (54), (55), (56), (57), (58), (59), (60);

-- Insert 15 drivers with their license information
INSERT INTO drivers (user_id, license_number, license_class, license_expiry, driver_status) VALUES 
(16, 'A123456', 'B2', '2025-12-31', 'available'),
(17, 'B234567', 'B2', '2025-11-30', 'available'),
(18, 'C345678', 'B2', '2025-10-31', 'available'),
(19, 'D456789', 'B2', '2025-09-30', 'available'),
(20, 'E567890', 'B2', '2025-08-31', 'available'),
(21, 'F678901', 'B2', '2025-07-31', 'available'),
(22, 'G789012', 'B2', '2025-06-30', 'available'),
(23, 'H890123', 'B2', '2025-05-31', 'available'),
(24, 'I901234', 'B2', '2025-04-30', 'available'),
(25, 'J012345', 'B2', '2025-03-31', 'available'),
(26, 'K123456', 'B2', '2025-02-28', 'available'),
(27, 'L234567', 'B2', '2025-01-31', 'available'),
(28, 'M345678', 'B2', '2024-12-31', 'available'),
(29, 'N456789', 'B2', '2024-11-30', 'available'),
(30, 'O567890', 'B2', '2024-10-31', 'available');

-- Insert 15 assistants
INSERT INTO assistants (user_id, assistant_status) VALUES 
(31, 'available'),
(32, 'available'),
(33, 'available'),
(34, 'available'),
(35, 'available'),
(36, 'available'),
(37, 'available'),
(38, 'available'),
(39, 'available'),
(40, 'available'),
(41, 'available'),
(42, 'available'),
(43, 'available'),
(44, 'available'),
(45, 'available');

INSERT INTO routes (route_name, ticket_price, distance, estimated_duration, route_status) VALUES 
('Quán Toan - Giải Phóng', 200000, 120.5, 120, 'active'),
('Mỹ Đình - Thủy Nguyên', 200000, 100, 120, 'active'),
('Gia Lâm - Lạch Tray', 200000, 105.3, 120, 'active'),
('Nước Ngầm - Cầu Rào', 200000, 115.2, 130, 'active'),
('Yên Nghĩa - Thượng Lý', 200000, 125.0, 140, 'active'),
('Long Biên - Trạm Bạc', 200000, 102.5, 120, 'active'),
('Giáp Bát - Cầu Vàng', 200000, 110.0, 125, 'active');