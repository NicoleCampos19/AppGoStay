package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbTipoHabitacion

class AdaptorTipoHabitacion (val Datos : List<tbTipoHabitacion>) : RecyclerView.Adapter<ViewHolderTipoHabitacion>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTipoHabitacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tipohabitacion, parent, false)
        return ViewHolderTipoHabitacion(view)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderTipoHabitacion, position: Int) {
        val item = Datos[position]
        holder.txtNombreTipoHabitacion.text = item.nombre
        holder.txtPrecioTipoHabitacion.text = "$${item.precio}"

        holder.btnVerMas.setOnClickListener {
            // Implementar la acción al hacer clic en "Ver más"
        }
    }
}