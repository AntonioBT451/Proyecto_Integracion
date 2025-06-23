package pi.biblioteca.basededatos;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import pi.biblioteca.modelo.Libro;

@Database(entities = {Libro.class, Lista.class, LibroLista.class}, version = 2,
          exportSchema = false)
public abstract class BaseDeDatos extends RoomDatabase{
    public abstract LibroDao libroDao();
    public abstract ListaDao listaDao();
    public abstract LibroListaDao libroListaDao();

    private static volatile BaseDeDatos instancia;

    public static BaseDeDatos getBaseDatos(final Context context) {
        if (instancia == null) {
            synchronized (BaseDeDatos.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(context.getApplicationContext(),
                                    BaseDeDatos.class, "bibliotecaPI.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    db.execSQL("PRAGMA foreign_keys=ON;");
                                }
                            })
                            .build();
                }
            }
        }
        return instancia;
    }
}
