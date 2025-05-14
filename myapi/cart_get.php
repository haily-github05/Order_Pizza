<?php
header("Content-Type: application/json");
require_once 'db.php'; // Kết nối CSDL

if (!isset($_GET['tableNumber'])) {
    http_response_code(400);
    echo json_encode(array("error" => "Thiếu tham số tableNumber"));
    exit();
}

$tableNumber = $_GET['tableNumber'];
$sql = "SELECT idProducts, name, price, quantity, note, table_number FROM carts WHERE table_number = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $tableNumber); 

if ($stmt->execute()) {
    $result = $stmt->get_result();
    $cartItems = [];

    while ($row = $result->fetch_assoc()) {
        $cartItems[] = [
            "idProducts" => $row["idProducts"],
            "name" => $row["name"],
            "price" => (float) $row["price"],
            "quantity" => (int) $row["quantity"],
            "note" => $row["note"],
            "tableNumber" => $row["table_number"]
        ];
    }

    echo json_encode($cartItems);
} else {
    http_response_code(500);
    echo json_encode(["error" => "Lỗi truy vấn: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
