package pi.biblioteca.modelo;

import android.graphics.Bitmap;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ProcesadorOpenCV {

    public Bitmap procesarImagen(Bitmap imagen) {
        if (imagen == null || imagen.isRecycled()) {
            return null;
        }

        try {
            // Se convierte la imagen en Mat para OpenCV
            Mat matImagen = new Mat(imagen.getHeight(), imagen.getWidth(), CvType.CV_8UC1);
            Utils.bitmapToMat(imagen, matImagen);

            // Se convierte la imagen a escala de grises
            Imgproc.cvtColor(matImagen, matImagen, Imgproc.COLOR_BGR2GRAY);

            // Se aplica suavizado para reducir el ruido
            Imgproc.GaussianBlur(matImagen, matImagen, new Size(5, 5), 0);

            // Se aplica umbralización dinámica
            Mat binarizada = aplicarUmbralizacion(matImagen);

            // Se convierte la imagen procesada de nuevo a Bitmap
            Bitmap bitmapProcesado = Bitmap.createBitmap(binarizada.cols(), binarizada.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(binarizada, bitmapProcesado);

            matImagen.release();
            binarizada.release();

            imagen = bitmapProcesado;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagen;
    }

    // Función para aplicar umbralización dinámica
    private Mat aplicarUmbralizacion(Mat matImagen) {
        // Calcular el valor medio de la imagen (brillo promedio)
        Scalar media = Core.mean(matImagen);
        double mediaValor = media.val[0];

        // Establecer el tipo de umbralización dependiendo del brillo de la imagen
        int tipoUmbral = 0;
        if (mediaValor > 127) {  // Si el fondo es claro (mediaValor alto), usa THRESH_BINARY
            tipoUmbral = Imgproc.THRESH_BINARY;
        } else {                 // Si el fondo es oscuro (mediaValor bajo), usa THRESH_BINARY_INV
            tipoUmbral = Imgproc.THRESH_BINARY_INV;
        }

        // Aplica la umbralización con el tipo elegido
        Mat binarizada = new Mat();
        Imgproc.threshold(matImagen, binarizada, 0, 255, tipoUmbral + Imgproc.THRESH_OTSU);

        return binarizada;
    }
}