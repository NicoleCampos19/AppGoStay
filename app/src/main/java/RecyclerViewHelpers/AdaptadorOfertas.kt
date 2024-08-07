package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbOfertas

class AdaptadorOfertas(var Datos: List<tbOfertas>): RecyclerView.Adapter<ViewHolderOfertas>() {

    private var expandedPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOfertas {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_ofertas, parent, false)
        return ViewHolderOfertas(vista)
    }



    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderOfertas, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        val nombreHotel = item.nombre
        val nombreOferta = item.nombre_oferta
        holder.txtNombreHotel.text = nombreHotel
        holder.txtNombreOferta.text = nombreOferta

        val isExpanded = position == expandedPosition
        holder.expandableContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val prevExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) {
                -1
            } else {
                position
            }
            notifyItemChanged(prevExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }
}

