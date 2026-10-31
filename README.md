# AvanTI Shop (NovaCore)

Tienda en línea de componentes de computadora. Tiene dos partes:

- **Tienda para clientes:** catálogo por categorías con búsqueda, detalle de cada producto, carrito de compras y confirmación de compra.
- **Panel de administración:** alta, edición, búsqueda y eliminación de productos, y control de entradas y salidas de inventario.

Cada compra se guarda como una orden en la base de datos y descuenta el stock de los productos al momento. Si no hay piezas suficientes, la compra se cancela completa y no se guarda nada.

---

## Tecnologías

| Parte | Herramienta |
|---|---|
| Lenguaje | Java 21 |
| Servidor | Spring Boot 3.5 |
| Vistas | JSF (JoinFaces) con PrimeFaces 14 |
| Base de datos | MySQL 8 |
| Nube | AWS: EC2 (aplicación), RDS (base de datos) y S3 (imágenes) |

---

## Estructura del proyecto

```
Examen-Computo-en-la-Nube/
├── db/
│   ├── NOBORRAR.sql            Script que crea la base de datos y sus tablas
│   └── productos_prueba.sql    Productos de ejemplo
├── src/main/java/mx/NovaCore/
│   ├── controlador/            Página de inicio
│   ├── modelos/                Tablas de la base de datos representadas en Java
│   ├── repositorio/            Consultas a la base de datos
│   ├── servicio/               Reglas del negocio (compras, stock, imágenes, usuarios)
│   ├── vista/Frijol/           Lógica de cada pantalla
│   └── web/                    Entrega de imágenes de productos
├── src/main/resources/
│   ├── META-INF/resources/     Pantallas (.xhtml) y estilos (css)
│   └── application.properties  Configuración de la aplicación
└── pom.xml                     Librerías del proyecto
```

---

## Base de datos

| Tabla | Qué guarda |
|---|---|
| `usuario` | Clientes y administradores, con su usuario, contraseña y rol |
| `productos` | Nombre, descripción, precio, stock, categoría, imagen e identificador |
| `carrito` | Cada orden de compra: quién compró y cuándo |
| `carrito_producto` | Los productos de cada orden, con cantidad y precio al momento de la compra |
| `movimiento_inventario` | Historial de entradas y salidas de stock |

---

## Instalación en una computadora (local)

### 1. Requisitos

Instala lo siguiente:

- **Java 21** (JDK)
- **Maven** (o un editor que ya lo incluya, como IntelliJ IDEA)
- **MySQL 8**
- **MySQL Workbench** (opcional, para correr los scripts de forma visual)
- **Git**

### 2. Descargar el proyecto

```bash
git clone <url-del-repositorio>
cd Examen-Computo-en-la-Nube
```

### 3. Crear la base de datos

Abre MySQL Workbench (o la terminal de MySQL) y ejecuta los scripts en este orden:

1. `db/NOBORRAR.sql`: crea la base `computo_examen` con todas sus tablas.
2. `db/productos_prueba.sql`: agrega productos de ejemplo (opcional).

Después crea el usuario administrador:

```sql
USE computo_examen;

INSERT INTO usuario (password, nombre_usuario, rol, id_publico)
VALUES ('admin123', 'Administrador', 'admin', 'admin');
```

Las cuentas de cliente se crean desde la propia aplicación, con el botón de registro en la pantalla de inicio de sesión.

### 4. Conectar la aplicación con tu base de datos

Abre `src/main/resources/application.properties` y cambia estas tres líneas con los datos de tu MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/computo_examen?serverTimezone=America/Tijuana&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=tu_contraseña
```

Si tu MySQL usa otro puerto (por ejemplo `3307`), cámbialo en la primera línea.

### 5. Ejecutar

**Con IntelliJ IDEA:** abre el proyecto, espera a que descargue las librerías y ejecuta la clase `NovaCore` (`src/main/java/mx/NovaCore/NovaCore.java`).

**Desde la terminal:**

```bash
mvn -DskipTests package
java -jar target/NovaCore-1.0.0.jar
```

Espera a que aparezca el mensaje `Started NovaCore`.

### 6. Abrir la tienda

Entra en el navegador a:

```
http://localhost:8080
```

| Usuario | Contraseña | Entra a |
|---|---|---|
| `admin` | `admin123` | Panel de administración |
| Cualquier cuenta registrada | La que elegiste | Tienda |

En modo local, las imágenes que subas desde el panel se guardan en tu computadora, en la carpeta `avanti-shop/imagenes` dentro de tu carpeta de usuario.

---

## Despliegue en AWS

La aplicación en la nube usa tres servicios:

| Servicio | Uso |
|---|---|
| **EC2** | Servidor donde corre la aplicación |
| **RDS (MySQL)** | Base de datos |
| **S3** | Almacenamiento de las imágenes de los productos |

Las imágenes se guardan en S3 de forma privada. La aplicación las lee con su propio permiso y se las muestra al navegador, así que el bucket no necesita ser público.

### 1. Base de datos en RDS

1. Crea una base de datos **MySQL 8** en RDS y conéctala a la instancia EC2 desde la opción de conexión de la consola.
2. Anota el **endpoint**, el **usuario** y la **contraseña**.
3. Desde la EC2, instala el cliente de MySQL y carga los scripts:

```bash
sudo apt update
sudo apt install -y mysql-client
mysql -h <endpoint> -u <usuario> -p < db/NOBORRAR.sql
mysql -h <endpoint> -u <usuario> -p < db/productos_prueba.sql
```

4. Crea el usuario administrador con el mismo `INSERT` del paso 3 de la instalación local.

### 2. Bucket de S3

1. Crea un bucket en la misma región que la EC2 (por ejemplo `us-east-2`).
2. Dentro del bucket, crea la carpeta `productos`.

### 3. Permisos de la EC2 para usar S3

1. En IAM, crea un rol para EC2 con esta política (cambia `<bucket>` por el nombre de tu bucket):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket"],
      "Resource": "arn:aws:s3:::<bucket>"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
      "Resource": "arn:aws:s3:::<bucket>/*"
    }
  ]
}
```

