package pi.biblioteca.vista;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pi.biblioteca.R;
import pi.biblioteca.presentador.PresentadorMostrarInfoLibro;

public class MostrarInfoActivity extends AppCompatActivity {
    private PresentadorMostrarInfoLibro presentadorMostrarInfoLibro;
    private TextView tvInformacion, tvTitulo, tvAutor, tvFechaPublicacion, tvCategoria, tvNumeroPaginas, tvDescripcion, tvListas;
    private EditText etTitulo, etAutor, etFechaPublicacion, etCategoria, etNumeroPaginas, etDescripcion;
    private Button btnGuardarLibro;
    private CheckBox chbLibrosNoLeidos, chbLibrosPrestados, chbLibrosPorComprar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_info);

        // Obtener la URI de la imagen o el código ISBN desde el Intent
        String uriImagenString = getIntent().getStringExtra("uriImagen");
        String codigoISBN = getIntent().getStringExtra("codigoISBN");

        inicializarVistas();

        if (uriImagenString != null) {
            Uri uriImagen = Uri.parse(uriImagenString);
            presentadorMostrarInfoLibro = new PresentadorMostrarInfoLibro(this, uriImagen);
            presentadorMostrarInfoLibro.iniciarProcesamiento();
        } else {
            Log.d("MostrarInfoActivity", "ISBN: " + codigoISBN);
            presentadorMostrarInfoLibro = new PresentadorMostrarInfoLibro(this, null);
            presentadorMostrarInfoLibro.iniciarProcesamientoISBN(codigoISBN);
        }
    }

    private void inicializarVistas() {
        tvInformacion = findViewById(R.id.tvInformacion);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvAutor = findViewById(R.id.tvAutor);
        tvFechaPublicacion = findViewById(R.id.tvFechaPublicacion);
        tvCategoria = findViewById(R.id.tvCategoria);
        tvNumeroPaginas = findViewById(R.id.tvNumeroPaginas);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvListas = findViewById(R.id.tvListas);

        etTitulo = findViewById(R.id.etTitulo);
        etAutor = findViewById(R.id.etAutor);
        etFechaPublicacion = findViewById(R.id.etFechaPublicacion);
        etCategoria = findViewById(R.id.etCategoria);
        etNumeroPaginas = findViewById(R.id.etNumeroPaginas);
        etDescripcion = findViewById(R.id.etDescripcion);

        chbLibrosNoLeidos = findViewById(R.id.chbLibrosNoLeidos);
        chbLibrosPrestados = findViewById(R.id.chbLibrosPrestados);
        chbLibrosPorComprar = findViewById(R.id.chbLibrosPorComprar);

        btnGuardarLibro = findViewById(R.id.btnGuardarLibro);
        //btnGuardarLibro.setOnClickListener(v -> presentadorMostrarInfoLibro.guardarLibro());
        btnGuardarLibro.setOnClickListener(v -> {
            if(validarCampos()){
                confirmarRegistro();
            } else {
                Toast.makeText(this, "Por favor, ingrese el titulo u autor del libro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cerrarPantalla() {
        finish();
    }

    public void mostrarInformacionLibro(String titulo, String autores, String fechaPublicacion, String categoria, String numeroPaginas, String descripcion) {
        etTitulo.setText(titulo);
        etAutor.setText(autores);
        etFechaPublicacion.setText(fechaPublicacion);
        etCategoria.setText(categoria);
        etNumeroPaginas.setText(numeroPaginas);
        etDescripcion.setText(descripcion);
    }

    public Boolean estaSeleccionadaNoLeidos(){
        return chbLibrosNoLeidos.isChecked();
    }

    public Boolean estaSeleccionadaPrestados(){
        return chbLibrosPrestados.isChecked();
    }

    public Boolean estaSeleccionadaPorComprar(){
        return chbLibrosPorComprar.isChecked();
    }

    public void limpiarPantalla(){
        etTitulo.setText("");
        etAutor.setText("");
        etFechaPublicacion.setText("");
        etCategoria.setText("");
        etNumeroPaginas.setText("");
        etDescripcion.setText("");
        chbLibrosPrestados.setChecked(false);
        chbLibrosPorComprar.setChecked(false);
        chbLibrosNoLeidos.setChecked(false);
    }

    private boolean validarCampos() {
        String titulo = etTitulo.getText().toString().trim();
        String autor = etAutor.getText().toString().trim();

        if (titulo.isEmpty() || autor.isEmpty()) {
            return false;
        }

        return true;
    }

    // Ventana emergente de confirmación para guardar un libro
    private void confirmarRegistro() {
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Confirmar registro")
                .setMessage("¿Registrar libro " + etTitulo.getText() + "?")
                .setPositiveButton("Sí", (d, which) -> presentadorMostrarInfoLibro.guardarLibro())
                .setNegativeButton("No", null)
                .show();
    }

    public void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
