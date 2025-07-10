package pi.biblioteca.basededatos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listas")
public class Lista {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;

    public Lista(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
