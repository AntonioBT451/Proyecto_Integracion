package pi.biblioteca.modelo;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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


            // Se convierte la imagen procesada de nuevo a Bitmap
            Bitmap bitmapProcesado = Bitmap.createBitmap(matImagen.cols(), matImagen.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(matImagen, bitmapProcesado);

            matImagen.release();

            imagen = bitmapProcesado;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagen;
    }
}