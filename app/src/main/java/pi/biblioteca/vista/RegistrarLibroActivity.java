package pi.biblioteca.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.opencv.android.OpenCVLoader;

import java.io.File;

import pi.biblioteca.presentador.PresentadorISBN;
import pi.biblioteca.R;

public class RegistrarLibroActivity extends AppCompatActivity {
    private PresentadorISBN presentadorISBN;

    private EditText etISBN;
    private Button btnCapturaImagen, btnIngresarISBN, btnCapturaISBN;

    private Uri uriImagenCapturada;
    private static final String CARPETA_IMAGENES = "PIBibliotecaPersonal"; // Nombre de la carpeta donde se guardarán las imágenes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_libro);

        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV se ha inicializado correctamente.");
        } else {
            Log.d("OpenCV", "Error al inicializar OpenCV.");
        }

        presentadorISBN = new PresentadorISBN(this, GmsBarcodeScanning.getClient(this));

        btnCapturaImagen = findViewById(R.id.botonCaptura);
        btnIngresarISBN = findViewById(R.id.btnIngresarISBN);
        btnCapturaISBN = findViewById(R.id.btnCapturaISBN);
        etISBN = findViewById(R.id.etISBN);

        btnCapturaImagen.setOnClickListener(v -> iniciarCapturaImagen());
        btnCapturaISBN.setOnClickListener(v -> presentadorISBN.iniciarEscaneo());
        btnIngresarISBN.setOnClickListener(v -> {
            String codigoISBN = etISBN.getText().toString().trim();
            registrarConISBN(codigoISBN);
        });
    }

    private void iniciarCapturaImagen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            crearArchivoImagenCapturada();
            if (uriImagenCapturada != null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagenCapturada); // Se pasa la URI donde se guardará la imagen al tomar la foto
                capturaImagenLauncher.launch(intent);  // Se inicia la cámara
            } else {
                Log.d("RegistrarLibroActivity", "No se pudo crear el archivo de imagen.");
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private final ActivityResultLauncher<Intent> capturaImagenLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imagenConfirmada();
                }
            }
    );

    // Método para crear un archivo para guardar la imagen capturada del libro
    private void crearArchivoImagenCapturada() {
        // Se crea el directorio donde se guardarán las imágenes
        File mediaStorageDir = new File(getExternalFilesDir(null), CARPETA_IMAGENES);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("RegistrarLibroActivity", "Error al crear el directorio.");
                return;
            }
        }

        // Nombre único del archivo
        String fileName = "IMG_PI_Biblioteca" + System.currentTimeMillis() + ".jpg";
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);

        // Se usa FileProvider para generar una URI segura para el archivo
        uriImagenCapturada = FileProvider.getUriForFile(this, "pi.biblioteca.provider", mediaFile);
    }

    private void imagenConfirmada() {
        if (uriImagenCapturada != null) {
            Intent intent = new Intent(this, MostrarInfoActivity.class);
            intent.putExtra("uriImagen", uriImagenCapturada.toString());
            startActivity(intent);
        } else {
            mostrarMensaje("Primero capture una imagen.");
        }
    }

    public void  registrarConISBN(String codigoISBN) {
        if (presentadorISBN.validarISBN(codigoISBN)) {
            Intent intent = new Intent(this, MostrarInfoActivity.class);
            intent.putExtra("codigoISBN", codigoISBN);
            startActivity(intent);
            etISBN.setText("");
        }
    }

    public void mostrarCodigoISBN(String isbn) {
        etISBN.setText(isbn);
        Log.d("RegistrarLibroActivity", "ISBN válido: " + isbn);
    }

    public void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
