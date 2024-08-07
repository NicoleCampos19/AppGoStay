package RecyclerViewHelpers

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderOfertas (view: View) : RecyclerView.ViewHolder(view) {

    val txtNombreHotel = view.findViewById<TextView>(R.id.txtNombreHotel)
    val txtNombreOferta = view.findViewById<TextView>(R.id.txtNombreOferta)
    val expandableContainer: LinearLayout = view.findViewById(R.id.expandableContainer)
}