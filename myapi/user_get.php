
<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

include('db.php');

// Kiểm tra lỗi kết nối
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

// Truy vấn dữ liệu
$sql = "SELECT sdt, points FROM users"; // đổi 'users' nếu bảng bạn tên khác
$result = $conn->query($sql);

$users = [];

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
}

echo json_encode($users);

$conn->close();
?>
