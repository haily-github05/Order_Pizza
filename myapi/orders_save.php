<?php
header("Content-Type: application/json");
require_once("db.php");

$data = json_decode(file_get_contents("php://input"), true);

if (!$data || !isset($data['order_id']) || !isset($data['items']) || !isset($data['totalAmount'])) {
    http_response_code(400);
    echo json_encode(["message" => "Invalid input"]);
    exit;
}

$orderId = $data['order_id'];
$totalAmount = $data['totalAmount'];
$timestamp = $data['timestamp'];
$items = $data['items'];
$phone_number = isset($data['phoneNumber']) ? $data['phoneNumber'] : null;

// Lưu đơn hàng
$stmt = $conn->prepare("INSERT INTO orders (order_id, total_amount, timestamp, phone_number) VALUES (?, ?, ?, ?)");
$stmt->bind_param("sdds", $orderId, $totalAmount, $timestamp, $phone_number);

if (!$stmt->execute()) {
    http_response_code(500);
    echo json_encode(["message" => "Failed to save order: " . $stmt->error]);
    exit;
}
$stmt->close();

// Lưu từng món trong đơn hàng
$itemStmt = $conn->prepare("INSERT INTO order_items (order_id, product_id, name, price, quantity, note) VALUES (?, ?, ?, ?, ?, ?)");
foreach ($items as $item) {
    $productId = $item['idProducts'];
    $name = $item['name'];
    $price = $item['price'];
    $quantity = $item['quantity'];
    $note = isset($item['note']) ? $item['note'] : "";

    $itemStmt->bind_param("sssdis", $orderId, $productId, $name, $price, $quantity, $note);
    if (!$itemStmt->execute()) {
        http_response_code(500);
        echo json_encode(["message" => "Failed to save item: " . $itemStmt->error]);
        exit;
    }
}
$itemStmt->close();

echo json_encode(["message" => "Order saved successfully"]);
?>
