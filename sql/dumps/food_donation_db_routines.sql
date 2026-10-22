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
-- Temporary view structure for view `distribution_schedules`
--

DROP TABLE IF EXISTS `distribution_schedules`;
/*!50001 DROP VIEW IF EXISTS `distribution_schedules`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `distribution_schedules` AS SELECT 
 1 AS `id`,
 1 AS `place_id`,
 1 AS `day_of_week`,
 1 AS `time_slot`,
 1 AS `food_type`,
 1 AS `quantity_target`,
 1 AS `unit`,
 1 AS `assigned_volunteer`,
 1 AS `volunteer_phone`,
 1 AS `donation_id`,
 1 AS `status`,
 1 AS `notes`,
 1 AS `recurring`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `distribution_schedules`
--

/*!50001 DROP VIEW IF EXISTS `distribution_schedules`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `distribution_schedules` AS select `distribution_schedule`.`id` AS `id`,`distribution_schedule`.`place_id` AS `place_id`,`distribution_schedule`.`day_of_week` AS `day_of_week`,`distribution_schedule`.`time_slot` AS `time_slot`,`distribution_schedule`.`food_type` AS `food_type`,`distribution_schedule`.`quantity_target` AS `quantity_target`,`distribution_schedule`.`unit` AS `unit`,`distribution_schedule`.`assigned_volunteer` AS `assigned_volunteer`,`distribution_schedule`.`volunteer_phone` AS `volunteer_phone`,`distribution_schedule`.`donation_id` AS `donation_id`,`distribution_schedule`.`status` AS `status`,`distribution_schedule`.`notes` AS `notes`,`distribution_schedule`.`recurring` AS `recurring`,`distribution_schedule`.`created_at` AS `created_at` from `distribution_schedule` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-29  0:40:44
