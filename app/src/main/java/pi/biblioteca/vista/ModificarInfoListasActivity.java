package pi.biblioteca.vista;

import android.app.AlertDialog;
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
import pi.biblioteca.modelo.Libro;
import pi.biblioteca.presentador.PresentadorMostrarInfoLibro;

public class ModificarInfoListasActivity extends AppCompatActivity {
    private TextView tvInformacion, tvTitulo, tvAutor, tvFechaPublicacion;
    private EditText etTitulo, etAutor, etFechaPublicacion;
    private Button btnGuardarLibro;
    private CheckBox chbLibrosNoLeidos, chbLibrosPrestados, chbLibrosPorComprar;

    private Libro libro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_info_listas);

        inicializarVistas();

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
        tvFechaPublicacion = findViewById(R.id.tvFechaPublicacion);

        etTitulo = findViewById(R.id.etTitulo);
        etAutor = findViewById(R.id.etAutor);
        etFechaPublicacion = findViewById(R.id.etFechaPublicacion);

        chbLibrosNoLeidos = findViewById(R.id.chbLibrosNoLeidos);
        chbLibrosPrestados = findViewById(R.id.chbLibrosPrestados);
        chbLibrosPorComprar = findViewById(R.id.chbLibrosPorComprar);

        btnGuardarLibro = findViewById(R.id.btnActualizar);
        btnGuardarLibro.setOnClickListener(v -> {
            confirmarActualizacion();
        });
    }

    public void mostrarInformacionLibro(Libro libro) {
        etTitulo.setText(libro.getTitulo());
        etAutor.setText(libro.getAutores());
        etFechaPublicacion.setText(libro.getFechaPublicacion());

        LibroRepositorio repositorio = new LibroRepositorio(this);
        if (repositorio.isLibroEnLista(libro.getId(), "No leídos")) {
            chbLibrosNoLeidos.setChecked(true);
        }
        if (repositorio.isLibroEnLista(libro.getId(), "Prestados")) {
            chbLibrosPrestados.setChecked(true);
        }
        if (repositorio.isLibroEnLista(libro.getId(), "Por comprar")) {
            chbLibrosPorComprar.setChecked(true);
        }
    }

    private void actualizarLibro() {
        if (libro != null) {

            LibroRepositorio repositorio = new LibroRepositorio(this);

            boolean listasActualizadas = actualizarListasLibro(repositorio);

            if (listasActualizadas) {
                //actualizarListasLibro(repositorio);
                Toast.makeText(this, "Listas del libro actualizadas correctamente", Toast.LENGTH_SHORT).show();
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
            Log.e("ModificarInfoListasActivity", "Información de listas del libro actualizadas.");
            return true;
        } catch (Exception e) {
            Log.e("ModificarInfoActivity", "Error al actualizar las listas del libro: " + e.getMessage());
            return false;
        }
    }

    // Ventana emergente de confirmación para actualizar las listas un libro
    private void confirmarActualizacion() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)                .setTitle("Confirmar actualización")
                .setMessage("¿Está seguro de actualizar las listas del libro?")
                .setPositiveButton("Sí", (d, which) -> actualizarLibro())
                .setNegativeButton("No", null)
                .show();
    }
}
