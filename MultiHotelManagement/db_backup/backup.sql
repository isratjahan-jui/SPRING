-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: multihotel
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
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
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_rooms`
--

LOCK TABLES `booking_rooms` WRITE;
/*!40000 ALTER TABLE `booking_rooms` DISABLE KEYS */;
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
  `advance_amount` double NOT NULL,
  `booking_date` datetime(6) DEFAULT NULL,
  `check_in_date` datetime(6) DEFAULT NULL,
  `check_out_date` datetime(6) DEFAULT NULL,
  `contract_person_name` varchar(255) DEFAULT NULL,
  `discount_rate` double NOT NULL,
  `due_amount` double NOT NULL,
  `number_of_rooms` int NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `total_amount` double NOT NULL,
  `total_guests` int NOT NULL,
  `total_price` double DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `status` enum('CANCELLED','CHECKED_IN','CHECKED_OUT','CONFIRMED','PENDING') DEFAULT NULL,
  `food_cancellable_until` datetime(6) DEFAULT NULL,
  `food_cancelled` bit(1) DEFAULT NULL,
  `digital_key` varchar(255) DEFAULT NULL,
  `id_image_path` varchar(255) DEFAULT NULL,
  `online_check_in` bit(1) NOT NULL,
  `cancellation_deadline` datetime(6) DEFAULT NULL,
  `cancellation_policy_text` varchar(255) DEFAULT NULL,
  `extra_charges` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbvfibgflhsb0g2hnjauiv5khs` (`customer_id`),
  KEY `FK7y09f5lun38jnooaw2hch0ke9` (`hotel_id`),
  KEY `FKrgoycol97o21kpjodw1qox4nc` (`room_id`),
  CONSTRAINT `FK7y09f5lun38jnooaw2hch0ke9` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKbvfibgflhsb0g2hnjauiv5khs` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKrgoycol97o21kpjodw1qox4nc` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,0,'2026-07-11 22:27:38.167000','2026-07-12 06:00:00.000000','2026-07-13 06:00:00.000000',NULL,0,22000,1,NULL,22000,3,22000,1,1,1,'CHECKED_OUT',NULL,_binary '\0',NULL,NULL,_binary '\0',NULL,NULL,0),(2,0,'2026-07-11 22:32:57.721000','2026-07-11 06:00:00.000000','2026-07-12 06:00:00.000000',NULL,0,22000,1,NULL,22000,3,22000,1,1,1,'CHECKED_OUT',NULL,_binary '\0',NULL,NULL,_binary '\0',NULL,NULL,0),(3,0,'2026-07-11 22:36:28.179000','2026-07-16 06:00:00.000000','2026-07-17 06:00:00.000000',NULL,0,44000,2,NULL,44000,3,44000,1,1,1,'CANCELLED',NULL,_binary '\0',NULL,NULL,_binary '\0',NULL,NULL,0),(4,0,'2026-07-11 23:29:10.292000','2026-07-15 06:00:00.000000','2026-07-17 06:00:00.000000',NULL,0,22000,1,NULL,22000,3,22000,1,1,1,'CANCELLED',NULL,_binary '\0',NULL,NULL,_binary '\0',NULL,NULL,0),(5,0,'2026-07-11 23:54:47.444000','2026-07-15 06:00:00.000000','2026-07-16 06:00:00.000000',NULL,0,22000,1,NULL,22000,3,22000,1,1,1,'CHECKED_OUT',NULL,_binary '\0','DK-5-1-D789AC1D','booking_5_3e935b2e-9669-4322-96e8-13ddb2cee148.jpg',_binary '','2026-07-14 06:00:00.000000','Free cancellation up to 24 hours before check-in',0);
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
  `admin_earnings` double DEFAULT NULL,
  `commission_rate` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `owner_earnings` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `hotel_owner_earnings` double DEFAULT NULL,
  `extra_service_id` bigint DEFAULT NULL,
  `payment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhfrf44p5dkjf5x4fvepysglok` (`payment_id`),
  UNIQUE KEY `UKpbph4tvv3vtgdyo4y44dhqh9h` (`extra_service_id`),
  KEY `FK1rm0049yawxsoiv34mvf2jk1e` (`booking_id`),
  CONSTRAINT `FK1rm0049yawxsoiv34mvf2jk1e` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKcdhu9i5iscv0d57mmp9owe70j` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`),
  CONSTRAINT `FKiq69xlp2flvva8e82vdppe981` FOREIGN KEY (`extra_service_id`) REFERENCES `extra_services` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
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
  PRIMARY KEY (`id`),
  KEY `FKkcavuhfpctlrf43c3dibqqfyj` (`hotel_id`),
  CONSTRAINT `FKkcavuhfpctlrf43c3dibqqfyj` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupons`
--

LOCK TABLES `coupons` WRITE;
/*!40000 ALTER TABLE `coupons` DISABLE KEYS */;
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
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
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
INSERT INTO `customers` VALUES (1,'r35yrett6','2026-06-29','jui.isratjahan22@gmail.com','Female','jhytds_28911042-e126-41e3-a49c-890173a0633d.jpg',NULL,'098765432',4,'jhytds');
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
  `description` varchar(255) DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `discount_percent` double DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  `room_id` bigint DEFAULT NULL,
  `deal_type` enum('FIXED_AMOUNT','PERCENTAGE','SEASONAL') DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8uqosunxg994ddnpbrwi42paa` (`hotel_id`),
  KEY `FKrtljkklwxk06jnjfpr1jtu1gl` (`room_id`),
  CONSTRAINT `FK8uqosunxg994ddnpbrwi42paa` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKrtljkklwxk06jnjfpr1jtu1gl` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deals`
