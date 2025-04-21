package pi.biblioteca.modelo;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "libros")
public class Libro {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String titulo;
    private String autores;
    private String fechaPublicacion;
    private String categoria;
    private String numeroPaginas;
    private String descripcion;

    @Ignore
    private double similitudPuntaje;

    public Libro(String titulo, String autores, String fechaPublicacion, String categoria, String numeroPaginas, String descripcion) {
        this.titulo = titulo;
        this.autores = autores;
        this.fechaPublicacion = fechaPublicacion;
        this.categoria = categoria;
        this.numeroPaginas = numeroPaginas;
        this.descripcion = descripcion;
        this.similitudPuntaje = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setNumeroPaginas(String numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setSimilitudPuntaje(double similitudPuntaje) {
        this.similitudPuntaje = similitudPuntaje;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutores() {
        return autores;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNumeroPaginas() {
        return numeroPaginas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getSimilitudPuntaje() {
        return similitudPuntaje;
    }

    // Método para mostrar la información del libro como String
    public String infoLibro() {
        return String.format(
                "Título: %s\nAutor(es): %s\nAño de publicación: %s\nNúmero de páginas: %s\nCategorías: %s\nDescripción: %s\n",
                titulo, autores, fechaPublicacion, numeroPaginas, categoria, descripcion
        );
    }

    public double calcularSimilitud(String query, String text) {
        if (query == null || text == null) {
            return 0.0;
        }

        // Clean and normalize strings
        query = query.toLowerCase().trim();
        text = text.toLowerCase().trim();

        // Split into words and find best matching segments
        String[] queryWords = query.split("\\s+");
        String[] textWords = text.split("\\s+");

        double maxSimilarity = 0.0;
        JaroWinklerSimilarity calculadorSimilitud = new JaroWinklerSimilarity();
        // Compare each possible segment of text with query
        for (int i = 0; i < textWords.length; i++) {
            StringBuilder segment = new StringBuilder();
            for (int j = i; j < Math.min(i + queryWords.length + 1, textWords.length); j++) {
                segment.append(textWords[j]).append(" ");
                double similarity = calculadorSimilitud.apply(query, segment.toString().trim());
                maxSimilarity = Math.max(maxSimilarity, similarity);
            }
        }

        return maxSimilarity;
    }
}
