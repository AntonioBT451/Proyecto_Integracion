package pi.biblioteca.modelo;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pi.biblioteca.vista.MostrarInfoActivity;

public class ConsultorAPIs {
    private Libro libroSeleccionado;
    private RequestQueue solicitud;
    private double mejorSimilitud = 0.0;

    public interface LibroBusquedaCallback {
        void onLibroEncontrado(Libro libro);
    }

    public Libro buscarLibro(String consulta, Boolean isISBN, MostrarInfoActivity mostrarInfoActivity, LibroBusquedaCallback callback) {
        solicitud = Volley.newRequestQueue(mostrarInfoActivity);

        if (isISBN) {
            busquedaGoogleBooksISBN(consulta, callback);
        } else {
            busquedaGoogleBooks(consulta, callback);
        }
        return libroSeleccionado;
    }

    public void busquedaGoogleBooks(String consulta, LibroBusquedaCallback callback) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + Uri.encode(consulta) + "&printType=books&maxResults=10&key=AIzaSyDL4o-4r9eeDOcY2XjtuMQX74TpWaoOqVs";
        Log.d("GoogleBooks URL", url);

        JsonObjectRequest googleRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray items = response.getJSONArray("items");

                        if (items.length() == 0) {
                            Log.d("GoogleBooks URL", "No se encontro el libro para el término de búsqueda.");
                            return;
                        }

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject libro = items.getJSONObject(i).getJSONObject("volumeInfo");
                            String titulo = libro.optString("title", "No disponible");
                            String autores = libro.has("authors") ? libro.getJSONArray("authors").join(", ").replaceAll("\"", "") : "No disponible";
                            String fechaPublicacion = libro.optString("publishedDate", "No disponible");
                            String categoria = libro.has("categories") ? libro.getJSONArray("categories").join(", ").replaceAll("\"", "") : "No disponible";
                            String numeroPaginas = libro.optString("pageCount", "No disponible");
                            String descripcion = libro.optString("description", "No disponible");

                            Libro libroGoogleBooks = new Libro(titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);

                            // Calculate separate similarities for title and author
                            double similitudTitulo = libroGoogleBooks.calcularSimilitud(consulta, titulo);
                            double similitudAutor = autores.equals("No disponible") ? 0 :
                                    libroGoogleBooks.calcularSimilitud(consulta, autores);

                            // Combined similarity score with higher weight on title
                            double similitudCombinada = (similitudTitulo * 0.7) + (similitudAutor * 0.3);
                            libroGoogleBooks.setSimilitudPuntaje(similitudCombinada);

                            // Increased threshold and added more conditions
                            if (similitudCombinada > mejorSimilitud &&
                                    similitudTitulo > 0.6 && // Minimum title similarity
                                    !descripcion.equals("No disponible")) {

                                mejorSimilitud = similitudCombinada;
                                libroSeleccionado = libroGoogleBooks;

                                Log.d("GoogleBooks libro", String.format(
                                        "Libro encontrado:\nTítulo: %s (Similitud: %.2f)\nAutor: %s (Similitud: %.2f)\nSimilitud Total: %.2f",
                                        titulo, similitudTitulo, autores, similitudAutor, similitudCombinada));
                            }
                        }

                        // Only proceed to OpenLibrary if we have a good match
                        if (mejorSimilitud > 0.6) {
                            libroSeleccionado = busquedaOpenLibrary();
                        }

                        /*
                        libroGoogleBooks.setSimilitudPuntaje(libroGoogleBooks.calcularSimilitud(consulta, titulo + " " + autores));

                        if (libroGoogleBooks.getSimilitudPuntaje() > mejorSimilitud && !descripcion.contains("No disponible")) {
                            mejorSimilitud = libroGoogleBooks.getSimilitudPuntaje();
                            libroSeleccionado = libroGoogleBooks;

                            Log.d("GoogleBooks libro", "Libro seleccionado de Google Book\n" + libroSeleccionado.infoLibro());
                            Log.d("GoogleBooks libro", "Coincidencia: " + libroGoogleBooks.getSimilitudPuntaje());
                        }
                    }
*/
                    libroSeleccionado = busquedaOpenLibrary();
                    callback.onLibroEncontrado(libroSeleccionado);
                } catch(JSONException e){
            e.printStackTrace();
        }
    },
    error ->

    {
        Log.e("Error en GoogleBooks ", error.getMessage());
    }
        );
        solicitud.add(googleRequest);
}

