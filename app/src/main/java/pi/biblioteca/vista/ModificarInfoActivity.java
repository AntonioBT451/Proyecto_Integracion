package pi.biblioteca.vista;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pi.biblioteca.R;
import pi.biblioteca.basededatos.LibroRepositorio;
import pi.biblioteca.basededatos.Libro;
import pi.biblioteca.presentador.PresentadorMostrarInfoLibro;

public class ModificarInfoActivity extends AppCompatActivity {
    private TextView tvInformacion, tvTitulo, tvAutor, tvIsbn, tvFechaPublicacion, tvCategoria, tvNumeroPaginas, tvDescripcion;
    private EditText etTitulo, etAutor, etIsbn, etFechaPublicacion, etCategoria, etNumeroPaginas, etDescripcion;
    private Button btnGuardarLibro, btnEliminarLibro;
    private CheckBox chbLibrosNoLeidos, chbLibrosPrestados, chbLibrosPorComprar;
    private boolean noLeidos, prestados, porComprar;

    private Libro libro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_info);

        inicializarVistas();
        configurarBotones();

        int libroId = getIntent().getIntExtra("libro_id", -1);
        if (libroId != -1) {
            LibroRepositorio repositorio = new LibroRepositorio(this);
            libro = repositorio.obtenerLibroPorId(libroId);
            mostrarInformacionLibro(libro);
        }
    }

    private void inicializarVistas() {
        tvInformacion = findViewById(R.id.tvInformacion);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvAutor = findViewById(R.id.tvAutor);
        tvIsbn = findViewById(R.id.tvIsbn);
        tvFechaPublicacion = findViewById(R.id.tvFechaPublicacion);
        tvCategoria = findViewById(R.id.tvCategoria);
        tvNumeroPaginas = findViewById(R.id.tvNumeroPaginas);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        etTitulo = findViewById(R.id.etTitulo);
        etAutor = findViewById(R.id.etAutor);
        etIsbn = findViewById(R.id.etIsbn);
        etFechaPublicacion = findViewById(R.id.etFechaPublicacion);
        etCategoria = findViewById(R.id.etCategoria);
        etNumeroPaginas = findViewById(R.id.etNumeroPaginas);
        etDescripcion = findViewById(R.id.etDescripcion);

        chbLibrosNoLeidos = findViewById(R.id.chbLibrosNoLeidos);
        chbLibrosPrestados = findViewById(R.id.chbLibrosPrestados);
        chbLibrosPorComprar = findViewById(R.id.chbLibrosPorComprar);
    }

    private void configurarBotones() {
        btnGuardarLibro = findViewById(R.id.btnGuardarLibro);
        btnGuardarLibro.setText("Actualizar libro");
        btnGuardarLibro.setOnClickListener(v -> {
            if (validarCampos()) {
                confirmarActualizacion();
            } else {
                Toast.makeText(this, "Por favor, ingrese el titulo u autor del libro", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializar y configurar el botón de eliminar
        btnEliminarLibro = findViewById(R.id.btnEliminarLibro);
        btnEliminarLibro.setVisibility(android.view.View.VISIBLE);
        btnEliminarLibro.setOnClickListener(v -> confirmarEliminacion());
    }

    public void mostrarInformacionLibro(Libro libro) {
        // Estado por defecto de las listas
        noLeidos = false;
        prestados = false;
        porComprar = false;

        etTitulo.setText(libro.getTitulo());
        etAutor.setText(libro.getAutores());
        etIsbn.setText(libro.getIsbn());
        etFechaPublicacion.setText(libro.getFechaPublicacion());
        etCategoria.setText(libro.getCategoria());
        etNumeroPaginas.setText(libro.getNumeroPaginas());
        etDescripcion.setText(libro.getDescripcion());

        LibroRepositorio repositorio = new LibroRepositorio(this);
        if (repositorio.isLibroEnLista(libro.getId(), "No leídos")) {
            chbLibrosNoLeidos.setChecked(true);
            noLeidos = true;
        }
        if (repositorio.isLibroEnLista(libro.getId(), "Prestados")) {
            chbLibrosPrestados.setChecked(true);
            prestados = true;
        }
        if (repositorio.isLibroEnLista(libro.getId(), "Por comprar")) {
            chbLibrosPorComprar.setChecked(true);
            porComprar = true;
        }
    }

    private boolean validarCampos() {
        String titulo = etTitulo.getText().toString().trim();
        String autor = etAutor.getText().toString().trim();

        if (titulo.isEmpty() || autor.isEmpty()) {
            return false;
        }

        return true;
    }

    private void confirmarActualizacion() {
        if (!validarListas()) {
            return;
        }

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Confirmar actualización")
                .setMessage("¿Está seguro de actualizar la información del libro?")
                .setPositiveButton("Sí", (d, which) -> actualizarLibro())
                .setNegativeButton("No", null)
                .show();
    }

    private void actualizarLibro() {
        if (libro != null) {
            libro.setTitulo(etTitulo.getText().toString().trim());
            libro.setAutores(etAutor.getText().toString().trim());
            libro.setIsbn(etIsbn.getText().toString().trim());
            libro.setFechaPublicacion(etFechaPublicacion.getText().toString().trim());
            libro.setCategoria(etCategoria.getText().toString().trim());
            libro.setNumeroPaginas(etNumeroPaginas.getText().toString().trim());
            libro.setDescripcion(etDescripcion.getText().toString().trim());

            LibroRepositorio repositorio = new LibroRepositorio(this);

            boolean informacionActualizada = repositorio.actualizarLibro(libro);
            boolean listasActualizadas = actualizarListasLibro(repositorio);

            boolean actualizado = informacionActualizada && listasActualizadas;

            if (actualizado) {
                Toast.makeText(this, "Información del libro actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar la información del libro", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean actualizarListasLibro(LibroRepositorio repositorio) {
        try {
            // Actualizar "No leídos"
            if (chbLibrosNoLeidos.isChecked()) {
                if (!repositorio.isLibroEnLista(libro.getId(), "No leídos")) {
                    repositorio.agregarLibroALista(libro.getId(), "No leídos");
                }
            } else {
                if (repositorio.isLibroEnLista(libro.getId(), "No leídos")) {
                    repositorio.eliminarLibroDeLista(libro.getId(), "No leídos");
                }
            }

            // Actualizar "Prestados"
            if (chbLibrosPrestados.isChecked()) {
                if (!repositorio.isLibroEnLista(libro.getId(), "Prestados")) {
                    repositorio.agregarLibroALista(libro.getId(), "Prestados");
                }
            } else {
                if (repositorio.isLibroEnLista(libro.getId(), "Prestados")) {
                    repositorio.eliminarLibroDeLista(libro.getId(), "Prestados");
                }
            }

            // Actualizar "Por comprar"
            if (chbLibrosPorComprar.isChecked()) {
                if (!repositorio.isLibroEnLista(libro.getId(), "Por comprar")) {
                    repositorio.agregarLibroALista(libro.getId(), "Por comprar");
                }
            } else {
                if (repositorio.isLibroEnLista(libro.getId(), "Por comprar")) {
                    repositorio.eliminarLibroDeLista(libro.getId(), "Por comprar");
                }
            }
            Log.e("ModificarInfoActivity", "Información de libro en listas actualizadas.");
            return true;
        } catch (Exception e) {
            Log.e("ModificarInfoActivity", "Error al actualizar las listas del libro: " + e.getMessage());
            return false;
        }
    }

    private boolean validarListas() {
        boolean prestado = chbLibrosPrestados.isChecked();
        boolean porComprar = chbLibrosPorComprar.isChecked();

        // Regla: Un libro no puede estar tanto en 'Prestados' como en 'Por comprar'
        if (prestado && porComprar) {
            mostrarDialogoListas("Un libro no puede estar en 'Prestados' y 'Por comprar' al mismo tiempo.");
            return false;
        }

        return true;
    }

    private void mostrarDialogoListas(String mensaje) {
        new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("¡Alerta!")
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Restaurar los checkboxes al estado original
                    chbLibrosNoLeidos.setChecked(noLeidos);
                    chbLibrosPrestados.setChecked(prestados);
                    chbLibrosPorComprar.setChecked(porComprar);
                })
                .show();
    }

    // Método para confirmar la eliminación del libro
    private void confirmarEliminacion() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar este libro? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (d, which) -> eliminarLibro())
                .setNegativeButton("No", null)
                .show();
    }

    // Método para eliminar el libro
    private void eliminarLibro() {
        if (libro != null) {
            LibroRepositorio repositorio = new LibroRepositorio(this);
            boolean eliminado = repositorio.eliminarLibro(libro);

            if (eliminado) {
                Toast.makeText(this, "Libro eliminado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar el libro", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
