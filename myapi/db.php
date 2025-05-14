<?php
$host = "localhost";       // Hoặc địa chỉ IP của MySQL server
$user = "root";            // Tên đăng nhập MySQL (thường là root trên localhost)
$password = "";            // Mật khẩu MySQL (nếu có)
$dbname = "Orderpizza"; // Tên CSDL bạn đã tạo trong phpMyAdmin

// Kết nối
$conn = new mysqli($host, $user, $password, $dbname);
if ($conn->connect_error) {
    die("Kết nối thất bại: " . $conn->connect_error);
}
// $conn->set_charset("utf8mb4");
?>

