package RecyclerViewHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderDenuncias(view: View): RecyclerView.ViewHolder(view){
    val lblNombreHotel = view.findViewById<TextView>(R.id.lblNombreHotelVerMas)
    val lblDenunciaHotel = view.findViewById<TextView>(R.id.lblDenunciaVerMas)
}