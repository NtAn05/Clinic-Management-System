DROP DATABASE IF EXISTS clinic_management;

CREATE DATABASE clinic_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE clinic_management;

SET NAMES utf8mb4;
SET time_zone = '+07:00';

-- =========================================================
-- 1) USERS & AUTHENTICATION
-- =========================================================
CREATE TABLE users (
   user_id INT AUTO_INCREMENT PRIMARY KEY,
   full_name VARCHAR(100) NOT NULL,
   phone VARCHAR(15) UNIQUE,
   email VARCHAR(100) NULL,
   image_url VARCHAR(255) NULL,
   password_hash VARCHAR(255) NOT NULL,
   role ENUM('patient','doctor','receptionist','technician','admin') NOT NULL,
   status ENUM('active','inactive') DEFAULT 'active',
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================================
-- 2) PATIENT PROFILE
-- =========================================================
CREATE TABLE patients (
   patient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   user_id INT NULL,
   full_name VARCHAR(255) NOT NULL,
   phone VARCHAR(20),
   dob DATE,
   address VARCHAR(255),
   email VARCHAR(100) NULL,
   gender ENUM('male','female','other'),
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users(user_id)
       ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE INDEX idx_patients_contact ON patients (phone, email);

-- =========================================================
-- 3) DOCTOR & SCHEDULE
-- =========================================================
CREATE TABLE doctors (
   doctor_id INT AUTO_INCREMENT PRIMARY KEY,
   user_id INT NOT NULL,
   specialization VARCHAR(100) NOT NULL,
   qualification VARCHAR(255),
   experience_years INT,
   rating DECIMAL(2,1) DEFAULT 0.0,
   price_booking DECIMAL(10,2) DEFAULT 0,
   dob DATE NULL,
   gender ENUM('male','female','other') NULL,
   CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users(user_id)
       ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE doctor_shifts (
   shift_id INT AUTO_INCREMENT PRIMARY KEY,
   doctor_id INT NOT NULL,
   day_of_week TINYINT NOT NULL,
   start_time TIME NOT NULL,
   end_time TIME NOT NULL,
   max_patients INT DEFAULT 20,
   status ENUM('active','inactive') NOT NULL DEFAULT 'active',
   CONSTRAINT fk_shifts_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
       ON UPDATE CASCADE ON DELETE CASCADE,
   CONSTRAINT chk_day_of_week CHECK (day_of_week BETWEEN 0 AND 6),
   CONSTRAINT chk_shift_time CHECK (start_time < end_time),
   CONSTRAINT chk_shift_max_patients CHECK (max_patients > 0)
);

CREATE INDEX idx_shifts_doctor_day_status 
ON doctor_shifts (doctor_id, day_of_week, status);

-- =========================================================
-- 4) SCHEDULE CHANGE
-- =========================================================
CREATE TABLE schedule_change_requests (
   request_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
   doctor_id INT NOT NULL,
   request_type ENUM('TEMPORARY','PERMANENT') NOT NULL,
   scope_type ENUM('ONE_DATE','DATE_RANGE','WEEKLY_TEMPLATE') NOT NULL,
   reason TEXT NOT NULL,
   status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
   requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   reviewed_at DATETIME NULL,
   reviewed_by INT NULL,
   admin_note TEXT NULL,
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
   FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

CREATE TABLE schedule_change_request_items (
   item_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
   request_id BIGINT UNSIGNED NOT NULL,
   action_type ENUM('ADD','UPDATE','REMOVE') NOT NULL,
   target_shift_id INT NULL,
   work_date DATE NULL,
   day_of_week TINYINT NULL,
   start_time TIME NULL,
   end_time TIME NULL,
   max_patients INT NULL,
   FOREIGN KEY (request_id) REFERENCES schedule_change_requests(request_id),
   FOREIGN KEY (target_shift_id) REFERENCES doctor_shifts(shift_id)
);

CREATE TABLE doctor_shift_overrides (
   override_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
   doctor_id INT NOT NULL,
   work_date DATE NOT NULL,
   base_shift_id INT NULL,
   action_type ENUM('ADD','REPLACE','CANCEL') NOT NULL,
   start_time TIME NULL,
   end_time TIME NULL,
   max_patients INT NULL,
   source_request_id BIGINT UNSIGNED NOT NULL,
   is_active BOOLEAN DEFAULT TRUE,
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
   FOREIGN KEY (base_shift_id) REFERENCES doctor_shifts(shift_id),
   FOREIGN KEY (source_request_id) REFERENCES schedule_change_requests(request_id)
);

-- =========================================================
-- 5) APPOINTMENTS
-- =========================================================
CREATE TABLE appointments (
   appointment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   patient_id BIGINT NOT NULL,
   doctor_id INT,
   shift_id INT,
   booking_type ENUM('online','walk_in') NOT NULL,
   appointment_date DATE,
   appointment_time TIME,
   status ENUM('booked','checked_in','waiting','completed','cancelled') NOT NULL,
   symptom TEXT,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
   FOREIGN KEY (shift_id) REFERENCES doctor_shifts(shift_id)
);

