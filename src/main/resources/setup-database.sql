CREATE DATABASE daily_report_system CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'repuser'@'localhost' IDENTIFIED BY 'reppass';
GRANT ALL PRIVILEGES ON daily_report_system.* to 'repuser'@'localhost';