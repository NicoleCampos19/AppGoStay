package RecyclerViewHelpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.hotel_detalles
import kotlinx.coroutines.withContext

class AdaptadorDetalleHotel(private val imageUrls: List<String>) : RecyclerView.Adapter<AdaptadorDetalleHotel.HotelViewHolder>() {

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detalles_hotel_img, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(Context).load(imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}