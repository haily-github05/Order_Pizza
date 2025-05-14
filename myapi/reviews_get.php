<?php
include 'db.php'; // Kết nối cơ sở dữ liệu

header('Content-Type: application/json');

// Lấy tất cả các đánh giá từ bảng reviews
try {
    $stmt = $conn->prepare("SELECT phone_number, rating, comment, image_product FROM reviews ORDER BY rating DESC"); // Sắp xếp theo rating
    $stmt->execute();
    $result = $stmt->get_result();

    // Tạo mảng để chứa các đánh giá
    $reviews = [];
    while ($row = $result->fetch_assoc()) {
        $reviews[] = $row;
    }

    // Kiểm tra nếu không có đánh giá nào
    if (empty($reviews)) {
        echo json_encode(["status" => "error", "message" => "Không có đánh giá nào."]);
    } else {
        echo json_encode(["status" => "success", "reviews" => $reviews]);
    }
} catch (Exception $e) {
    // Xử lý khi có lỗi
    echo json_encode(["status" => "error", "message" => "Lỗi hệ thống: " . $e->getMessage()]);
}

$stmt->close();
$conn->close();
?>
