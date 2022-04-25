-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 25, 2022 at 05:10 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fyp_project`
--

-- --------------------------------------------------------

--
-- Table structure for table `alert_record`
--

CREATE TABLE `alert_record` (
  `alertRecord_id` int(10) NOT NULL,
  `alertRecord_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `VideoPath` varchar(30) NOT NULL,
  `RecordIcon` varchar(30) NOT NULL,
  `video_state` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `alert_record`
--

INSERT INTO `alert_record` (`alertRecord_id`, `alertRecord_time`, `VideoPath`, `RecordIcon`, `video_state`) VALUES
(105, '2022-04-25 09:20:00', '4_25_17_20_0', '4_25_17_20_0', 0);

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
  `id` varchar(50) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `token` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `location`
--

INSERT INTO `location` (`id`, `latitude`, `longitude`, `token`) VALUES
('3e92f9319ab9b0f3', 22.356861666666667, 114.12777833333332, 'fWb9AD78SnWGY4mTo1UhkR:APA91bFrjqbc-noantdX9BUzf_zjTGflPXy1zPRSyd5A9KuWVPCCqG1b4ZLfxWo_ibteX0YFrzFpJ6yq7Kl5hCv6_PofF1QqcuFUuvPhV5r0spGQhZNfqdOB0ew_Tfwc5zTvYqJ3zky_'),
('5c59cc5d70eef117', 22.35493333333333, 114.126585, 'dBdvtRbiRV-UDsxsHzRAeS:APA91bGLvDMlyTdtmLsq8YC_pzXNhS2kNk6xk3Wt_xp4Rdw0eurJiJ4phhRniZBmA2JFpoazxkFc3cMniioDJ7UsZL5FKz2wo-t4BGJ4PLeH7lk6AmlV4mQwckmUBTwwakdbHmg8RQP3'),
('8fe46d187257f377', 22.354683333333334, 114.102795, 'dZIlHpXyTAS4nt8wzZdzJ3:APA91bHthEujHUI8Dy3xXp04ZurGxrxQ4Sb4cjUHITEjmIQQm6EEl1rVjwNuClr_waddyGNsdVm2-NtXJT4qX51FqUhcKtriSO0P__FYM01Qa95S2S61CTunsttxcZcsnoNe8SsFflkb');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `alert_record`
--
ALTER TABLE `alert_record`
  ADD PRIMARY KEY (`alertRecord_id`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `alert_record`
--
ALTER TABLE `alert_record`
  MODIFY `alertRecord_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=106;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
