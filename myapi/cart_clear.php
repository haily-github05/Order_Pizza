<?php
session_start();
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include 'db.php';

// Kiểm tra session để lấy số bàn
if (!isset($_SESSION['tableNumber'])) {
    echo json_encode(['error' => 'Table number not found in session']);
    exit;
}

$tableNumber = $_SESSION['tableNumber'];

// Câu lệnh xóa toàn bộ giỏ hàng của bàn
$sql = "DELETE FROM carts WHERE table_number = '$tableNumber'";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["message" => "Cart cleared successfully"]);
} else {
    http_response_code(500);
    echo json_encode(["message" => "Failed to clear cart: " . $conn->error]);
}

$conn->close();
?>
