-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
<<<<<<< HEAD
-- Tiempo de generación: 01-10-2019 a las 04:43:04
-- Versión del servidor: 10.1.37-MariaDB
-- Versión de PHP: 7.3.1
=======
-- Tiempo de generación: 01-10-2019 a las 03:45:49
-- Versión del servidor: 10.1.38-MariaDB
-- Versión de PHP: 7.2.15
>>>>>>> ac7e3a2f5de08a786c38b8226933bf64df35739b

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `boletazo`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `apartados`
--

CREATE TABLE `apartados` (
  `idApartado` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `idEvento` int(11) NOT NULL,
  `pagado` double NOT NULL,
  `tiempo` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asientos`
--

CREATE TABLE `asientos` (
  `idAsiento` int(11) NOT NULL,
  `idZona` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `eventos`
--

CREATE TABLE `eventos` (
  `idEvento` int(11) NOT NULL,
  `nombre` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `lugar` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `hora` time NOT NULL,
<<<<<<< HEAD
  `idLugar` int(11) NOT NULL,
  `fecha` date NOT NULL
=======
  `fecha` date NOT NULL,
  `idLugar` int(11) DEFAULT NULL
>>>>>>> ac7e3a2f5de08a786c38b8226933bf64df35739b
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `eventos`
--

<<<<<<< HEAD
INSERT INTO `eventos` (`idEvento`, `nombre`, `lugar`, `hora`, `idLugar`, `fecha`) VALUES
(1, 'Daft Punk', 'Estadio Azteca', '22:00:00', 0, '0000-00-00'),
(2, 'José José', 'Estadio La Corregidora', '17:00:00', 0, '0000-00-00');
=======
INSERT INTO `eventos` (`idEvento`, `nombre`, `lugar`, `hora`, `fecha`, `idLugar`) VALUES
(1, 'Daft Punk', 'Estadio Azteca', '22:00:00', '2019-10-16', NULL),
(2, 'José José', 'Estadio La Corregidora', '17:00:00', '2019-10-31', NULL);
>>>>>>> ac7e3a2f5de08a786c38b8226933bf64df35739b

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `eventosasientos`
--

CREATE TABLE `eventosasientos` (
  `idEvento` int(11) NOT NULL,
  `idAsiento` int(11) NOT NULL,
  `idApartado` int(11) NOT NULL,
  `idZona` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `eventoszonas`
--

CREATE TABLE `eventoszonas` (
  `idEvento` int(11) NOT NULL,
  `idZona` int(11) NOT NULL,
  `precio` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lugar`
--

CREATE TABLE `lugar` (
  `idLugar` int(11) NOT NULL,
  `nombre` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
<<<<<<< HEAD
  `estado` varchar(100) COLLATE utf8_spanish_ci NOT NULL
=======
  `estado` varchar(45) COLLATE utf8_spanish_ci NOT NULL
>>>>>>> ac7e3a2f5de08a786c38b8226933bf64df35739b
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `metodospago`
--

CREATE TABLE `metodospago` (
  `idMetodoPago` int(11) NOT NULL,
  `idUsuario` int(11) NOT NULL,
  `numeroTarjeta` varchar(16) COLLATE utf8_spanish_ci NOT NULL,
  `domicilio` varchar(100) COLLATE utf8_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `idUsuario` int(11) NOT NULL,
  `nombre` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `direccion` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `telefono` varchar(12) COLLATE utf8_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `zona`
--

CREATE TABLE `zona` (
  `idZona` int(11) NOT NULL,
  `idLugar` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `apartados`
--
ALTER TABLE `apartados`
  ADD PRIMARY KEY (`idApartado`),
  ADD KEY `idUsuario` (`idUsuario`),
  ADD KEY `idEvento` (`idEvento`);

--
-- Indices de la tabla `asientos`
--
ALTER TABLE `asientos`
  ADD PRIMARY KEY (`idAsiento`),
  ADD KEY `idZona` (`idZona`);

--
-- Indices de la tabla `eventos`
--
ALTER TABLE `eventos`
  ADD PRIMARY KEY (`idEvento`),
<<<<<<< HEAD
  ADD KEY `idLugar` (`idLugar`);
=======
  ADD KEY `fk_eventos_lugar` (`idLugar`);
>>>>>>> ac7e3a2f5de08a786c38b8226933bf64df35739b

--
-- Indices de la tabla `eventosasientos`
--
ALTER TABLE `eventosasientos`
  ADD PRIMARY KEY (`idEvento`,`idAsiento`,`idZona`),
  ADD KEY `idApartado` (`idApartado`),
  ADD KEY `idAsiento` (`idAsiento`),
  ADD KEY `idZona` (`idZona`);

--
-- Indices de la tabla `eventoszonas`
--
ALTER TABLE `eventoszonas`
  ADD PRIMARY KEY (`idEvento`,`idZona`),
  ADD KEY `idZona` (`idZona`);

--
-- Indices de la tabla `lugar`
--
ALTER TABLE `lugar`
  ADD PRIMARY KEY (`idLugar`);

--
-- Indices de la tabla `metodospago`
--
ALTER TABLE `metodospago`
  ADD PRIMARY KEY (`idMetodoPago`),
  ADD KEY `idUsuario` (`idUsuario`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`idUsuario`);

--
-- Indices de la tabla `zona`
--
ALTER TABLE `zona`
  ADD PRIMARY KEY (`idZona`),
  ADD KEY `idLugar` (`idLugar`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `apartados`
--
ALTER TABLE `apartados`
  MODIFY `idApartado` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `asientos`
--
ALTER TABLE `asientos`
  MODIFY `idAsiento` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `eventos`
--
ALTER TABLE `eventos`
  MODIFY `idEvento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `zona`
--
ALTER TABLE `zona`
  MODIFY `idZona` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `apartados`
--
ALTER TABLE `apartados`
  ADD CONSTRAINT `apartados_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`idUsuario`),
  ADD CONSTRAINT `apartados_ibfk_2` FOREIGN KEY (`idEvento`) REFERENCES `eventos` (`idEvento`);

--
-- Filtros para la tabla `asientos`
--
ALTER TABLE `asientos`
  ADD CONSTRAINT `asientos_ibfk_1` FOREIGN KEY (`idZona`) REFERENCES `zona` (`idZona`);

--
-- Filtros para la tabla `eventos`
--
ALTER TABLE `eventos`
<<<<<<< HEAD
  ADD CONSTRAINT `eventos_ibfk_1` FOREIGN KEY (`idLugar`) REFERENCES `lugar` (`idLugar`);
=======
  ADD CONSTRAINT `fk_eventos_lugar` FOREIGN KEY (`idLugar`) REFERENCES `lugar` (`idLugar`);
>>>>>>> ac7e3a2f5de08a786c38b8226933bf64df35739b

--
-- Filtros para la tabla `eventosasientos`
--
ALTER TABLE `eventosasientos`
  ADD CONSTRAINT `eventosasientos_ibfk_1` FOREIGN KEY (`idEvento`) REFERENCES `eventos` (`idEvento`),
  ADD CONSTRAINT `eventosasientos_ibfk_2` FOREIGN KEY (`idAsiento`) REFERENCES `asientos` (`idAsiento`),
  ADD CONSTRAINT `eventosasientos_ibfk_3` FOREIGN KEY (`idApartado`) REFERENCES `apartados` (`idApartado`),
  ADD CONSTRAINT `eventosasientos_ibfk_4` FOREIGN KEY (`idZona`) REFERENCES `zona` (`idZona`);

--
-- Filtros para la tabla `eventoszonas`
--
ALTER TABLE `eventoszonas`
  ADD CONSTRAINT `eventoszonas_ibfk_1` FOREIGN KEY (`idEvento`) REFERENCES `eventos` (`idEvento`),
  ADD CONSTRAINT `eventoszonas_ibfk_2` FOREIGN KEY (`idZona`) REFERENCES `zona` (`idZona`);

--
-- Filtros para la tabla `metodospago`
--
ALTER TABLE `metodospago`
  ADD CONSTRAINT `metodospago_ibfk_1` FOREIGN KEY (`idUsuario`) REFERENCES `usuarios` (`idUsuario`);

--
-- Filtros para la tabla `zona`
--
ALTER TABLE `zona`
  ADD CONSTRAINT `zona_ibfk_1` FOREIGN KEY (`idLugar`) REFERENCES `lugar` (`idLugar`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
