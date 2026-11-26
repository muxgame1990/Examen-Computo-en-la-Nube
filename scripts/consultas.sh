#!/usr/bin/env bash
set -euo pipefail

DB_HOST=${DB_HOST:-127.0.0.1}
DB_PORT=${DB_PORT:-3306}
DB_USER=${DB_USER:-root}
DB_NAME=${DB_NAME:-computo_examen}

SCRIPT="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"
COMANDOS="ultimas resumen respuestas usuarios hoy tablas todo sql shell"

ayuda() {
    cat <<EOF
Uso: $(basename "$0") [comando] [opciones]

  (sin comando)   Abrir el menú
  ultimas [n]     Las últimas n recomendaciones (10 por defecto)
  resumen         Cuántas veces se recomendó cada pulsera y con qué seguridad
  respuestas      Qué contestó la gente en cada pregunta
  usuarios        Cuántas consultas hizo cada usuario
  hoy             Las consultas de hoy
  tablas          Cuántos registros tiene cada tabla de la base
  todo            resumen + respuestas + últimas 5
  sql "<query>"   Correr una consulta propia
  shell           Abrir una consola interactiva (mycli si está instalado)
EOF
}

titulo() {
    echo
    echo "== $1 =="
}

consulta() {
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -D "$DB_NAME" --table -e "$1"
}

ultimas() {
    titulo "Últimas $1 recomendaciones"
    consulta "
        SELECT consulta_id AS id,
               DATE_FORMAT(fecha_hora, '%d/%m/%Y %H:%i') AS fecha,
               COALESCE(id_publico_cliente, '(sin sesión)') AS usuario,
               interes, ejercicio, motivacion, tecnologia,
               recomendado AS pulsera,
               CONCAT(ROUND(GREATEST(prob_i100, prob_i500) * 100, 1), ' %') AS seguridad
        FROM consulta_bayes
        ORDER BY consulta_id DESC
        LIMIT $1;"
}

resumen() {
    titulo "Resumen por pulsera"
    consulta "
        SELECT recomendado AS pulsera,
               COUNT(*) AS veces,
               CONCAT(ROUND(COUNT(*) * 100 / (SELECT COUNT(*) FROM consulta_bayes), 1), ' %') AS del_total,
               CONCAT(ROUND(AVG(GREATEST(prob_i100, prob_i500)) * 100, 1), ' %') AS seguridad_promedio
        FROM consulta_bayes
        GROUP BY recomendado
        ORDER BY recomendado;"
}

respuestas() {
    titulo "Respuestas por pregunta"
    consulta "
        SELECT 'interes' AS pregunta, interes AS respuesta, COUNT(*) AS veces,
               SUM(recomendado = 'i100') AS i100, SUM(recomendado = 'i500') AS i500
        FROM consulta_bayes GROUP BY interes
        UNION ALL
        SELECT 'ejercicio', ejercicio, COUNT(*), SUM(recomendado = 'i100'), SUM(recomendado = 'i500')
        FROM consulta_bayes GROUP BY ejercicio
        UNION ALL
        SELECT 'motivacion', motivacion, COUNT(*), SUM(recomendado = 'i100'), SUM(recomendado = 'i500')
        FROM consulta_bayes GROUP BY motivacion
        UNION ALL
        SELECT 'tecnologia', tecnologia, COUNT(*), SUM(recomendado = 'i100'), SUM(recomendado = 'i500')
        FROM consulta_bayes GROUP BY tecnologia;"
}

usuarios() {
    titulo "Consultas por usuario"
    consulta "
        SELECT COALESCE(c.id_publico_cliente, '(sin sesión)') AS usuario,
               u.nombre_usuario AS nombre,
               COUNT(*) AS consultas,
               SUM(c.recomendado = 'i100') AS i100,
               SUM(c.recomendado = 'i500') AS i500,
               DATE_FORMAT(MAX(c.fecha_hora), '%d/%m/%Y %H:%i') AS ultima
        FROM consulta_bayes c
        LEFT JOIN usuario u ON u.id_publico = c.id_publico_cliente
        GROUP BY c.id_publico_cliente, u.nombre_usuario
        ORDER BY consultas DESC;"
}

