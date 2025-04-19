package pi.biblioteca.basededatos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ListaDao {
    @Insert
    void insertarLista(Lista lista);

    @Query("SELECT * FROM listas")
    List<Lista> obtenerListas();

    @Query("SELECT * FROM listas WHERE nombre = :nombre")
    Lista obtenerListaPorNombre(String nombre);
}
