package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_habitacion_economica
import emily.jacobo.gostay.activity_reserva
import modelo.tbTipoHabitacion

class AdaptorTipoHabitacion (val Datos : List<tbTipoHabitacion>) : RecyclerView.Adapter<ViewHolderTipoHabitacion>() {

    companion object{
        var idTipoHabitacionGlobal = -1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTipoHabitacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tipohabitacion, parent, false)
        return ViewHolderTipoHabitacion(view)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderTipoHabitacion, position: Int) {
        val item = Datos[position]
        holder.txtNombreTipoHabitacion.text = item.nombre
        holder.txtPrecioTipoHabitacion.text = "$${item.precio}"
        idTipoHabitacionGlobal = item.id_tipo_habitacion

        holder.btnVerMas.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, activity_habitacion_economica::class.java)
            context.startActivity(intent)        }
    }
}