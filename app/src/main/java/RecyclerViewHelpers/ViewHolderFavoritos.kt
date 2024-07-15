package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderFavoritos(view: View): RecyclerView.ViewHolder(view) {
    val imgFav = view.findViewById<ImageView>(R.id.imgFav)
    val txtNombreHotelCard = view.findViewById<TextView>(R.id.txtNombreHotelCard)
    val tbFavoritos = view.findViewById<ToggleButton>(R.id.tbFavoritos)
    val rcvFavoritos = view.findViewById<RecyclerView>(R.id.rcvFavoritos)
}