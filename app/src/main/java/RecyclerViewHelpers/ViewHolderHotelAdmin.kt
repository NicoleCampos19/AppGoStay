package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbHotel

class ViewHolderHotelAdmin (view: View): RecyclerView.ViewHolder(view){
    val imgHotelCard = view.findViewById<ImageView>(R.id.imgHotelCard)
    val txtNombreHotelCard = view.findViewById<TextView>(R.id.txtNombreHotelCard)
    val ImageView = view.findViewById<ImageView>(R.id.menu_button)

    fun bind(hotel: tbHotel, clickListener: (tbHotel) -> Unit) {

        txtNombreHotelCard.text = hotel.nombreHotel
        Glide.with(itemView)
            .load(hotel.img_url)
            .into(imgHotelCard)
        itemView.setOnClickListener { clickListener(hotel) }
    }
}