--

LOCK TABLES `deals` WRITE;
/*!40000 ALTER TABLE `deals` DISABLE KEYS */;
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
  `created_at` datetime(6) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `service_type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `cancellable_until` datetime(6) DEFAULT NULL,
  `cancelled` bit(1) DEFAULT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `service_status` enum('CANCELLED','COMPLETED','PENDING') DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkkk6rlaa428v6qb4xqj51yy3o` (`booking_id`),
  CONSTRAINT `FKkkk6rlaa428v6qb4xqj51yy3o` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extra_services`
--

LOCK TABLES `extra_services` WRITE;
/*!40000 ALTER TABLE `extra_services` DISABLE KEYS */;
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
INSERT INTO `facilities` VALUES (1,'2026-07-11 21:46:59.043475','liuyhtgf','Outdoor swimming pool Free WiFi Airport shuttle Free parking Spa & wellness centre Fitness centre Restaurant Tea/coffee maker in all rooms','2026-07-11 21:46:59.043475',1);
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
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  `cancellable_until` datetime(6) DEFAULT NULL,
  `cancelled` bit(1) DEFAULT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `food_price` double DEFAULT NULL,
  `ordered_at` datetime(6) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrn3kq9cbnfoj33e9duxdmqcif` (`booking_id`),
  KEY `FKbf8eefptx5n7xh6gcgvco0x8o` (`hotel_id`),
  CONSTRAINT `FKbf8eefptx5n7xh6gcgvco0x8o` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`),
  CONSTRAINT `FKrn3kq9cbnfoj33e9duxdmqcif` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_items`
--

