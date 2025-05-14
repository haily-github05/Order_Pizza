<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include 'db.php';

$data = json_decode(file_get_contents("php://input"), true);

// Kiểm tra dữ liệu đầu vào
if (!isset($data['idProducts']) || !isset($data['table_number'])) {
    http_response_code(400);
    echo json_encode(["message" => "Missing required fields: idProducts or table_number"]);
    exit;
}

$idProducts = $conn->real_escape_string($data['idProducts']);
$tableNumber = $conn->real_escape_string($data['table_number']);

// Câu lệnh xóa item trong giỏ hàng
$sql = "DELETE FROM carts WHERE idProducts = '$idProducts' AND table_number = '$tableNumber'";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["message" => "Item deleted successfully"]);
} else {
    http_response_code(500);
    echo json_encode(["message" => "Failed to delete item: " . $conn->error]);
}

$conn->close();
?>
