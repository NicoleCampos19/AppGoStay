package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.Hotel

class HotelAdapter(private val hoteles: List<Hotel>) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hoteles[position]
        holder.tvNombreHotel.text = hotel.nombre

        // Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(hotel.imgUrl)
            .into(holder.imgHotel)
    }

    override fun getItemCount(): Int {
        return hoteles.size
    }

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHotel: ImageView = itemView.findViewById(R.id.imgHotel)
        val tvNombreHotel: TextView = itemView.findViewById(R.id.tvNombreHotel)
    }
}