2. Asigna el rol a la instancia: **EC2 → tu instancia → Acciones → Seguridad → Modificar rol de IAM**.

Con esto la aplicación puede subir y leer imágenes sin escribir contraseñas en el código.

### 4. Preparar la EC2

1. En el grupo de seguridad de la instancia, agrega una regla de entrada **TCP, puerto 8080, origen 0.0.0.0/0** para poder abrir la tienda desde el navegador.
2. Conéctate a la instancia e instala Java, Maven y Git:

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk maven git
```

3. Si la instancia tiene poca memoria (2 GB o menos), crea un espacio de intercambio de 2 GB para que la compilación no se detenga:

```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 5. Descargar y compilar

```bash
git clone <url-del-repositorio>
cd Examen-Computo-en-la-Nube
export MAVEN_OPTS="-Xmx512m"
mvn -DskipTests package
```

Al terminar debe aparecer `BUILD SUCCESS`.

### 6. Ejecutar con la configuración de la nube

La configuración de la nube se da con variables de entorno, sin modificar el código:

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://<endpoint>:3306/computo_examen?serverTimezone=America/Tijuana&characterEncoding=UTF-8"
export SPRING_DATASOURCE_USERNAME=<usuario>
export SPRING_DATASOURCE_PASSWORD='<contraseña>'
export IMAGENES_ALMACENAMIENTO=s3
export S3_BUCKET=<bucket>
export AWS_REGION=us-east-2

java -Xmx768m -jar target/NovaCore-1.0.0.jar
```

| Variable | Para qué sirve |
|---|---|
| `SPRING_DATASOURCE_URL` | Dirección de la base de datos en RDS |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos |
| `IMAGENES_ALMACENAMIENTO` | `s3` para guardar imágenes en S3, `local` para guardarlas en el servidor |
| `S3_BUCKET` | Nombre del bucket de S3 |
| `AWS_REGION` | Región donde está el bucket |

Para que la aplicación siga funcionando al cerrar la terminal, ejecútala así:

```bash
nohup java -Xmx768m -jar target/NovaCore-1.0.0.jar > app.log 2>&1 &
```

Para ver sus mensajes: `tail -f app.log`. Para detenerla: `pkill java`.

### 7. Abrir la tienda

```
http://<ip-publica-de-la-ec2>:8080
```

La IP pública aparece en la consola de EC2, en los detalles de la instancia.

### Actualizar la aplicación

Cada vez que se suban cambios al repositorio:

```bash
pkill java
cd Examen-Computo-en-la-Nube
git pull
mvn -DskipTests package
```

Después vuelve a ejecutarla como en el paso 6.

---

## Problemas comunes

| Mensaje o síntoma | Solución |
|---|---|
| `Port 8080 was already in use` | La aplicación ya estaba corriendo. Detenla con `pkill java` y vuelve a ejecutarla |
| `Communications link failure` | La aplicación no llega a la base de datos. Revisa el endpoint y que la RDS esté disponible |
| `Access denied for user` | Usuario o contraseña de la base de datos incorrectos |
| `Public Key Retrieval is not allowed` | Agrega `&allowPublicKeyRetrieval=true` al final de `SPRING_DATASOURCE_URL` |
| `Falta la variable de entorno S3_BUCKET` | Se activó S3 sin indicar el bucket. Define `S3_BUCKET` |
| `No se pudo subir la imagen a S3` | La EC2 no tiene el rol de IAM asignado o la política no incluye el bucket |
| La página no abre desde el navegador | Falta la regla del puerto 8080 en el grupo de seguridad, o se está usando `https` en lugar de `http` |
| La compilación se detiene sin terminar | Falta memoria. Crea el espacio de intercambio del paso 4 |
