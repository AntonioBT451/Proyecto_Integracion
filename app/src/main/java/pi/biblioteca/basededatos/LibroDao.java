package pi.biblioteca.basededatos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pi.biblioteca.modelo.Libro;

@Dao
public interface LibroDao {
    // Insertar un libro en la base de datos
    @Insert
    void insertarLibro(Libro libro);

    @Query("SELECT COUNT(*) FROM libros WHERE titulo = :titulo AND autores = :autores")
    int verificarExistencia(String titulo, String autores);

    @Update
    void actualizarLibro(Libro libro); // Devuelve el número de filas afectadas, o 0 si no se azactualizó nada

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

    // Buscar libros por año (fecha de publicación)
    @Query("SELECT * FROM libros WHERE fechaPublicacion LIKE '%' || :query || '%'")
    List<Libro> buscarPorAno(String query);

    // Buscar libros por título, autor o año combinados
    @Query("SELECT * FROM libros WHERE titulo LIKE '%' || :query || '%' OR autores LIKE '%' || :query || '%' OR fechaPublicacion LIKE '%' || :query || '%'")
    List<Libro> buscarPorTituloAutorAno(String query);

    // Obtener un libro por su ID
    @Query("SELECT * FROM libros WHERE id = :libroId")
    Libro obtenerLibroPorId(int libroId);


}
