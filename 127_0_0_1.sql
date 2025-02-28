SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

DROP DATABASE IF EXISTS `fyp_project`;
CREATE DATABASE IF NOT EXISTS `fyp_project` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `fyp_project`;

DROP TABLE IF EXISTS `alert_record`;
CREATE TABLE `alert_record` (
  `alertRecord_id` int(10) NOT NULL,
  `alertRecord_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `VideoPath` varchar(30) NOT NULL,
  `RecordIcon` varchar(30) NOT NULL,
  `video_state` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
  `id` varchar(50) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `token` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `alert_record`
  ADD PRIMARY KEY (`alertRecord_id`);

ALTER TABLE `location`
  ADD PRIMARY KEY (`id`);


ALTER TABLE `alert_record`
  MODIFY `alertRecord_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=120;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
