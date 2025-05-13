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
    private Button btnLibrosNoLeidosInicial, btnLibrosPrestadosInicial, btnLibrosPorComprarInicial, btnBuscarInicial;
    private TextView tvTituloLista;
    private LinearLayout llHeader, botonesIniciales, contenedorBotonesInferior;

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
        
        // Botones inferiores (después de seleccionar)
        btnLibrosNoLeidos = findViewById(R.id.btnLibrosNoLeidos);
        btnLibrosPrestados = findViewById(R.id.btnLibrosPrestados);
        btnLibrosPorComprar = findViewById(R.id.btnLibrosPorComprar);
        btnBuscar = findViewById(R.id.btnBuscarLibro);
        
        // Botones iniciales (centrados)
        btnLibrosNoLeidosInicial = findViewById(R.id.btnLibrosNoLeidosInicial);
        btnLibrosPrestadosInicial = findViewById(R.id.btnLibrosPrestadosInicial);
        btnLibrosPorComprarInicial = findViewById(R.id.btnLibrosPorComprarInicial);
        btnBuscarInicial = findViewById(R.id.btnBuscarLibroInicial);
        
        tvTituloLista = findViewById(R.id.tv_tituloLista);
        llHeader = findViewById(R.id.headerLayout);
        
        // Contenedores
        botonesIniciales = findViewById(R.id.botonesIniciales);
        contenedorBotonesInferior = findViewById(R.id.contenedorBotonesInferior);
        
        // Configuración inicial: mostrar botones centrales, ocultar el resto
        botonesIniciales.setVisibility(View.VISIBLE);
        contenedorBotonesInferior.setVisibility(View.GONE);
        tvTituloLista.setVisibility(View.INVISIBLE);
        llHeader.setVisibility(View.INVISIBLE);
        rvLibrosLista.setVisibility(View.INVISIBLE);
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
        // Configurar botones inferiores
        btnLibrosNoLeidos.setOnClickListener(v -> {
            mostrarListaSeleccionada("Libros no leídos", "No leídos");
        });

        btnLibrosPrestados.setOnClickListener(v -> {
            mostrarListaSeleccionada("Libros prestados", "Prestados");
        });

        btnLibrosPorComprar.setOnClickListener(v -> {
            mostrarListaSeleccionada("Libros por comprar", "Por comprar");
        });

        btnBuscar.setOnClickListener(v -> {
            abrirBusquedaLibros();
        });
        
        // Configurar botones iniciales (centrados)
        btnLibrosNoLeidosInicial.setOnClickListener(v -> {
            mostrarListaSeleccionada("Libros no leídos", "No leídos");
        });

        btnLibrosPrestadosInicial.setOnClickListener(v -> {
            mostrarListaSeleccionada("Libros prestados", "Prestados");
        });

        btnLibrosPorComprarInicial.setOnClickListener(v -> {
            mostrarListaSeleccionada("Libros por comprar", "Por comprar");
        });
        
        // Configurar botón de búsqueda inicial
        btnBuscarInicial.setOnClickListener(v -> {
            abrirBusquedaLibros();
        });
    }
    
    // Método para abrir la actividad de búsqueda
    private void abrirBusquedaLibros() {
        Intent intent = new Intent(ListasActivity.this, BuscarLibroActivity.class);
        startActivity(intent);
    }
    
    private void mostrarListaSeleccionada(String tituloMostrado, String nombreLista) {
        // Cambiar visibilidad de los elementos
        botonesIniciales.setVisibility(View.GONE);
        contenedorBotonesInferior.setVisibility(View.VISIBLE);
        tvTituloLista.setVisibility(View.VISIBLE);
        llHeader.setVisibility(View.VISIBLE);
        rvLibrosLista.setVisibility(View.VISIBLE);
        
        // Actualizar título y mostrar libros
        tvTituloLista.setText(tituloMostrado);
        mostrarLibrosDeLista(nombreLista);
    }

    private void mostrarLibrosDeLista(String nombreLista) {
        List<Libro> libros = repositorio.obtenerLibrosPorLista(nombreLista);
        adaptador.actualizarLista(libros);
    }
}