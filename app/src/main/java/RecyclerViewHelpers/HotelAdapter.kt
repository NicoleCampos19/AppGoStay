package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.hotel_detalles
import modelo.tbHotel

class HotelAdapter(var Datos: List<tbHotel>, val clickListener: (tbHotel) -> Unit): RecyclerView.Adapter<ViewHolderHotel>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vistaHotel = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }



    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {
        val itemHotel = Datos[position]
        holder.bind(itemHotel, clickListener)


        Glide.with(holder.itemView)
            .load(itemHotel.img_url)
            .into(holder.imgHotelCard)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, hotel_detalles::class.java).apply {
                putExtra("id_hoteles", itemHotel.id_hoteles)
                putExtra("hotel", itemHotel)
            }
            context.startActivity(intent)
        }
    }
}