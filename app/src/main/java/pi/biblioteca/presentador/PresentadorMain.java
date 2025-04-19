package pi.biblioteca.presentador;

import pi.biblioteca.vista.MainActivity;

public class PresentadorMain {
    private MainActivity view;

    public PresentadorMain(MainActivity view) {
        this.view = view;
    }

    public void onRegistrarLibroClicked() {
        view.dirigirseARegistrarLibro();
    }

    public void onBuscarLibroClicked() {
        view.dirigirseABuscarLibro();
    }

    public void onListasClicked() {
        view.dirigirseAListas();
    }
}

