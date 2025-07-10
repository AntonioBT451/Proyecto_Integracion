# Biblioteca Personal

Biblioteca Personal es una aplicación Android diseñada para gestionar una colección personal de libros. Permite a los usuarios registrar, buscar y organizar sus libros en diferentes listas, facilitando el seguimiento de su biblioteca personal.

## Características Principales

#### 1. Registro de Libros
- Captura de portadas mediante la cámara del dispositivo
- Reconocimiento de texto en portadas para extraer información
- Escaneo de códigos ISBN
- Obtención automatizada de información (título, autor, editorial, etc.) a través de las APIs de Google Books y Open Library

#### 2. Búsqueda de Libros
- Búsqueda por título o autor
- Visualización de todos los libros registrados
- Acceso a detalles completos de cada libro

#### 3. Organización en Listas
- Lista de "Libros no leídos"
- Lista de "Libros prestados"
- Lista de "Libros por comprar"

#### 4. Gestión de Información
- Visualización de detalles completos de cada libro
- Modificación de información de libros
- Eliminación de libros

## Tecnologías Utilizadas
- Lenguaje: Java
- Arquitectura : Patrón MVP (Modelo-Vista-Presentador)
- Base de Datos : Room (SQLite)
- Procesamiento de Imágenes : OpenCV
- Reconocimiento de Texto (OCR): Google ML Kit
- Escaneo de Códigos : Google ML Kit para escaneo de códigos de barras
- APIs: GoogleBooks y OpenLibrary

## Entorno de desarrollo recomendado

- Android Studio (versión recomendada: Android Studio Koala Feature Drop | 2024.1.2).
- JDK (Versión 21.0.1 LTS).
- Android 11 (API 30) o superior
- Permisos requeridos: Cámara, almacenamiento

## Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

- `app/`: Contiene el código fuente principal de la aplicación Android, recursos y archivos de configuración.
- `opencv/`: Contiene los módulos y archivos de la biblioteca OpenCV integrados en el proyecto. Esto incluye:
- `gradle/`: Contiene los archivos wrapper de Gradle y configuraciones relacionadas.
- `build.gradle.kts`: Archivo de configuración de Gradle a nivel de proyecto.
- `settings.gradle.kts`: Define los módulos incluidos en el proyecto (por ejemplo, `app` y `opencv`).


## Configuración del Entorno

1.  **Clonar el Repositorio:**
    ```bash
    git clone https://github.com/AntonioBT451/Proyecto_Integracion.git
    ```

2.  **Abrir en Android Studio:**
    Abre el proyecto en Android Studio. Android Studio debería detectar automáticamente la configuración de Gradle y sincronizar el proyecto.

3.  **Sincronizar Gradle:**
    Asegúrate de que Gradle se sincronice correctamente. Si hay algún problema, verifica las dependencias en `build.gradle.kts` y `opencv/build.gradle`.

## Construcción y Ejecución

Para construir y ejecutar la aplicación:

1.  **Seleccionar Dispositivo/Emulador:**
    En Android Studio, selecciona un dispositivo Android conectado o un emulador.

2.  **Ejecutar la Aplicación:**
    Haz clic en el botón "Run" (el icono de triángulo verde) en la barra de herramientas de Android Studio, o usa `Shift + F10`.

## Proyecto de Integración
Esta aplicación fue desarrollada como un Proyecto de Integración por Antonio Baca Tesillo, estudiante de la carrera de Ingeniería en Computación en la UAM Azcapotzalco, bajo la tutoría de la Dra. Beatriz Adriana González Beltrán.

## Licencia

Este proyecto está bajo la licencia MIT.
