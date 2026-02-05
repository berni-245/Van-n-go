# PAW GRUPO 1 2024B

## 👋 Introducción

En este trabajo práctico de la materia de Proyecto de Aplicaciones Web se implementó un marketplace de fletes utilizando Spring MVC, Spring Security y Hibernate. El sistema cuenta con búsqueda de fletes por filtros específicos, reservas y manejo de días ocupados para evitar colisiones, y mensajería para coordinar los pagos entre flete/cliente. Actualmente se está migrando el sistema para que deje de usar Spring MVC y se use Jersey para la construcción de una API REST y Solid.js para el frontend.

### ❗ Requisitos:
- Java 21
- [Maven](https://maven.apache.org/download.cgi)
- Tomcat 9.0.93

Clonar el proyecto utilizando:
```shell
git clone https://github.com/AlekDG/pod-tp1.git
```

## 🛠️ Compilación
Desde la terminal y parándose en la carpeta raíz del proyecto correr el siguiente comando:
```shell
mvn clean package
```
Se generará un archivo `webapp.war` en la carpeta `webapp/target`.

## 🏃 Ejecución

Parado en la carpeta donde está instalado el Tomcat, se deberá copiar el archivo war a la carpeta `webapps`. Luego, en la carpeta `bin`, se deberá correr `startup.sh` o `startup.bat` si se está en Windows.

Finalmente acceder a `http://localhost:8080/webapp` para visualizar la página.
