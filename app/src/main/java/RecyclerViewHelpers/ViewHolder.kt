package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
val imgServicioHotel = view.findViewById<ImageView>(R.id.imgServicioHotelCard)
val txtServicioHotelCard = view.findViewById<TextView>(R.id.txtServicioHotelCard)
}