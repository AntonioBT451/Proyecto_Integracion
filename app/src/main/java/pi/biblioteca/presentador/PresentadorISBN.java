package pi.biblioteca.presentador;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import pi.biblioteca.modelo.ValidadorISBN;
import pi.biblioteca.vista.RegistrarLibroActivity;

public class PresentadorISBN {
    private RegistrarLibroActivity vistaRegistrarLibroActivity;
    private ValidadorISBN modeloValidadorISBN;
    private GmsBarcodeScanner escaner;

    public PresentadorISBN(RegistrarLibroActivity vistaRegistrarLibroActivity, GmsBarcodeScanner escaner) {
        this.vistaRegistrarLibroActivity = vistaRegistrarLibroActivity;
        this.modeloValidadorISBN = new ValidadorISBN();
        this.escaner = escaner;
    }

    public void iniciarEscaneo() {
        /*
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.TYPE_ISBN)
                .build();
         */

        escaner = GmsBarcodeScanning.getClient(vistaRegistrarLibroActivity.getApplicationContext());

        escaner.startScan()
                .addOnSuccessListener(barcode -> {
                    String codigoISBN = barcode.getRawValue();
                    vistaRegistrarLibroActivity.mostrarCodigoISBN(codigoISBN);
                    //validarISBN(codigoISBN);
                })
                .addOnCanceledListener(this::onEscaneoCancelado)
                .addOnFailureListener(e -> onEscaneoFallido());
    }

    public void onEscaneoCancelado() {
        vistaRegistrarLibroActivity.mostrarMensaje("Escaneo cancelado");
    }

    public void onEscaneoFallido() {
        vistaRegistrarLibroActivity.mostrarMensaje("Error al escanear el código");
    }

    public Boolean validarISBN(String isbn) {
        Boolean isISBN = null;
        if (modeloValidadorISBN.validarISBN(isbn)) {
            vistaRegistrarLibroActivity.mostrarCodigoISBN(isbn);
            isISBN = true;
        } else {
            vistaRegistrarLibroActivity.mostrarMensaje("ISBN inválido");
            isISBN = false;
        }
        return isISBN;
    }
}
