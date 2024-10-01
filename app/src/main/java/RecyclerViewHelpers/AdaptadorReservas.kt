package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbHotel
import modelo.tbReservas

// Adaptador para un RecyclerView que muestra una lista de reservas
class AdaptadorReservas (var datos: List<tbReservas>) : RecyclerView.Adapter<ViewHolderReservas>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderReservas {
        // Infla el layout para cada elemento en el RecyclerView
        val vistaReseva = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_mostrar_reservaciones, parent, false)
        return ViewHolderReservas(vistaReseva)
    }

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderReservas, position: Int) {
        val item = datos[position]
    }
    // Devuelve el número de elementos en la lista
    override fun getItemCount()= datos.size
}