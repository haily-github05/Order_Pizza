<?php
include 'db.php';

// Đặt Content-Type cho phản hồi JSON
header('Content-Type: application/json');

// Kiểm tra nếu các giá trị POST tồn tại
if (!isset($_POST['phone_number']) || !isset($_POST['rating']) || !isset($_POST['comment'])) {
    echo json_encode(["status" => "error", "message" => "Hãy nhập đủ thông tin trước khi đánh giá"]);
    exit;
}

// Lấy dữ liệu từ request
$phone_number = $_POST['phone_number'];
$rating = $_POST['rating'];
$comment = $_POST['comment'];
$image_product = isset($_POST['image_product']) ? $_POST['image_product'] : null;

// Kiểm tra dữ liệu rỗng
if (empty($phone_number) || empty($rating) || empty($comment)) {
    echo json_encode(["status" => "error", "message" => "Hãy nhập đủ thông tin trước khi đánh giá"]);
    exit;
}

// Kiểm tra bảo mật dữ liệu đầu vào
$phone_number = $conn->real_escape_string($phone_number);
$rating = intval($rating);  // Chuyển đổi rating thành integer
$comment = $conn->real_escape_string($comment);
$image_product = $image_product ? $conn->real_escape_string($image_product) : null;

// Chuẩn bị truy vấn SQL
if ($image_product) {
    $stmt = $conn->prepare("INSERT INTO reviews (phone_number, rating, comment, image_product) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("siss", $phone_number, $rating, $comment, $image_product);
} else {
    $stmt = $conn->prepare("INSERT INTO reviews (phone_number, rating, comment) VALUES (?, ?, ?)");
    $stmt->bind_param("sis", $phone_number, $rating, $comment);
}

// Thực hiện truy vấn
if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Đánh giá đã được lưu."]);
} else {
    echo json_encode(["status" => "error", "message" => "Lỗi khi lưu đánh giá."]);
}

// Ghi vào log server (để kiểm tra nếu có lỗi)
error_log("REVIEW_DATA: Rating: $rating, Comment: $comment, Image: $image_product, Phone: $phone_number");

// Đóng kết nối
$stmt->close();
$conn->close();
?>