<?php
include 'db.php'; // File kết nối MySQLi

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');

// Bật hiển thị lỗi để debug (tạm thời)
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$query = isset($_GET['query']) ? $_GET['query'] : '';
$query = trim($query);

if (empty($query)) {
    http_response_code(400);
    echo json_encode(["error" => "Query không được để trống"]);
    exit;
}

// Truy vấn
$safeQuery = $conn->real_escape_string($query);
$sql = "SELECT idProducts, name, description, price, image, type 
        FROM products 
        WHERE LOWER(name) LIKE '%$safeQuery%' OR LOWER(description) LIKE '%$safeQuery%'";
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