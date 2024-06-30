package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderHotel (view: View): RecyclerView.ViewHolder(view) {
    val imgHotelCard = view.findViewById<ImageView>(R.id.imgHotelCard)
    val txtNombreHotelCard = view.findViewById<TextView>(R.id.txtNombreHotelCard)
}