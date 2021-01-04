-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 05, 2021 at 12:03 AM
-- Server version: 10.4.11-MariaDB
-- PHP Version: 7.4.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gdp`
--

-- --------------------------------------------------------

--
-- Table structure for table `buy`
--

CREATE TABLE `buy` (
  `buy_id` int(11) NOT NULL,
  `buy_qte` int(11) NOT NULL,
  `buy_unit_price` int(11) NOT NULL,
  `buy_price` int(11) NOT NULL,
  `buy_date` datetime NOT NULL DEFAULT current_timestamp(),
  `user_id` int(11) NOT NULL,
  `prod_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `client_id` int(11) NOT NULL,
  `fullname` varchar(50) CHARACTER SET utf8 NOT NULL,
  `tel` varchar(50) CHARACTER SET utf8 NOT NULL,
  `rc` varchar(50) CHARACTER SET utf8 NOT NULL,
  `nif` varchar(50) CHARACTER SET utf8 NOT NULL,
  `ai` varchar(50) CHARACTER SET utf8 NOT NULL,
  `image` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `paid` int(11) NOT NULL DEFAULT 0,
  `remain` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `pay_id` int(11) NOT NULL,
  `pay_sum` int(11) NOT NULL,
  `pay_date` date NOT NULL,
  `client_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `privs`
--

CREATE TABLE `privs` (
  `user_id` int(11) NOT NULL,
  `manage_products` int(1) NOT NULL DEFAULT 0,
  `manage_users` int(1) NOT NULL DEFAULT 0,
  `manage_buys` int(1) NOT NULL DEFAULT 0,
  `manage_sells` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `privs`
--

INSERT INTO `privs` (`user_id`, `manage_products`, `manage_users`, `manage_buys`, `manage_sells`) VALUES
(1, 1, 1, 1, 1),
(5, 1, 0, 1, 1),
(14, 1, 1, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `prod_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `sell_price` int(11) NOT NULL,
  `prod_quantity` int(11) NOT NULL DEFAULT 0,
  `add_date` date NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `nbrSells` int(11) NOT NULL DEFAULT 0,
  `nbrBuys` int(11) NOT NULL DEFAULT 0,
  `on_hold` int(1) NOT NULL DEFAULT 0,
  `last_change` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `product_history`
--

CREATE TABLE `product_history` (
  `prod_hist_id` int(11) NOT NULL,
  `prod_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `change_date` datetime NOT NULL,
  `new_name` varchar(50) DEFAULT NULL,
  `new_date` date DEFAULT NULL,
  `new_price` int(11) DEFAULT NULL,
  `new_qte` int(11) DEFAULT NULL,
  `old_name` varchar(50) DEFAULT NULL,
  `old_date` date DEFAULT NULL,
  `old_price` int(11) DEFAULT NULL,
  `old_qte` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `product_stats`
--

CREATE TABLE `product_stats` (
  `prod_id` int(11) NOT NULL,
  `total_bought` int(11) NOT NULL DEFAULT 0,
  `total_solde` int(11) NOT NULL DEFAULT 0,
  `qte_bought` int(11) NOT NULL DEFAULT 0,
  `qte_sold` int(11) NOT NULL DEFAULT 0,
  `capital` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sell`
--

CREATE TABLE `sell` (
  `sell_id` int(11) NOT NULL,
  `sell_price_unit` int(11) NOT NULL,
  `sell_price` int(11) NOT NULL,
  `sell_quantity` int(11) NOT NULL,
  `sell_date` datetime NOT NULL,
  `prod_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `client_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE `settings` (
  `setting_id` int(11) NOT NULL,
  `setting_name` varchar(50) NOT NULL,
  `setting_value` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`setting_id`, `setting_name`, `setting_value`) VALUES
(3, 'animations', 'true'),
(4, 'app_name', 'Inventory'),
(5, 'app_language', 'en_DZ'),
(6, 'min_qte', '10');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `fullname` varchar(80) NOT NULL,
  `telephone` varchar(20) NOT NULL,
  `admin` int(1) NOT NULL DEFAULT 0,
  `active` int(1) NOT NULL DEFAULT 1,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `last_logged_in` datetime DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `fullname`, `telephone`, `admin`, `active`, `username`, `password`, `last_logged_in`, `image`) VALUES
(1, 'admin account', '0553538880', 1, 1, 'admin', 'password', '2021-01-04 23:53:18', 'C:/gdp-uploads/2020-10-26-06-25-22.png'),
(5, 'ada meceffeuk', '0553792748', 0, 1, 'averroes96', 'password', '2020-09-12 19:41:59', 'C:/gdp-uploads/2020-3-14-10-52-17.jpg'),
(14, 'illuminati', '0666666666', 0, 1, 'illuminati', 'illuminati', NULL, 'C:/gdp-uploads/2020-10-15-08-30-29.png');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `buy`
--
ALTER TABLE `buy`
  ADD PRIMARY KEY (`buy_id`),
  ADD KEY `fk_buyProd` (`prod_id`),
  ADD KEY `fk_buyuser` (`user_id`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`client_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`pay_id`);

--
-- Indexes for table `privs`
--
ALTER TABLE `privs`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`prod_id`),
  ADD UNIQUE KEY `product_name` (`name`);

--
-- Indexes for table `product_history`
--
ALTER TABLE `product_history`
  ADD PRIMARY KEY (`prod_hist_id`),
  ADD KEY `fk_prod_hist_user` (`user_id`),
  ADD KEY `fk_prod_hist_prod` (`prod_id`);

--
-- Indexes for table `product_stats`
--
ALTER TABLE `product_stats`
  ADD PRIMARY KEY (`prod_id`);

--
-- Indexes for table `sell`
--
ALTER TABLE `sell`
  ADD PRIMARY KEY (`sell_id`),
  ADD KEY `fk_sellprod` (`prod_id`),
  ADD KEY `fk_selluser` (`user_id`);

--
-- Indexes for table `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`setting_id`),
  ADD UNIQUE KEY `setting_name` (`setting_name`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `buy`
--
ALTER TABLE `buy`
  MODIFY `buy_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `client_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `pay_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `prod_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `product_history`
--
ALTER TABLE `product_history`
  MODIFY `prod_hist_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sell`
--
ALTER TABLE `sell`
  MODIFY `sell_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `settings`
--
ALTER TABLE `settings`
  MODIFY `setting_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `buy`
--
ALTER TABLE `buy`
  ADD CONSTRAINT `fk_buyProd` FOREIGN KEY (`prod_id`) REFERENCES `product` (`prod_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_buyuser` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `privs`
--
ALTER TABLE `privs`
  ADD CONSTRAINT `fk_user_privs` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `product_history`
--
ALTER TABLE `product_history`
  ADD CONSTRAINT `fk_prod_hist_prod` FOREIGN KEY (`prod_id`) REFERENCES `product` (`prod_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_prod_hist_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `product_stats`
--
ALTER TABLE `product_stats`
  ADD CONSTRAINT `fk_prod_stats` FOREIGN KEY (`prod_id`) REFERENCES `product` (`prod_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `sell`
--
ALTER TABLE `sell`
  ADD CONSTRAINT `fk_sell_client` FOREIGN KEY (`user_id`) REFERENCES `client` (`client_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  ADD CONSTRAINT `fk_sellprod` FOREIGN KEY (`prod_id`) REFERENCES `product` (`prod_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  ADD CONSTRAINT `fk_selluser` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
