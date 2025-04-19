package pi.biblioteca.modelo;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class CorrectorOrtografico {
    private String textoDeEntrada;

    public interface CorrectorCallback {
        void onTextoCorregido(String textoCorregido);
        void onError(String error);
    }

    public void corregirTexto(String textoDeEntrada, CorrectorCallback callback) {
        this.textoDeEntrada = textoDeEntrada;  // Guardamos el texto para procesarlo
        verificarOrtografiaConLanguageTool(textoDeEntrada, callback);  // Realizamos la corrección
    }

    // Método para hacer la solicitud a la API de LanguageTool
    private void verificarOrtografiaConLanguageTool(String textoDeEntrada, CorrectorCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Se crea la solicitud POST con el texto y el idioma (español en este caso)
        RequestBody body = new FormBody.Builder()
                .add("text", textoDeEntrada)
                .add("language", "es")  // Otras opciones "en" para inglés, o "fr" para francés, etc.
                .build();

        Request request = new Request.Builder()
                .url("https://api.languagetool.org/v2/check")
                .post(body)
                .build();

        // Se realiza una solicitud asincrónica
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("CorrectorOrtografico\n Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();

                        // Se procesar la respuesta de la API
                        String correctedText = procesarRespuestaAPI(jsonResponse);
                        callback.onTextoCorregido(correctedText);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("CorrectorOrtografico\nError al procesar los resultados: " + e.getMessage());
                    }
                } else {
                    callback.onError("CorrectorOrtografico\nError al procesar la respuesta: " + response.message());
                }
            }
        });
    }

    // Método para procesar la respuesta de la API
    private String procesarRespuestaAPI(String jsonResponse) {
        StringBuilder textoCorregido = new StringBuilder(textoDeEntrada);
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray matches = responseObject.getJSONArray("matches");

            // Si no hay errores, muestra el texto original
            if (matches.length() == 0) {
                return textoCorregido.toString();
            }

            // Se mantiene un contador para ajustar el offset de errores posteriores
            int ajusteOffset = 0;

            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);
                String sugerencia = match.getJSONArray("replacements")
                        .getJSONObject(0)
                        .getString("value");

                // Se obtiene el offset original y la longitud del error
                int offset = match.getInt("offset");
                int length = match.getInt("length");

                // Se ajusta el offset en caso de que ya se haya modificado el texto
                int offsetAjustado = offset + ajusteOffset;

                // Se corrige el texto original
                textoCorregido.replace(offsetAjustado, offsetAjustado + length, sugerencia);

                // Actualizar el ajuste de offset en función del cambio realizado
                ajusteOffset += sugerencia.length() - length;
            }
            Log.d("CorrectorOrtografico", "Corrección ortográfica exitosa.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CorrectorOrtografico", "Error al procesar los resultados.");
        }
        return textoCorregido.toString();
    }
}
