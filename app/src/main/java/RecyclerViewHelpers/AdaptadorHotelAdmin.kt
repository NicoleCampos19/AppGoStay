package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbFavoritos
import modelo.tbHotel

class AdaptadorHotelAdmin(var Datos: List<tbHotel>, val clickListener: (tbHotel) -> Unit): RecyclerView.Adapter<ViewHolderHotelAdmin>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotelAdmin {
val vistaHotelAdmin = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_hotel_admin, parent, false)
    return ViewHolderHotelAdmin(vistaHotelAdmin)
    }

    override fun getItemCount() = Datos.size


    override fun onBindViewHolder(holder: ViewHolderHotelAdmin, position: Int) {
        val item = Datos[position]

        val itemHotelAdmin = Datos[position]
        holder.bind(itemHotelAdmin, clickListener)

        Glide.with(holder.itemView)
            .load(itemHotelAdmin.img_url)
            .into(holder.imgHotelCard)
    }

}

