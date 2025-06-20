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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import pi.biblioteca.vista.MostrarInfoActivity;

public class ConsultorAPIs {
    private Libro libroSeleccionado;
    private RequestQueue solicitud;
    private double mejorSimilitud = 0.0;

    public interface LibroBusquedaCallback {
        void onLibroEncontrado(Libro libro);
    }

    public interface OpenLibraryCallback {
        void onOpenLibraryComplete(Libro libro);
    }

    public Libro buscarLibro(String consulta, Boolean isISBN, MostrarInfoActivity mostrarInfoActivity, LibroBusquedaCallback callback) {
        solicitud = Volley.newRequestQueue(mostrarInfoActivity);
        libroSeleccionado = null;

        if (isISBN) {
            busquedaGoogleBooksISBN(consulta, callback);
        } else {
            busquedaGoogleBooks(consulta, callback);
        }
        return libroSeleccionado;
    }

    public void busquedaGoogleBooks(String consulta, LibroBusquedaCallback callback) {
        if(consulta.isEmpty()){
            Log.d("ConsultorAPIs", "BusquedaGoogleBooks()\n No se ha detectado texto en la imagen");
            return;
        }

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + Uri.encode(consulta) + "&printType=books&maxResults=10&key=AIzaSyDL4o-4r9eeDOcY2XjtuMQX74TpWaoOqVs";
        Log.d("GoogleBooks URL", url);

        JsonObjectRequest googleRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Boolean mejorSimilitudExist = false;

                        int totalItems = response.getInt("totalItems");
                        if (totalItems == 0) {
                            Log.d("GoogleBooks URL", "No se encontraron libros para el término de búsqueda (totalItems == 0).");
                            callback.onLibroEncontrado(null);
                            return;
                        }

                        JSONArray items = response.getJSONArray("items");
                        if (items.length() == 0) {
                            Log.d("GoogleBooks URL", "No se encontraron libros para el término de búsqueda (items.length() == 0).");
                            callback.onLibroEncontrado(null);
                            return;
                        }

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject libro = items.getJSONObject(i).getJSONObject("volumeInfo");

                            String isbn = getISBNGoogleBooks(libro);
                            String titulo = libro.optString("title", "No disponible");
                            String autores = libro.has("authors") ? libro.getJSONArray("authors").join(", ").replaceAll("\"", "") : "No disponible";
                            String fechaPublicacion = libro.optString("publishedDate", "No disponible");
                            String categoria = libro.has("categories") ? libro.getJSONArray("categories").join(", ").replaceAll("\"", "") : "No disponible";
                            String numeroPaginas = libro.optString("pageCount", "No disponible");
                            String descripcion = libro.optString("description", "No disponible");

                            Libro libroGoogleBooks = new Libro(isbn, titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);
                            libroGoogleBooks.setFechaPublicacion(fechaPublicacion);

                            // Calculate separate similarities for title and author
                            double similitudTitulo = libroGoogleBooks.calcularSimilitud(consulta, titulo);
                            double similitudAutor = autores.equals("No disponible") ? 0 :
                                    libroGoogleBooks.calcularSimilitud(consulta, autores);

                            // Combined similarity score with higher weight on title
                            double similitudCombinada = (similitudTitulo * 0.7) + (similitudAutor * 0.3);
                            libroGoogleBooks.setSimilitudPuntaje(similitudCombinada);

                            // Increased threshold and added more conditions
                            if (similitudCombinada > mejorSimilitud &&
                                    similitudCombinada > 0.6) {

                                mejorSimilitud = similitudCombinada;
                                mejorSimilitudExist = true;
                                libroSeleccionado = libroGoogleBooks;

                                Log.d("GoogleBooks libro", String.format(
                                        "Libro encontrado:\nTítulo: %s (Similitud: %.2f)\nAutor: %s (Similitud: %.2f)\nSimilitud Total: %f",
                                        titulo, similitudTitulo, autores, similitudAutor, similitudCombinada));
                                Log.d("GoogleBooks libro", libroGoogleBooks.infoLibro());
                            } else if (i < items.length() && !mejorSimilitudExist) {
                                Log.d("GoogleBooks libro", "No se encontraron libros con una similitud mayor a la establecida.");
                                callback.onLibroEncontrado(null);
                                return;
                            }
                        }

                        //libroSeleccionado = busquedaOpenLibrary();
                        //callback.onLibroEncontrado(libroSeleccionado);

