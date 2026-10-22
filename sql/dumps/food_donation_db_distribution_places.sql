-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: food_donation_db
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `distribution_places`
--

DROP TABLE IF EXISTS `distribution_places`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `distribution_places` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `address` text,
  `contact_person` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(50) DEFAULT NULL,
  `operating_hours` varchar(255) DEFAULT NULL,
  `capacity` int DEFAULT NULL,
  `notes` text,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribution_places`
--

LOCK TABLES `distribution_places` WRITE;
/*!40000 ALTER TABLE `distribution_places` DISABLE KEYS */;
INSERT INTO `distribution_places` VALUES (1,'Happy Paws Rescue','dog_food_service','123 Bark Ave','John Doe','555-0101','9 AM - 5 PM',50,NULL,1,'2026-09-25 14:04:23'),(2,'Sunset Retirement Home','old_age_home','456 Golden Years Way','Jane Smith','555-0102','24/7',100,NULL,1,'2026-09-25 14:04:23'),(3,'Sri Ram Mandir','temple','789 Faith Blvd','Pandit Ji','555-0103','6 AM - 8 PM',500,NULL,1,'2026-09-25 14:04:23'),(4,'Hope Orphanage','orphanage','101 Children St','Sister Mary','555-0104','24/7',75,NULL,1,'2026-09-25 14:04:23'),(5,'City Community Kitchen','community_kitchen','202 Center Sq','Chef Mike','555-0105','11 AM - 2 PM',200,NULL,1,'2026-09-25 14:04:23'),(6,'General Hospital','hospital','303 Health Rd','Dr. Adams','555-0106','24/7',300,NULL,1,'2026-09-25 14:04:23'),(7,'Springfield Elementary','school','404 Education Ln','Principal Skinner','555-0107','8 AM - 3 PM',400,NULL,1,'2026-09-25 14:04:23'),(8,'Downtown Shelter','other','505 Main St','Bob Builder','555-0108','6 PM - 8 AM',150,NULL,1,'2026-09-25 14:04:23');
/*!40000 ALTER TABLE `distribution_places` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-29  0:40:43
