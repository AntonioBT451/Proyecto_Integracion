package pi.biblioteca.modelo;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.List;

public class ProcesadorOCR {

    public interface ProcesamientoOCRCallback {
        void onTextoDetectado(String textoDetectado);

        void onTextoNoDetectado();

        void onError(String mensajeError);
    }

    public void reconocerTexto(Bitmap BitmapImagen, ProcesamientoOCRCallback callback) {
        try {
            // Se convierte el bitmap a InputImage
            InputImage inputImage = InputImage.fromBitmap(BitmapImagen, 0);

            // Crear el cliente TextRecognizer
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            // Reconocimiento del texto
            recognizer.process(inputImage)
                    .addOnSuccessListener(text -> {
                        StringBuilder textoResultante = new StringBuilder();
                        List<String> bloques = new ArrayList<>();
                        List<String> lineas = new ArrayList<>();

                        for (Text.TextBlock block : text.getTextBlocks()) {
                            String blockText = block.getText();
                            bloques.add(blockText);
                            Log.d("ProcesadorOCR", "Bloque: " + blockText);
                            for (Text.Line line : block.getLines()) {
                                String lineText = line.getText();
                                lineas.add(lineText);
                                textoResultante.append(lineText).append(" ");
                                Log.d("ProcesadorOCR", "Línea: " + lineText);
                            }
                        }

                        if (textoResultante.length() == 0) {
                            callback.onTextoNoDetectado();
                            Log.d("ProcesadorOCR", "Texto no detectado");
                        } else {
                            callback.onTextoDetectado(textoResultante.toString());
                            Log.d("ProcesadorOCR", "Texto detectado: " + textoResultante);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProcesadorOCR", "Error al reconocer texto", e);
                        callback.onError("Error en el reconocimiento de texto.");
                    })
                    .addOnCompleteListener(task -> {
                        recognizer.close();
                    });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError("Error al procesar la imagen.");
        }
    }
}

