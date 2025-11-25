# Documentación de la Aplicación de Notas

## 1\. Descripción General

Esta aplicación de gestión de notas permite a los usuarios crear, leer, actualizar y eliminar notas personales con funcionalidades adicionales como priorización, clasificación por importancia y calificación.

## 2\. Estructura del Proyecto

El proyecto sigue la arquitectura típica de Android con los siguientes componentes principales:

### Modelo de Datos

- **Note.kt**: Clase de datos que representa una nota con los siguientes campos:
    - `id`: Identificador único (Long)
    - `title`: Título de la nota (String)
    - `content`: Contenido de la nota (String)
    - `date`: Fecha de creación/actualización (String)
    - `important`: Indicador de importancia (Boolean)
    - `rating`: Calificación de 0 a 5 (Float)
    - `priority`: Nivel de prioridad (Int)

### Base de Datos

- **DatabaseHelper.kt**: Clase que gestiona la base de datos SQLite con las siguientes características:
    - Nombre de la base de datos: `notes.db`
    - Versión actual: 4
    - Tabla: `notes` con columnas para todos los campos de la clase Note
    - Funcionalidades CRUD completas (Create, Read, Update, Delete)
    - Sistema de migración para actualizaciones de esquema

### Interfaz de Usuario

- **MainActivity.kt**: Actividad principal que contiene:
    - Formulario para crear/editar notas
    - Lista de notas en un RecyclerView
    - Opciones de ordenación (más reciente, más antiguo, por prioridad)
    - Filtros de visualización
    - Diálogos para selección de fecha

## 3\. Funcionalidades Principales

### Gestión de Notas

- **Crear nueva nota**: Permite añadir una nueva nota con título, contenido, fecha, importancia, calificación y prioridad.
- **Editar nota**: Permite modificar cualquier campo de una nota existente.
- **Eliminar nota**: Elimina permanentemente una nota de la base de datos.
- **Marcar como importante**: Alterna el estado de importancia de una nota.

### Características Adicionales

- **Ordenación**: Las notas se pueden ordenar por:
    - Más recientes
    - Más antiguas
    - Mayor prioridad
    - Menor prioridad
- **Calificación**: Las notas pueden calificarse con estrellas (0-5).
- **Prioridad**: Las notas tienen un nivel de prioridad configurable.
- **Persistencia**: Los datos se guardan localmente en una base de datos SQLite.

## 4\. Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Almacenamiento**: SQLite con SQLiteOpenHelper
- **Interfaz de Usuario**:
    - RecyclerView para la lista de notas
    - Diálogos personalizados
    - Widgets estándar de Material Design (EditText, Button, CheckBox, etc.)

## 5\. Estructura de la Base de Datos

La base de datos `notes.db` contiene una tabla llamada `notes` con la siguiente estructura:

## 

## 

## 6\. Flujo de la Aplicación

1. Al iniciar, la aplicación muestra la lista de notas existentes.
2. El usuario puede:
    - Añadir una nueva nota con el botón correspondiente
    - Seleccionar una nota para ver sus detalles o editarla
    - Deslizar o mantener presionada una nota para marcarla como importante
    - Ordenar las notas según diferentes criterios
    - Eliminar notas no deseadas

## 7\. Consideraciones Técnicas

- La aplicación utiliza el patrón de diseño Singleton para la gestión de la base de datos.
- Implementa el patrón de diseño Adapter para la visualización de la lista de notas.
- Utiliza SharedPreferences para recordar la última nota seleccionada.
- Maneja correctamente el ciclo de vida de las actividades y la persistencia de datos.

## 8\. Posibles Mejoras

- Implementar búsqueda de notas.
- Añadir categorías o etiquetas.
- Sincronización con servicios en la nube.
- Exportar/importar notas.
- Temas claros/oscuros.

## 9\. Requisitos del Sistema

- Android 5.0 (API 21\) o superior
- Permisos de almacenamiento (si se implementa exportación/importación)

## 10\. Conclusión

Esta aplicación demuestra un conocimiento sólido del desarrollo de aplicaciones Android nativas con Kotlin, incluyendo el manejo de bases de datos SQLite, interfaces de usuario interactivas y buenas prácticas de programación.