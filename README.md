# Recomendador de pulseras con Naive Bayes

Este proyecto parte de AvanTI Shop, la tienda en línea que hicimos antes en el curso: catálogo, carrito, panel de
administración, base de datos en MySQL e imágenes en S3. No cambié nada de la tienda. La usé como base porque ya
tenía lo que necesitaba: usuarios con sesión, un administrador, conexión a la base de datos y almacenamiento local
o en S3. Sobre eso agregué una página nueva, `recomendador-bayes.xhtml`, y el modelo que la hace funcionar.

## El problema

Hay dos pulseras: la **i100**, sencilla, y la **i500**, más completa. La idea es recomendar una a partir de
cuatro preguntas: qué le interesa a la persona (su salud, su apariencia o ambas), cuánto ejercicio hace, qué tan
motivada está y si se siente cómoda con la tecnología. Para aprender solo hay 15 ejemplos de compradores
anteriores, cada uno con sus cuatro respuestas y la pulsera que eligió.

## Por qué Naive Bayes

Con tan pocos datos no tenía sentido algo pesado. Naive Bayes encaja bien con preguntas de opción múltiple, se
programa en unas cuantas líneas sin librerías y, sobre todo, se puede explicar. Además de decir qué pulsera
conviene, da una probabilidad para cada una y permite mostrar cuánto empujó cada respuesta hacia un lado o hacia
el otro.

## Cómo piensa el modelo

Entrenar es contar. Para cada pulsera se cuenta cuántos de sus compradores dieron cada respuesta: por ejemplo,
cuántos de los que eligieron la i500 dijeron que estaban muy motivados. Esa tabla de conteos es todo el modelo, y
la página la muestra tal cual en la sección «Lo que aprendió el modelo».

Para una persona nueva se empieza por qué tan común es cada pulsera (en los datos, 6 personas eligieron la i100 y
9 la i500). Luego se multiplica por qué tan común es cada una de sus respuestas entre los compradores de esa
pulsera. Al final se comparan los dos resultados y se ajustan para que sumen 100 %. Lo de "naive" viene de
suponer que las preguntas no dependen unas de otras, algo que no siempre es cierto pero simplifica mucho las
cuentas.

Hay un detalle: si una respuesta nunca apareció con una pulsera, su conteo es cero y toda la multiplicación se
vuelve cero. Para evitarlo uso suavizado de Laplace, que suma 1 a cada conteo.

En la página, después de la recomendación aparece una sección «¿Por qué?». Primero dice qué tan común era cada
pulsera antes de ver las respuestas, y luego, para cada respuesta, qué porcentaje de los compradores de cada
pulsera contestó igual y hacia cuál inclina la balanza.

## Qué tan bien funciona

Para medirlo usé validación cruzada "uno fuera": se entrena con 14 datos, se le pregunta por el que quedó fuera y
se repite con cada uno de los 15. Acierta 7 de 15, cerca de 47 %. Es un resultado modesto, y el problema no es
el algoritmo sino la cantidad de datos: con 15 ejemplos y 36 combinaciones posibles de respuestas, la mayoría de
las personas contestan algo que el modelo nunca vio completo. Con más datos debería mejorar, y el sistema ya
está pensado para eso.

## Cómo se conecta con la tienda y con la nube

Los datos de entrenamiento viven en un archivo CSV y el modelo entrenado en un JSON. Los dos se guardan en una
carpeta local mientras desarrollo y en S3 cuando la aplicación corre en AWS; se cambia con la variable
`MODELO_ALMACENAMIENTO`. La primera vez que arranca, si el CSV todavía no existe, la aplicación copia el que viene
en el proyecto, entrena el modelo y lo guarda. Si después se agregan datos al CSV, el administrador puede pulsar
«Reentrenar modelo» desde la página.

Cada vez que alguien pide una recomendación, sus respuestas y el resultado se guardan en la tabla
`consulta_bayes` de MySQL (RDS en AWS), ligadas a su usuario si inició sesión. Con eso cada usuario ve su
historial, y el administrador ve las últimas consultas de todos. La tabla se crea con `db/naive_bayes.sql`.

Así cada parte de la nube tiene su papel: EC2 corre la aplicación y el modelo, RDS guarda lo que contestan los
usuarios y S3 guarda los datos de entrenamiento y el modelo.

## Dónde está el código

- `src/main/java/mx/NovaCore/ia/NaiveBayes.java`: el modelo (entrenar, predecir y validar).
- `src/main/java/mx/NovaCore/servicio/RecomendadorBayesServicio.java`: une el modelo con el almacenamiento y la base de datos.
- `src/main/java/mx/NovaCore/vista/Frijol/RecomendadorBayesBean.java`: prepara lo que muestra la página.
- `src/main/resources/META-INF/resources/recomendador-bayes.xhtml`: la página.
