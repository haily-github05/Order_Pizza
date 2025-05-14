<?php
header('Content-Type: application/json');
require_once 'db.php'; // File này chứa thông tin kết nối database

if (!isset($_GET['phone_number'])) {
    echo json_encode(["error" => "Missing phone_number"]);
    exit;
}

$phone_number = $_GET['phone_number'];

// Chuẩn bị câu truy vấn lấy danh sách đơn hàng
$sql = "SELECT * FROM orders WHERE phone_number = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $phone_number);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];

while ($row = $result->fetch_assoc()) {
    $orderId = $row['order_id'];
    $items = [];

    // Lấy chi tiết từng sản phẩm trong đơn hàng
    $item_sql = "SELECT * FROM order_items WHERE order_id = ?";
    $item_stmt = $conn->prepare($item_sql);
    $item_stmt->bind_param("s", $orderId);
    $item_stmt->execute();
    $item_result = $item_stmt->get_result();

    while ($item_row = $item_result->fetch_assoc()) {
        $items[] = [
            "idProducts" => $item_row['product_id'],
            "name" => $item_row['name'],
            "price" => (double)$item_row['price'],
            "quantity" => (int)$item_row['quantity'],
            "note" => $item_row['note']
        ];
    }

    $orders[] = [
        "order_id" => $orderId,
        "items" => $items,
        "totalAmount" => (double)$row['total_amount'],
        "timestamp" => (int)$row['timestamp'],
        "phoneNumber" => $row['phone_number']
    ];
}

echo json_encode($orders);
$conn->close();
?>
