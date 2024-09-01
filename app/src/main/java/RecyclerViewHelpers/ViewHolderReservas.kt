package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderReservas (view: View): RecyclerView.ViewHolder(view) {
     //val vCardImagenHotel = view.findViewById<ImageView>(R.id.vCardImagenHotel)
    val vCardHotelMostrar = view.findViewById<TextView>(R.id.tvCardHotelMostrar)

}