-- =========================================================
-- 6) PAYMENT
-- =========================================================
CREATE TABLE payments (
   payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   appointment_id BIGINT NOT NULL,
   amount DECIMAL(12,2) NOT NULL,
   method ENUM('cash','online') NOT NULL,
   transaction_id VARCHAR(255),
   status ENUM('pending','paid','failed') NOT NULL,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

-- =========================================================
-- 7) MEDICAL RECORD
-- =========================================================
CREATE TABLE medical_records (
   record_id INT AUTO_INCREMENT PRIMARY KEY,
   appointment_id BIGINT NOT NULL,
   doctor_id INT NOT NULL,
   symptoms TEXT,
   diagnosis TEXT,
   notes TEXT,
   updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

CREATE TABLE vital_signs (
   vital_id INT AUTO_INCREMENT PRIMARY KEY,
   record_id INT NOT NULL,
   blood_pressure_sys INT,
   blood_pressure_dia INT,
   blood_sugar DECIMAL(5,2),
   heart_rate INT,
   temperature DECIMAL(4,2),
   respiratory_rate INT,
   measured_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (record_id) REFERENCES medical_records(record_id)
);

-- =========================================================
-- 8) PRESCRIPTION
-- =========================================================
CREATE TABLE prescriptions (
   prescription_id INT AUTO_INCREMENT PRIMARY KEY,
   record_id INT NOT NULL,
   doctor_id INT NOT NULL,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (record_id) REFERENCES medical_records(record_id),
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

CREATE TABLE prescription_items (
   item_id INT AUTO_INCREMENT PRIMARY KEY,
   prescription_id INT NOT NULL,
   medicine_name VARCHAR(255),
   dosage VARCHAR(100),
   frequency VARCHAR(100),
   duration VARCHAR(100),
   FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id)
);

-- =========================================================
-- 9) LAB
-- =========================================================
CREATE TABLE lab_requests (
   request_id INT AUTO_INCREMENT PRIMARY KEY,
   appointment_id BIGINT NOT NULL,
   doctor_id INT NOT NULL,
   status ENUM('pending','processing','completed','cancelled') DEFAULT 'pending',
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

CREATE TABLE lab_results (
   result_id INT AUTO_INCREMENT PRIMARY KEY,
   request_id INT NOT NULL,
   technician_id INT,
   result_file VARCHAR(255),
   notes TEXT,
   completed_at DATETIME,
   FOREIGN KEY (request_id) REFERENCES lab_requests(request_id),
   FOREIGN KEY (technician_id) REFERENCES users(user_id)
);

-- =========================================================
-- 10) SYSTEM
-- =========================================================
CREATE TABLE service_prices (
   service_id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   service_type ENUM('booking_fee','lab') NOT NULL,
   price DECIMAL(10,2) NOT NULL
);

CREATE TABLE system_logs (
   log_id INT AUTO_INCREMENT PRIMARY KEY,
   user_id INT,
   action VARCHAR(255),
   description TEXT,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =========================================================
-- 11) QUEUE
-- =========================================================
CREATE TABLE exam_queue (
   queue_id INT AUTO_INCREMENT PRIMARY KEY,
   appointment_id BIGINT NOT NULL,
   doctor_id INT NOT NULL,
   queue_position INT NOT NULL,
   status ENUM('waiting','examining','done') DEFAULT 'waiting',
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
   FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
   UNIQUE (appointment_id)
);

-- =========================================================
-- 12) RATING
-- =========================================================
CREATE TABLE rating_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_text VARCHAR(255) NOT NULL
);

CREATE TABLE review_answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    rating INT NOT NULL,
    users_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_id INT,
    note VARCHAR(50),
    FOREIGN KEY (question_id) REFERENCES rating_questions(id)
);

-- =========================================================
-- 13) ALTER ROLE
-- =========================================================
ALTER TABLE users
MODIFY COLUMN role ENUM(
    'patient',
    'doctor',
    'receptionist',
    'technician',
    'admin',
    'patient_manager'
) NOT NULL;


ALTER TABLE doctors
DROP COLUMN specialization,
DROP COLUMN qualification,
DROP COLUMN experience_years;

ALTER TABLE payments
ADD COLUMN lab_request_id INT NULL,
ADD CONSTRAINT fk_payments_lab_request FOREIGN KEY (lab_request_id) REFERENCES lab_requests(request_id);



-- =========================================================
-- SAMPLE DATA — adjusted for new schema
-- Changes vs dbase.sql:
--   - doctors: removed specialization, qualification, experience_years columns
--   - review_answers: added appointment_id and note columns
-- =========================================================

USE clinic_management;

SET NAMES utf8mb4;
SET time_zone = '+07:00';