LOCK TABLES `food_items` WRITE;
/*!40000 ALTER TABLE `food_items` DISABLE KEYS */;
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
  `image_url` varchar(255) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlovtoweag6co6rgsctbbt954j` (`hotel_id`),
  CONSTRAINT `FKlovtoweag6co6rgsctbbt954j` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `galleries`
--

LOCK TABLES `galleries` WRITE;
/*!40000 ALTER TABLE `galleries` DISABLE KEYS */;
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
  `check_in_time` varchar(255) DEFAULT NULL,
  `check_out_time` varchar(255) DEFAULT NULL,
  `contact_email` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `hotel_policy` varchar(1000) DEFAULT NULL,
  `owner_info` varchar(1000) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint NOT NULL,
  `cancellation_policy` varchar(1000) DEFAULT NULL,
  `child_policy` varchar(1000) DEFAULT NULL,
  `languages` varchar(1000) DEFAULT NULL,
  `nearby_attractions` varchar(1000) DEFAULT NULL,
  `pet_policy` varchar(1000) DEFAULT NULL,
  `smoking_policy` varchar(1000) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
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
INSERT INTO `hotel_details` VALUES (1,'12pm','11 am','iuyt09','098765432','2026-07-11 21:45:52.466066','Pan Pacific Sonargaon Dhaka is centrally located close to Gulshan and the Embassy districts, just 14 km from the International Airport. Pan Pacific Sonargaon Dhaka is located in the city Center.','kiyutresiuytr','l;kjhg','2026-07-11 21:45:52.466066',1,'kljiuytr','lkjuoiu','','8967564','iu87t','oiu',15000);
/*!40000 ALTER TABLE `hotel_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_owners`
--

DROP TABLE IF EXISTS `hotel_owners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel_owners` (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `date_of_birth` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3l971t4ymni5b60u0cs4nye1x` (`email`),
  UNIQUE KEY `UK82o9jqgteum1o079ytd8h8mlh` (`user_id`),
  CONSTRAINT `FKrhvnm559g5eel2hijw1kmwr8i` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_owners`
--

LOCK TABLES `hotel_owners` WRITE;
/*!40000 ALTER TABLE `hotel_owners` DISABLE KEYS */;
INSERT INTO `hotel_owners` VALUES (1,NULL,NULL,'isratjahanjui847@gmail.com',NULL,NULL,'Israt Jahan Jui','01710535897',2,'2026-07-07 00:32:25.143991','2026-07-07 00:32:25.143991'),(2,'dfgh','2026-06-28 06:00:00.000000','uytrew@gmail.com','Male','hgfgh098765_0241366c-32ff-441a-94f3-96b32caf47fc.jpg','hgfgh098765','098765456789',3,'2026-07-09 13:24:02.015438','2026-07-09 13:24:02.015438'),(3,'Barisal','2026-06-28 06:00:00.000000','jui.isratjahan11@gmail.com','Female','Israt_38338099-c90c-4573-bef2-4be14bcb4fb2.jpg','Israt','01710535809',5,'2026-07-10 21:33:25.996808','2026-07-10 21:33:25.996808');
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
  `approved` bit(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `hotel_name` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `rating` varchar(255) DEFAULT NULL,
  `location_id` bigint DEFAULT NULL,
  `owner_id` int NOT NULL,
  `status` enum('ACTIVE','APPROVED','INACTIVE','PENDING_APPROVAL','REJECTED') DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `food_available` bit(1) DEFAULT NULL,
  `food_service_hours` varchar(255) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqs8u4n6x2f5anae9lllt3857p` (`location_id`),
  KEY `FK4a6yejtias6qjndncdm11id1g` (`owner_id`),
  CONSTRAINT `FK4a6yejtias6qjndncdm11id1g` FOREIGN KEY (`owner_id`) REFERENCES `hotel_owners` (`id`),
  CONSTRAINT `FKqs8u4n6x2f5anae9lllt3857p` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotels`
--

LOCK TABLES `hotels` WRITE;
/*!40000 ALTER TABLE `hotels` DISABLE KEYS */;
INSERT INTO `hotels` VALUES (1,'Dhaka',NULL,'lkjghf','Panpasific','aaaaaaaaaaaaa_286a4830-2ed8-4a13-a644-4311f170ba90.jpg','4',3,3,'APPROVED','2026-07-10 21:43:13.495516',NULL,NULL,NULL,'2026-07-11 01:11:12.660406'),(2,'Dhaka',NULL,'kljhg','aaaaaaaaaaaaa','aaaaaaaaaaaaa_7213e666-1d11-4ca2-ba05-c841a15f2f4d.jpg','4',1,3,'PENDING_APPROVAL','2026-07-10 21:45:39.300343',NULL,NULL,NULL,'2026-07-10 21:45:39.300343'),(4,'Plot 6,7 & 8, Hotel Motel Zone, Sea Beach 4701 Cox\'s Bazar, Chittagong Division, Bangladesh, 4701 Cox\'s Bazar, Bangladesh',NULL,'lkjkhgfds','Seagull Hotel Ltd','Seagull_Hotel_Ltd_46c9716a-313a-4a76-84ed-2e36fb8a6349.jpg','5',1,3,'APPROVED','2026-07-11 00:11:44.163565',NULL,NULL,NULL,'2026-07-11 01:09:57.848349');
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
  `invoice_number` varchar(255) DEFAULT NULL,
  `issued_at` datetime(6) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `payment_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `net_amount` double DEFAULT NULL,
  `status` enum('CANCELLED','ISSUED','PAID') DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `commission_id` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqn380ix1ge287r0rd8th12bwi` (`booking_id`),
  UNIQUE KEY `UK8cfbd92rwby0aqvqocuv6aa05` (`payment_id`),
  UNIQUE KEY `UKbsd97jjclowbotctav1vt0s0k` (`commission_id`),
  KEY `FKq2w4hmh6l9othnp6cepp0cfe2` (`customer_id`),
  CONSTRAINT `FK1nm4wl2dwd6q64xl0qcb458dv` FOREIGN KEY (`commission_id`) REFERENCES `commissions` (`id`),
  CONSTRAINT `FKb9bhb7xre5v64qvjeholh3qj0` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKq2w4hmh6l9othnp6cepp0cfe2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKq6fs19k0gqw3rg0mb87h60h6p` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
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
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `location_image` varchar(255) DEFAULT NULL,
  `location_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locations`
--

LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
INSERT INTO `locations` VALUES (1,NULL,NULL,'Dhaka',NULL,'Gulshan'),(2,NULL,NULL,'Dhaka',NULL,'Banani'),(3,NULL,NULL,'Dhaka',NULL,'Uttara'),(4,NULL,NULL,'Dhaka',NULL,'Uttara'),(5,NULL,NULL,'Dhaka',NULL,'Uttara'),(6,NULL,NULL,'Dhaka',NULL,'Uttara'),(7,NULL,NULL,'Coxbazar',NULL,'Coxbazar');
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `read_status` bit(1) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `channel` enum('EMAIL','PUSH','SMS','SYSTEM','WEB') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
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
  `amount` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint NOT NULL,
  `extra_service_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnuscjm6x127hkb15kcb8n56wo` (`booking_id`),
  UNIQUE KEY `UKfm8xkje8k108j34karfe7ybv1` (`extra_service_id`),
  CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKsrw79y2bpk1idgs5sqgivow7s` FOREIGN KEY (`extra_service_id`) REFERENCES `extra_services` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `generated_at` datetime(6) DEFAULT NULL,
  `monthly_income` double DEFAULT NULL,
  `occupancy_rate` double DEFAULT NULL,
  `total_bookings` int NOT NULL,
  `hotel_id` bigint NOT NULL,
  `income` double DEFAULT NULL,
  `type` enum('DAILY','MONTHLY','WEEKLY','YEARLY') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKawbu2vda5qljn76pbvirw1wuv` (`hotel_id`),
  CONSTRAINT `FKawbu2vda5qljn76pbvirw1wuv` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
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
  `rating` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `hotel_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4sm0k8kw740iyuex3vwwv1etu` (`customer_id`),
  KEY `FKb9igk5exfb4knqklcvka6cdhx` (`hotel_id`),
  CONSTRAINT `FK4sm0k8kw740iyuex3vwwv1etu` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKb9igk5exfb4knqklcvka6cdhx` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'kjhg','2026-07-11 21:33:24.388709',5,'2026-07-11 21:33:24.388709',1,1);
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
  `price` double DEFAULT NULL,
  `room_type` varchar(255) DEFAULT NULL,
  `total_rooms` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hotel_id` bigint DEFAULT NULL,
  `is_available` bit(1) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
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
INSERT INTO `rooms` VALUES (1,2,'jghf',4,0,1,'2026-07-11 21:50:02.864430','oyiutrf','dulex_a3b0415b-a843-4e62-88c4-279b64fec9b6.jpg',NULL,'dulex',1,'2026-07-11 21:51:07.813366',1,_binary '',22000);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `support_tickets`
--

DROP TABLE IF EXISTS `support_tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `priority` enum('HIGH','LOW','MEDIUM','URGENT') DEFAULT NULL,
  `agent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbj61s5pm6gwms5405fcdvgm1t` (`customer_id`),
  KEY `FK5n9d68hb5bo380qroq4894qcu` (`agent_id`),
  CONSTRAINT `FK5n9d68hb5bo380qroq4894qcu` FOREIGN KEY (`agent_id`) REFERENCES `users` (`id`),
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
  `email` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','HOTEL_OWNER') DEFAULT NULL,
  `verification_token` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKdu5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin@gmail.com',NULL,'Admin','$2a$10$H3Q.hkPO13M16IBZo9ZaUOWlkXko7Rgx2jLdW2F1U.yVAgVHe4SfW','01700000000','ADMIN',NULL,_binary ''),(2,'isratjahanjui847@gmail.com',NULL,'Israt Jahan Jui','$2a$10$YQ9bX4A9oWMKRTUvHGEXweq0bamXzNGAd6a.GurrVe5wBdgltttIS','01710535897','HOTEL_OWNER',NULL,_binary ''),(3,'uytrew@gmail.com','','hgfgh098765','$2a$10$7bwyhE4uMeuP6DzGeQhmTuuZjTwoXBCkokoxoh9JZVAccfr3VP/JW','098765456789','HOTEL_OWNER',NULL,_binary '\0'),(4,'jui.isratjahan22@gmail.com',NULL,'ert','$2a$10$9vCXnx7lrRHHriSz9z0BCeIkEaDznend8VQZPOeSkF8ze22nqBG6O','098765432','CUSTOMER',NULL,_binary ''),(5,'jui.isratjahan11@gmail.com','','Israt','$2a$10$BSSOtjlDMpHXQO5EdfXO0usIH.I/v52R3AuPyKnPfT.YcVzbqOGwu','01710535809','HOTEL_OWNER',NULL,_binary '');
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
  `amount` double DEFAULT NULL,
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
  `balance` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `total_earned` double DEFAULT NULL,
  `total_withdrawn` double DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
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
  `hotel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKteloqgusn16evq07nswqd92or` (`hotel_id`),
  KEY `FK330pyw2el06fn5g28ypyljt16` (`user_id`),
  KEY `FK60mulb9dr06pbuur46ywfp8fk` (`customer_id`),
  CONSTRAINT `FK330pyw2el06fn5g28ypyljt16` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK60mulb9dr06pbuur46ywfp8fk` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKteloqgusn16evq07nswqd92or` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlists`
--

LOCK TABLES `wishlists` WRITE;
/*!40000 ALTER TABLE `wishlists` DISABLE KEYS */;
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

-- Dump completed on 2026-07-12 14:18:11
