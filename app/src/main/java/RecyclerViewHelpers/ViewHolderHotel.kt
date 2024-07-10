package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbHotel

class ViewHolderHotel (view: View): RecyclerView.ViewHolder(view) {
    val imgHotelCard = view.findViewById<ImageView>(R.id.imgHotelCard)
    val txtNombreHotelCard = view.findViewById<TextView>(R.id.txtNombreHotelCard)
    val tbToogleFavoritos = view.findViewById<ToggleButton>(R.id.tbFavoritosHotel)

    fun bind(hotel: tbHotel, clickListener: (tbHotel) -> Unit) {

        txtNombreHotelCard.text = hotel.nombreHotel
        Glide.with(itemView)
            .load(hotel.img_url)
            .into(imgHotelCard)
        itemView.setOnClickListener { clickListener(hotel) }
    }
}