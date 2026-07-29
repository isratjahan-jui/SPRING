-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: multihotel
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK47bvqemyk6vlm0w7crc3opdd4` (`email`),
  UNIQUE KEY `UKpiovo1hsx7hi5f9ax85epqya9` (`user_id`),
  UNIQUE KEY `UKt7lspe46d49rf6ce5h660ve5t` (`phone`),
  CONSTRAINT `FKgc8dtql9mkq268detxiox7fpm` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_trail`
--

DROP TABLE IF EXISTS `audit_trail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_trail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `entity_type` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `performed_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_trail`
--

LOCK TABLES `audit_trail` WRITE;
/*!40000 ALTER TABLE `audit_trail` DISABLE KEYS */;
INSERT INTO `audit_trail` VALUES (1,'INVOICE_CREATED','2026-07-29 05:30:27.718197','Invoice created & paid for payment 4',1,'Invoice',NULL,'SYSTEM'),(2,'RECEIPT_GENERATED','2026-07-29 05:30:27.752275','Receipt generated for payment 4',4,'Receipt',NULL,'SYSTEM'),(3,'CHECK_OUT_REMINDER_SENT','2026-07-29 12:53:15.060130','Check-out reminder sent for booking 4',4,'Booking',NULL,'SYSTEM'),(4,'CHECK_OUT_REMINDER_SENT','2026-07-29 12:53:15.063330','Check-out reminder sent for booking 5',5,'Booking',NULL,'SYSTEM'),(5,'CHECK_OUT_REMINDER_SENT','2026-07-29 12:53:15.078688','Check-out reminder sent for booking 6',6,'Booking',NULL,'SYSTEM'),(6,'INVOICE_CREATED','2026-07-29 13:49:38.337433','Invoice created & paid for payment 5',2,'Invoice',NULL,'SYSTEM'),(7,'RECEIPT_GENERATED','2026-07-29 13:49:38.365583','Receipt generated for payment 5',5,'Receipt',NULL,'SYSTEM'),(8,'COMMISSION_CREATED','2026-07-29 13:49:38.396894','Commission auto-created for payment 5',5,'Commission',NULL,'SYSTEM');
/*!40000 ALTER TABLE `audit_trail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_food_items`
--

DROP TABLE IF EXISTS `booking_food_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_food_items` (
  `booking_id` bigint NOT NULL,
  `food_item_id` bigint NOT NULL,
  KEY `FK4fcm9exypn9icqwcaneprsspj` (`food_item_id`),
  KEY `FKrvtvm9ilee4idnj9i9l1472d` (`booking_id`),
  CONSTRAINT `FK4fcm9exypn9icqwcaneprsspj` FOREIGN KEY (`food_item_id`) REFERENCES `food_items` (`id`),
  CONSTRAINT `FKrvtvm9ilee4idnj9i9l1472d` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_food_items`
--

