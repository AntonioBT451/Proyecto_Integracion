package pi.biblioteca.vista;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pi.biblioteca.R;
import pi.biblioteca.modelo.Libro;
import pi.biblioteca.presentador.BuscarLibroPresentador;
import pi.biblioteca.vista.LibroAdaptador;

public class BuscarLibroActivity extends AppCompatActivity
        implements BuscarLibroPresentador.IBuscarLibroVista, LibroAdaptador.OnLibroClickListener {
    private EditText etBuscar;
    private Button btnBuscar, btnMostrarTodos;
    private View headerView;
    private RecyclerView rvResultados;
    private LibroAdaptador adaptador;
    private BuscarLibroPresentador presentador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_libro);

        etBuscar = findViewById(R.id.etBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        rvResultados = findViewById(R.id.rvResultados);

        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        //adaptador = new LibroAdaptador();
        adaptador = new LibroAdaptador(this);
        // rvResultados.setAdapter(adaptador);
        headerView = findViewById(R.id.headerLayout);
        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        rvResultados.setAdapter(adaptador);


        rvResultados.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 1; // 1dp space between items
            }
        });

        presentador = new BuscarLibroPresentador(this, this);
        //presentador.buscarTodosLosLibros();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etBuscar.getText().toString().trim();
                if (query.isEmpty()) {
                    mostrarMensaje("Ingrese un título o autor a buscar");
                    return;
                }

                if(presentador.buscarLibros(query) > 0) {
                    btnMostrarTodos.setVisibility(View.VISIBLE);
                }
            }
        });
        
        btnMostrarTodos = findViewById(R.id.btnMostrarTodos);
        btnMostrarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etBuscar.setText("");
                btnMostrarTodos.setVisibility(View.GONE);
                presentador.buscarTodosLosLibros();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar la lista de libros cada vez que la actividad vuelve a estar visible
        //Metodo para borrar busqueda y mostrar todos lo libros
        //etBuscar.setText("");
        //Metodo para respetar lo que se buscó
        String query = etBuscar.getText().toString().trim();
        if (query.isEmpty()) {
            presentador.buscarTodosLosLibros();
        } else {
            presentador.buscarLibros(query);
        }
    }

    @Override
    public void mostrarResultados(List<Libro> libros) {
        headerView.setVisibility(View.VISIBLE);
        rvResultados.setVisibility(View.VISIBLE);
        adaptador.actualizarLista(libros);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLibroClick(Libro libro) {
        Intent intent = new Intent(this, ModificarInfoActivity.class);
        intent.putExtra("libro_id", libro.getId());
        startActivity(intent);
    }

    @Override
    public void ocultarTabla() {
        headerView.setVisibility(View.GONE);
        rvResultados.setVisibility(View.GONE);
    }
}
