<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST");
header("Access-Control-Allow-Headers: Content-Type");

// Kết nối cơ sở dữ liệu
include 'db.php';


// Lấy type
$type = isset($_GET['type']) ? $_GET['type'] : '';
$type = trim($type);

if (empty($type)) {
    http_response_code(400);
    echo json_encode(["error" => "Type không được để trống"]);
    exit;
}

// Truy vấn
$safeType = $conn->real_escape_string($type);
$sql = "SELECT idProducts, name, description, price, image, type 
        FROM products 
        WHERE type = '$safeType'";
$result = $conn->query($sql);

$products = [];

if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $products[] = [
            "idProducts" => $row["idProducts"],
            "name" => $row["name"],
            "description" => $row["description"],
            "price" => (float)$row["price"],
            "image" => $row["image"],
            "type" => $row["type"]
        ];
    }
} else {
    error_log("SQL Query: $sql");
    error_log("SQL Error: " . $conn->error);
}

// Trả về kết quả
http_response_code(200);
echo json_encode($products);

$conn->close();
?>