LOCK TABLES `booking_food_items` WRITE;
/*!40000 ALTER TABLE `booking_food_items` DISABLE KEYS */;
INSERT INTO `booking_food_items` VALUES (6,1);
/*!40000 ALTER TABLE `booking_food_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_rooms`
--

DROP TABLE IF EXISTS `booking_rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `adults` int NOT NULL,
  `children` int NOT NULL,
  `number_of_rooms` int NOT NULL,
  `price` double DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK71qgxnmri4s08xrlny5wptrej` (`booking_id`),
  KEY `FKcjk0abrppkbsw5w03uq8tvgfc` (`room_id`),
  CONSTRAINT `FK71qgxnmri4s08xrlny5wptrej` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKcjk0abrppkbsw5w03uq8tvgfc` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_rooms`
--

LOCK TABLES `booking_rooms` WRITE;
/*!40000 ALTER TABLE `booking_rooms` DISABLE KEYS */;
INSERT INTO `booking_rooms` VALUES (1,3,0,1,14598,4,1),(2,3,0,1,14598,5,1),(3,3,0,1,14598,6,1),(4,3,0,1,14598,7,1),(5,1,0,1,14598,8,1);
/*!40000 ALTER TABLE `booking_rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `advance_amount` decimal(38,2) DEFAULT NULL,
  `booking_date` datetime(6) DEFAULT NULL,
  `cancellation_deadline` datetime(6) DEFAULT NULL,
  `cancellation_policy_text` varchar(255) DEFAULT NULL,
  `check_in_date` datetime(6) DEFAULT NULL,
  `check_out_date` datetime(6) DEFAULT NULL,
  `contract_person_name` varchar(255) DEFAULT NULL,
  `digital_key` varchar(255) DEFAULT NULL,
  `discount_amount` decimal(38,2) DEFAULT NULL,
  `discount_rate` decimal(38,2) DEFAULT NULL,
  `due_amount` decimal(38,2) DEFAULT NULL,
  `extra_charges` decimal(38,2) DEFAULT NULL,
  `food_cancellable_until` datetime(6) DEFAULT NULL,
  `food_cancelled` bit(1) DEFAULT NULL,
  `id_image_path` varchar(255) DEFAULT NULL,
  `net_amount` decimal(38,2) DEFAULT NULL,
  `number_of_rooms` int NOT NULL,
  `online_check_in` bit(1) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','CHECKED_IN','CHECKED_OUT','CONFIRMED','EXPIRED','NO_SHOW','PENDING') DEFAULT NULL,
  `tax_amount` decimal(38,2) DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `total_guests` int NOT NULL,
  `total_price` decimal(38,2) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbvfibgflhsb0g2hnjauiv5khs` (`customer_id`),
  KEY `FK7y09f5lun38jnooaw2hch0ke9` (`hotel_id`),
  KEY `FKrgoycol97o21kpjodw1qox4nc` (`room_id`),
  CONSTRAINT `FK7y09f5lun38jnooaw2hch0ke9` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKbvfibgflhsb0g2hnjauiv5khs` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKrgoycol97o21kpjodw1qox4nc` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (4,0.00,'2026-07-28 01:00:09.203000','2026-07-28 06:00:00.000000','Free cancellation up to 24 hours before check-in','2026-07-29 06:00:00.000000','2026-07-31 06:00:00.000000',NULL,'DK-4-1-121CF989',0.00,0.00,29196.00,0.00,NULL,_binary '\0','booking_4_b4020df9-330b-4a55-bda0-d88c230d9db7.png',33575.40,1,_binary '',NULL,'CHECKED_IN',4379.40,29196.00,3,29196.00,1,1,1),(5,0.00,'2026-07-28 01:48:04.648000','2026-07-28 06:00:00.000000','Free cancellation up to 24 hours before check-in','2026-07-29 06:00:00.000000','2026-07-31 06:00:00.000000',NULL,NULL,1386.81,5.00,27736.20,0.00,NULL,_binary '\0',NULL,30301.80,1,_binary '\0',NULL,'CONFIRMED',3952.41,27736.20,3,29196.00,1,1,1),(6,0.00,'2026-07-28 23:23:33.888000','2026-07-28 06:00:00.000000','Free cancellation up to 24 hours before check-in','2026-07-29 06:00:00.000000','2026-07-31 06:00:00.000000',NULL,NULL,4098.99,15.00,27326.60,0.00,NULL,_binary '\0',NULL,26711.75,1,_binary '\0',NULL,'PENDING',3484.14,27326.60,3,29196.00,1,1,1),(7,40634.60,'2026-07-29 05:29:52.345000','2026-07-31 06:00:00.000000','Free cancellation up to 24 hours before check-in','2026-08-01 06:00:00.000000','2026-08-04 06:00:00.000000',NULL,'DK-7-1-898A900B',0.00,0.00,0.00,0.00,NULL,_binary '\0',NULL,46729.79,1,_binary '',NULL,'CHECKED_IN',6095.19,40634.60,3,43794.00,1,1,1),(8,14428.20,'2026-07-29 13:49:06.100000','2026-07-28 06:00:00.000000','Free cancellation up to 24 hours before check-in','2026-07-29 06:00:00.000000','2026-07-30 06:00:00.000000',NULL,NULL,0.00,0.00,0.00,0.00,NULL,_binary '\0',NULL,16592.43,1,_binary '\0',NULL,'CONFIRMED',2164.23,14428.20,1,14598.00,1,1,1);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commissions`
--

DROP TABLE IF EXISTS `commissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_earnings` decimal(38,2) DEFAULT NULL,
  `commission_rate` decimal(38,2) DEFAULT NULL,
  `commission_status` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `hotel_owner_earnings` decimal(38,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `extra_service_id` bigint DEFAULT NULL,
  `payment_id` bigint NOT NULL,
  `payment_amount` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhfrf44p5dkjf5x4fvepysglok` (`payment_id`),
  UNIQUE KEY `UKpbph4tvv3vtgdyo4y44dhqh9h` (`extra_service_id`),
  KEY `FK1rm0049yawxsoiv34mvf2jk1e` (`booking_id`),
  CONSTRAINT `FK1rm0049yawxsoiv34mvf2jk1e` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKcdhu9i5iscv0d57mmp9owe70j` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`),
  CONSTRAINT `FKiq69xlp2flvva8e82vdppe981` FOREIGN KEY (`extra_service_id`) REFERENCES `extra_services` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
INSERT INTO `commissions` VALUES (1,721.41,5.00,'ACTIVE','2026-07-29 13:49:38.372510',13706.79,'2026-07-29 13:49:38.372510',8,NULL,5,14428.20);
/*!40000 ALTER TABLE `commissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupons`
--

DROP TABLE IF EXISTS `coupons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupons` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `discount_percent` double DEFAULT NULL,
  `valid_from` datetime(6) DEFAULT NULL,
  `valid_until` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  `usage_limit` int DEFAULT NULL,
  `used_count` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkcavuhfpctlrf43c3dibqqfyj` (`hotel_id`),
  CONSTRAINT `FKkcavuhfpctlrf43c3dibqqfyj` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupons`
--

LOCK TABLES `coupons` WRITE;
/*!40000 ALTER TABLE `coupons` DISABLE KEYS */;
INSERT INTO `coupons` VALUES (1,_binary '','summer20',0,10,'2026-07-28 00:41:00.000000','2026-08-29 00:41:00.000000',1,NULL,2);
/*!40000 ALTER TABLE `coupons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrfbvkrffamfql7cjmen8v976v` (`email`),
  UNIQUE KEY `UKeuat1oase6eqv195jvb71a93s` (`user_id`),
  CONSTRAINT `FKrh1g1a20omjmn6kurd35o3eit` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,NULL,'Emon',NULL,'emon@gmail.com',NULL,NULL,'+8801710535098',3);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deals`
--

DROP TABLE IF EXISTS `deals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deal_title` varchar(255) DEFAULT NULL,
  `deal_type` enum('FIXED_AMOUNT','PERCENTAGE','SEASONAL') DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `discount_percent` double DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  `room_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8uqosunxg994ddnpbrwi42paa` (`hotel_id`),
  KEY `FKrtljkklwxk06jnjfpr1jtu1gl` (`room_id`),
  CONSTRAINT `FK8uqosunxg994ddnpbrwi42paa` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKrtljkklwxk06jnjfpr1jtu1gl` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deals`
--

LOCK TABLES `deals` WRITE;
/*!40000 ALTER TABLE `deals` DISABLE KEYS */;
INSERT INTO `deals` VALUES (1,'2026-07-28 00:40:12.778568','rainy offers','PERCENTAGE','sefgrstyt',0,5,'2026-08-28 18:40:00.000000',_binary '','2026-07-27 18:39:00.000000','2026-07-28 00:40:12.778568',1,NULL);
/*!40000 ALTER TABLE `deals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `extra_services`
--

DROP TABLE IF EXISTS `extra_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `extra_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancellable_until` datetime(6) DEFAULT NULL,
  `cancelled` bit(1) DEFAULT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `service_status` enum('CANCELLED','COMPLETED','PENDING') DEFAULT NULL,
  `service_type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkkk6rlaa428v6qb4xqj51yy3o` (`booking_id`),
  CONSTRAINT `FKkkk6rlaa428v6qb4xqj51yy3o` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extra_services`
--

LOCK TABLES `extra_services` WRITE;
/*!40000 ALTER TABLE `extra_services` DISABLE KEYS */;
INSERT INTO `extra_services` VALUES (4,NULL,_binary '\0',NULL,'2026-07-28 23:23:33.967919',NULL,1220,'PENDING','Spa','2026-07-28 23:23:33.967919',NULL,6),(5,NULL,_binary '\0',NULL,'2026-07-28 23:23:33.973521',NULL,1290,'PENDING','Laundry','2026-07-28 23:23:33.973521',NULL,6),(6,NULL,_binary '\0',NULL,'2026-07-29 05:29:52.525893',NULL,1220,'PENDING','Spa','2026-07-29 05:29:52.525893',NULL,7),(7,NULL,_binary '\0',NULL,'2026-07-29 13:49:06.247623',NULL,1290,'PENDING','Laundry','2026-07-29 13:49:06.247623',NULL,8);
/*!40000 ALTER TABLE `extra_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facilities`
--

DROP TABLE IF EXISTS `facilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facilities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `facility_name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaaoto2svry961i8t15i2ufc9m` (`hotel_id`),
  CONSTRAINT `FKaaoto2svry961i8t15i2ufc9m` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facilities`
--

LOCK TABLES `facilities` WRITE;
/*!40000 ALTER TABLE `facilities` DISABLE KEYS */;
INSERT INTO `facilities` VALUES (1,'2026-07-28 00:38:16.703798','kjuiytr',' Restaurant Tea/coffee maker in all rooms','2026-07-28 00:38:16.703798',1);
/*!40000 ALTER TABLE `facilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_items`
--

DROP TABLE IF EXISTS `food_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancellable_until` datetime(6) DEFAULT NULL,
  `cancelled` bit(1) DEFAULT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `food_price` double DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `ordered_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbf8eefptx5n7xh6gcgvco0x8o` (`hotel_id`),
  CONSTRAINT `FKbf8eefptx5n7xh6gcgvco0x8o` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_items`
--

LOCK TABLES `food_items` WRITE;
/*!40000 ALTER TABLE `food_items` DISABLE KEYS */;
INSERT INTO `food_items` VALUES (1,NULL,_binary '\0',NULL,'Launch','2026-07-28 00:39:03.786279','ersytjyu',789,'Rice_Item_61c33bec-11ab-4908-afab-397314e4caaf.jpg','Rice Item','2026-07-28 00:39:03.786279','2026-07-28 00:39:03.786279',1);
/*!40000 ALTER TABLE `food_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `galleries`
--

DROP TABLE IF EXISTS `galleries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `galleries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `caption` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlovtoweag6co6rgsctbbt954j` (`hotel_id`),
  CONSTRAINT `FKlovtoweag6co6rgsctbbt954j` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `galleries`
--

LOCK TABLES `galleries` WRITE;
/*!40000 ALTER TABLE `galleries` DISABLE KEYS */;
INSERT INTO `galleries` VALUES (1,'picture in room','ROOM','2026-07-28 00:38:36.803882','gallery/room/Hotel_Sea_Queen_13053b34-35ff-432c-a409-2658445c1a6f.jpg','2026-07-28 00:38:36.803882',1);
/*!40000 ALTER TABLE `galleries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_details`
--

DROP TABLE IF EXISTS `hotel_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancellation_deposit_refundable` varchar(50) DEFAULT NULL,
  `cancellation_policy` varchar(1000) DEFAULT NULL,
  `check_in_time` varchar(255) DEFAULT NULL,
  `check_out_time` varchar(255) DEFAULT NULL,
  `child_policy` varchar(1000) DEFAULT NULL,
  `contact_email` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deposit_percentage` double DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `hotel_policy` varchar(1000) DEFAULT NULL,
  `languages` varchar(1000) DEFAULT NULL,
  `nearby_attractions` varchar(1000) DEFAULT NULL,
  `owner_info` varchar(1000) NOT NULL,
  `payment_option` varchar(50) DEFAULT NULL,
  `pet_policy` varchar(1000) DEFAULT NULL,
  `pre_auth_required` bit(1) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
  `smoking_policy` varchar(1000) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmnnhmobh4lueykv2gw5avc648` (`hotel_id`),
  CONSTRAINT `FKniwlkipppcwevt5if6lmervw9` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_details`
--

LOCK TABLES `hotel_details` WRITE;
/*!40000 ALTER TABLE `hotel_details` DISABLE KEYS */;
INSERT INTO `hotel_details` VALUES (1,'CONDITIONAL_REFUND','tryuik','12pm','11 am','allow (condition)','jui.isratjahan1@gmail.com','0987654w345','2026-07-28 00:35:54.034948',20,'grthtrdhteththt','drdfghjk','English,Bangla','sea beach','gtehh','ADVANCE','',_binary '\0',12900,'not allow','2026-07-28 00:35:54.034948',1);
/*!40000 ALTER TABLE `hotel_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_extra_services`
--

DROP TABLE IF EXISTS `hotel_extra_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_extra_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `service_name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKemc4oh5ua979sbapckr7i2eq4` (`hotel_id`),
  CONSTRAINT `FKemc4oh5ua979sbapckr7i2eq4` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_extra_services`
--

LOCK TABLES `hotel_extra_services` WRITE;
/*!40000 ALTER TABLE `hotel_extra_services` DISABLE KEYS */;
INSERT INTO `hotel_extra_services` VALUES (1,'2026-07-28 00:40:32.251046','ewertgse',_binary '',1220,'Spa','2026-07-28 00:40:32.251046',1),(2,'2026-07-28 00:40:46.657535','ewfgrsthr',_binary '',1290,'Laundry','2026-07-28 00:40:46.657535',1);
/*!40000 ALTER TABLE `hotel_extra_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_owners`
--

DROP TABLE IF EXISTS `hotel_owners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_owners` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_of_birth` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3l971t4ymni5b60u0cs4nye1x` (`email`),
  UNIQUE KEY `UK82o9jqgteum1o079ytd8h8mlh` (`user_id`),
  CONSTRAINT `FKrhvnm559g5eel2hijw1kmwr8i` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_owners`
--

LOCK TABLES `hotel_owners` WRITE;
/*!40000 ALTER TABLE `hotel_owners` DISABLE KEYS */;
INSERT INTO `hotel_owners` VALUES (1,'Dhaka','2026-07-28 00:31:54.922946','2024-10-01 06:00:00.000000','jui.isratjahan1@gmail.com','Female','Israt_Jahan_Jui_f064b2b8-ba98-4de9-978c-3582d8e207d7.jpg','Israt Jahan Jui','+8801710535890','2026-07-29 12:28:50.354834',2);
/*!40000 ALTER TABLE `hotel_owners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotels`
--

DROP TABLE IF EXISTS `hotels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `food_available` bit(1) DEFAULT NULL,
  `food_service_hours` varchar(255) DEFAULT NULL,
  `hotel_name` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
  `rating` varchar(255) DEFAULT NULL,
  `rejection_reason` text,
  `status` enum('ACTIVE','APPROVED','INACTIVE','PENDING_APPROVAL','REJECTED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `location_id` bigint DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL,
  `advance_percentage` double DEFAULT NULL,
  `payment_type` enum('CASH_ON_STAY','FULL_ADVANCE','PARTIAL_ADVANCE') DEFAULT NULL,
  `property_type` enum('APARTMENT','BOUTIQUE','GUEST_HOUSE','HOSTEL','HOTEL','LODGGE','RESORT','VILLA') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqs8u4n6x2f5anae9lllt3857p` (`location_id`),
  KEY `FK4a6yejtias6qjndncdm11id1g` (`owner_id`),
  CONSTRAINT `FK4a6yejtias6qjndncdm11id1g` FOREIGN KEY (`owner_id`) REFERENCES `hotel_owners` (`id`),
  CONSTRAINT `FKqs8u4n6x2f5anae9lllt3857p` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotels`
--

LOCK TABLES `hotels` WRITE;
/*!40000 ALTER TABLE `hotels` DISABLE KEYS */;
INSERT INTO `hotels` VALUES (1,'Dhaka','2026-07-28 00:34:35.113020','htyfguhkjl;',NULL,NULL,'Panpasific','Panpasific_75f3adeb-9539-4339-9933-4012a54e35d7.jpg',NULL,'5',NULL,'APPROVED','2026-07-28 00:41:49.647379',1,1,NULL,NULL,NULL),(2,'Plot 6,7 & 8, Hotel Motel Zone, Sea Beach 4701 Cox\'s Bazar, Chittagong Division, Bangladesh, 4701 Cox\'s Bazar, Bangladesh','2026-07-29 01:31:12.970505','fgsdhsgdsdh',NULL,NULL,'Seagull Hotel Ltd','Seagull_Hotel_Ltd_08fef7e0-0ce2-4709-b318-6cdd593996fd.jpg',NULL,'',NULL,'APPROVED','2026-07-29 01:49:29.989594',16,1,100,'FULL_ADVANCE',NULL),(3,'Rajshahi','2026-07-29 01:37:56.318886','efer',NULL,NULL,'Kazi Castel Rajshahi','Kazi_Castel_Rajshahi_b75630ca-1efe-4f94-a5da-8e80c7051d15.webp',NULL,'4',NULL,'APPROVED','2026-07-29 01:49:31.255598',31,1,30,'PARTIAL_ADVANCE',NULL),(4,'Khulna','2026-07-29 01:40:48.997745','dsfesdfr',NULL,NULL,'Royel Hotel Khulna City Center','Royel_Hotel_Khulna_City_Center_607514d0-42e1-45e5-84b7-9c8665f8773d.webp',NULL,'3',NULL,'APPROVED','2026-07-29 01:49:32.022151',40,1,100,'CASH_ON_STAY',NULL),(5,'Barisal','2026-07-29 01:42:43.106258','sdsfsd',NULL,NULL,' Hotel Grand Park BarishalOpens in new window','Hotel_Grand_Park_BarishalOpens_in_new_window_d07154bb-4b5b-4ee8-9788-32697e8eef50.webp',NULL,'',NULL,'APPROVED','2026-07-29 01:49:32.722669',51,1,20,'PARTIAL_ADVANCE',NULL),(6,'Sylhet','2026-07-29 01:44:22.619981','sdfefgergrt',NULL,NULL,'Rose View Hotel Sylhet','Rose_View_Hotel_Sylhet_4e830653-5aca-4823-8a69-4a27d94b0d98.webp',NULL,'5',NULL,'APPROVED','2026-07-29 01:49:33.434003',27,1,100,'FULL_ADVANCE',NULL),(7,'Rangpur','2026-07-29 01:46:28.826861','wdwef',NULL,NULL,'Grand Palace Hotel & Resorts RangpurOpens in new window','Grand_Palace_Hotel_&_Resorts_RangpurOpens_in_new_window_f6d9ec46-0d33-462e-8665-b2d4a42d6a5c.webp',NULL,'4',NULL,'APPROVED','2026-07-29 01:49:34.755607',57,1,10,'PARTIAL_ADVANCE',NULL),(8,'Maymensigh','2026-07-29 01:49:04.150291','ergrt',NULL,NULL,'Eastern Heritage ResortOpens in new window','Eastern_Heritage_ResortOpens_in_new_window_8473ed1b-7935-432e-bb6d-fe8f74672afb.jpg',NULL,'4',NULL,'APPROVED','2026-07-29 01:49:36.170872',12,1,100,'CASH_ON_STAY',NULL);
/*!40000 ALTER TABLE `hotels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `discount_amount` decimal(38,2) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  `issued_at` datetime(6) DEFAULT NULL,
  `net_amount` decimal(38,2) DEFAULT NULL,
  `status` enum('CANCELLED','ISSUED','PAID') DEFAULT NULL,
  `tax_amount` decimal(38,2) DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `commission_id` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `payment_id` bigint NOT NULL,
  `invoice_type` enum('FINAL','PROFORMA') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbsd97jjclowbotctav1vt0s0k` (`commission_id`),
  KEY `FKb9bhb7xre5v64qvjeholh3qj0` (`booking_id`),
  KEY `FKq2w4hmh6l9othnp6cepp0cfe2` (`customer_id`),
  KEY `FKq6fs19k0gqw3rg0mb87h60h6p` (`payment_id`),
  CONSTRAINT `FK1nm4wl2dwd6q64xl0qcb458dv` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`id`),
  CONSTRAINT `FKb9bhb7xre5v64qvjeholh3qj0` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKq2w4hmh6l9othnp6cepp0cfe2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKq6fs19k0gqw3rg0mb87h60h6p` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (1,'2026-07-29 05:30:27.711667',NULL,0.00,'INV-B6F5185F','2026-07-29 05:30:27.711667',46729.79,'PAID',6095.19,40634.60,'2026-07-29 05:30:27.711667',NULL,7,NULL,1,4,'FINAL'),(2,'2026-07-29 13:49:38.328338',NULL,0.00,'INV-E8477C9C','2026-07-29 13:49:38.328338',16592.43,'PAID',2164.23,14428.20,'2026-07-29 13:49:38.328338',NULL,8,NULL,1,5,'FINAL');
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `division` varchar(255) DEFAULT NULL,
  `location_image` varchar(255) DEFAULT NULL,
  `location_name` varchar(255) NOT NULL,
  `upazila` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdkw8mrr8kuqilpr7ti75dx1b9` (`location_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3511 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locations`
--

LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
INSERT INTO `locations` VALUES (1,'Dhaka',NULL,'Dhaka','Dhaka',NULL,'Dhaka','Dhaka Sadar',NULL),(2,'Gazipur',NULL,'Gazipur','Dhaka',NULL,'Gazipur','Gazipur Sadar',NULL),(3,'Narayanganj',NULL,'Narayanganj','Dhaka',NULL,'Narayanganj','Narayanganj Sadar',NULL),(4,'Tangail',NULL,'Tangail','Dhaka',NULL,'Tangail','Tangail Sadar',NULL),(5,'Manikganj',NULL,'Manikganj','Dhaka',NULL,'Manikganj','Manikganj Sadar',NULL),(6,'Madaripur',NULL,'Madaripur','Dhaka',NULL,'Madaripur','Madaripur Sadar',NULL),(7,'Faridpur',NULL,'Faridpur','Dhaka',NULL,'Faridpur','Faridpur Sadar',NULL),(8,'Rajbari',NULL,'Rajbari','Dhaka',NULL,'Rajbari','Rajbari Sadar',NULL),(9,'Shariatpur',NULL,'Shariatpur','Dhaka',NULL,'Shariatpur','Shariatpur Sadar',NULL),(10,'Kishoreganj',NULL,'Kishoreganj','Dhaka',NULL,'Kishoreganj','Kishoreganj Sadar',NULL),(11,'Narsingdi',NULL,'Narsingdi','Dhaka',NULL,'Narsingdi','Narsingdi Sadar',NULL),(12,'Mymensingh',NULL,'Mymensingh','Mymensingh',NULL,'Mymensingh','Mymensingh Sadar',NULL),(13,'Netrakona',NULL,'Netrakona','Mymensingh',NULL,'Netrakona','Netrakona Sadar',NULL),(14,'Sherpur',NULL,'Sherpur','Mymensingh',NULL,'Sherpur','Sherpur Sadar',NULL),(15,'Jamalpur',NULL,'Jamalpur','Mymensingh',NULL,'Jamalpur','Jamalpur Sadar',NULL),(16,'Chittagong',NULL,'Chittagong','Chittagong',NULL,'Chittagong','Chittagong Sadar',NULL),(17,'Comilla',NULL,'Comilla','Chittagong',NULL,'Comilla','Comilla Sadar',NULL),(18,'Brahmanbaria',NULL,'Brahmanbaria','Chittagong',NULL,'Brahmanbaria','Brahmanbaria Sadar',NULL),(19,'Chandpur',NULL,'Chandpur','Chittagong',NULL,'Chandpur','Chandpur Sadar',NULL),(20,'Lakshmipur',NULL,'Lakshmipur','Chittagong',NULL,'Lakshmipur','Lakshmipur Sadar',NULL),(21,'Noakhali',NULL,'Noakhali','Chittagong',NULL,'Noakhali','Noakhali Sadar',NULL),(22,'Feni',NULL,'Feni','Chittagong',NULL,'Feni','Feni Sadar',NULL),(23,'Cox\'s Bazar',NULL,'Cox\'s Bazar','Chittagong',NULL,'Cox\'s Bazar','Cox\'s Bazar Sadar',NULL),(24,'Rangamati',NULL,'Rangamati','Chittagong',NULL,'Rangamati','Rangamati Sadar',NULL),(25,'Bandarban',NULL,'Bandarban','Chittagong',NULL,'Bandarban','Bandarban Sadar',NULL),(26,'Khagrachari',NULL,'Khagrachari','Chittagong',NULL,'Khagrachari','Khagrachari Sadar',NULL),(27,'Sylhet',NULL,'Sylhet','Sylhet',NULL,'Sylhet','Sylhet Sadar',NULL),(28,'Moulvibazar',NULL,'Moulvibazar','Sylhet',NULL,'Moulvibazar','Moulvibazar Sadar',NULL),(29,'Habiganj',NULL,'Habiganj','Sylhet',NULL,'Habiganj','Habiganj Sadar',NULL),(30,'Sunamganj',NULL,'Sunamganj','Sylhet',NULL,'Sunamganj','Sunamganj Sadar',NULL),(31,'Rajshahi',NULL,'Rajshahi','Rajshahi',NULL,'Rajshahi','Rajshahi Sadar',NULL),(32,'Bogura',NULL,'Bogura','Rajshahi',NULL,'Bogura','Bogura Sadar',NULL),(33,'Joypurhat',NULL,'Joypurhat','Rajshahi',NULL,'Joypurhat','Joypurhat Sadar',NULL),(34,'Naogaon',NULL,'Naogaon','Rajshahi',NULL,'Naogaon','Naogaon Sadar',NULL),(35,'Natore',NULL,'Natore','Rajshahi',NULL,'Natore','Natore Sadar',NULL),(36,'Chapainawabganj',NULL,'Chapainawabganj','Rajshahi',NULL,'Chapainawabganj','Chapainawabganj Sadar',NULL),(37,'Sirajganj',NULL,'Sirajganj','Rajshahi',NULL,'Sirajganj','Sirajganj Sadar',NULL),(38,'Pabna',NULL,'Pabna','Rajshahi',NULL,'Pabna','Pabna Sadar',NULL),(39,'Ishwardi',NULL,'Ishwardi','Rajshahi',NULL,'Ishwardi','Ishwardi Sadar',NULL),(40,'Khulna',NULL,'Khulna','Khulna',NULL,'Khulna','Khulna Sadar',NULL),(41,'Jashore',NULL,'Jashore','Khulna',NULL,'Jessore','Jashore Sadar',NULL),(42,'Satkhira',NULL,'Satkhira','Khulna',NULL,'Satkhira','Satkhira Sadar',NULL),(43,'Meherpur',NULL,'Meherpur','Khulna',NULL,'Meherpur','Meherpur Sadar',NULL),(44,'Narail',NULL,'Narail','Khulna',NULL,'Narail','Narail Sadar',NULL),(45,'Magura',NULL,'Magura','Khulna',NULL,'Magura','Magura Sadar',NULL),(46,'Jhenaidah',NULL,'Jhenaidah','Khulna',NULL,'Jhenaidah','Jhenaidah Sadar',NULL),(47,'Kushtia',NULL,'Kushtia','Khulna',NULL,'Kushtia','Kushtia Sadar',NULL),(48,'Chuadanga',NULL,'Chuadanga','Khulna',NULL,'Chuadanga','Chuadanga Sadar',NULL),(49,'Bagerhat',NULL,'Bagerhat','Khulna',NULL,'Bagerhat','Bagerhat Sadar',NULL),(50,'Gopalganj',NULL,'Gopalganj','Khulna',NULL,'Gopalganj','Gopalganj Sadar',NULL),(51,'Barishal',NULL,'Barishal','Barishal',NULL,'Barishal','Barishal Sadar',NULL),(52,'Jhalakathi',NULL,'Jhalakathi','Barishal',NULL,'Jhalakathi','Jhalakathi Sadar',NULL),(53,'Patuakhali',NULL,'Patuakhali','Barishal',NULL,'Patuakhali','Patuakhali Sadar',NULL),(54,'Bhola',NULL,'Bhola','Barishal',NULL,'Bhola','Bhola Sadar',NULL),(55,'Pirojpur',NULL,'Pirojpur','Barishal',NULL,'Pirojpur','Pirojpur Sadar',NULL),(56,'Barguna',NULL,'Barguna','Barishal',NULL,'Barguna','Barguna Sadar',NULL),(57,'Rangpur',NULL,'Rangpur','Rangpur',NULL,'Rangpur','Rangpur Sadar',NULL),(58,'Dinajpur',NULL,'Dinajpur','Rangpur',NULL,'Dinajpur','Dinajpur Sadar',NULL),(59,'Thakurgaon',NULL,'Thakurgaon','Rangpur',NULL,'Thakurgaon','Thakurgaon Sadar',NULL),(60,'Panchagarh',NULL,'Panchagarh','Rangpur',NULL,'Panchagarh','Panchagarh Sadar',NULL),(61,'Nilphamari',NULL,'Nilphamari','Rangpur',NULL,'Nilphamari','Nilphamari Sadar',NULL),(62,'Lalmonirhat',NULL,'Lalmonirhat','Rangpur',NULL,'Lalmonirhat','Lalmonirhat Sadar',NULL),(63,'Kurigram',NULL,'Kurigram','Rangpur',NULL,'Kurigram','Kurigram Sadar',NULL),(64,'Gaibandha',NULL,'Gaibandha','Rangpur',NULL,'Gaibandha','Gaibandha Sadar',NULL),(65,'Gopalganj Dh',NULL,'Gopalganj Dh','Dhaka',NULL,'Gopalganj Dhaka','Gopalganj Sadar',NULL);
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_attempts`
--

DROP TABLE IF EXISTS `login_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_attempts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempt_count` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `last_attempt_at` datetime(6) DEFAULT NULL,
  `locked_until` datetime(6) DEFAULT NULL,
  `success` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_attempts`
--

LOCK TABLES `login_attempts` WRITE;
/*!40000 ALTER TABLE `login_attempts` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_attempts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel` enum('EMAIL','PUSH','SMS','SYSTEM','WEB') DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `read_status` bit(1) NOT NULL,
  `type` enum('BOOKING_CANCELLED','BOOKING_CONFIRMED','BOOKING_REMINDER','GENERAL','HOTEL_APPROVED','HOTEL_REJECTED','PAYMENT_FAILED','PAYMENT_REFUNDED','PAYMENT_SUCCESSFUL','PROMOTIONAL','REVIEW_RECEIVED','SUPPORT_REPLIED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `subject` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'WEB','2026-07-28 00:40:12.795250','New deal at Panpasific! rainy offers - 5.0% off. Valid from 2026-07-27T18:39 to 2026-08-28T18:40',_binary '\0','PROMOTIONAL','2026-07-28 00:40:12.795250',3,NULL),(2,'WEB','2026-07-28 00:41:24.236535','New coupon available at Panpasific! Use code summer20 for 10.0% off. Valid until 2026-08-29T00:41',_binary '\0','PROMOTIONAL','2026-07-28 00:41:24.236535',3,NULL),(9,'WEB','2026-07-28 20:20:45.963910','Your booking at Panpasific has been confirmed. Booking ID: #4',_binary '\0','BOOKING_CONFIRMED','2026-07-28 20:20:45.963910',3,NULL),(10,'WEB','2026-07-28 20:20:46.026056','Booking #4 has been confirmed for Panpasific',_binary '\0','BOOKING_CONFIRMED','2026-07-28 20:20:46.026056',2,NULL),(11,'WEB','2026-07-28 20:20:50.006536','Your booking at Panpasific has been confirmed. Booking ID: #5',_binary '\0','BOOKING_CONFIRMED','2026-07-28 20:20:50.006536',3,NULL),(12,'WEB','2026-07-28 20:20:50.017398','Booking #5 has been confirmed for Panpasific',_binary '\0','BOOKING_CONFIRMED','2026-07-28 20:20:50.017398',2,NULL),(13,'WEB','2026-07-28 20:40:30.819372','New review received for Panpasific from Emon. Rating: 5/5',_binary '\0','REVIEW_RECEIVED','2026-07-28 20:40:30.819372',2,NULL),(14,'WEB','2026-07-28 20:41:23.486828','The owner of Panpasific has replied to your review.',_binary '\0','SUPPORT_REPLIED','2026-07-28 20:41:23.486828',3,NULL),(15,'WEB','2026-07-29 12:53:15.044208','Your stay at Panpasific ends on 2026-07-31 06:00:00.0. Please prepare for check-out.',_binary '\0','BOOKING_REMINDER','2026-07-29 12:53:15.044208',1,NULL),(16,'WEB','2026-07-29 12:53:15.063330','Your stay at Panpasific ends on 2026-07-31 06:00:00.0. Please prepare for check-out.',_binary '\0','BOOKING_REMINDER','2026-07-29 12:53:15.063330',1,NULL),(17,'WEB','2026-07-29 12:53:15.063330','Your stay at Panpasific ends on 2026-07-31 06:00:00.0. Please prepare for check-out.',_binary '\0','BOOKING_REMINDER','2026-07-29 12:53:15.063330',1,NULL),(18,'WEB','2026-07-29 12:54:10.862583','Online check-in successful at Panpasific. Your digital key is: DK-4-1-121CF989',_binary '\0','BOOKING_REMINDER','2026-07-29 12:54:10.862583',3,NULL),(19,'WEB','2026-07-29 12:54:10.869337','Guest null has completed online check-in at Panpasific',_binary '\0','BOOKING_REMINDER','2026-07-29 12:54:10.869337',2,NULL),(20,'WEB','2026-07-29 12:54:29.255618','Online check-in successful at Panpasific. Your digital key is: DK-7-1-898A900B',_binary '\0','BOOKING_REMINDER','2026-07-29 12:54:29.255618',3,NULL),(21,'WEB','2026-07-29 12:54:29.271023','Guest null has completed online check-in at Panpasific',_binary '\0','BOOKING_REMINDER','2026-07-29 12:54:29.271023',2,NULL);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `bank_transaction_id` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `status` enum('FAILED','PAID','PENDING','REFUNDED','UNPAID') DEFAULT NULL,
  `transaction_date` datetime(6) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `validation_id` varchar(255) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `extra_service_id` bigint DEFAULT NULL,
  `version` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfm8xkje8k108j34karfe7ybv1` (`extra_service_id`),
  KEY `FKc52o2b1jkxttngufqp3t7jr3h` (`booking_id`),
  CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKsrw79y2bpk1idgs5sqgivow7s` FOREIGN KEY (`extra_service_id`) REFERENCES `extra_services` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,29196.00,NULL,'2026-07-28 01:00:15.490428',1,'SSLCOMMERZ','PENDING','2026-07-28 01:00:15.489420','TXN-342622F7-A0A','2026-07-28 01:00:15.490428',NULL,4,NULL,0),(3,27326.60,NULL,'2026-07-28 23:30:12.461775',1,'SSLCOMMERZ','PENDING','2026-07-29 02:52:27.910616','TXN-3A18A321-D0D','2026-07-29 02:52:28.359010',NULL,6,NULL,4),(4,40634.60,'','2026-07-29 05:29:56.689488',1,'SSLCOMMERZ','PAID','2026-07-29 05:30:27.699627','TXN-FCA5B8DB-884','2026-07-29 05:30:27.756108','26072953022Ef5RC6OmyXUU0x8',7,NULL,1),(5,14428.20,'','2026-07-29 13:49:11.548120',1,'SSLCOMMERZ','PAID','2026-07-29 13:49:38.320611','TXN-6D0A7C4E-96B','2026-07-29 13:49:38.372510','260729134933EaYizfR77ggULp1',8,NULL,1);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipts`
--

DROP TABLE IF EXISTS `receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `issued_at` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `receipt_number` varchar(255) NOT NULL,
  `tax_amount` decimal(38,2) DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `invoice_id` bigint DEFAULT NULL,
  `payment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsehtr7fmqdjw4f4n3xys7x19l` (`receipt_number`),
  KEY `FKntnfas8eiqfewh0oy62dryvsm` (`booking_id`),
  KEY `FKaqpv1sipadmdcrmp03v38ec5l` (`customer_id`),
  KEY `FK3hmid8b40s5yd0jo2s36684ql` (`invoice_id`),
  KEY `FKecr3sh9ed2cda7v8n3gyy7530` (`payment_id`),
  CONSTRAINT `FK3hmid8b40s5yd0jo2s36684ql` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`),
  CONSTRAINT `FKaqpv1sipadmdcrmp03v38ec5l` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKecr3sh9ed2cda7v8n3gyy7530` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`),
  CONSTRAINT `FKntnfas8eiqfewh0oy62dryvsm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipts`
--

LOCK TABLES `receipts` WRITE;
/*!40000 ALTER TABLE `receipts` DISABLE KEYS */;
INSERT INTO `receipts` VALUES (1,40634.60,'2026-07-29 05:30:27.739719','2026-07-29 05:30:27.740714','SSLCOMMERZ','RCP-20260729053027-8A85',6095.19,46729.79,'TXN-FCA5B8DB-884',7,1,1,4),(2,14428.20,'2026-07-29 13:49:38.355988','2026-07-29 13:49:38.355988','SSLCOMMERZ','RCP-20260729134938-90F3',2164.23,16592.43,'TXN-6D0A7C4E-96B',8,1,2,5);
/*!40000 ALTER TABLE `receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date_range_end` date DEFAULT NULL,
  `date_range_start` date DEFAULT NULL,
  `generated_at` datetime(6) DEFAULT NULL,
  `income` double DEFAULT NULL,
  `occupancy_rate` double DEFAULT NULL,
  `total_bookings` int NOT NULL,
  `total_rooms` int NOT NULL,
  `type` enum('DAILY','MONTHLY','WEEKLY','YEARLY') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKawbu2vda5qljn76pbvirw1wuv` (`hotel_id`),
  CONSTRAINT `FKawbu2vda5qljn76pbvirw1wuv` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (1,'2026-07-29','2026-07-29','2026-07-29 13:51:36.080976',55062.8,66.7,2,6,'DAILY','2026-07-29 13:51:36.080976',1);
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `edit_count` int NOT NULL,
  `edited_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `status` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `owner_reply` varchar(255) DEFAULT NULL,
  `reply_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK28an517hrxtt2bsg93uefugrm` (`booking_id`),
  KEY `FK4sm0k8kw740iyuex3vwwv1etu` (`customer_id`),
  KEY `FKb9igk5exfb4knqklcvka6cdhx` (`hotel_id`),
  CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FK4sm0k8kw740iyuex3vwwv1etu` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKb9igk5exfb4knqklcvka6cdhx` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'Good','2026-07-28 20:40:30.755340',1,'2026-07-28 20:40:45.919404',5,'APPROVED','2026-07-28 20:41:23.501326',5,1,1,'thanks','2026-07-28 20:41:23.484818');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `adults` int NOT NULL,
  `amenities` varchar(255) DEFAULT NULL,
  `available_rooms` int NOT NULL,
  `booked_rooms` int NOT NULL,
  `children` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_available` bit(1) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `total_rooms` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp5lufxy0ghq53ugm93hdc941k` (`hotel_id`),
  CONSTRAINT `FKp5lufxy0ghq53ugm93hdc941k` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,2,'Bathroom: Eco-friendly or branded toiletries, plush bathrobes, slippers, and hairdryers.Connectivity & Tech: High-speed Wi-Fi, smart TVs with streaming apps, and bedside charging ports',1,5,1,'2026-07-28 00:37:01.307883','ghjk','Dulex_baad8177-056d-43c4-800d-b17f69e0e802.jpg',_binary '',14598,'Dulex',6,'2026-07-29 13:49:06.264171',1);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `support_replies`
--

DROP TABLE IF EXISTS `support_replies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_replies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_internal` bit(1) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `replier_id` bigint NOT NULL,
  `ticket_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKja1oup7lvbxx4s4g054ci3jod` (`replier_id`),
  KEY `FKj7bdrts32toqri8g3kpvp247y` (`ticket_id`),
  CONSTRAINT `FKj7bdrts32toqri8g3kpvp247y` FOREIGN KEY (`ticket_id`) REFERENCES `support_tickets` (`id`),
  CONSTRAINT `FKja1oup7lvbxx4s4g054ci3jod` FOREIGN KEY (`replier_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `support_replies`
--

LOCK TABLES `support_replies` WRITE;
/*!40000 ALTER TABLE `support_replies` DISABLE KEYS */;
/*!40000 ALTER TABLE `support_replies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `support_tickets`
--

DROP TABLE IF EXISTS `support_tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `escalated` bit(1) DEFAULT NULL,
  `first_response_at` datetime(6) DEFAULT NULL,
  `priority` enum('HIGH','LOW','MEDIUM','URGENT') DEFAULT NULL,
  `resolved_at` datetime(6) DEFAULT NULL,
  `status` enum('CLOSED','ESCALATED','IN_PROGRESS','PENDING','RESOLVED') DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `agent_id` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `hotel_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5n9d68hb5bo380qroq4894qcu` (`agent_id`),
  KEY `FKbj61s5pm6gwms5405fcdvgm1t` (`customer_id`),
  KEY `FK8qtx39yh1inu0fitnuum86kc5` (`hotel_id`),
  CONSTRAINT `FK5n9d68hb5bo380qroq4894qcu` FOREIGN KEY (`agent_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK8qtx39yh1inu0fitnuum86kc5` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKbj61s5pm6gwms5405fcdvgm1t` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `support_tickets`
--

LOCK TABLES `support_tickets` WRITE;
/*!40000 ALTER TABLE `support_tickets` DISABLE KEYS */;
/*!40000 ALTER TABLE `support_tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','HOTEL_OWNER') DEFAULT NULL,
  `verification_token` varchar(255) DEFAULT NULL,
  `password_changed_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKdu5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','admin@gmail.com',NULL,'Admin','$2a$10$E7DJkhCmirGwbC8TIkpb7etHWAcw0gO5wbTvA2yav1bdAMFt4kmTK','01700000000','ADMIN',NULL,NULL),(2,_binary '','jui.isratjahan1@gmail.com',NULL,'Israt Jahan Jui','$2a$10$9Nd0jqTRTEpVeC2zJ0t2yOF05JoRkLor2w/xO7kL2V57niUE3mbAm','+8801710535890','HOTEL_OWNER',NULL,NULL),(3,_binary '','emon@gmail.com',NULL,'Emon','$2a$10$3lI9aX.K9PaWMDjEsMgMW.ku4oL3lSxKztevVZ3Elb3NJz67w8x16','+8801710535098','CUSTOMER',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet_transactions`
--

DROP TABLE IF EXISTS `wallet_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `reference_id` bigint DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `wallet_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8seu7b87ifqi09ghhssusmb0x` (`wallet_id`),
  CONSTRAINT `FK8seu7b87ifqi09ghhssusmb0x` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet_transactions`
--

LOCK TABLES `wallet_transactions` WRITE;
/*!40000 ALTER TABLE `wallet_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallet_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallets`
--

DROP TABLE IF EXISTS `wallets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(38,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `total_earned` decimal(38,2) DEFAULT NULL,
  `total_withdrawn` decimal(38,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `version` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsswfdl9fq40xlkove1y5kc7kv` (`user_id`),
  CONSTRAINT `FKc1foyisidw7wqqrkamafuwn4e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallets`
--

LOCK TABLES `wallets` WRITE;
/*!40000 ALTER TABLE `wallets` DISABLE KEYS */;
/*!40000 ALTER TABLE `wallets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlists`
--

DROP TABLE IF EXISTS `wishlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK60mulb9dr06pbuur46ywfp8fk` (`customer_id`),
  KEY `FKteloqgusn16evq07nswqd92or` (`hotel_id`),
  KEY `FK330pyw2el06fn5g28ypyljt16` (`user_id`),
  CONSTRAINT `FK330pyw2el06fn5g28ypyljt16` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK60mulb9dr06pbuur46ywfp8fk` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKteloqgusn16evq07nswqd92or` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlists`
--

LOCK TABLES `wishlists` WRITE;
/*!40000 ALTER TABLE `wishlists` DISABLE KEYS */;
INSERT INTO `wishlists` VALUES (1,'2026-07-28 00:42:10.480379',_binary '',NULL,'2026-07-28 00:42:10.480379',1,1,3);
/*!40000 ALTER TABLE `wishlists` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-29 14:55:37
