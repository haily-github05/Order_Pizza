<?php
include 'db.php'; // Đảm bảo đã kết nối với cơ sở dữ liệu

$data = json_decode(file_get_contents("php://input"));

$sdt = $data->sdt;
$points = $data->points;

$query = "INSERT INTO users (sdt, points) VALUES ('$sdt', '$points')";
if (mysqli_query($conn, $query)) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", "message" => "Không thể thêm người dùng"]);
}
?>
