-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 10, 2022 at 09:35 AM
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
-- Database: `4511_assignment_db`
--
CREATE DATABASE IF NOT EXISTS `4511_assignment_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `4511_assignment_db`;
--
-- Database: `fyp_project`
--
CREATE DATABASE IF NOT EXISTS `fyp_project` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `fyp_project`;

-- --------------------------------------------------------

--
-- Table structure for table `alert_record`
--

CREATE TABLE `alert_record` (
  `alertRecord_id` int(10) NOT NULL,
  `alertRecord_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `VideoPath` varchar(30) NOT NULL,
  `RecordIcon` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `alert_record`
--

INSERT INTO `alert_record` (`alertRecord_id`, `alertRecord_time`, `VideoPath`, `RecordIcon`) VALUES
(48, '2022-02-15 03:43:48', '2_15_11_43_48', '2_15_11_43_48'),
(49, '2022-02-15 03:44:05', '2_15_11_44_5', '2_15_11_44_5'),
(50, '2022-02-15 05:10:12', '2_15_13_10_12', '2_15_13_10_12'),
(51, '2022-02-15 05:12:20', '2_15_13_12_20', '2_15_13_12_20'),
(52, '2022-03-01 06:08:16', '3_1_14_8_16', '3_1_14_8_16'),
(53, '2022-03-01 06:08:51', '3_1_14_8_51', '3_1_14_8_51'),
(54, '2022-03-01 06:09:04', '3_1_14_9_4', '3_1_14_9_4'),
(55, '2022-03-01 06:09:18', '3_1_14_9_18', '3_1_14_9_18'),
(56, '2022-03-01 06:10:55', '3_1_14_10_55', '3_1_14_10_55'),
(57, '2022-03-01 06:18:05', '3_1_14_18_5', '3_1_14_18_5'),
(58, '2022-03-01 06:18:18', '3_1_14_18_18', '3_1_14_18_18'),
(59, '2022-03-01 06:23:00', '3_1_14_23_0', '3_1_14_23_0'),
(60, '2022-03-01 06:24:24', '3_1_14_24_24', '3_1_14_24_24'),
(61, '2022-03-01 06:24:37', '3_1_14_24_37', '3_1_14_24_37'),
(62, '2022-03-01 06:30:03', '3_1_14_30_3', '3_1_14_30_3'),
(63, '2022-03-01 06:30:14', '3_1_14_30_14', '3_1_14_30_14'),
(64, '2022-03-01 06:39:21', '3_1_14_39_21', '3_1_14_39_21'),
(65, '2022-03-01 06:42:46', '3_1_14_42_46', '3_1_14_42_46');

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
('250011f40e21833f', 22.35493333333333, 114.126585, 'dqKSCYb5RP-mbIHWtx0fP7:APA91bHA7zhHRVTTn9qd5Lv17XHZHe_Vqk2k0c9Q3_awhcSjrKJBxemOrcS4RE6_XiBhNOM1TYXvinNjoCzB2nBxaUNKUOioFyQfU4D6OqkFgiQDM13P9Kv9EH_2CFKc92BaErvZNRWc'),
('3e92f9319ab9b0f3', 22.356861666666667, 114.12777833333332, 'dvftQnmbQuGTW2WsyS-vTP:APA91bFEhwXeEQ2NpDSy0zST6I-Nb3WvAY_DsFVYZGM9eEqPb28qzNzJgeMKGmnWnDxjzMGhcWeqQ5mgTIkML7TP8T1QSpurqj_IstRGguZXZyri8tUJTK6v-e1jSNe0lVlFmDYJe9Ei'),
('8fe46d187257f377', 22.35698, 114.12759, 'e_FI_BmgRKunOpXEcKFq6B:APA91bF7RO3OPSU6jXiPRFVdbd_PxINfnd2DgRpgIulgihbo1cxN4-G6V-3rLNUaoKQbOoOyuXtb7ahlEf6074A3bfJOvbScm3c0i9xRfuV5bOrGopMt2YnUdg_6MTZNdmvzYAxo2hwn');

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
  MODIFY `alertRecord_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=66;
--
-- Database: `itp4511_db`
--
CREATE DATABASE IF NOT EXISTS `itp4511_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `itp4511_db`;

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `custId` varchar(5) NOT NULL,
  `name` varchar(25) NOT NULL,
  `tel` varchar(10) NOT NULL,
  `age` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`custId`, `name`, `tel`, `age`) VALUES