-- =========================================================
-- USERS
-- =========================================================
INSERT INTO users (user_id, full_name, phone, email, image_url, password_hash, role, status) VALUES
(1,'Admin System','0900000001','admin@clinic.local',NULL,'$2a$10$demoHash','admin','active'),
(2,'Le Thi Thu Ngan','0900000002','reception@clinic.local',NULL,'$2a$10$demoHash','receptionist','active'),
(3,'Tran Van Khoa','0900000003','tech1@clinic.local',NULL,'$2a$10$demoHash','technician','active'),
(4,'Nguyen Van A','0900000004','doctor.a@clinic.local','https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(5,'Pham Thi B','0900000005','doctor.b@clinic.local','https://images.unsplash.com/photo-1594824476967-48c8b964273f?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(6,'Hoang Minh C','0900000006','doctor.c@clinic.local','https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(7,'Vo Quoc D','0900000007','doctor.d@clinic.local','https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(8,'Family Account 1','0900000008','family1@clinic.local',NULL,'$2a$10$demoHash','patient','active'),
(9,'Family Account 2','0900000009','family2@clinic.local',NULL,'$2a$10$demoHash','patient','active'),
(10,'Family Account 3','0900000010','family3@clinic.local',NULL,'$2a$10$demoHash','patient','active'),
(11,'Nguyen Van E','0900000011','doctor.e@clinic.local','https://images.unsplash.com/photo-1651008376811-b90baee60c1f?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(12,'Le Thi F','0900000012','doctor.f@clinic.local','https://images.unsplash.com/photo-1584515933487-779824d29309?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(13,'Tran Minh G','0900000013','doctor.g@clinic.local','https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(14,'Le Thu H','0900000014','doctor.h@clinic.local','https://images.unsplash.com/photo-1551601651-2a8555f1a136?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(15,'Pham Quoc I','0900000015','doctor.i@clinic.local','https://images.unsplash.com/photo-1614608682850-e0d6ed316d47?auto=format&fit=crop&w=800&q=80','$2a$10$demoHash','doctor','active'),
(36,'Patient Manager','0900000036','patient.manager@clinic.local',NULL,'$2a$10$demoHash','patient_manager','active');

-- =========================================================
-- PATIENTS
-- =========================================================
INSERT INTO patients (patient_id, user_id, full_name, phone, dob, address, email, gender) VALUES
(1,8,'Nguyen Thi Lan','0911000001','1990-04-10','Ha Noi','lan.nguyen@family1.local','female'),
(2,8,'Nguyen Minh Khang','0911000002','2017-06-12','Ha Noi','khang.nguyen@family1.local','male'),
(3,8,'Tran Thi Hoa','0911000003','1965-09-20','Ha Noi','hoa.tran@family1.local','female'),
(4,9,'Pham Quoc Anh','0911000004','1988-03-15','Da Nang','anh.pham@family2.local','male'),
(5,9,'Pham Bao Ngoc','0911000005','2014-11-02','Da Nang','ngoc.pham@family2.local','female'),
(6,10,'Vo Thi Mai','0911000006','1995-01-25','HCM','mai.vo@family3.local','female'),
(7,NULL,'Walk-in Benh Nhan 1','0911000007','1978-02-17','HCM','walkin1@clinic.local','male'),
(8,NULL,'Walk-in Benh Nhan 2','0911000008','2001-12-01','HCM','walkin2@clinic.local','female'),
(9,NULL,'Tran Van Son','0911000009','1982-07-07','Can Tho','son.tran@clinic.local','male'),
(10,NULL,'Le Huynh My','0911000010','1992-08-21','Can Tho','my.le@clinic.local','female'),
(11,NULL,'Nguyen Gia Bao','0911000011','2010-04-28','Ha Noi','bao.nguyen@clinic.local','male'),
(12,NULL,'Doan Thi Yen','0911000012','1970-05-19','Da Nang','yen.doan@clinic.local','female');

-- =========================================================
-- DOCTORS  (specialization / qualification / experience_years removed)
-- =========================================================
INSERT INTO doctors (doctor_id, user_id, rating, price_booking, dob, gender) VALUES
(1,4,4.8,200000,'1985-06-20','male'),
(2,5,4.6,200000,'1988-03-14','female'),
(3,6,4.7,300000,'1989-09-01','male'),
(4,7,4.5,300000,'1984-11-23','male'),
(5,11,4.4,200000,'1990-12-12','female'),
(6,12,4.6,400000,'1987-02-02','female'),
(7,13,4.3,200000,'1992-05-10','female'),
(8,14,4.2,200000,'1993-07-21','male'),
(9,15,4.9,300000,'1980-01-15','male');

-- =========================================================
-- DOCTOR SHIFTS
-- =========================================================
INSERT INTO doctor_shifts (shift_id, doctor_id, day_of_week, start_time, end_time, max_patients, status) VALUES
-- Doctor 1: Mon + Fri
(1,1,1,'07:00:00','11:30:00',20,'active'),
(2,1,1,'13:00:00','16:30:00',20,'active'),
(3,1,5,'07:00:00','11:30:00',20,'active'),
(4,1,5,'13:00:00','16:30:00',20,'active'),
-- Doctor 2: Wed + Fri
(5,2,3,'07:00:00','11:30:00',18,'active'),
(6,2,3,'13:00:00','16:30:00',18,'active'),
(7,2,5,'07:00:00','11:30:00',18,'active'),
(8,2,5,'13:00:00','16:30:00',18,'active'),
-- Doctor 3: Tue + Sat
(9,3,2,'07:00:00','11:30:00',22,'active'),
(10,3,2,'13:00:00','16:30:00',22,'active'),
(11,3,6,'07:00:00','11:30:00',22,'active'),
(12,3,6,'13:00:00','16:30:00',22,'active'),
-- Doctor 4: Thu + Fri
(13,4,4,'07:00:00','11:30:00',16,'active'),
(14,4,4,'13:00:00','16:30:00',16,'active'),
(15,4,5,'07:00:00','11:30:00',16,'active'),
(16,4,5,'13:00:00','16:30:00',16,'active'),
-- Doctor 5: Tue + Thu
(17,5,2,'07:00:00','11:30:00',20,'active'),
(18,5,2,'13:00:00','16:30:00',20,'active'),
(19,5,4,'07:00:00','11:30:00',20,'active'),
(20,5,4,'13:00:00','16:30:00',20,'active'),
-- Doctor 6: Wed + Sun
(21,6,3,'07:00:00','11:30:00',20,'active'),
(22,6,3,'13:00:00','16:30:00',20,'active'),
(23,6,0,'07:00:00','11:30:00',20,'active'),
(24,6,0,'13:00:00','16:30:00',20,'active'),
-- Doctor 7: Mon + Thu
(25,7,1,'07:00:00','11:30:00',18,'active'),
(26,7,1,'13:00:00','16:30:00',18,'active'),
(27,7,4,'07:00:00','11:30:00',18,'active'),
(28,7,4,'13:00:00','16:30:00',18,'active'),
-- Doctor 8: Tue + Fri
(29,8,2,'07:00:00','11:30:00',18,'active'),
(30,8,2,'13:00:00','16:30:00',18,'active'),
(31,8,5,'07:00:00','11:30:00',18,'active'),
(32,8,5,'13:00:00','16:30:00',18,'active'),
-- Doctor 9: Wed + Sat
(33,9,3,'07:00:00','11:30:00',22,'active'),
(34,9,3,'13:00:00','16:30:00',22,'active'),
(35,9,6,'07:00:00','11:30:00',22,'active'),
(36,9,6,'13:00:00','16:30:00',22,'active');

-- =========================================================
-- APPOINTMENTS
-- =========================================================
INSERT INTO appointments (appointment_id, patient_id, doctor_id, shift_id, booking_type, appointment_date, appointment_time, status, symptom, created_at) VALUES
(1,1,1,1,'online','2026-03-02','08:30:00','completed','Mụn viêm vùng má kéo dài','2026-02-28 09:00:00'),
(2,1,1,1,'online','2026-03-09','08:45:00','completed','Tái khám mụn sau 1 tuần','2026-03-07 08:50:00'),
(3,2,3,9,'online','2026-03-03','09:10:00','completed','Nổi mẩn đỏ ngứa toàn thân','2026-03-01 10:20:00'),
(4,2,3,9,'online','2026-03-10','09:00:00','booked','Tái khám viêm da cơ địa','2026-03-08 11:00:00'),
(5,3,2,5,'walk_in','2026-03-04','10:00:00','completed','Ngứa da đầu, bong vảy','2026-03-04 08:10:00'),
(6,3,2,5,'online','2026-03-11','10:20:00','waiting','Tái khám viêm da dị ứng','2026-03-09 14:15:00'),
(7,4,4,14,'online','2026-03-05','14:00:00','completed','Sẹo rỗ sau mụn','2026-03-03 09:30:00'),
(8,4,4,14,'online','2026-03-12','14:15:00','checked_in','Tái khám điều trị laser sẹo','2026-03-10 16:00:00'),
(9,5,3,11,'online','2026-03-07','08:20:00','cancelled','Ban đỏ nghi dị ứng thuốc','2026-03-06 07:30:00'),
(10,6,5,18,'online','2026-03-03','15:00:00','completed','Viêm da tiếp xúc vùng tay','2026-03-01 13:00:00'),
(11,6,5,18,'online','2026-03-17','15:15:00','booked','Tái khám da liễu dị ứng','2026-03-15 10:00:00'),
(12,7,6,21,'walk_in','2026-03-04','08:40:00','completed','Nghi nấm da vùng cổ','2026-03-04 08:00:00'),
(13,8,6,23,'online','2026-03-08','09:30:00','completed','Nấm kẽ chân tái phát','2026-03-06 12:00:00'),
(14,9,1,4,'online','2026-03-06','13:30:00','completed','Da dầu, mụn đầu đen','2026-03-04 09:10:00'),
(15,10,2,8,'online','2026-03-06','14:00:00','completed','Ngứa da do thời tiết','2026-03-04 10:00:00'),
(16,11,3,11,'online','2026-03-14','10:00:00','waiting','Tái khám viêm nang lông','2026-03-12 15:10:00'),
(17,12,4,16,'walk_in','2026-03-13','15:20:00','booked','Sẹo lồi cũ','2026-03-13 08:20:00'),
(18,5,2,6,'online','2026-03-18','13:50:00','booked','Khám tổng quát da liễu','2026-03-16 09:50:00');

-- =========================================================
-- PAYMENTS
-- =========================================================
INSERT INTO payments (appointment_id, amount, method, transaction_id, status, created_at) VALUES
(1,250000,'online','TXN-0001','paid','2026-02-28 09:01:00'),
(2,250000,'online','TXN-0002','paid','2026-03-07 08:51:00'),
(3,210000,'online','TXN-0003','paid','2026-03-01 10:21:00'),
(4,210000,'online','TXN-0004','pending','2026-03-08 11:01:00'),
(5,220000,'cash',NULL,'paid','2026-03-04 08:12:00'),
(6,220000,'online','TXN-0006','pending','2026-03-09 14:16:00'),
(7,260000,'online','TXN-0007','paid','2026-03-03 09:31:00'),
(8,260000,'online','TXN-0008','paid','2026-03-10 16:01:00'),
(9,210000,'online','TXN-0009','failed','2026-03-06 07:31:00'),
(10,200000,'online','TXN-0010','paid','2026-03-01 13:02:00'),
(11,200000,'online','TXN-0011','pending','2026-03-15 10:01:00'),
(12,230000,'cash',NULL,'paid','2026-03-04 08:05:00'),
(13,230000,'online','TXN-0013','paid','2026-03-06 12:01:00'),
(14,250000,'online','TXN-0014','paid','2026-03-04 09:11:00'),
(15,220000,'online','TXN-0015','paid','2026-03-04 10:01:00');

-- =========================================================
-- MEDICAL RECORDS
-- =========================================================
INSERT INTO medical_records (record_id, appointment_id, doctor_id, symptoms, diagnosis, notes, updated_at) VALUES
(1,1,1,'Mụn viêm vùng má','Mụn trứng cá mức độ vừa','Lần đầu khám da liễu','2026-03-02 09:20:00'),
(2,2,1,'Tái khám mụn','Đáp ứng thuốc tốt','Tái khám sau 7 ngày','2026-03-09 09:30:00'),
(3,3,3,'Mẩn đỏ ngứa','Viêm da dị ứng','Lần đầu khám','2026-03-03 10:10:00'),
(4,5,2,'Ngứa da đầu','Viêm da tiết bã','Đã hướng dẫn chăm sóc da đầu','2026-03-04 10:40:00'),
(5,7,4,'Sẹo rỗ','Sẹo rỗ sau mụn','Hẹn liệu trình laser','2026-03-05 15:30:00'),
(6,10,5,'Viêm da tay','Viêm da tiếp xúc','Lần đầu khám','2026-03-03 15:30:00'),
(7,12,6,'Ngứa vùng cổ','Nghi nấm da','Kê đơn 5 ngày','2026-03-04 09:30:00'),
(8,13,6,'Nấm kẽ chân','Nấm da tái phát','Tái khám sau 2 tuần','2026-03-08 10:10:00'),
(9,14,1,'Mụn đầu đen','Da dầu mụn mức độ nhẹ','Theo dõi 1 tháng','2026-03-06 14:10:00'),
(10,15,2,'Ngứa da','Viêm da kích ứng','Dưỡng ẩm và tránh tác nhân kích ứng','2026-03-06 15:00:00');

-- =========================================================
-- VITAL SIGNS
-- =========================================================
INSERT INTO vital_signs (record_id, blood_pressure_sys, blood_pressure_dia, blood_sugar, heart_rate, temperature, respiratory_rate, measured_at) VALUES
(1,120,80,5.80,86,36.9,18,'2026-03-02 08:40:00'),
(2,118,78,5.60,82,36.7,17,'2026-03-09 08:50:00'),
(3,110,70,4.90,90,37.0,20,'2026-03-03 09:20:00'),
(4,116,74,5.20,80,36.6,18,'2026-03-04 10:15:00'),
(5,122,80,5.50,80,36.8,18,'2026-03-05 14:20:00'),
(6,114,72,5.00,78,36.5,17,'2026-03-03 15:10:00'),
(7,118,78,5.30,84,37.2,20,'2026-03-04 08:55:00');

-- =========================================================
-- PRESCRIPTIONS
-- =========================================================
INSERT INTO prescriptions (prescription_id, record_id, doctor_id, created_at) VALUES
(1,1,1,'2026-03-02 09:25:00'),
(2,2,1,'2026-03-09 09:35:00'),
(3,3,3,'2026-03-03 10:15:00'),
(4,5,4,'2026-03-05 15:35:00'),
(5,7,6,'2026-03-04 09:35:00'),
(6,10,2,'2026-03-06 15:05:00');

INSERT INTO prescription_items (prescription_id, medicine_name, dosage, frequency, duration) VALUES
(1,'Clindamycin gel','1 lan boi','2 lan/ngay','14 ngay'),
(1,'Niacinamide serum','1 lan boi','1 lan/toi','30 ngay'),
(2,'Adapalene 0.1%','1 lan boi','1 lan/toi','30 ngay'),
(3,'Cetirizine 10mg','1 vien','1 lan/ngay','5 ngay'),
(4,'Kem tri seo','1 lan boi','2 lan/ngay','30 ngay'),
(5,'Ketoconazole cream','1 lan boi','2 lan/ngay','14 ngay'),
(6,'Kem duong am phuc hoi','1 lan boi','2 lan/ngay','21 ngay');

-- =========================================================
-- LAB REQUESTS & RESULTS
-- =========================================================
INSERT INTO lab_requests (request_id, appointment_id, doctor_id, status, created_at) VALUES
(1,1,1,'completed','2026-03-02 09:00:00'),
(2,2,1,'completed','2026-03-09 09:10:00'),
(3,3,3,'cancelled','2026-03-03 09:30:00'),
(4,7,4,'processing','2026-03-05 14:30:00'),
(5,14,1,'completed','2026-03-06 13:40:00'),
(6,15,2,'pending','2026-03-06 14:10:00'),
(7,8,4,'pending','2026-03-12 14:20:00'),
(8,11,5,'pending','2026-03-17 15:05:00'),
(9,16,3,'pending','2026-03-14 10:15:00'),
(10,18,2,'pending','2026-03-18 14:00:00'),
(11,13,6,'pending','2026-03-08 09:45:00'),
(12,12,6,'pending','2026-03-04 09:00:00');

INSERT INTO lab_results (request_id, technician_id, result_file, notes, completed_at) VALUES
(1,3,'/lab-results/skin-test-apt1.pdf','Ket qua test da trong gioi han','2026-03-02 12:00:00'),
(2,3,'/lab-results/allergy-apt2.pdf','Chi so di ung giam so voi lan truoc','2026-03-09 12:30:00'),
(5,3,'/lab-results/dermoscopy-apt14.pdf','Khong ghi nhan bat thuong nguy hiem','2026-03-06 16:00:00');

-- Payments for lab requests (lab_request_id links payment to the lab request)
-- request 4: processing → payment đã paid (đã confirm trước khi tech bắt đầu XN)
-- requests 6-12: pending → payment pending (chờ receptionist xác nhận)
INSERT INTO payments (appointment_id, lab_request_id, amount, method, status, created_at) VALUES
(7,  4, 150000, 'cash',   'paid',    '2026-03-05 14:31:00'),
(15, 6, 150000, 'cash',   'pending', '2026-03-06 14:11:00'),
(8,  7, 180000, 'cash',   'pending', '2026-03-12 14:21:00'),
(11, 8, 130000, 'cash',   'pending', '2026-03-17 15:06:00'),
(16, 9, 160000, 'online', 'pending', '2026-03-14 10:16:00'),
(18,10, 120000, 'cash',   'pending', '2026-03-18 14:01:00'),
(13,11, 170000, 'online', 'pending', '2026-03-08 09:46:00'),
(12,12, 280000, 'cash',   'pending', '2026-03-04 09:01:00');

-- =========================================================
-- SERVICE PRICES
-- =========================================================
INSERT INTO service_prices (name, service_type, price) VALUES
('Khám da liễu tổng quát', 'booking_fee', 150000),
('Khám điều trị mụn', 'booking_fee', 180000),
('Khám da liễu dị ứng', 'booking_fee', 180000),
('Khám da liễu nhiễm trùng', 'booking_fee', 180000),
('Khám da liễu trẻ em', 'booking_fee', 200000),
('Khám tái khám da liễu', 'booking_fee', 100000),
('Khám chuyên sâu bệnh da', 'booking_fee', 250000),
('Khám tư vấn kết quả xét nghiệm da liễu', 'booking_fee', 120000),
('Soi da', 'lab', 120000),
('Dermoscopy', 'lab', 150000),
('Test dị ứng da', 'lab', 180000),
('Xét nghiệm nấm da', 'lab', 130000),
('Xét nghiệm soi tươi vi nấm', 'lab', 100000),
('Xét nghiệm ký sinh trùng da', 'lab', 160000),
('Xét nghiệm vi khuẩn da', 'lab', 170000),
('Cấy vi khuẩn da', 'lab', 250000),
('Kháng sinh đồ', 'lab', 300000),
('Xét nghiệm virus da liễu', 'lab', 280000),
('Sinh thiết da', 'lab', 450000),
('Giải phẫu bệnh da', 'lab', 500000),
('Xét nghiệm máu dị ứng', 'lab', 350000),
('Xét nghiệm công thức máu', 'lab', 120000);

-- =========================================================
-- EXAM QUEUE
-- =========================================================
INSERT INTO exam_queue (appointment_id, doctor_id, queue_position, status, created_at) VALUES
(6,2,1,'waiting','2026-03-11 08:00:00'),
(16,3,1,'waiting','2026-03-14 08:00:00'),
(8,4,1,'examining','2026-03-12 13:30:00'),
(4,3,2,'waiting','2026-03-10 08:15:00');

-- =========================================================
-- SCHEDULE CHANGE REQUESTS
-- =========================================================
INSERT INTO schedule_change_requests (request_id, doctor_id, request_type, scope_type, reason, status, requested_at, reviewed_at, reviewed_by, admin_note) VALUES
(1,1,'TEMPORARY','ONE_DATE','Xin doi ca sang ngay 2026-03-20 do hoi thao chuyen mon','APPROVED','2026-03-15 09:00:00','2026-03-15 14:00:00',1,'Da duyet va tao override'),
(2,1,'PERMANENT','WEEKLY_TEMPLATE','Dieu chinh lich thu 6 sang thu 6 chieu de phu hop lich giang day','PENDING','2026-03-16 10:00:00',NULL,NULL,NULL),
(3,5,'TEMPORARY','ONE_DATE','Xin nghi ca chieu 2026-03-19 vi ly do suc khoe','REJECTED','2026-03-16 11:20:00','2026-03-16 17:30:00',1,'Can bo tri nguoi truc thay, vui long gui lai som hon');

INSERT INTO schedule_change_request_items (request_id, action_type, target_shift_id, work_date, day_of_week, start_time, end_time, max_patients) VALUES
(1,'REMOVE',3,'2026-03-20',NULL,NULL,NULL,NULL),
(2,'UPDATE',3,NULL,5,'13:00:00','16:30:00',20),
(3,'REMOVE',20,'2026-03-19',NULL,NULL,NULL,NULL);

INSERT INTO doctor_shift_overrides (doctor_id, work_date, base_shift_id, action_type, start_time, end_time, max_patients, source_request_id, is_active) VALUES
(1,'2026-03-20',3,'CANCEL',NULL,NULL,NULL,1,TRUE);

-- =========================================================
-- SYSTEM LOGS
-- =========================================================
INSERT INTO system_logs (user_id, action, description, created_at) VALUES
(1,'SEED_DATABASE','Khoi tao du lieu mau cho toan bo chuc nang','2026-03-01 08:00:00'),
(4,'DOCTOR_CREATE_SCHEDULE_CHANGE_REQUEST','Bac si Nguyen Van A gui yeu cau doi lich tam thoi','2026-03-15 09:01:00'),
(1,'ADMIN_REVIEW_SCHEDULE_CHANGE_REQUEST','Duyet yeu cau #1 cua bac si Nguyen Van A','2026-03-15 14:01:00');

-- =========================================================
-- RATING
-- =========================================================
INSERT INTO rating_questions (question_text) VALUES
('Thái độ của bác sĩ có thân thiện không?'),
('Bác sĩ giải thích bệnh có dễ hiểu không?'),
('Bác sĩ có lắng nghe bệnh nhân không?'),
('Thời gian khám có hợp lý không?');

-- review_answers now includes appointment_id and note columns
INSERT INTO review_answers (question_id, rating, users_id, doctor_id, appointment_id, note) VALUES
(1,5,8,1,1,NULL),
(2,4,8,1,1,NULL),
(3,5,8,1,1,NULL),
(4,4,8,1,1,NULL),
(1,4,9,3,3,NULL),
(2,5,9,3,3,NULL),
(3,4,9,3,3,NULL),
(4,3,9,3,3,'Thời gian chờ hơi lâu'),
(1,5,10,5,10,NULL),
(2,4,10,5,10,NULL),
(3,5,10,5,10,NULL),
(4,5,10,5,10,NULL);

-- =========================================================
-- BULK SEED — patients
-- =========================================================
INSERT INTO patients (user_id, full_name, phone, dob, address, email, gender)
SELECT
   CASE WHEN nums.n % 3 = 1 THEN 8 WHEN nums.n % 3 = 2 THEN 9 ELSE 10 END,
   CONCAT('Seed Patient ', LPAD(nums.n, 2, '0')),
   CASE WHEN nums.n % 4 = 0 THEN NULL ELSE CONCAT('0932', LPAD(nums.n, 7, '0')) END,
   DATE_ADD('1992-01-01', INTERVAL nums.n * 57 DAY),
   CASE WHEN nums.n % 2 = 0 THEN 'Ha Noi' ELSE 'HCM' END,
   CASE WHEN nums.n % 5 = 0 THEN NULL ELSE CONCAT('seed.patient', nums.n, '@clinic.local') END,
   CASE WHEN nums.n % 3 = 0 THEN 'male' WHEN nums.n % 3 = 1 THEN 'female' ELSE 'other' END
FROM (
   SELECT (t.t * 10 + o.o + 1) AS n
   FROM (SELECT 0 AS t UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) t
   CROSS JOIN (SELECT 0 AS o UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
               SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) o
) nums
WHERE nums.n <= 36;

-- =========================================================
-- BULK SEED — appointments
-- =========================================================
INSERT INTO appointments (patient_id, doctor_id, shift_id, booking_type, appointment_date, appointment_time, status, symptom, created_at)
SELECT
   13 + ((nums.n - 1) % 36),
   1 + ((nums.n - 1) % 9),
   CASE (1 + ((nums.n - 1) % 9))
      WHEN 1 THEN CASE WHEN nums.n % 2 = 0 THEN 1 ELSE 2 END
      WHEN 2 THEN CASE WHEN nums.n % 2 = 0 THEN 5 ELSE 6 END
      WHEN 3 THEN CASE WHEN nums.n % 2 = 0 THEN 9 ELSE 10 END
      WHEN 4 THEN CASE WHEN nums.n % 2 = 0 THEN 13 ELSE 14 END
      WHEN 5 THEN CASE WHEN nums.n % 2 = 0 THEN 17 ELSE 18 END
      WHEN 6 THEN CASE WHEN nums.n % 2 = 0 THEN 21 ELSE 22 END
      WHEN 7 THEN CASE WHEN nums.n % 2 = 0 THEN 25 ELSE 26 END
      WHEN 8 THEN CASE WHEN nums.n % 2 = 0 THEN 29 ELSE 30 END
      ELSE CASE WHEN nums.n % 2 = 0 THEN 33 ELSE 34 END
   END,
   CASE WHEN nums.n % 4 = 0 THEN 'walk_in' ELSE 'online' END,
   DATE_ADD('2026-04-01', INTERVAL (nums.n % 28) DAY),
   CASE WHEN nums.n % 2 = 0 THEN '08:30:00' ELSE '14:00:00' END,
   CASE WHEN nums.n % 9 = 0 THEN 'cancelled' WHEN nums.n % 5 = 0 THEN 'waiting' WHEN nums.n % 4 = 0 THEN 'checked_in' WHEN nums.n % 3 = 0 THEN 'booked' ELSE 'completed' END,
   CONCAT('Trieu chung da lieu mau #', nums.n),
   DATE_ADD('2026-03-25 08:00:00', INTERVAL nums.n HOUR)
FROM (
   SELECT (t.t * 10 + o.o + 1) AS n
   FROM (SELECT 0 AS t UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) t
   CROSS JOIN (SELECT 0 AS o UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
               SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) o
) nums
WHERE nums.n <= 54;

-- =========================================================
-- BULK SEED — payments
-- =========================================================
INSERT INTO payments (appointment_id, amount, method, transaction_id, status, created_at)
SELECT
   a.appointment_id,
   d.price_booking,
   CASE WHEN a.booking_type = 'walk_in' THEN 'cash' ELSE 'online' END,
   CASE WHEN a.booking_type = 'walk_in' THEN NULL ELSE CONCAT('TXN-BULK-', a.appointment_id) END,
   CASE WHEN a.status = 'completed' THEN 'paid' WHEN a.status = 'cancelled' THEN 'failed' ELSE 'pending' END,
   DATE_ADD(a.created_at, INTERVAL 5 MINUTE)
FROM appointments a
JOIN doctors d ON d.doctor_id = a.doctor_id
WHERE a.appointment_id >= 19
  AND a.status <> 'cancelled';

-- =========================================================
-- BULK SEED — medical records
-- =========================================================
INSERT INTO medical_records (appointment_id, doctor_id, symptoms, diagnosis, notes, updated_at)
SELECT
   a.appointment_id,
   a.doctor_id,
   a.symptom,
   CONCAT('Chan doan da lieu mau cho appointment #', a.appointment_id),
   CASE WHEN a.appointment_id % 2 = 0 THEN 'Tai kham dinh ky' ELSE 'Kham lan dau' END,
   DATE_ADD(a.created_at, INTERVAL 2 DAY)
FROM appointments a
WHERE a.appointment_id >= 19
  AND a.status = 'completed';

-- =========================================================
-- BULK SEED — vital signs
-- =========================================================
INSERT INTO vital_signs (record_id, blood_pressure_sys, blood_pressure_dia, blood_sugar, heart_rate, temperature, respiratory_rate, measured_at)
SELECT
   mr.record_id,
   110 + (mr.record_id % 25),
   70 + (mr.record_id % 15),
   4.8 + ((mr.record_id % 10) * 0.1),
   72 + (mr.record_id % 25),
   36.4 + ((mr.record_id % 6) * 0.1),
   16 + (mr.record_id % 6),
   DATE_ADD(mr.updated_at, INTERVAL -4 HOUR)
FROM medical_records mr
WHERE mr.record_id > 10
  AND mr.record_id % 2 = 0;

-- =========================================================
-- BULK SEED — prescriptions & items
-- =========================================================
INSERT INTO prescriptions (record_id, doctor_id, created_at)
SELECT mr.record_id, mr.doctor_id, DATE_ADD(mr.updated_at, INTERVAL 10 MINUTE)
FROM medical_records mr
WHERE mr.record_id > 10
  AND mr.record_id % 3 <> 0;

INSERT INTO prescription_items (prescription_id, medicine_name, dosage, frequency, duration)
SELECT
   p.prescription_id,
   CASE WHEN p.prescription_id % 3 = 0 THEN 'Paracetamol 500mg' WHEN p.prescription_id % 3 = 1 THEN 'Vitamin C 500mg' ELSE 'Cetirizine 10mg' END,
   '1 vien',
   CASE WHEN p.prescription_id % 2 = 0 THEN '2 lan/ngay' ELSE '1 lan/ngay' END,
   CASE WHEN p.prescription_id % 2 = 0 THEN '5 ngay' ELSE '7 ngay' END
FROM prescriptions p
WHERE p.prescription_id > 6;

-- =========================================================
-- BULK SEED — lab requests & results
-- =========================================================
INSERT INTO lab_requests (appointment_id, doctor_id, status, created_at)
SELECT
   a.appointment_id,
   a.doctor_id,
   CASE WHEN a.appointment_id % 5 = 0 THEN 'cancelled' WHEN a.appointment_id % 3 = 0 THEN 'processing' WHEN a.appointment_id % 2 = 0 THEN 'completed' ELSE 'pending' END,
   DATE_ADD(a.created_at, INTERVAL 30 MINUTE)
FROM appointments a
WHERE a.appointment_id >= 19
  AND a.status IN ('completed', 'checked_in', 'waiting')
  AND a.appointment_id % 2 = 0;

INSERT INTO lab_results (request_id, technician_id, result_file, notes, completed_at)
SELECT lr.request_id, 3, CONCAT('/lab-results/bulk-', lr.request_id, '.pdf'), 'Ket qua xet nghiem du lieu mau', DATE_ADD(lr.created_at, INTERVAL 6 HOUR)
FROM lab_requests lr
WHERE lr.request_id > 6
  AND lr.status = 'completed';

-- Bulk seed payments for lab requests (pending/processing need a payment record)
INSERT INTO payments (appointment_id, lab_request_id, amount, method, status, created_at)
SELECT
   lr.appointment_id,
   lr.request_id,
   120000,
   'cash',
   CASE WHEN lr.status = 'completed' THEN 'paid' ELSE 'pending' END,
   DATE_ADD(lr.created_at, INTERVAL 1 MINUTE)
FROM lab_requests lr
WHERE lr.request_id > 6
  AND lr.status IN ('pending', 'processing', 'completed');

-- =========================================================
-- BULK SEED — exam queue
-- =========================================================
SET @qpos := 0;

INSERT INTO exam_queue (appointment_id, doctor_id, queue_position, status, created_at)
SELECT
   a.appointment_id,
   a.doctor_id,
   (@qpos := @qpos + 1),
   CASE WHEN a.status = 'checked_in' THEN 'examining' ELSE 'waiting' END,
   DATE_ADD(a.created_at, INTERVAL 15 MINUTE)
FROM appointments a
WHERE a.appointment_id >= 19
  AND a.status IN ('waiting', 'checked_in')
  AND a.appointment_id NOT IN (SELECT appointment_id FROM exam_queue)
ORDER BY a.doctor_id, a.appointment_date, a.appointment_time;

-- =========================================================
-- RESET AUTO_INCREMENT
-- =========================================================
ALTER TABLE users AUTO_INCREMENT = 100;
ALTER TABLE patients AUTO_INCREMENT = 1000;
ALTER TABLE doctors AUTO_INCREMENT = 50;
ALTER TABLE doctor_shifts AUTO_INCREMENT = 200;
ALTER TABLE appointments AUTO_INCREMENT = 5000;
ALTER TABLE medical_records AUTO_INCREMENT = 3000;
ALTER TABLE prescriptions AUTO_INCREMENT = 3000;
ALTER TABLE lab_requests AUTO_INCREMENT = 3000;
ALTER TABLE schedule_change_requests AUTO_INCREMENT = 2000;
ALTER TABLE schedule_change_request_items AUTO_INCREMENT = 5000;
ALTER TABLE doctor_shift_overrides AUTO_INCREMENT = 5000;
