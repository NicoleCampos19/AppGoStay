package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbDenuncias

// Adaptador para un RecyclerView que muestra una lista de denuncias
class AdaptadorDenuncias(var Datos: List<tbDenuncias>): RecyclerView.Adapter<ViewHolderDenuncias>() {
    // Crea y devuelve un ViewHolderDenuncias cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDenuncias {
        // Infla el layout para cada elemento en el RecyclerView (item de denuncias)
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_ver_mas_denuncias, parent, false)
        return ViewHolderDenuncias(vista)
    }

    // Devuelve el número de elementos en la lista de denuncias
    override fun getItemCount() = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderDenuncias, position: Int) {
        val item = Datos[position]

        // Asigna el nombre de la denuncia al label correspondiente en el ViewHolder
        holder.lblDenunciaHotel.text = item.nombre_denuncia
        // Asigna el nombre del hotel asociado a la denuncia al label correspondiente
        holder.lblNombreHotel.text = item.nombre
    }
}