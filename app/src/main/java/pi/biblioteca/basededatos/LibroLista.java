package pi.biblioteca.basededatos;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "libro_lista",
        primaryKeys = {"libroId", "listaId"},
        indices = {
                @Index("listaId")
        },
        foreignKeys = {
                @ForeignKey(entity = Libro.class, parentColumns = "id", childColumns = "libroId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Lista.class, parentColumns = "id", childColumns = "listaId",
                        onDelete = CASCADE)
        }
)
public class LibroLista {
    private int libroId;
    private int listaId;

    public LibroLista(int libroId, int listaId) {
        this.libroId = libroId;
        this.listaId = listaId;
    }

    public int getLibroId() {
        return libroId;
    }

    public int getListaId() {
        return listaId;
    }
}
