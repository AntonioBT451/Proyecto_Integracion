package pi.biblioteca.presentador;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pi.biblioteca.modelo.Libro;
import pi.biblioteca.basededatos.LibroRepositorio;
import pi.biblioteca.vista.MostrarInfoActivity;
import pi.biblioteca.modelo.ProcesadorOpenCV;
import pi.biblioteca.modelo.ProcesadorOCR;
import pi.biblioteca.modelo.CorrectorOrtografico;
import pi.biblioteca.modelo.ConsultorAPIs;

public class PresentadorMostrarInfoLibro {
    private final MostrarInfoActivity vistaMostrarInfoActivity;
    private final ProcesadorOpenCV procesadorOpenCV;
    private final ProcesadorOCR procesadorOCR;
    private final CorrectorOrtografico correctorOrtografico;
    private final ConsultorAPIs consultorAPIs;
    private final Uri uriImagen;
    private final ExecutorService executorService;
    private Libro libro;

    public PresentadorMostrarInfoLibro(MostrarInfoActivity vistaMostrarInfoActivity, Uri uriImagen) {
        this.vistaMostrarInfoActivity = vistaMostrarInfoActivity;
        this.uriImagen = uriImagen;
        this.procesadorOpenCV = new ProcesadorOpenCV();
        this.procesadorOCR = new ProcesadorOCR();
        this.correctorOrtografico = new CorrectorOrtografico();
        this.consultorAPIs = new ConsultorAPIs();
        this.executorService = Executors.newSingleThreadExecutor();
        this.libro = null;
    }

    public void iniciarProcesamiento() {
        vistaMostrarInfoActivity.mostrarMensaje("Procesando imagen...");

        executorService.submit(() -> {
            try {
                // Se obtiene Bitmap
                Bitmap bitmapImagen = obtenerBitmapDesdeUri(uriImagen);

                // Se procesa la imagen con OpenCV
                Bitmap imagenProcesada = procesadorOpenCV.procesarImagen(bitmapImagen);
                Log.d("PresentadorMostrarInfoLibro", "Imagen procesada con OpenCV");

                // Se reconocer el texto de la imagen con OCR
                procesadorOCR.reconocerTexto(imagenProcesada, new ProcesadorOCR.ProcesamientoOCRCallback() {
                    @Override
                    public void onTextoDetectado(String textoDetectado) {
                        Log.d("PresentadorMostrarInfoLibro", "Texto detectado de la imagen: " + textoDetectado);

                        consultorAPIs.buscarLibro(textoDetectado, false, vistaMostrarInfoActivity, libroObtenido -> {
                            if (libroObtenido == null) {
                                vistaMostrarInfoActivity.mostrarMensaje("No se ha encontrado el libro");
                            } else {
                                vistaMostrarInfoActivity.runOnUiThread(() -> vistaMostrarInfoActivity.mostrarInformacionLibro(
                                        libroObtenido.getTitulo(),
                                        libroObtenido.getAutores(),
                                        libroObtenido.getFechaPublicacion(),
                                        libroObtenido.getCategoria(),
                                        String.valueOf(libroObtenido.getNumeroPaginas()),
                                        libroObtenido.getDescripcion()
                                ));
                                libro = libroObtenido;
                                vistaMostrarInfoActivity.mostrarMensaje("Libro encontrado\nCoincidencia: " + (int)(libroObtenido.getSimilitudPuntaje() * 100) + "%");
                                Log.d("PresentadorMostrarInfoLibro", "Información del libro mostrada con éxito.");
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("PresentadorMostrarInfoLibro", "Error al procesar la imagen en OCR o corrección ortográfica" + error);
                    }

                    @Override
                    public void onTextoNoDetectado() {
                        vistaMostrarInfoActivity.mostrarMensaje("No se ha podido reconocer el texto.");
                    }
                });
            } catch (Exception e) {
                Log.d("PresentadorMostrarInfo", "Error en el procesamiento de imagen: " + e.getMessage(), e);
            }
        });
    }

    public void iniciarProcesamientoISBN(String isbn) {
        vistaMostrarInfoActivity.mostrarMensaje("Procesando código ISBN...");

        consultorAPIs.buscarLibro(isbn, true, vistaMostrarInfoActivity, libroObtenido -> {
            if (libroObtenido == null) {
                vistaMostrarInfoActivity.mostrarMensaje("No se ha encontrado el libro");
            } else {
                vistaMostrarInfoActivity.runOnUiThread(() -> vistaMostrarInfoActivity.mostrarInformacionLibro(
                        libroObtenido.getTitulo(),
                        libroObtenido.getAutores(),
                        libroObtenido.getFechaPublicacion(),
                        libroObtenido.getCategoria(),
                        String.valueOf(libroObtenido.getNumeroPaginas()),
                        libroObtenido.getDescripcion()
                ));
                libro = libroObtenido;
                vistaMostrarInfoActivity.mostrarMensaje("Libro encontrado\nISBN: " + isbn);
            }
        });
    }

    public void guardarLibro() {
        try {
            if (libro != null) {
                LibroRepositorio repositorio = new LibroRepositorio(vistaMostrarInfoActivity);
                repositorio.insertarLibro(libro);

                // Se recuperar el libro insertado (con ID asignado)
                Libro libroInsertado = repositorio.buscarPorTitulo(libro.getTitulo()).get(0);

                // Se guardar el libro en las listas
                guardarLibroEnLista(libroInsertado, repositorio);

                vistaMostrarInfoActivity.limpiarPantalla();
                vistaMostrarInfoActivity.mostrarMensaje("Libro guardado exitosamente.");
                vistaMostrarInfoActivity.cerrarPantalla();
            } else {
                vistaMostrarInfoActivity.mostrarMensaje("No se ha podido guardar el libro.");
            }
        } catch (Exception e) {
            Log.e("PresentadorMostrarInfoLibro", "Error al guardar el libro: " + e.getMessage());
        }
    }

    public void guardarLibroEnLista(Libro libro, LibroRepositorio repositorio) {
        if (vistaMostrarInfoActivity.estaSeleccionadaNoLeidos()) {
            repositorio.agregarLibroALista(libro.getId(), "No leídos");
        }

        if (vistaMostrarInfoActivity.estaSeleccionadaPrestados()) {
            repositorio.agregarLibroALista(libro.getId(), "Prestados");
        }

        if (vistaMostrarInfoActivity.estaSeleccionadaPorComprar()) {
            repositorio.agregarLibroALista(libro.getId(), "Por comprar");
        }
    }

    private Bitmap obtenerBitmapDesdeUri(Uri imageUri) throws IOException {
        ContentResolver contentResolver = vistaMostrarInfoActivity.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        if (inputStream != null) {
            inputStream.close();
        }

        return bitmap;
    }
}
