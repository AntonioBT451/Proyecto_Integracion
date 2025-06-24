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

public class ModificarInfoListasActivity extends AppCompatActivity {
    private TextView tvInformacion, tvTitulo, tvAutor, tvFechaPublicacion;
    private EditText etTitulo, etAutor, etFechaPublicacion;
    private Button btnGuardarLibro;
    private CheckBox chbLibrosNoLeidos, chbLibrosPrestados, chbLibrosPorComprar;
    private boolean noLeidos, prestados, porComprar;

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
        // Estado por defecto de las listas
        noLeidos = false;
        prestados = false;
        porComprar = false;

        etTitulo.setText(libro.getTitulo());
        etAutor.setText(libro.getAutores());
        etFechaPublicacion.setText(libro.getFechaPublicacion());

        // Estado incial de las listas para un libro
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
        if (!validarListas()) {
            return; // Detiene la ejecución si la lógica no se cumple
        }

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Confirmar actualización")
                .setMessage("¿Está seguro de actualizar las listas del libro?")
                .setPositiveButton("Sí", (d, which) -> actualizarLibro())
                .setNegativeButton("No", null)
                .show();
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

    private void mostrarToastListas(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

        // Restaurar checkboxes a su estado original
        chbLibrosNoLeidos.setChecked(noLeidos);
        chbLibrosPrestados.setChecked(prestados);
        chbLibrosPorComprar.setChecked(porComprar);
    }
}