                        // Modified to use callback
                        if (mejorSimilitud > 0.6) {
                            busquedaOpenLibrary(libro -> {
                                libroSeleccionado = libro;
                                callback.onLibroEncontrado(libroSeleccionado);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
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
                        int totalItems = response.getInt("totalItems");
                        if (totalItems == 0) {
                            Log.d("GoogleBooks URL", "No se encontraron libros para el término de búsqueda (totalItems == 0).");
                            callback.onLibroEncontrado(null);
                            return;
                        }

                        JSONArray items = response.has("items") ? response.getJSONArray("items") : null;
                        if (items == null) {
                            Log.d("GoogleBooks URL", "No se encontro el libro para el término de búsqueda.");
                            callback.onLibroEncontrado(null);
                            return;
                        }

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject libro = items.getJSONObject(i).getJSONObject("volumeInfo");
                            String isbn = consulta;
                            String titulo = libro.optString("title", "No disponible");
                            String autores = libro.has("authors") ? libro.getJSONArray("authors").join(", ").replaceAll("\"", "") : "No disponible";
                            String fechaPublicacion = libro.optString("publishedDate", "No disponible");
                            String categoria = libro.has("categories") ? libro.getJSONArray("categories").join(", ").replaceAll("\"", "") : "No disponible";
                            String numeroPaginas = libro.optString("pageCount", "No disponible");
                            String descripcion = libro.optString("description", "No disponible");

                            Libro libroGoogleBooks = new Libro(isbn, titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);
                            libroGoogleBooks.setFechaPublicacion(fechaPublicacion);
                            libroSeleccionado = libroGoogleBooks;

                            Log.d("GoogleBooks libro", "Libro seleccionado de Google Book\n" + libroSeleccionado.infoLibro());
                        }

                        //busquedaGoogleBooks(libroSeleccionado.getTitulo() + " " + libroSeleccionado.getAutores(), callback);

                        busquedaOpenLibraryISBN(consulta, libro -> {
                            libroSeleccionado = libro;
                            callback.onLibroEncontrado(libroSeleccionado);
                        });
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

    private void busquedaOpenLibrary(OpenLibraryCallback callback) {
        if (libroSeleccionado == null) {
            Log.d("OpenLibrary", "Libro no encontrado con GoogleBooks.");
            callback.onOpenLibraryComplete(null);
            return;
        }

        try {
            String query = libroSeleccionado.getTitulo() + " " + libroSeleccionado.getAutores();
            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String url = "https://openlibrary.org/search.json?q=" + encodedQuery
                    + "&fields=title,author_name,first_publish_year,number_of_pages_median,ia";
            Log.d("URL OpenLibrary:", url);

        JsonObjectRequest openLibraryRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray docs = response.getJSONArray("docs");

                        if (docs.length() == 0) {
                            Log.d("OpenLibrary", "No se encontro el libro para el término de búsqueda en Open Library.");
                            callback.onOpenLibraryComplete(libroSeleccionado);
                            return;
                        }

                        for (int i = 0; i < docs.length(); i++) {
                            JSONObject libro = docs.getJSONObject(i);
                            String isbn = getISBNOpenLibrary(libro);
                            String titulo = libro.optString("title", "No disponible");
                            String autores = libro.has("author_name") ? libro.getJSONArray("author_name").join(", ").replaceAll("\"", "") : "No disponible";
                            String fechaPublicacion = libro.optString("first_publish_year", "No disponible");
                            String categoria = libro.has("subject") ? libro.getJSONArray("subject").join(", ").replaceAll("\"", "") : "No disponible";
                            String numeroPaginas = libro.has("number_of_pages_median") ? String.valueOf(libro.getInt("number_of_pages_median")) : "No disponible";
                            String descripcion = libro.has("description") ? libro.getString("description") : "No disponible";

                            Libro libroOpenLibrary = new Libro(isbn, titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);

                            if (libroOpenLibrary.calcularSimilitud(libroSeleccionado.getTitulo(), titulo) > 0.80 && libroOpenLibrary.calcularSimilitud(libroSeleccionado.getAutores(), autores) > 0.60) {
                                if(libroSeleccionado.getIsbn().contains("No disponible")){
                                    libroSeleccionado.setIsbn(isbn);
                                }
                                libroSeleccionado.setFechaPublicacion(fechaPublicacion);
                                libroSeleccionado.setNumeroPaginas(numeroPaginas);

                                Log.d("Open Library", "Libro seleccionado de Open Library\n" + libroOpenLibrary.infoLibro());
                                Log.d("LIBRO ENCONTRADO", "Libro:\n" + libroSeleccionado.infoLibro());
                                break;
                            }
                        }
                        callback.onOpenLibraryComplete(libroSeleccionado);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onOpenLibraryComplete(libroSeleccionado);
                    }
                },
                error -> {
                    Log.e("Open Library Error", error.getMessage());
                    callback.onOpenLibraryComplete(libroSeleccionado);
                }
        );
        solicitud.add(openLibraryRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void busquedaOpenLibraryISBN(String isbn, OpenLibraryCallback callback) {
        if (libroSeleccionado == null) {
            Log.d("OpenLibrary", "Libro no encontrado con GoogleBooks.");
            callback.onOpenLibraryComplete(null);
            return;
        }

        String url = "https://openlibrary.org/isbn/" + Uri.encode(isbn) + ".json";
        Log.d("URL OpenLibrary: ", url);

        JsonObjectRequest openLibraryRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String titulo = response.optString("title", "No disponible");

                        JSONArray authorsArray = response.optJSONArray("authors");
                        String autores = "No disponible";
                        if (authorsArray != null && authorsArray.length() > 0) {
                            List<String> autoresList = new ArrayList<>();
                            for (int i = 0; i < authorsArray.length(); i++) {
                                JSONObject autorObj = authorsArray.getJSONObject(i);
                                String authorKey = autorObj.optString("key");
                                autoresList.add(authorKey.replace("/authors/", ""));
                            }
                            autores = String.join(", ", autoresList);
                        }

                        String fechaPublicacion = response.optString("publish_date", "No disponible");
                        String numeroPaginas = response.has("number_of_pages") ?
                                String.valueOf(response.getInt("number_of_pages")) : "No disponible";
                        String descripcion = response.has("description") ?
                                (response.get("description") instanceof JSONObject ?
                                        ((JSONObject) response.get("description")).optString("value", "No disponible")
                                        : response.getString("description"))
                                : "No disponible";

                        // No se proporciona una lista clara de categorías directamente en esta API
                        String categoria = "No disponible";

                        Libro libroOpenLibrary = new Libro(isbn, titulo, autores, fechaPublicacion, categoria, numeroPaginas, descripcion);


                        if (fechaPublicacion != "No disponible") {
                            libroSeleccionado.setFechaPublicacion(fechaPublicacion);
                        }

                        if (numeroPaginas != "No disponible") {
                            libroSeleccionado.setNumeroPaginas(numeroPaginas);
                        }

                        Log.d("Open Library", "Libro seleccionado de Open Library\n" + libroOpenLibrary.infoLibro());


                        callback.onOpenLibraryComplete(libroSeleccionado);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onOpenLibraryComplete(libroSeleccionado);
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        Log.e("Open Library", "Libro no encontrado en Open Library (404).");
                    } else {
                        Log.e("Open Library Error", "Error en la petición: " + error.getMessage());
                    }

                    callback.onOpenLibraryComplete(libroSeleccionado);
                }
        );
        solicitud.add(openLibraryRequest);
    }

    private String getISBNGoogleBooks(JSONObject volumeInfo){
        try {
            if (volumeInfo.has("industryIdentifiers")) {
                JSONArray identificadores = volumeInfo.getJSONArray("industryIdentifiers");
                for (int i = 0; i < identificadores.length(); i++) {
                    JSONObject identificador = identificadores.getJSONObject(i);
                    if ("ISBN_10".equals(identificador.getString("type"))) {
                        return identificador.getString("identifier");
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("ISBN_10 Error", "Error al obtener ISBN_10: " + e.getMessage());
        }
        return "No disponible";
    }

    private String getISBNOpenLibrary(JSONObject libro) {
        try {
            if (libro.has("ia")) {
                JSONArray iaArray = libro.getJSONArray("ia");
                for (int i = 0; i < iaArray.length(); i++) {
                    String valor = iaArray.getString(i);
                    if (valor.startsWith("isbn_")) {
                        String posibleIsbn = valor.substring(5); // Remueve "isbn_"
                        if ((posibleIsbn.length() == 10 && posibleIsbn.matches("\\d{9}[\\dX]"))
                                || (posibleIsbn.length() == 13 && posibleIsbn.matches("\\d{13}")) ) {
                            return posibleIsbn;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("ISBN_10 Error", "Error extrayendo ISBN_10 desde 'ia': " + e.getMessage());
        }
        return "No disponible";
    }

    private void mostrarLibroEncontrado() {
        if (libroSeleccionado != null) {
            Log.d("ConsultorAPIs", "Libro seleccionado: \n" + libroSeleccionado.infoLibro());
        } else {
            Log.d("ConsultorAPIs", "No se encontró ningún libro que coincida.");
        }
    }
}
