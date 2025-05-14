<?php
include 'db.php'; // Đảm bảo đã kết nối với cơ sở dữ liệu

$sdt = $_GET['sdt'];

$query = "SELECT * FROM users WHERE sdt = '$sdt'";
$result = mysqli_query($conn, $query);
if (mysqli_num_rows($result) > 0) {
    // Nếu có, trả về thông tin người dùng
    $user = mysqli_fetch_assoc($result);
    echo json_encode($user);
} else {
    // Nếu không có, trả về null
    echo json_encode(null);
}
?>
