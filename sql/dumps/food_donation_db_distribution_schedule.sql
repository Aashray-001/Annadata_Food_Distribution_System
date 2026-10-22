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
-- Table structure for table `distribution_schedule`
--

DROP TABLE IF EXISTS `distribution_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `distribution_schedule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `place_id` int NOT NULL,
  `day_of_week` varchar(20) DEFAULT NULL,
  `time_slot` varchar(50) DEFAULT NULL,
  `food_type` varchar(100) DEFAULT NULL,
  `quantity_target` int DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL,
  `assigned_volunteer` varchar(255) DEFAULT NULL,
  `volunteer_phone` varchar(50) DEFAULT NULL,
  `donation_id` int DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `notes` text,
  `recurring` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `place_id` (`place_id`),
  KEY `donation_id` (`donation_id`),
  CONSTRAINT `distribution_schedule_ibfk_1` FOREIGN KEY (`place_id`) REFERENCES `distribution_places` (`id`) ON DELETE CASCADE,
  CONSTRAINT `distribution_schedule_ibfk_2` FOREIGN KEY (`donation_id`) REFERENCES `donations` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribution_schedule`
--

LOCK TABLES `distribution_schedule` WRITE;
/*!40000 ALTER TABLE `distribution_schedule` DISABLE KEYS */;
INSERT INTO `distribution_schedule` VALUES (1,1,'monday','morning','Dog Food',20,'kg','Alice','555-1111',NULL,'scheduled',NULL,1,'2026-09-25 14:04:23'),(2,5,'wednesday','afternoon','Hot Meals',100,'plates','Bob','555-2222',NULL,'assigned',NULL,1,'2026-09-25 14:04:23');
/*!40000 ALTER TABLE `distribution_schedule` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-29  0:40:42