public void busquedaGoogleBooksISBN(String consulta, LibroBusquedaCallback callback) {
    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + Uri.encode(consulta) + "&projection=lite&key=AIzaSyDL4o-4r9eeDOcY2XjtuMQX74TpWaoOqVs";
    Log.d("GoogleBooks URL", url);

    JsonObjectRequest googleRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray items = response.has("items") ? response.getJSONArray("items") : null;

                    if (items == null) {
                        Log.d("GoogleBooks URL", "No se encontro el libro para el término de búsqueda.");
                        return;
                    }

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject libro = items.getJSONObject(i).getJSONObject("volumeInfo");
                        String titulo = libro.optString("title", "No disponible");
                        String autores = libro.has("authors") ? libro.getJSONArray("authors").join(", ").replaceAll("\"", "") : "No disponible";
                        String fechaPublicacion = libro.optString("publishedDate", "No disponible");
                        String categoria = libro.has("categories") ? libro.getJSONArray("categories").join(", ").replaceAll("\"", "") : "No disponible";
                        String numeroPaginas = libro.optString("pageCount", "No disponible");
                        String descripcion = libro.optString("description", "No disponible");

                        Libro libroGoogleBooks = new Libro(titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);
                        libroSeleccionado = libroGoogleBooks;

                        Log.d("GoogleBooks libro", "Libro seleccionado de Google Book mediante ISBN\n" + libroSeleccionado.infoLibro());
                    }

                    busquedaGoogleBooks(libroSeleccionado.getTitulo() + " " + libroSeleccionado.getAutores(), callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                Log.e("GoogleBooks Error", error.getMessage());
            }
    );
    solicitud.add(googleRequest);
}

private Libro busquedaOpenLibrary() {
    if (libroSeleccionado == null) {
        Log.d("OpenLibrary", "Libro no encontrado con GoogleBooks.");
        return null;
    }

    String url = "https://openlibrary.org/search.json?q=" + Uri.encode(libroSeleccionado.getTitulo() + " ") + Uri.encode(libroSeleccionado.getAutores()) + "&fields=title,author_name,first_publish_year,number_of_pages_median";
    Log.d("URL OpenLibrary: ", url);

    JsonObjectRequest openLibraryRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray docs = response.getJSONArray("docs");

                    if (docs.length() == 0) {
                        //mostrarLibroEncontrado();
                        Log.d("OpenLibrary", "No se encontro el libro para el término de búsqueda en Open Library.");
                        return;
                    }

                    for (int i = 0; i < docs.length(); i++) {
                        JSONObject libro = docs.getJSONObject(i);
                        String titulo = libro.optString("title", "No disponible");
                        String autores = libro.has("author_name") ? libro.getJSONArray("author_name").join(", ").replaceAll("\"", "") : "No disponible";
                        String fechaPublicacion = libro.optString("first_publish_year", "No disponible");
                        String categoria = libro.has("subject") ? libro.getJSONArray("subject").join(", ").replaceAll("\"", "") : "No disponible";
                        String numeroPaginas = libro.has("number_of_pages_median") ? String.valueOf(libro.getInt("number_of_pages_median")) : "No disponible";
                        String descripcion = libro.has("description") ? libro.getString("description") : "No disponible";

                        Libro libroOpenLibrary = new Libro(titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);

                        if (libroOpenLibrary.calcularSimilitud(libroSeleccionado.getTitulo(), titulo) > 0.50 && libroOpenLibrary.calcularSimilitud(libroSeleccionado.getAutores(), autores) > 0.50) {
                            libroSeleccionado.setFechaPublicacion(fechaPublicacion);
                            libroSeleccionado.setNumeroPaginas(numeroPaginas);

                            Log.d("Open Library", "Libro seleccionado de Open Library\n" + libroOpenLibrary.infoLibro());
                            break;
                        }
                    }
                    //mostrarLibroEncontrado();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                Log.e("Open Library Error", error.getMessage());
            }
    );
    solicitud.add(openLibraryRequest);
    return libroSeleccionado;
}

private void mostrarLibroEncontrado() {
    if (libroSeleccionado != null) {
        Log.d("ConsultorAPIs", "Libro seleccionado: \n" + libroSeleccionado.infoLibro());
    } else {
        Log.d("ConsultorAPIs", "No se encontró ningún libro que coincida.");
    }
}
}
