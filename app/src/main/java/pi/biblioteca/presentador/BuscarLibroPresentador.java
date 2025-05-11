package pi.biblioteca.presentador;

import java.util.List;

import android.content.Context;

import pi.biblioteca.modelo.Libro;
import pi.biblioteca.basededatos.LibroRepositorio;

public class BuscarLibroPresentador {
    private IBuscarLibroVista vista;
    private LibroRepositorio repositorio;

    public interface IBuscarLibroVista {
        void mostrarResultados(List<Libro> libros);
        void mostrarMensaje(String mensaje);
        void ocultarTabla();
    }

    public BuscarLibroPresentador(IBuscarLibroVista vista, Context context) {
        this.vista = vista;
        this.repositorio = new LibroRepositorio(context);
    }

    public void buscarLibros(String query) {
        List<Libro> resultados = repositorio.buscarPorTituloAutorAno(query);
        if (resultados.isEmpty()) {
            vista.ocultarTabla();
            vista.mostrarMensaje("No se encontraron resultados");
        } else {
            vista.mostrarResultados(resultados);
        }
    }

    public void buscarTodosLosLibros(){
        List<Libro> resultados = repositorio.obtenerTodosLosLibros();
        if (resultados.isEmpty()) {
            vista.ocultarTabla();
            vista.mostrarMensaje("No se tiene libros registrados");
        } else {
            vista.mostrarResultados(resultados);
        }
    }
}
