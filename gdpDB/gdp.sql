-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 12, 2020 at 10:32 PM
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
  `buy_date` date NOT NULL,
  `user_id` int(11) NOT NULL,
  `prod_id` int(11) NOT NULL
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
  `on_hold` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `product_stats`
--

CREATE TABLE `product_stats` (
  `prod_id` int(11) NOT NULL,
  `buy_moy_price` int(11) NOT NULL DEFAULT 0,
  `sell_moy_price` int(11) NOT NULL DEFAULT 0,
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
  `sell_date` date NOT NULL,
  `prod_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
(1, 'admin admin', '0553538880', 1, 1, 'admin', 'admin', '2020-02-12 22:15:06', NULL);
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
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `prod_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sell`
--
ALTER TABLE `sell`
  MODIFY `sell_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

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
-- Constraints for table `product_stats`
--
ALTER TABLE `product_stats`
  ADD CONSTRAINT `fk_prod_stats` FOREIGN KEY (`prod_id`) REFERENCES `product` (`prod_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `sell`
--
ALTER TABLE `sell`
  ADD CONSTRAINT `fk_sellprod` FOREIGN KEY (`prod_id`) REFERENCES `product` (`prod_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  ADD CONSTRAINT `fk_selluser` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
