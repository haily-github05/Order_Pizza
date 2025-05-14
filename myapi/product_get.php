
<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

// Thông tin kết nối database
include 'db.php';

// Truy vấn dữ liệu
$sql = "SELECT idProducts, name, type, description, image, price  FROM products"; 
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

echo json_encode($products);

$conn->close();
?>