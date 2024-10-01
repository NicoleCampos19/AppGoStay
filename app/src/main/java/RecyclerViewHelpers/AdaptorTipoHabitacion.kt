package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_habitacion_economica
import emily.jacobo.gostay.activity_reserva
import modelo.tbTipoHabitacion

class AdaptorTipoHabitacion(val Datos: List<tbTipoHabitacion>) : RecyclerView.Adapter<ViewHolderTipoHabitacion>() {

    companion object {
        var idTipoHabitacionGlobal = -1  // Variable global que guarda el ID del tipo de habitación seleccionado.
    }

    // Método que crea y devuelve un nuevo ViewHolder inflando el layout 'item_tipohabitacion'.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTipoHabitacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tipohabitacion, parent, false)
        return ViewHolderTipoHabitacion(view)
    }

    // Retorna el tamaño de la lista 'Datos', que define cuántos elementos se mostrarán.
    override fun getItemCount() = Datos.size

    // Método que vincula los datos de un elemento específico a un ViewHolder.
    override fun onBindViewHolder(holder: ViewHolderTipoHabitacion, position: Int) {
        val item = Datos[position]

        // Asigna el nombre del tipo de habitación al TextView correspondiente.
        holder.txtNombreTipoHabitacion.text = item.nombre

        // Asigna el precio del tipo de habitación al TextView correspondiente.
        holder.txtPrecioTipoHabitacion.text = "$${item.precio}"

        // Carga la imagen de la habitación usando Glide.
        Glide.with(holder.itemView.context)
            .load(item.img_url)
            .into(holder.imgTipoHabitacion)

        // Guarda el ID del tipo de habitación actual en la variable global.
        idTipoHabitacionGlobal = item.id_tipo_habitacion

        // Configura el clic en el botón "Ver Más" para iniciar la actividad 'activity_habitacion_economica'.
        holder.btnVerMas.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, activity_habitacion_economica::class.java)
            context.startActivity(intent)  // Inicia la actividad de la habitación económica.
        }
    }
}
