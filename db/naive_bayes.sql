-- -----------------------------------------------------
-- Recomendador de pulsera (Naive Bayes)
-- Guarda cada respuesta del formulario. Correr después de NOBORRAR.sql.
-- Se puede ejecutar más de una vez sin borrar datos.
-- -----------------------------------------------------
USE `computo_examen`;

CREATE TABLE IF NOT EXISTS `consulta_bayes` (
  `consulta_id` INT NOT NULL AUTO_INCREMENT,
  `id_publico_cliente` VARCHAR(45) NULL,
  `interes` VARCHAR(20) NOT NULL,
  `ejercicio` VARCHAR(20) NOT NULL,
  `motivacion` VARCHAR(20) NOT NULL,
  `tecnologia` VARCHAR(5) NOT NULL,
  `recomendado` VARCHAR(10) NOT NULL,
  `prob_i100` DOUBLE NOT NULL,
  `prob_i500` DOUBLE NOT NULL,
  `fecha_hora` DATETIME NOT NULL,
  PRIMARY KEY (`consulta_id`),
  INDEX `idx_consulta_bayes_cliente` (`id_publico_cliente` ASC),
  CONSTRAINT `fk_consulta_bayes_usuario`
    FOREIGN KEY (`id_publico_cliente`)
    REFERENCES `usuario` (`id_publico`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
