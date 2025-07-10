package pi.biblioteca.basededatos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LibroDao {
    // Insertar un libro en la base de datos
    @Insert
    void insertarLibro(Libro libro);

    // Verificar el registro de un libro mediante un titulo y autor
    @Query("SELECT COUNT(*) FROM libros WHERE titulo = :titulo AND autores = :autores")
    int verificarExistencia(String titulo, String autores);

    // Actualización de la información de un libro
    @Update
    void actualizarLibro(Libro libro); // Devuelve el número de filas afectadas, o 0 si no se azactualizó nada

    // Eliminar un libro de la base de datos
    @Delete
    void eliminarLibro(Libro libro);

    // Obtener todos los libros
    @Query("SELECT * FROM libros")
    List<Libro> obtenerTodosLibros();

    // Buscar libros por título
    @Query("SELECT * FROM libros WHERE titulo LIKE :titulo")
    List<Libro> buscarPorTitulo(String titulo);

    // Buscar libros por autor
    @Query("SELECT * FROM libros WHERE autores LIKE '%' || :query || '%'")
    List<Libro> buscarPorAutor(String query);

    // Buscar libros por ISBN
    @Query("SELECT * FROM libros WHERE isbn LIKE '%' || :query || '%'")
    List<Libro> buscarPorIsbn(String query);

    // Buscar libros por año (fecha de publicación)
    @Query("SELECT * FROM libros WHERE fechaPublicacion LIKE '%' || :query || '%'")
    List<Libro> buscarPorAno(String query);

    // Buscar libros por título, autor o año combinados (sin tomar en cuenta acentos)
    @Query("SELECT * FROM libros WHERE " +
            "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(titulo, 'á', 'a'), 'é', 'e'), 'í', 'i'), 'ó', 'o'), 'ú', 'u')) LIKE LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE('%' || :query || '%', 'á', 'a'), 'é', 'e'), 'í', 'i'), 'ó', 'o'), 'ú', 'u')) OR " +
            "LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(autores, 'á', 'a'), 'é', 'e'), 'í', 'i'), 'ó', 'o'), 'ú', 'u')) LIKE LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE('%' || :query || '%', 'á', 'a'), 'é', 'e'), 'í', 'i'), 'ó', 'o'), 'ú', 'u')) OR " +
            "fechaPublicacion LIKE '%' || :query || '%'")
    List<Libro> buscarPorTituloAutorAno(String query);

    // Obtener un libro por su ID
    @Query("SELECT * FROM libros WHERE id = :libroId")
    Libro obtenerLibroPorId(int libroId);

}
