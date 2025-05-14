<!-- <?php
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
$data = json_decode(file_get_contents("php://input"), true);

// Kiểm tra dữ liệu đầu vào
if (!isset($data['items']) || !is_array($data['items'])) {
    http_response_code(400);
    echo json_encode(["message" => "Missing required field: items"]);
    exit;
}

$items = $data['items'];
$orderId = uniqid($tableNumber . '_');

// Bắt đầu transaction
$conn->begin_transaction();

try {
    // Insert vào bảng orders
    $sql = "INSERT INTO orders (order_id, table_number, status, created_at) VALUES ('$orderId', '$tableNumber', 'CONFIRMED', NOW())";
    if ($conn->query($sql) === TRUE) {
        // Insert vào bảng order_items
        $sql_order_items = "INSERT INTO order_items (order_id, product_id, quantity, note) VALUES (?, ?, ?, ?)";
        $stmt = $conn->prepare($sql_order_items);

        foreach ($items as $item) {
            $stmt->bind_param("siis", $orderId, $item['idProducts'], $item['quantity'], $item['note']);
            $stmt->execute();
        }

        // Xóa giỏ hàng sau khi đặt đơn
        $sql_clear_cart = "DELETE FROM carts WHERE table_number = '$tableNumber'";
        $conn->query($sql_clear_cart);

        // Commit transaction
        $conn->commit();

        echo json_encode(["message" => "Order submitted successfully", "orderId" => $orderId]);
    } else {
        throw new Exception("Failed to insert order into database.");
    }
} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode(["message" => "Failed to submit order: " . $e->getMessage()]);
}

$conn->close();
?> -->
