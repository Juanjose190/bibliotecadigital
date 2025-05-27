# 📚 Sistema de Biblioteca Digital - Backend

## 🚀 Descripción

**Backend del sistema de gestión bibliotecaria** desarrollado con **Spring Boot**.  
Proporciona una **API REST completa** para la administración de **usuarios**, **libros**, **préstamos** y **sanciones**.

---

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Web**
- **MySQL** (Base de datos)
- **Maven** (Gestión de dependencias)

---

## 📋 Características

- ✅ Gestión de usuarios  
- ✅ Sistema de préstamos y devoluciones  
- ✅ Control de sanciones automático  

---

## 🗄️ Estructura de Base de Datos

El sistema utiliza las siguientes tablas:

- `user` – Información de usuarios  
- `categorias` – Clasificación de libros  
- `libros` – Catálogo de libros  
- `prestamo` – Registro de préstamos  
- `sancion` – Sistema de penalizaciones  

---

## ⚙️ Configuración e Instalación

### ✅ Prerrequisitos

- Java 17 o superior  
- MySQL 8.0 o superior  
- Maven 3.6 o superior  
- IDE (IntelliJ IDEA, Eclipse, VSCode)

---

### 🧱 Configuración de Base de Datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE biblioteca_db;
```


### 🧱 Configuración de Properties

spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca_db

spring.datasource.username=root



## 💻 Instalación y Ejecución
git clone https://github.com/Juanjose190/bibliotecadigital
cd bibliotecadigital


## Instalar dependencias:
mvn clean install



## ✅ Verificación
El servidor se ejecutará en:
📍 http://localhost:8080

