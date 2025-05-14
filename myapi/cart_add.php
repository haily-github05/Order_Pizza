<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include 'db.php';

$data = json_decode(file_get_contents("php://input"), true);

if (
    !isset($data['idProducts']) ||
    !isset($data['name']) ||
    !isset($data['price']) ||
    !isset($data['quantity']) ||
    !isset($data['tableNumber'])
) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Missing required fields"]);
    exit;
}

$idProducts = $conn->real_escape_string($data['idProducts']);
$name = $conn->real_escape_string($data['name']);
$price = floatval($data['price']);
$quantity = intval($data['quantity']);
$tableNumber = $conn->real_escape_string($data['tableNumber']);
$note = isset($data['note']) ? $conn->real_escape_string($data['note']) : null;

$checkSql = "SELECT * FROM carts WHERE idProducts = '$idProducts' AND table_number = '$tableNumber'";
$result = $conn->query($checkSql);

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $newQuantity = $row['quantity'] + $quantity;
    $updateSql = "UPDATE carts SET quantity = $newQuantity, note = " . ($note ? "'$note'" : "NULL") . " WHERE idProducts = '$idProducts' AND table_number = '$tableNumber'";
    
    if ($conn->query($updateSql) === TRUE) {
        echo json_encode(["success" => true, "message" => "Cart updated successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Error updating cart: " . $conn->error]);
    }
} else {
    $insertSql = "INSERT INTO carts (idProducts, name, price, quantity, note, table_number) 
                  VALUES ('$idProducts', '$name', $price, $quantity, " . ($note ? "'$note'" : "NULL") . ", '$tableNumber')";
    
    if ($conn->query($insertSql) === TRUE) {
        echo json_encode(["success" => true, "message" => "Item added to cart successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Error adding item: " . $conn->error]);
    }
}

$conn->close();
?>