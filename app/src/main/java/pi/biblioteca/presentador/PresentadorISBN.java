package pi.biblioteca.presentador;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
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
        escaner = GmsBarcodeScanning.getClient(vistaRegistrarLibroActivity.getApplicationContext());

        escaner.startScan()
                .addOnSuccessListener(barcode -> {
                    String codigoISBN = barcode.getRawValue();
                    if (validarISBN(codigoISBN)) {
                        vistaRegistrarLibroActivity.registrarConISBN(codigoISBN);
                    } else {
                        vistaRegistrarLibroActivity.mostrarMensaje("Código ISBN invalido");
                    }
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
        Boolean isISBN;
        if (modeloValidadorISBN.validarISBN(isbn)) {
            isISBN = true;
        } else {
            vistaRegistrarLibroActivity.mostrarMensaje("ISBN inválido");
            isISBN = false;
        }
        return isISBN;
    }
}
