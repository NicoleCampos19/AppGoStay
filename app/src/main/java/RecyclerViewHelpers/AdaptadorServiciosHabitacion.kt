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

class AdaptadorServiciosHabitacion (val Datos : List<tbServiciosHabitacion>) : RecyclerView.Adapter<ViewHolderServiciosHabitacion>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderServiciosHabitacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicios_habitacion, parent, false)
        return ViewHolderServiciosHabitacion(view)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderServiciosHabitacion, position: Int) {
        val item = Datos[position]
        holder.txtNombreServicioHabitacion.text = item.nombre_servicio_habitacion
        Glide.with(holder.itemView.context)
            .load(item.img_icono_habitacion)
            .into(holder.imgServicioHabitacion)
    }
}