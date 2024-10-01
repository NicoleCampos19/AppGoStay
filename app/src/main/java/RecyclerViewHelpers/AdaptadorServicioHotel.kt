package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbServiciosHotel

// Adaptador para un RecyclerView que muestra una lista de servicios de hotel
class AdaptadorServicioHotel(var Datos: List<tbServiciosHotel>): RecyclerView.Adapter<ViewHolderServicioHotel>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderServicioHotel {
        // Infla el layout para cada elemento en el RecyclerView
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_card_servicio_hotel, parent, false)
        return ViewHolderServicioHotel(vista)
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount() = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderServicioHotel, position: Int) {
        val item = Datos[position]
        holder.txtServicioHotelCard.text = item.nombre_servicio
        Glide.with(holder.itemView.context)
            .load(item.img_icono_hotel)
            .into(holder.imgServicioHotel)


    }


}