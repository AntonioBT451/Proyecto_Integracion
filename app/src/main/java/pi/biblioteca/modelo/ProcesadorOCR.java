package pi.biblioteca.modelo;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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
        void onError(String mensajeError);
    }

    public void reconocerTexto(Bitmap BitmapImagen, ProcesamientoOCRCallback callback) {
        try {
            // Se convierte el bitmap a InputImage
            InputImage inputImage = InputImage.fromBitmap(BitmapImagen, 0);

            // Crear el cliente TextRecognizer
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

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
/*
                        // Después de obtener el texto, se pasa a la corrección ortográfica
                        CorrectorOrtografico correctorOrtografico = new CorrectorOrtografico();
                        correctorOrtografico.corregirTexto(textoResultante.toString().toLowerCase(), new CorrectorOrtografico.CorrectorCallback() {
                            @Override
                            public void onTextoCorregido(String textoCorregido) {
                                callback.onTextoDetectado(textoCorregido);
                                Log.d("ProcesadorOCR", "Texto detectado: " + textoResultante);
                                Log.d("ProcesadorOCR", "Texto corregido: " + textoCorregido);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });

                        Log.d("ProcesadorOCR", "Procesamiento de texto (OCR y ortografico) completado exitosamente");
 */
                        callback.onTextoDetectado(textoResultante.toString());
                        Log.d("ProcesadorOCR", "Texto detectado: " + textoResultante);
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