('1234', 'qwer', 'qwer', 10),
('2', 'Nancy', '12345678', 50),
('24456', 'qwer', 'fghfgh', 50);

-- --------------------------------------------------------

--
-- Table structure for table `userinfo`
--

CREATE TABLE `userinfo` (
  `id` varchar(5) NOT NULL,
  `Username` varchar(25) NOT NULL,
  `password` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `userinfo`
--

INSERT INTO `userinfo` (`id`, `Username`, `password`) VALUES
('1', 'abc', '123'),
('2', 'xyz', '123');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`custId`);

--
-- Indexes for table `userinfo`
--
ALTER TABLE `userinfo`
  ADD PRIMARY KEY (`id`);
--
-- Database: `phpmyadmin`
--
CREATE DATABASE IF NOT EXISTS `phpmyadmin` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `phpmyadmin`;

-- --------------------------------------------------------

--
-- Table structure for table `pma__bookmark`
--

CREATE TABLE `pma__bookmark` (
  `id` int(10) UNSIGNED NOT NULL,
  `dbase` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `user` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `label` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `query` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Bookmarks';

-- --------------------------------------------------------

--
-- Table structure for table `pma__central_columns`
--

CREATE TABLE `pma__central_columns` (
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `col_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `col_type` varchar(64) COLLATE utf8_bin NOT NULL,
  `col_length` text COLLATE utf8_bin DEFAULT NULL,
  `col_collation` varchar(64) COLLATE utf8_bin NOT NULL,
  `col_isNull` tinyint(1) NOT NULL,
  `col_extra` varchar(255) COLLATE utf8_bin DEFAULT '',
  `col_default` text COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Central list of columns';

-- --------------------------------------------------------

--
-- Table structure for table `pma__column_info`
--

CREATE TABLE `pma__column_info` (
  `id` int(5) UNSIGNED NOT NULL,
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `table_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `column_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `comment` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `mimetype` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `transformation` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `transformation_options` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `input_transformation` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `input_transformation_options` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Column information for phpMyAdmin';

-- --------------------------------------------------------

--
-- Table structure for table `pma__designer_settings`
--

CREATE TABLE `pma__designer_settings` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `settings_data` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Settings related to Designer';

-- --------------------------------------------------------

--
-- Table structure for table `pma__export_templates`
--

CREATE TABLE `pma__export_templates` (
  `id` int(5) UNSIGNED NOT NULL,
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `export_type` varchar(10) COLLATE utf8_bin NOT NULL,
  `template_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `template_data` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Saved export templates';

--
-- Dumping data for table `pma__export_templates`
--

INSERT INTO `pma__export_templates` (`id`, `username`, `export_type`, `template_name`, `template_data`) VALUES
(1, 'root', 'server', 'fyp_project', '{\"quick_or_custom\":\"quick\",\"what\":\"sql\",\"db_select[]\":[\"4511_assignment_db\",\"fyp_project\",\"itp4511_db\",\"phpmyadmin\",\"projectdb\",\"singing_competition\",\"test\"],\"aliases_new\":\"\",\"output_format\":\"sendit\",\"filename_template\":\"@SERVER@\",\"remember_template\":\"on\",\"charset\":\"utf-8\",\"compression\":\"none\",\"maxsize\":\"\",\"codegen_structure_or_data\":\"data\",\"codegen_format\":\"0\",\"csv_separator\":\",\",\"csv_enclosed\":\"\\\"\",\"csv_escaped\":\"\\\"\",\"csv_terminated\":\"AUTO\",\"csv_null\":\"NULL\",\"csv_structure_or_data\":\"data\",\"excel_null\":\"NULL\",\"excel_columns\":\"something\",\"excel_edition\":\"win\",\"excel_structure_or_data\":\"data\",\"json_structure_or_data\":\"data\",\"json_unicode\":\"something\",\"latex_caption\":\"something\",\"latex_structure_or_data\":\"structure_and_data\",\"latex_structure_caption\":\"Structure of table @TABLE@\",\"latex_structure_continued_caption\":\"Structure of table @TABLE@ (continued)\",\"latex_structure_label\":\"tab:@TABLE@-structure\",\"latex_relation\":\"something\",\"latex_comments\":\"something\",\"latex_mime\":\"something\",\"latex_columns\":\"something\",\"latex_data_caption\":\"Content of table @TABLE@\",\"latex_data_continued_caption\":\"Content of table @TABLE@ (continued)\",\"latex_data_label\":\"tab:@TABLE@-data\",\"latex_null\":\"\\\\textit{NULL}\",\"mediawiki_structure_or_data\":\"data\",\"mediawiki_caption\":\"something\",\"mediawiki_headers\":\"something\",\"htmlword_structure_or_data\":\"structure_and_data\",\"htmlword_null\":\"NULL\",\"ods_null\":\"NULL\",\"ods_structure_or_data\":\"data\",\"odt_structure_or_data\":\"structure_and_data\",\"odt_relation\":\"something\",\"odt_comments\":\"something\",\"odt_mime\":\"something\",\"odt_columns\":\"something\",\"odt_null\":\"NULL\",\"pdf_report_title\":\"\",\"pdf_structure_or_data\":\"data\",\"phparray_structure_or_data\":\"data\",\"sql_include_comments\":\"something\",\"sql_header_comment\":\"\",\"sql_use_transaction\":\"something\",\"sql_compatibility\":\"NONE\",\"sql_structure_or_data\":\"structure_and_data\",\"sql_create_table\":\"something\",\"sql_auto_increment\":\"something\",\"sql_create_view\":\"something\",\"sql_create_trigger\":\"something\",\"sql_backquotes\":\"something\",\"sql_type\":\"INSERT\",\"sql_insert_syntax\":\"both\",\"sql_max_query_size\":\"50000\",\"sql_hex_for_binary\":\"something\",\"sql_utc_time\":\"something\",\"texytext_structure_or_data\":\"structure_and_data\",\"texytext_null\":\"NULL\",\"yaml_structure_or_data\":\"data\",\"\":null,\"as_separate_files\":null,\"csv_removeCRLF\":null,\"csv_columns\":null,\"excel_removeCRLF\":null,\"json_pretty_print\":null,\"htmlword_columns\":null,\"ods_columns\":null,\"sql_dates\":null,\"sql_relation\":null,\"sql_mime\":null,\"sql_disable_fk\":null,\"sql_views_as_tables\":null,\"sql_metadata\":null,\"sql_drop_database\":null,\"sql_drop_table\":null,\"sql_if_not_exists\":null,\"sql_simple_view_export\":null,\"sql_view_current_user\":null,\"sql_or_replace_view\":null,\"sql_procedure_function\":null,\"sql_truncate\":null,\"sql_delayed\":null,\"sql_ignore\":null,\"texytext_columns\":null}');

-- --------------------------------------------------------

--
-- Table structure for table `pma__favorite`
--

CREATE TABLE `pma__favorite` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `tables` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Favorite tables';

-- --------------------------------------------------------

--
-- Table structure for table `pma__history`
--

CREATE TABLE `pma__history` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `username` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `db` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `table` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `timevalue` timestamp NOT NULL DEFAULT current_timestamp(),
  `sqlquery` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='SQL history for phpMyAdmin';

-- --------------------------------------------------------

--
-- Table structure for table `pma__navigationhiding`
--

CREATE TABLE `pma__navigationhiding` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `item_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `item_type` varchar(64) COLLATE utf8_bin NOT NULL,
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `table_name` varchar(64) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Hidden items of navigation tree';

-- --------------------------------------------------------

--
-- Table structure for table `pma__pdf_pages`
--

CREATE TABLE `pma__pdf_pages` (
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `page_nr` int(10) UNSIGNED NOT NULL,
  `page_descr` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='PDF relation pages for phpMyAdmin';

-- --------------------------------------------------------

--
-- Table structure for table `pma__recent`
--

CREATE TABLE `pma__recent` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `tables` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Recently accessed tables';

--
-- Dumping data for table `pma__recent`
--

INSERT INTO `pma__recent` (`username`, `tables`) VALUES
('root', '[{\"db\":\"itp4511_db\",\"table\":\"customer\"},{\"db\":\"itp4511_db\",\"table\":\"userinfo\"},{\"db\":\"singing_competition\",\"table\":\"Enrollment\"},{\"db\":\"singing_competition\",\"table\":\"enrollment\"},{\"db\":\"singing_competition\",\"table\":\"competition\"},{\"db\":\"singing_competition\",\"table\":\"singer\"},{\"db\":\"test\",\"table\":\"ad_student\"},{\"db\":\"test\",\"table\":\"AD_Student\"},{\"db\":\"fyp_project\",\"table\":\"location\"},{\"db\":\"fyp_project\",\"table\":\"alert_record\"}]');

-- --------------------------------------------------------

--
-- Table structure for table `pma__relation`
--

CREATE TABLE `pma__relation` (
  `master_db` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `master_table` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `master_field` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `foreign_db` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `foreign_table` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `foreign_field` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Relation table';

-- --------------------------------------------------------

--
-- Table structure for table `pma__savedsearches`
--

CREATE TABLE `pma__savedsearches` (
  `id` int(5) UNSIGNED NOT NULL,
  `username` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `search_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `search_data` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Saved searches';

-- --------------------------------------------------------

--
-- Table structure for table `pma__table_coords`
--

CREATE TABLE `pma__table_coords` (
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `table_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `pdf_page_number` int(11) NOT NULL DEFAULT 0,
  `x` float UNSIGNED NOT NULL DEFAULT 0,
  `y` float UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table coordinates for phpMyAdmin PDF output';

-- --------------------------------------------------------

--
-- Table structure for table `pma__table_info`
--

CREATE TABLE `pma__table_info` (
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `table_name` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `display_field` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Table information for phpMyAdmin';

-- --------------------------------------------------------

--
-- Table structure for table `pma__table_uiprefs`
--

CREATE TABLE `pma__table_uiprefs` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `table_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `prefs` text COLLATE utf8_bin NOT NULL,
  `last_update` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Tables'' UI preferences';

--
-- Dumping data for table `pma__table_uiprefs`
--

INSERT INTO `pma__table_uiprefs` (`username`, `db_name`, `table_name`, `prefs`, `last_update`) VALUES
('root', 'fyp_project', 'location', '{\"CREATE_TIME\":\"2022-03-08 12:48:02\"}', '2022-03-15 02:29:35');

-- --------------------------------------------------------

--
-- Table structure for table `pma__tracking`
--

CREATE TABLE `pma__tracking` (
  `db_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `table_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `version` int(10) UNSIGNED NOT NULL,
  `date_created` datetime NOT NULL,
  `date_updated` datetime NOT NULL,
  `schema_snapshot` text COLLATE utf8_bin NOT NULL,
  `schema_sql` text COLLATE utf8_bin DEFAULT NULL,
  `data_sql` longtext COLLATE utf8_bin DEFAULT NULL,
  `tracking` set('UPDATE','REPLACE','INSERT','DELETE','TRUNCATE','CREATE DATABASE','ALTER DATABASE','DROP DATABASE','CREATE TABLE','ALTER TABLE','RENAME TABLE','DROP TABLE','CREATE INDEX','DROP INDEX','CREATE VIEW','ALTER VIEW','DROP VIEW') COLLATE utf8_bin DEFAULT NULL,
  `tracking_active` int(1) UNSIGNED NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Database changes tracking for phpMyAdmin';

-- --------------------------------------------------------

--
-- Table structure for table `pma__userconfig`
--

CREATE TABLE `pma__userconfig` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `timevalue` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `config_data` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='User preferences storage for phpMyAdmin';

--
-- Dumping data for table `pma__userconfig`
--

INSERT INTO `pma__userconfig` (`username`, `timevalue`, `config_data`) VALUES
('root', '2022-04-10 07:35:01', '{\"Console\\/Mode\":\"collapse\"}');

-- --------------------------------------------------------

--
-- Table structure for table `pma__usergroups`
--

CREATE TABLE `pma__usergroups` (
  `usergroup` varchar(64) COLLATE utf8_bin NOT NULL,
  `tab` varchar(64) COLLATE utf8_bin NOT NULL,
  `allowed` enum('Y','N') COLLATE utf8_bin NOT NULL DEFAULT 'N'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='User groups with configured menu items';

-- --------------------------------------------------------

--
-- Table structure for table `pma__users`
--

CREATE TABLE `pma__users` (
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `usergroup` varchar(64) COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Users and their assignments to user groups';

--
-- Indexes for dumped tables
--

--
-- Indexes for table `pma__bookmark`
--
ALTER TABLE `pma__bookmark`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pma__central_columns`
--
ALTER TABLE `pma__central_columns`
  ADD PRIMARY KEY (`db_name`,`col_name`);

--
-- Indexes for table `pma__column_info`
--
ALTER TABLE `pma__column_info`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `db_name` (`db_name`,`table_name`,`column_name`);

--
-- Indexes for table `pma__designer_settings`
--
ALTER TABLE `pma__designer_settings`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `pma__export_templates`
--
ALTER TABLE `pma__export_templates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `u_user_type_template` (`username`,`export_type`,`template_name`);

--
-- Indexes for table `pma__favorite`
--
ALTER TABLE `pma__favorite`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `pma__history`
--
ALTER TABLE `pma__history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `username` (`username`,`db`,`table`,`timevalue`);

--
-- Indexes for table `pma__navigationhiding`
--
ALTER TABLE `pma__navigationhiding`
  ADD PRIMARY KEY (`username`,`item_name`,`item_type`,`db_name`,`table_name`);

--
-- Indexes for table `pma__pdf_pages`
--
ALTER TABLE `pma__pdf_pages`
  ADD PRIMARY KEY (`page_nr`),
  ADD KEY `db_name` (`db_name`);

--
-- Indexes for table `pma__recent`
--
ALTER TABLE `pma__recent`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `pma__relation`
--
ALTER TABLE `pma__relation`
  ADD PRIMARY KEY (`master_db`,`master_table`,`master_field`),
  ADD KEY `foreign_field` (`foreign_db`,`foreign_table`);

--
-- Indexes for table `pma__savedsearches`
--
ALTER TABLE `pma__savedsearches`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `u_savedsearches_username_dbname` (`username`,`db_name`,`search_name`);

--
-- Indexes for table `pma__table_coords`
--
ALTER TABLE `pma__table_coords`
  ADD PRIMARY KEY (`db_name`,`table_name`,`pdf_page_number`);

--
-- Indexes for table `pma__table_info`
--
ALTER TABLE `pma__table_info`
  ADD PRIMARY KEY (`db_name`,`table_name`);

--
-- Indexes for table `pma__table_uiprefs`
--
ALTER TABLE `pma__table_uiprefs`
  ADD PRIMARY KEY (`username`,`db_name`,`table_name`);

--
-- Indexes for table `pma__tracking`
--
ALTER TABLE `pma__tracking`
  ADD PRIMARY KEY (`db_name`,`table_name`,`version`);

--
-- Indexes for table `pma__userconfig`
--
ALTER TABLE `pma__userconfig`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `pma__usergroups`
--
ALTER TABLE `pma__usergroups`
  ADD PRIMARY KEY (`usergroup`,`tab`,`allowed`);

--
-- Indexes for table `pma__users`
--
ALTER TABLE `pma__users`
  ADD PRIMARY KEY (`username`,`usergroup`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `pma__bookmark`
--
ALTER TABLE `pma__bookmark`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pma__column_info`
--
ALTER TABLE `pma__column_info`
  MODIFY `id` int(5) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pma__export_templates`
--
ALTER TABLE `pma__export_templates`
  MODIFY `id` int(5) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `pma__history`
--
ALTER TABLE `pma__history`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pma__pdf_pages`
--
ALTER TABLE `pma__pdf_pages`
  MODIFY `page_nr` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pma__savedsearches`
--
ALTER TABLE `pma__savedsearches`
  MODIFY `id` int(5) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- Database: `projectdb`
--
CREATE DATABASE IF NOT EXISTS `projectdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `projectdb`;

-- --------------------------------------------------------

--
-- Table structure for table `airwaybill`
--

CREATE TABLE `airwaybill` (
  `airWaybillNo` int(50) NOT NULL,
  `customerEmail` varchar(50) NOT NULL,
  `staffID` varchar(15) DEFAULT NULL,
  `locationID` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `receiverName` varchar(100) NOT NULL,
  `receiverPhoneNumber` varchar(100) NOT NULL,
  `receiverAddress` varchar(255) NOT NULL,
  `weight` float DEFAULT NULL,
  `totalPrice` decimal(10,1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `airwaybill`
--

INSERT INTO `airwaybill` (`airWaybillNo`, `customerEmail`, `staffID`, `locationID`, `date`, `receiverName`, `receiverPhoneNumber`, `receiverAddress`, `weight`, `totalPrice`) VALUES
(1, 'marcus@gmail.com', 'Mary112', 1, '2021-03-24 08:12:13', 'Peter', '23456454', 'Flat 8, Chates Farm Court, John Street, Brighton', 25.5, '1880.0'),
(2, 'marcus@gmail.com', NULL, 2, '2021-03-25 09:20:30', 'John', '76548273', 'Flat 1, Trevena Court, Avenue Road, London', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `airwaybilldeliveryrecord`
--

CREATE TABLE `airwaybilldeliveryrecord` (
  `airWaybillDeliveryRecordID` int(11) NOT NULL,
  `airWaybillNo` int(50) NOT NULL,
  `deliveryStatusID` int(2) NOT NULL,
  `recordDateTime` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `currentLocation` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `airwaybilldeliveryrecord`
--

INSERT INTO `airwaybilldeliveryrecord` (`airWaybillDeliveryRecordID`, `airWaybillNo`, `deliveryStatusID`, `recordDateTime`, `currentLocation`) VALUES
(1, 1, 1, '2021-03-22 20:36:00', NULL),
(2, 2, 1, '2021-03-25 21:37:00', NULL),
(3, 1, 2, '2021-03-23 23:36:00', NULL),
(4, 1, 3, '2021-03-24 09:36:00', 'Hong Kong'),
(5, 1, 3, '2021-03-25 09:36:00', 'Shenzhen'),
(6, 1, 3, '2021-03-26 09:36:00', 'Shanghai'),
(7, 1, 4, '2021-03-27 09:36:00', 'Shanghai'),
(8, 1, 5, '2021-03-28 09:36:00', 'Shanghai');

-- --------------------------------------------------------

--
-- Table structure for table `chargetable`
--

CREATE TABLE `chargetable` (
  `chargeID` int(11) NOT NULL,
  `locationID` int(11) NOT NULL,
  `weight` float NOT NULL,
  `rate` decimal(10,1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `chargetable`
--

INSERT INTO `chargetable` (`chargeID`, `locationID`, `weight`, `rate`) VALUES
(1, 1, 1, '150.0'),
(2, 1, 2, '298.0'),
(3, 1, 3, '440.0'),
(4, 1, 4, '586.0'),
(5, 1, 5, '731.0'),
(6, 1, 6, '876.0'),
(7, 1, 7, '1021.0'),
(8, 1, 8, '1166.0'),
(9, 1, 9, '1311.0'),
(10, 1, 10, '1456.0'),
(11, 2, 1, '300.0'),
(12, 2, 2, '590.0'),
(13, 2, 3, '880.0'),
(14, 2, 4, '1170.0'),
(15, 2, 5, '1460.0'),
(16, 2, 6, '1750.0'),
(17, 2, 7, '2040.0'),
(18, 2, 8, '2330.0'),
(19, 2, 9, '2620.0'),
(20, 2, 10, '2910.0'),
(21, 3, 1, '549.0'),
(22, 3, 2, '1096.0'),
(23, 3, 3, '1643.0'),
(24, 3, 4, '2190.0'),
(25, 3, 5, '2737.0'),
(26, 3, 6, '3284.0'),
(27, 3, 7, '3831.0'),
(28, 3, 8, '4378.0'),
(29, 3, 9, '4925.0'),
(30, 3, 10, '5472.0');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customerEmail` varchar(50) NOT NULL,
  `customerName` varchar(100) NOT NULL,
  `customerPassword` varchar(40) NOT NULL,
  `accountCreationDate` date NOT NULL,
  `phoneNumber` varchar(8) NOT NULL,
  `address` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customerEmail`, `customerName`, `customerPassword`, `accountCreationDate`, `phoneNumber`, `address`) VALUES
('marcus@gmail.com', 'Marcus Cheung', 'a1234', '2021-03-21', '57685876', '2/F 7 Carmel Village Street HO MAN TIN KOWLOON');

-- --------------------------------------------------------

--
-- Table structure for table `deliverystatus`
--

CREATE TABLE `deliverystatus` (
  `deliveryStatusID` int(2) NOT NULL,
  `deliveryStatusName` varchar(255) NOT NULL,
  `deliveryStatusDescription` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `deliverystatus`
--

INSERT INTO `deliverystatus` (`deliveryStatusID`, `deliveryStatusName`, `deliveryStatusDescription`) VALUES
(1, 'Waiting for Confirmation', 'Waiting staff to verify the information'),
(2, 'Confirmed', 'Order is confirmed'),
(3, 'In Transit', 'Means that the parcel is on the way to the destination'),
(4, 'Delivering', 'Means that the deliveryman is sending the parcel to the receiver'),
(5, 'Completed', 'Means that the receiver received the parcel');

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
  `locationID` int(11) NOT NULL,
  `locationName` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `location`
--

INSERT INTO `location` (`locationID`, `locationName`) VALUES
(1, 'China Shanghai'),
(2, 'Japan'),
(3, 'Australia');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `staffID` varchar(15) NOT NULL,
  `staffName` varchar(255) NOT NULL,
  `staffPassword` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`staffID`, `staffName`, `staffPassword`) VALUES
('Mary112', 'Mary Chau', 'mary999');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `airwaybill`
--
ALTER TABLE `airwaybill`
  ADD PRIMARY KEY (`airWaybillNo`),
  ADD KEY `FKAirWaybill444828` (`customerEmail`),
  ADD KEY `FKAirWaybill454482` (`staffID`),
  ADD KEY `FKAirWaybill118245` (`locationID`);

--
-- Indexes for table `airwaybilldeliveryrecord`
--
ALTER TABLE `airwaybilldeliveryrecord`
  ADD PRIMARY KEY (`airWaybillDeliveryRecordID`),
  ADD KEY `FKAirWaybill437304` (`deliveryStatusID`),
  ADD KEY `FKAirWaybill115654` (`airWaybillNo`);

--
-- Indexes for table `chargetable`
--
ALTER TABLE `chargetable`
  ADD PRIMARY KEY (`chargeID`),
  ADD KEY `FKChargeTabl634318` (`locationID`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customerEmail`);

--
-- Indexes for table `deliverystatus`
--
ALTER TABLE `deliverystatus`
  ADD PRIMARY KEY (`deliveryStatusID`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`locationID`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`staffID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `airwaybill`
--
ALTER TABLE `airwaybill`
  MODIFY `airWaybillNo` int(50) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `airwaybilldeliveryrecord`
--
ALTER TABLE `airwaybilldeliveryrecord`
  MODIFY `airWaybillDeliveryRecordID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `chargetable`
--
ALTER TABLE `chargetable`
  MODIFY `chargeID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `deliverystatus`
--
ALTER TABLE `deliverystatus`
  MODIFY `deliveryStatusID` int(2) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `airwaybill`
--
ALTER TABLE `airwaybill`
  ADD CONSTRAINT `FKAirWaybill118245` FOREIGN KEY (`locationID`) REFERENCES `location` (`locationID`),
  ADD CONSTRAINT `FKAirWaybill444828` FOREIGN KEY (`customerEmail`) REFERENCES `customer` (`customerEmail`),
  ADD CONSTRAINT `FKAirWaybill454482` FOREIGN KEY (`staffID`) REFERENCES `staff` (`staffID`);

--
-- Constraints for table `airwaybilldeliveryrecord`
--
ALTER TABLE `airwaybilldeliveryrecord`
  ADD CONSTRAINT `FKAirWaybill115654` FOREIGN KEY (`airWaybillNo`) REFERENCES `airwaybill` (`airWaybillNo`),
  ADD CONSTRAINT `FKAirWaybill437304` FOREIGN KEY (`deliveryStatusID`) REFERENCES `deliverystatus` (`deliveryStatusID`);

--
-- Constraints for table `chargetable`
--
ALTER TABLE `chargetable`
  ADD CONSTRAINT `FKChargeTabl634318` FOREIGN KEY (`locationID`) REFERENCES `location` (`locationID`);
--
-- Database: `singing_competition`
--
CREATE DATABASE IF NOT EXISTS `singing_competition` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `singing_competition`;

-- --------------------------------------------------------

--
-- Table structure for table `competition`
--

CREATE TABLE `competition` (
  `CompetitionId` char(7) NOT NULL,
  `Competition_Name` varchar(30) NOT NULL,
  `Location` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `competition`
--

INSERT INTO `competition` (`CompetitionId`, `Competition_Name`, `Location`) VALUES
('1', '1', 'Hong Kong'),
('2', '2', 'japan'),
('3', '3', 'Hong Kong'),
('4', '4', 'Japan'),
('5', '5', 'Hong Kong');

-- --------------------------------------------------------

--
-- Table structure for table `enrollment`
--

CREATE TABLE `enrollment` (
  `SingerId` int(11) NOT NULL,
  `CompetitionId` char(7) NOT NULL,
  `Language` varchar(9) DEFAULT NULL,
  `Score` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `enrollment`
--

INSERT INTO `enrollment` (`SingerId`, `CompetitionId`, `Language`, `Score`) VALUES
(1, '4', NULL, 3),
(1, '5', NULL, 1),
(2, '5', NULL, 2);

-- --------------------------------------------------------

--
-- Table structure for table `singer`
--

CREATE TABLE `singer` (
  `SingerId` int(11) NOT NULL,
  `Singer_Name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `singer`
--

INSERT INTO `singer` (`SingerId`, `Singer_Name`) VALUES
(1, 'A'),
(2, 'B');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `competition`
--
ALTER TABLE `competition`
  ADD PRIMARY KEY (`CompetitionId`);

--
-- Indexes for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD PRIMARY KEY (`SingerId`,`CompetitionId`),
  ADD KEY `CompetitionId` (`CompetitionId`);

--
-- Indexes for table `singer`
--
ALTER TABLE `singer`
  ADD PRIMARY KEY (`SingerId`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `enrollment`
--
ALTER TABLE `enrollment`
  ADD CONSTRAINT `enrollment_ibfk_1` FOREIGN KEY (`SingerId`) REFERENCES `singer` (`SingerId`),
  ADD CONSTRAINT `enrollment_ibfk_2` FOREIGN KEY (`CompetitionId`) REFERENCES `competition` (`CompetitionId`);
--
-- Database: `test`
--
CREATE DATABASE IF NOT EXISTS `test` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `test`;

-- --------------------------------------------------------

--
-- Table structure for table `ad_student`
--

CREATE TABLE `ad_student` (
  `Student_id` varchar(10) NOT NULL,
  `Student_name` varchar(50) NOT NULL,
  `Study_program` varchar(20) NOT NULL,
  `GPA` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ad_student`
--

INSERT INTO `ad_student` (`Student_id`, `Student_name`, `Study_program`, `GPA`) VALUES
('20220328A', 'Cheung', 'IT', '3.83'),
('20221234A', 'Chan Tai Man', 'Business', '2.90'),
('20221235A', 'Wong Tai Sin', 'Business', '1.70'),
('20221236A', 'Tong Tai Chun', 'IT', '2.95'),
('20221237A', 'A', 'IT', '3.50'),
('20221238A', 'Law Bak Go', 'SDS', '3.50'),
('20221239A', 'C', 'Engineering', '3.82'),
('20221240A', 'D', 'SDS', '2.60'),
('20221241A', 'E', 'Engineering', '2.95'),
('20221242A', 'F', 'SDS', '3.50'),
('20221243A', 'G', 'IT', '1.86');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ad_student`
--
ALTER TABLE `ad_student`
  ADD PRIMARY KEY (`Student_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
