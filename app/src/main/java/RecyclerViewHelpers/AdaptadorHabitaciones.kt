package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbHabitaciones

class AdaptadorHabitaciones (var Datos: List<tbHabitaciones>): RecyclerView.Adapter<ViewHolderHabitaciones>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHabitaciones {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_habitaciones, parent, false)
        return ViewHolderHabitaciones(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHabitaciones, position: Int) {
        val item = Datos[position]

    }
}