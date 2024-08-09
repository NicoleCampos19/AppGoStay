package RecyclerViewHelpers

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderHotelConDenuncias(view: View): RecyclerView.ViewHolder(view) {
    val imvHotelDenunciado = view.findViewById<ImageView>(R.id.imvFotoHotelDenunciado)
    val lblNombreHotelDenunciado = view.findViewById<TextView>(R.id.lblNombreHotelDenunciado)
    val lblNumeroDenuncias = view.findViewById<TextView>(R.id.lblNumeroDenuncias)
    val btnVerDenunciasHotel = view.findViewById<Button>(R.id.btnVerDenunciasHotel)
}