hoy() {
    titulo "Consultas de hoy"
    consulta "
        SELECT consulta_id AS id,
               DATE_FORMAT(fecha_hora, '%H:%i:%s') AS hora,
               COALESCE(id_publico_cliente, '(sin sesión)') AS usuario,
               interes, ejercicio, motivacion, tecnologia,
               recomendado AS pulsera
        FROM consulta_bayes
        WHERE DATE(fecha_hora) = CURDATE()
        ORDER BY consulta_id DESC;"
}

tablas() {
    titulo "Registros por tabla en $DB_NAME"
    consulta "
        SELECT 'usuario' AS tabla, COUNT(*) AS registros FROM usuario
        UNION ALL SELECT 'productos', COUNT(*) FROM productos
        UNION ALL SELECT 'carrito', COUNT(*) FROM carrito
        UNION ALL SELECT 'carrito_producto', COUNT(*) FROM carrito_producto
        UNION ALL SELECT 'consulta_bayes', COUNT(*) FROM consulta_bayes;"
}

shell() {
    if command -v mycli > /dev/null; then
        mycli -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -D "$DB_NAME" \
            --password-file <(printf '%s' "$MYSQL_PWD")
    else
        mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -D "$DB_NAME"
    fi
}

correr() {
    case "$1" in
        ultimas)    ultimas "${2:-10}" ;;
        resumen)    resumen ;;
        respuestas) respuestas ;;
        usuarios)   usuarios ;;
        hoy)        hoy ;;
        tablas)     tablas ;;
        todo)       resumen; respuestas; ultimas 5 ;;
        sql)        consulta "${2:?Falta la consulta entre comillas}" ;;
        shell)      shell ;;
    esac
}

ver() {
    less -S -P'q = volver al menú'
}

menu() {
    if ! command -v fzf > /dev/null; then
        echo "No se encontró fzf. Usa un comando directo:" >&2
        ayuda
        exit 1
    fi

    local opciones=(
        "todo        Resumen, respuestas y últimas 5"
        "resumen     Cuántas veces se recomendó cada pulsera"
        "respuestas  Qué contestó la gente en cada pregunta"
        "ultimas     Las últimas recomendaciones"
        "usuarios    Consultas por usuario"
        "hoy         Las consultas de hoy"
        "tablas      Registros por tabla"
        "sql         Escribir una consulta propia"
        "shell       Abrir consola interactiva"
        "salir       Cerrar el menú"
    )
    local opcion comando n query

    while true; do
        opcion=$(printf '%s\n' "${opciones[@]}" | fzf \
            --reverse \
            --no-sort \
            --header="Recomendador Naive Bayes · $DB_USER@$DB_HOST · Enter abre, Esc sale" \
            --prompt="Consulta > " \
            --preview="'$SCRIPT' --vista {1}" \
            --preview-window="down,70%,nowrap") || break

        comando=${opcion%% *}

        case "$comando" in
            salir)
                break
                ;;
            shell)
                shell || true
                ;;
            ultimas)
                read -r -p "¿Cuántas? [10]: " n
                ultimas "${n:-10}" 2>&1 | ver
                ;;
            sql)
                read -r -e -p "SQL> " query
                if [[ -n "$query" ]]; then
                    consulta "$query" 2>&1 | ver
                fi
                ;;
            *)
                correr "$comando" 2>&1 | ver
                ;;
        esac
    done
}

if [[ "${1:-}" == "--vista" ]]; then
    case "$2" in
        sql)   echo "Enter para escribir una consulta propia." ;;
        shell) echo "Enter para abrir una consola interactiva (mycli si está instalado)." ;;
        salir) echo "Cerrar el menú." ;;
        *)     correr "$2" 2>&1 || true ;;
    esac
    exit 0
fi

comando=${1:-menu}
if [[ "$comando" != "menu" && " $COMANDOS " != *" $comando "* ]]; then
    ayuda
    exit 0
fi

if [[ -z "${MYSQL_PWD:-}" ]]; then
    read -r -s -p "Contraseña de $DB_USER@$DB_HOST: " MYSQL_PWD
    echo
fi
export MYSQL_PWD

if [[ "$comando" == "menu" ]]; then
    if ! consulta "SELECT 1;" > /dev/null 2>&1; then
        echo "No se pudo conectar a $DB_HOST:$DB_PORT. Revisa la contraseña o la conexión." >&2
        exit 1
    fi
    menu
else
    correr "$@"
fi
