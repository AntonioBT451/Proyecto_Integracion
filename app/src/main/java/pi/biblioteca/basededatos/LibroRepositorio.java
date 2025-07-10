package pi.biblioteca.basededatos;

import android.content.Context;
import android.util.Log;

import java.util.List;

public class LibroRepositorio {
    private LibroDao libroDao;
    private ListaDao listaDao;
    private LibroListaDao libroListaDao;

    public LibroRepositorio(Context context) {
        BaseDeDatos db = BaseDeDatos.getBaseDatos(context);

        libroDao = db.libroDao();
        listaDao = db.listaDao();
        libroListaDao = db.libroListaDao();

        inicializarListas();
    }

    // Inicializar listas predeterminadas
    private void inicializarListas() {
        if (listaDao.obtenerListaPorNombre("No leídos") == null) {
            listaDao.insertarLista(new Lista("No leídos"));
        }
        if (listaDao.obtenerListaPorNombre("Prestados") == null) {
            listaDao.insertarLista(new Lista("Prestados"));
        }
        if (listaDao.obtenerListaPorNombre("Por comprar") == null) {
            listaDao.insertarLista(new Lista("Por comprar"));
        }
    }

    // Métodos para libros
    public boolean insertarLibro(Libro libro) {
        int existe = libroDao.verificarExistencia(libro.getTitulo(), libro.getAutores());
        if (existe == 0) {
            libroDao.insertarLibro(libro);
            Log.d("LibroRepositorio", "Libro registrado en la base de datos.");

            return true;
        } else {
            Log.d("LibroRepositorio", "El libro ya existe en la base de datos.");

            return false;
        }
    }

    public boolean actualizarLibro(Libro libro) {
        try {
            libroDao.actualizarLibro(libro);

            // Verificar si el libro ha sido actualizado
            if (libroDao.verificarExistencia(libro.getTitulo(), libro.getAutores()) > 0) {
                Log.d("LibroRepositorio", "El libro existe en la base de datos.");

                return true;
            }

            Log.d("LibroRepositorio", "El libro no ha sido actualizado.");

            return false;
        } catch (Exception e) {
            Log.e("LibroRepositorio", "Error al actualizar el libro: " + e.getMessage());

            return false;
        }
    }

    public boolean eliminarLibro(Libro libro) {
        try {
            libroDao.eliminarLibro(libro);

            // Verificar si el libro ha sido eliminado
            Libro libroVerificacion = libroDao.obtenerLibroPorId(libro.getId());

            if (libroVerificacion == null) {
                Log.d("LibroRepositorio", "El libro ha sido eliminado correctamente.");

                return true;
            } else {
                Log.d("LibroRepositorio", "El libro no ha sido eliminado.");

                return false;
            }
        } catch (Exception e) {
            Log.e("LibroRepositorio", "Error al eliminar el libro: " + e.getMessage());
            return false;
        }
    }

    public List<Libro> obtenerTodosLosLibros() {
        return libroDao.obtenerTodosLibros();
    }

    public Libro obtenerLibroPorId(int query) {
        return libroDao.obtenerLibroPorId(query);
    }

    public List<Libro> buscarPorTitulo(String query) {
        return libroDao.buscarPorTitulo(query);
    }

    public List<Libro> buscarPorAutor(String query) {
        return libroDao.buscarPorAutor(query);
    }

    public List<Libro> buscarPorAno(String query) {
        return libroDao.buscarPorAno(query);
    }

    public List<Libro> buscarPorTituloAutorAno(String query) {
        return libroDao.buscarPorTituloAutorAno(query);
    }

    // Métodos para listas
    public List<Lista> obtenerListas() {
        return listaDao.obtenerListas();
    }

    public Lista obtenerListaPorNombre(String nombre) {
        return listaDao.obtenerListaPorNombre(nombre);
    }

    // Métodos para asociar libros con listas
    public void agregarLibroALista(int libroId, String nombreLista) {
        Lista lista = listaDao.obtenerListaPorNombre(nombreLista);
        Libro libro = libroDao.obtenerLibroPorId(libroId);

        if (libro == null) {
            Log.d("LibroRepositorio", "El libro no existe en la base de datos.");

            return;
        }

        if (lista != null) {
            LibroLista libroLista = new LibroLista(libroId, lista.getId());
            libroListaDao.agregarLibroALista(libroLista);
        } else {
            Log.d("LibroRepositorio", "La lista no existe.");
        }
    }

    public Boolean isLibroEnLista(int libroId, String nombreLista) {
        Lista lista = listaDao.obtenerListaPorNombre(nombreLista);
        if (lista != null) {
            Libro libro = libroListaDao.obtenerLibroDeLista(libroId, lista.getId());
            if (libro != null) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<Libro> obtenerLibrosPorLista(String nombreLista) {
        Lista lista = listaDao.obtenerListaPorNombre(nombreLista);
        if (lista != null) {
            return libroListaDao.obtenerLibrosPorLista(lista.getId());
        } else {
            Log.d("LibroRepositorio", "La lista no existe.");
            return null;
        }
    }

    public void eliminarLibroDeLista(int libroId, String nombreLista) {
        Lista lista = listaDao.obtenerListaPorNombre(nombreLista);
        if (lista != null) {
            libroListaDao.eliminarLibroDeLista(libroId, lista.getId());
        } else {
            Log.d("LibroRepositorio", "La lista no existe.");
        }
    }
}
