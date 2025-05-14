-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Máy chủ: localhost
-- Thời gian đã tạo: Th5 14, 2025 lúc 06:50 PM
-- Phiên bản máy phục vụ: 10.4.21-MariaDB
-- Phiên bản PHP: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `Orderpizza`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `carts`
--

CREATE TABLE `carts` (
  `id` int(11) NOT NULL,
  `idProducts` varchar(255) NOT NULL,
  `table_number` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `note` text DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `carts`
--

INSERT INTO `carts` (`id`, `idProducts`, `table_number`, `name`, `price`, `note`, `quantity`) VALUES
(67, 'PZ001', '5', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', 189000, NULL, 1),
(68, 'PZ001', '7', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', 189000, NULL, 3),
(69, 'OF001', '7', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', 239000, NULL, 1),
(74, 'OF001', '8', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', 239000, NULL, 1),
(76, 'OF001', '6', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', 239000, NULL, 1),
(79, 'PZ002', '5', 'PIZZA GÀ NƯỚNG NẤM', 189000, NULL, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `orders`
--

CREATE TABLE `orders` (
  `order_id` varchar(255) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `orders`
--

INSERT INTO `orders` (`order_id`, `total_amount`, `timestamp`, `phone_number`, `created_at`) VALUES
('1746907928356', '1116000.00', 1746907928356, '0865529572', '2025-05-10 20:12:08'),
('1746908195318', '707000.00', 1746908195318, '012345678', '2025-05-10 20:16:35'),
('1746938476739', '478000.00', 1746938476740, '0865529572', '2025-05-11 04:41:29'),
('1746938515527', '279000.00', 1746938515527, '0865529572', '2025-05-11 04:42:08'),
('1747021104073', '189000.00', 1747021104074, '0865529572', '2025-05-12 03:38:24'),
('1747021118287', '189000.00', 1747021118287, '0865529572', '2025-05-12 03:38:38'),
('1747021361259', '428000.00', 1747021361260, NULL, '2025-05-12 03:42:50'),
('1747021589317', '428000.00', 1747021589318, NULL, '2025-05-12 03:46:38'),
('1747021645910', '428000.00', 1747021645910, NULL, '2025-05-12 03:47:35'),
('1747021745899', '239000.00', 1747021745899, '0865529572', '2025-05-12 03:49:06'),
('1747021882779', '469000.00', 1747021882779, NULL, '2025-05-12 03:51:22'),
('1747170295761', '897000.00', 1747170295761, NULL, '2025-05-13 21:04:56'),
('1747171924128', '189000.00', 1747171924129, NULL, '2025-05-13 21:32:04'),
('1747214189841', '567000.00', 1747214189841, '0865529572', '2025-05-14 09:16:30');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_items`
--

CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `note` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `name`, `price`, `quantity`, `note`) VALUES
(13, '1746907928356', 'OF002', 'MUA 1 TẶNG 1 PIZZA (CỠ VỪA)', '369000.00', 2, 'Không cay'),
(14, '1746907928356', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 1, 'Cay vừa'),
(15, '1746907928356', 'PZ001', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', '189000.00', 1, 'Cay vừa'),
(16, '1746908195318', 'OF001', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', '239000.00', 1, 'Cay'),
(17, '1746908195318', 'CM002', 'COMBO 2', '279000.00', 1, ''),
(18, '1746908195318', 'CM005', 'COMBO 5 _ 1 NGƯỜI', '189000.00', 1, 'K cay'),
(19, '1746938476739', 'OF001', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', '239000.00', 2, 'Thêm phomai'),
(20, '1746938515527', 'CM002', 'COMBO 2', '279000.00', 1, ''),
(21, '1747021104073', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 1, 'k cay'),
(22, '1747021118287', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 1, ''),
(23, '1747021361259', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 1, ''),
(24, '1747021589317', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 1, ''),
(25, '1747021645910', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 1, ''),
(26, '1747021645910', 'OF001', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', '239000.00', 1, ''),
(27, '1747021745899', 'OF001', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', '239000.00', 1, 'Cay'),
(28, '1747021882779', 'OF003', 'MUA 1 TẶNG 1 PIZZA (CỠ LỚN)', '469000.00', 1, 'Cay nhiều'),
(29, '1747170295761', 'PZ001', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', '189000.00', 1, ''),
(30, '1747170295761', 'OF001', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', '239000.00', 1, ''),
(31, '1747170295761', 'CM001', 'COMBO 1 _ 4 NGƯỜI', '469000.00', 1, ''),
(32, '1747171924128', 'PZ001', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', '189000.00', 1, ''),
(33, '1747214189841', 'PZ001', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', '189000.00', 1, ''),
(34, '1747214189841', 'PZ002', 'PIZZA GÀ NƯỚNG NẤM', '189000.00', 2, '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `products`
--

CREATE TABLE `products` (
  `idProducts` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `image` varchar(50) DEFAULT NULL,
  `price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `products`
--

INSERT INTO `products` (`idProducts`, `name`, `type`, `description`, `image`, `price`) VALUES
('CM001', 'COMBO 1 _ 4 NGƯỜI', 'COMBO', '1 Large Pizza Pan + 1 French Fries + 1 Garden salad + 1 Garlic bread + 1 Pépo pitcher.', 'combo1', 469000),
('CM002', 'COMBO 2', 'COMBO', '2 Nước + 2 Bánh.', 'combo2', 279000),
('CM003', 'COMBO 3 _ 3 NGƯỜI', 'COMBO', '2 Pizza Cỡ Lớn.', 'combo3_3ng', 379000),
('CM004', 'COMBO 4 _ BUỔI TRƯA', 'COMBO', 'Siêu tiết kiệm chỉ từ 49k.', 'combo4_btrua', 339000),
('CM005', 'COMBO 5 _ 1 NGƯỜI', 'COMBO', 'Combo ngọt ngào.', 'combo5_1ng', 189000),
('DR001', 'Pepsi Lon', 'DRINK', 'Vị cola mát lạnh, sảng khoái tức thì.', 'pepsi', 29000),
('DR002', 'Pepsi Không Calo', 'DRINK', 'Giải khát không lo tăng cân.', 'pepsinocal', 29000),
('DR003', '7Up', 'DRINK', 'Hương chanh tự nhiên, không caffeine.', 'sevenup', 29000),
('DR004', 'Mirinda Cam', 'DRINK', 'Nước cam ngọt ngào, phù hợp với mọi độ tuổi.', 'mirinda', 29000),
('DR005', 'Aquafina', 'DRINK', 'Nước suối tinh khiết giúp giải nhiệt tức thì.', 'aquafina', 20000),
('DR006', 'Trà Đào', 'DRINK', 'Trà thanh mát, hỗ trợ tiêu hóa.', 'tea_peach', 35000),
('DR007', 'Trà Vải', 'DRINK', 'Trà thanh mát, thơm vị vải nhẹ nhàng.', 'tea_litchi', 37000),
('MB001', 'COMBO MY BOX 1', 'MYBOX', 'Dành Cho 1-2 Người\n01 Pizza Gà Nướng Nấm / Phô Mai Cao Cấp / Pepperoni (Cỡ Nhỏ)\n01 Bánh Mì Bơ Tỏi / Bánh Cuộn Phô Mai / Khoai Tây Chiên', 'mybox1', 99000),
('MB002', 'COMBO MY BOX 2', 'MYBOX', '01 Pizza Rau Củ / Pepperoni / Hawaiian / Phô Mai Cao Cấp / Gà Nướng Nấm / Bò BBQ Xốt Cay Hàn Quốc (Cỡ Nhỏ)\n01 Khoai Tây Chiên\n01 Bánh Mì Bơ Tỏi / Bánh Cuộn Phô Mai', 'mybox2', 139000),
('MB003', 'COMBO MY BOX 3', 'MYBOX', '01 Pizza (Cỡ Nhỏ)\n01 Khoai Tây Chiên\n01 Bánh Mì Bơ Tỏi / Bánh Cuộn Phô Mai', 'mybox3', 159000),
('MB004', 'COMBO MY BOX 4', 'MYBOX', '01 Pizza (Cỡ Nhỏ)\n01 Khoai Tây Chiên / Bánh Mì Bơ Tỏi / Bánh Cuộn Phô Mai\n01 Cánh Gà BBQ Múa Lửa Hồng (2 Miếng) / Cánh Gà Xốt Cay-Pop Hàn Quốc (2 Miếng) / Cánh Gà Giòn Karaage (2 Miếng) / Cánh Gà Xóc Mắm Tỏi Mekong (2 Miếng)', 'mybox4', 179000),
('MB005', 'COMBO MY BOX 5', 'MYBOX', '01 Pizza (Cỡ Vừa)\n01 Bánh Mì Bơ Tỏi / Bánh Cuộn Phô Mai\n01 Khoai Tây Chiên', 'mybox5', 249000),
('MB006', 'COMBO MY BOX 6', 'MYBOX', '01 Pizza (Cỡ Vừa)\n01 Bánh Mì Bơ Tỏi / Bánh Cuộn Phô Mai\n01 Cánh Gà BBQ Múa Lửa Hồng (2 Miếng) / Cánh Gà Xốt Cay-Pop Hàn Quốc (2 Miếng) / Cánh Gà Giòn Karaage (2 Miếng) / Cánh Gà Xóc Mắm Tỏi Mekong (2 Miếng)', 'mybox6', 269000),
('MN001', 'Pizza Phô Mai (Cỡ Nhỏ)', 'PIZZA49K', 'Ba Rọi Xông Khói, Phô Mai Mozzarella, Hành Tây, Xốt Sweet Chili, Xốt Cheesy Mayo, Ngò Tây Băm', 'pizzaphomai', 49000),
('MN002', 'Pizza Bò BBQ Xốt Cay Hàn Quốc (Cỡ Nhỏ)', 'PIZZA49K', 'Bò Bằm, Thơm, Bắp Hạt, Phô Mai Mozzarella, Xốt Tiêu Đen, Ngò Tây Băm', 'pizzabohanquoc', 49000),
('MN003', 'Pizza Cá Ngừ (Cỡ Nhỏ)', 'PIZZA49K', 'Xốt Pesto, Phô Mai Mozzarella, Cá Ngừ, Thanh Cua, Hành Tây, Ngò Băm', 'pizzacangu', 49000),
('MN004', 'Mì Ý Thanh Cua', 'PASTA', 'Cua, Phô Mai Parmesan, Xốt Gà Cay', 'miythanhcua', 49000),
('MN005', 'Mì Ý Thịt Bò Bằm', 'PASTA', 'Thịt Bò Bằm, Hành Tây, Phô Mai Parmesan, Xốt Pesto', 'miythitxongkhoi', 49000),
('OF001', 'MUA 1 TẶNG 1 PIZZA (CỠ NHỎ)', 'MUA1TANG1', 'Mua Pizza Cỡ Nhỏ + Lon Nước.', 'mua1tang1', 239000),
('OF002', 'MUA 1 TẶNG 1 PIZZA (CỠ VỪA)', 'MUA1TANG1', 'Mua Pizza Cỡ Vừa + Lon Nước.', 'mua1tang1', 369000),
('OF003', 'MUA 1 TẶNG 1 PIZZA (CỠ LỚN)', 'MUA1TANG1', 'Mua Pizza Cỡ Lớn + Lon Nước.', 'mua1tang1', 469000),
('OF004', 'MUA 1 TẶNG 2 PIZZA (CỠ VỪA)', 'MUA1TANG2', 'Mua 2 Pizza Cỡ Vừa + Bánh mì.', 'mua2tang1', 489000),
('OF005', 'MUA 1 TẶNG 2 PIZZA (CỠ LỚN)', 'MUA1TANG2', 'Mua 2 Pizza Cỡ Lớn + Bánh mì.', 'mua2tang1', 539000),
('PZ001', 'PIZZA BÒ BBQ XỐT CAY HÀN QUỐC', 'PIZZA', 'Hương vị thịt bò Úc thượng hạng, thơm hoà quyện sốt cay phủ rau mầm và mè rang.', 'pizzabohanquoc', 189000),
('PZ002', 'PIZZA GÀ NƯỚNG NẤM', 'PIZZA', 'Thịt gà, nấm thơm, mozzarella với xốt tiêu đen, phủ cà rốt và rau mầm tươi ngon.', 'pizzaga', 189000),
('PZ003', 'PIZZA RAU CỦ', 'PIZZA', 'Oliu đen, cà chua bi, nấm, thơm, bắp, hành tây trên nền xốt bơ tỏi và phô mai Mozzarella.', 'pizzaraucu', 189000),
('PZ004', 'PIZZA HEO XÉ BBQ HÀN QUỐC', 'PIZZA', 'Thịt heo xé, rau mầm, mè rang, thơm, xốt Bulgogi.', 'pizzaheoxe', 189000),
('PZ005', 'PIZZA HẢI SẢN', 'PIZZA', 'Tôm, thanh cua, cà chua bi, bắp ngọt, thơm, phô mai Mozarella.', 'pizzahaisan', 239000),
('PZ006', 'PIZZA TÔM', 'PIZZA', 'Tôm xốt bơ tỏi với hành tây và ớt chuông, phủ phô mai Mozzarella.', 'pizzatom', 229000),
('PZ007', 'PIZZA THẬP CẨM', 'PIZZA', 'Pepperoni, thịt bò, thịt xông khói, giăm bông, nấm, hành tây, ớt chuông, xốt cà chua, thơm.', 'pizzathapcam', 229000),
('PZ008', 'PIZZA THỊT VÀ XÚC XÍCH', 'PIZZA', 'Thơm ngon và giàu protein với thịt xông khói, xúc xích, thịt bò, giăm bông và pepperoni.', 'pizzathitvaxucxich', 229000),
('PZ009', 'PIZZA PHÔ MAI', 'PIZZA', 'Phô mai Mozzarella, mật ong, xốt cà chua. Ngon hơn với mật ong.', 'pizzaphomai', 189000),
('PZ010', 'PIZZA CÁ NGỪ', 'PIZZA', 'Cá ngừ, thanh cua, hành tây và thơm phủ phô mai Mozzarella.', 'pizzacangu', 229000),
('PZ011', 'PIZZA HAWAIIAN', 'PIZZA', 'Giăm bông và thơm ngọt dịu trên nền xốt cà chua truyền thông và phủ phô mai Mozzarella.', 'pizzahawaiian', 189000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reviews`
--

CREATE TABLE `reviews` (
  `phone_number` int(12) NOT NULL,
  `rating` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_product` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `reviews`
--

INSERT INTO `reviews` (`phone_number`, `rating`, `comment`, `image_product`) VALUES
(322250161, '5', 'Dịch vụ tốt', ''),
(329466605, '5', 'Đồ ăn ngon nha', ''),
(773556819, '5', 'Nhân viên phục vụ nhiệt tình', '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `sdt` varchar(15) NOT NULL,
  `points` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`sdt`, `points`) VALUES
('012345678', 0),
('0862074341', 0),
('08644256222', 0),
('0865529572', 2);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idProducts` (`idProducts`);

--
-- Chỉ mục cho bảng `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`);

--
-- Chỉ mục cho bảng `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`);

--
-- Chỉ mục cho bảng `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`idProducts`);

--
-- Chỉ mục cho bảng `reviews`
--
ALTER TABLE `reviews`
  ADD UNIQUE KEY `phone_number` (`phone_number`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`sdt`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `carts`
--
ALTER TABLE `carts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;

--
-- AUTO_INCREMENT cho bảng `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `carts_ibfk_1` FOREIGN KEY (`idProducts`) REFERENCES `products` (`idProducts`);

--
-- Các ràng buộc cho bảng `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
