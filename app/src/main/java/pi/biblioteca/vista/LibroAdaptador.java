package pi.biblioteca.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import pi.biblioteca.R;
import pi.biblioteca.basededatos.Libro;

public class LibroAdaptador extends RecyclerView.Adapter<LibroAdaptador.LibroViewHolder>{
    private List<Libro> libros = new ArrayList<>();
    private OnLibroClickListener listener;

    public interface OnLibroClickListener {
        void onLibroClick(Libro libro);
    }

    public LibroAdaptador(OnLibroClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LibroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        return new LibroViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull LibroViewHolder holder, int position) {
        Libro libro = libros.get(position);
        holder.tvTitulo.setText(libro.getTitulo());
        holder.tvAutor.setText(libro.getAutores());
        holder.tvAno.setText(libro.getFechaPublicacion());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLibroClick(libro);
            }
        });

        // Alternar colores de fondo con transparencia
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.item_background_dark);
        } else {
            holder.itemView.setBackgroundResource(R.color.item_background_light);
        }
    }

    @Override
    public int getItemCount() {
        return libros.size();
    }

    public void actualizarLista(List<Libro> nuevosLibros) {
        libros.clear();
        libros.addAll(nuevosLibros);
        notifyDataSetChanged();
    }

    static class LibroViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvAutor, tvAno;

        LibroViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            tvAno = itemView.findViewById(R.id.tvAno);
        }
    }
}
