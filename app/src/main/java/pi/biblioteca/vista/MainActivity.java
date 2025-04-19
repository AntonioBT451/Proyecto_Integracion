package pi.biblioteca.vista;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pi.biblioteca.R;
import pi.biblioteca.presentador.PresentadorMain;

public class MainActivity extends Activity {
    private PresentadorMain presentadorMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presentadorMain = new PresentadorMain(this);

        Button btnRegistrarLibro = findViewById(R.id.btn_registrar_libro);
        Button btnBuscar = findViewById(R.id.btn_buscar);
        Button btnListas = findViewById(R.id.btn_listas);

        btnRegistrarLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentadorMain.onRegistrarLibroClicked();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentadorMain.onBuscarLibroClicked();
            }
        });

        btnListas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentadorMain.onListasClicked();
            }
        });
    }

    public void dirigirseARegistrarLibro() {
        Intent intent = new Intent(MainActivity.this, RegistrarLibroActivity.class);
        startActivity(intent);
    }

    public void dirigirseABuscarLibro() {
        Intent intent = new Intent(MainActivity.this, BuscarLibroActivity.class);
        startActivity(intent);
    }

    public void dirigirseAListas() {
        Intent intent = new Intent(MainActivity.this, ListasActivity.class);
        startActivity(intent);
    }
}
