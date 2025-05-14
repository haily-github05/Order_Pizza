<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: PUT");
header("Access-Control-Allow-Headers: Content-Type");

include_once "db.php"; 

// Nhận dữ liệu từ request
$data = json_decode(file_get_contents("php://input"), true);

// Log dữ liệu nhận được để debug
error_log("Dữ liệu nhận được: " . json_encode($data));

// Kiểm tra dữ liệu đầu vào bắt buộc
if (!isset($data['idProducts']) || !isset($data['table_number']) || !isset($data['quantity'])) {
    http_response_code(400);
    echo json_encode(["error" => "Thiếu dữ liệu bắt buộc: idProducts, table_number hoặc quantity"]);
    exit;
}

// Gán giá trị và kiểm tra độ dài
$idProducts = trim($data['idProducts']);
$tableNumber = trim($data['table_number']);
$quantity = intval($data['quantity']);

// Nếu số lượng bằng 0, xóa sản phẩm khỏi giỏ hàng
if ($quantity === 0) {
    $sql_delete = "DELETE FROM carts WHERE idProducts = ? AND table_number = ?";
    $stmt_delete = $conn->prepare($sql_delete);
    $stmt_delete->bind_param("ss", $idProducts, $tableNumber);
    
    if ($stmt_delete->execute()) {
        echo json_encode(["message" => "Sản phẩm đã được xóa khỏi giỏ hàng"]);
    } else {
        http_response_code(500);
        echo json_encode(["error" => "Xóa sản phẩm thất bại: " . $conn->error]);
    }
    
    $stmt_delete->close();
    $conn->close();
    exit;
}

// Kiểm tra xem sản phẩm có tồn tại trong giỏ hàng không
$sql_check = "SELECT * FROM carts WHERE idProducts = ? AND table_number = ?";
$stmt_check = $conn->prepare($sql_check);
$stmt_check->bind_param("ss", $idProducts, $tableNumber);
$stmt_check->execute();
$result = $stmt_check->get_result();

if ($result->num_rows === 0) {
    http_response_code(404);
    echo json_encode(["error" => "Không tìm thấy sản phẩm trong giỏ hàng"]);
    $stmt_check->close();
    $conn->close();
    exit;
}

// Cập nhật số lượng
$sql_update = "UPDATE carts SET quantity = ? WHERE idProducts = ? AND table_number = ?";
$stmt_update = $conn->prepare($sql_update);
$stmt_update->bind_param("iss", $quantity, $idProducts, $tableNumber);

if ($stmt_update->execute()) {
    echo json_encode(["message" => "Cập nhật giỏ hàng thành công"]);
} else {
    http_response_code(500);
    echo json_encode(["error" => "Cập nhật giỏ hàng thất bại: " . $conn->error]);
}

// Đóng các statement và kết nối
$stmt_check->close();
$stmt_update->close();
$conn->close();
?>