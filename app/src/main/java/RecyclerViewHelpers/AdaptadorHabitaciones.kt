package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbHabitaciones

// Adaptador para un RecyclerView que muestra una lista de favoritos
class AdaptadorHabitaciones (var Datos: List<tbHabitaciones>): RecyclerView.Adapter<ViewHolderHabitaciones>() {
    // Infla el layout para cada elemento en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHabitaciones {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_habitaciones, parent, false)
        return ViewHolderHabitaciones(vista)
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount() = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderHabitaciones, position: Int) {
        val item = Datos[position]
    }
}