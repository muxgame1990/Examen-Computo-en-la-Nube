-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema computo_examen
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema computo_examen
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `computo_examen` DEFAULT CHARACTER SET utf8 ;
USE `computo_examen` ;

-- -----------------------------------------------------
-- Table `computo_examen`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computo_examen`.`usuario` (
  `usuario_id` INT(11) NOT NULL AUTO_INCREMENT,
  `password` VARCHAR(45) NOT NULL,
  `nombre_usuario` VARCHAR(80) NOT NULL,
  `rol` ENUM('admin', 'cliente') NOT NULL,
  `id_publico` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`usuario_id`),
  UNIQUE INDEX `uq_usuario_id_publico` (`id_publico` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `computo_examen`.`carrito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computo_examen`.`carrito` (
  `carrito_id` INT(11) NOT NULL AUTO_INCREMENT,
  `id_publico_cliente` VARCHAR(45) NOT NULL,
  `fecha_operacion` DATE NOT NULL,
  PRIMARY KEY (`carrito_id`),
  INDEX `id_publico_idx` (`id_publico_cliente` ASC) VISIBLE,
  CONSTRAINT `fk_carrito_usuario`
    FOREIGN KEY (`id_publico_cliente`)
    REFERENCES `computo_examen`.`usuario` (`id_publico`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `computo_examen`.`productos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computo_examen`.`productos` (
  `producto_id` INT(11) NOT NULL AUTO_INCREMENT,
  `nombreProducto` VARCHAR(45) NOT NULL,
  `precio` DOUBLE NOT NULL,
  `stock` INT(11) NOT NULL,
  `categoria` ENUM('perifericos', 'tarjetas de video', 'procesadores', 'memoria ram', 'accesorios') NOT NULL,
  `imagenProducto` VARCHAR(255) NOT NULL,
  `descripcion` VARCHAR(100) NOT NULL,
  `identificador` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`producto_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `computo_examen`.`carrito_producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computo_examen`.`carrito_producto` (
  `carrito_id` INT(11) NOT NULL,
  `producto_id` INT(11) NOT NULL,
  `cantidad` INT(11) NOT NULL,
  `precioUnitario` DOUBLE NOT NULL,
  PRIMARY KEY (`carrito_id`, `producto_id`),
  INDEX `ConexionProducto_idx` (`producto_id` ASC) VISIBLE,
  CONSTRAINT `fk_cp_carrito`
    FOREIGN KEY (`carrito_id`)
    REFERENCES `computo_examen`.`carrito` (`carrito_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_cp_producto`
    FOREIGN KEY (`producto_id`)
    REFERENCES `computo_examen`.`productos` (`producto_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
