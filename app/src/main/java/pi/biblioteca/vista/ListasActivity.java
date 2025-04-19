package pi.biblioteca.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pi.biblioteca.R;
import pi.biblioteca.basededatos.LibroRepositorio;
import pi.biblioteca.modelo.Libro;

public class ListasActivity extends AppCompatActivity implements LibroAdaptador.OnLibroClickListener {
    private RecyclerView rvLibrosLista;
    private LibroAdaptador adaptador;
    private LibroRepositorio repositorio;
    private Button btnLibrosNoLeidos, btnLibrosPrestados, btnLibrosPorComprar, btnBuscar;
    private TextView tvTituloLista;
    private LinearLayout llHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas);

        inicializarVistas();
        configurarRecyclerView();
        configurarBotones();

        repositorio = new LibroRepositorio(this);
    }

    private void inicializarVistas() {
        rvLibrosLista = findViewById(R.id.rvLibrosLista);
        btnLibrosNoLeidos = findViewById(R.id.btnLibrosNoLeidos);
        btnLibrosPrestados = findViewById(R.id.btnLibrosPrestados);
        btnLibrosPorComprar = findViewById(R.id.btnLibrosPorComprar);
        btnBuscar = findViewById(R.id.btnBuscarLibro);
        tvTituloLista = findViewById(R.id.tv_tituloLista);
        llHeader = findViewById(R.id.headerLayout);

        tvTituloLista.setVisibility(View.INVISIBLE);
        llHeader.setVisibility(View.INVISIBLE);
    }

    private void configurarRecyclerView() {
        adaptador = new LibroAdaptador(this);
        rvLibrosLista.setLayoutManager(new LinearLayoutManager(this));
        rvLibrosLista.setAdapter(adaptador);
    }

    @Override
    public void onLibroClick(Libro libro) {
        // Handle book click event
        Intent intent = new Intent(this, ModificarInfoListasActivity.class);
        intent.putExtra("libro_id", libro.getId());
        startActivity(intent);
    }

    private void configurarBotones() {
        btnLibrosNoLeidos.setOnClickListener(v -> {
            tvTituloLista.setVisibility(View.VISIBLE);
            llHeader.setVisibility(View.VISIBLE);
            tvTituloLista.setText("Libros no leídos");
            mostrarLibrosDeLista("No leídos");
        });

        btnLibrosPrestados.setOnClickListener(v -> {
            tvTituloLista.setVisibility(View.VISIBLE);
            llHeader.setVisibility(View.VISIBLE);
            tvTituloLista.setText("Libros prestados");
            mostrarLibrosDeLista("Prestados");
        });

        btnLibrosPorComprar.setOnClickListener(v -> {
            tvTituloLista.setVisibility(View.VISIBLE);
            llHeader.setVisibility(View.VISIBLE);
            tvTituloLista.setText("Libros por comprar");
            mostrarLibrosDeLista("Por comprar");
        });

        btnBuscar.setOnClickListener(v -> {
            Intent intent = new Intent(ListasActivity.this, BuscarLibroActivity.class);
            startActivity(intent);
        });
    }

    private void mostrarLibrosDeLista(String nombreLista) {
        List<Libro> libros = repositorio.obtenerLibrosPorLista(nombreLista);
        adaptador.actualizarLista(libros);
    }
}