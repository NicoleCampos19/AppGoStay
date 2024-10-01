package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_habitacion_economica
import modelo.tbServiciosHabitacion
import modelo.tbTipoHabitacion

// Adaptador para un RecyclerView que muestra una lista de servicios
class AdaptadorServiciosHabitacion (val Datos : List<tbServiciosHabitacion>) : RecyclerView.Adapter<ViewHolderServiciosHabitacion>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderServiciosHabitacion {
        // Infla el layout para cada elemento en el RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicios_habitacion, parent, false)
        return ViewHolderServiciosHabitacion(view)
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount() = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderServiciosHabitacion, position: Int) {
        val item = Datos[position]
        holder.txtNombreServicioHabitacion.text = item.nombre_servicio_habitacion
        Glide.with(holder.itemView.context)
            .load(item.img_icono_habitacion)
            .into(holder.imgServicioHabitacion)
    }
}