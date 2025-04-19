package pi.biblioteca.basededatos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;

import java.util.List;

import pi.biblioteca.modelo.Libro;

@Dao
public interface LibroListaDao {
    @Insert
    void agregarLibroALista(LibroLista libroLista);

    // Eliminar un libro de una lista específica
    @Query("DELETE FROM libro_lista WHERE libroId = :libroId AND listaId = :listaId")
    void eliminarLibroDeLista(int libroId, int listaId);

    // Obtener un libro de una lista(Libros leidos, Libros prestados, Libros por comprar) por su ID
    @Query("SELECT l.* FROM libros l INNER JOIN libro_lista ll ON l.id = ll.libroId WHERE ll.libroId = :libroId AND ll.listaId = :listaId")
    @RewriteQueriesToDropUnusedColumns
    Libro obtenerLibroDeLista(int libroId, int listaId);

    // Obtener los libros de una lista específica
    @Query("SELECT l.* FROM libros l INNER JOIN libro_lista ll ON l.id = ll.libroId WHERE ll.listaId = :listaId")
    @RewriteQueriesToDropUnusedColumns
    List<Libro> obtenerLibrosPorLista(int listaId);

    // Verificar si un libro está en una lista específica
    @Query("SELECT COUNT(*) FROM libro_lista WHERE libroId = :libroId AND listaId = :listaId")
    int verificarSiLibroEstaEnLista(int libroId, int listaId);

}
