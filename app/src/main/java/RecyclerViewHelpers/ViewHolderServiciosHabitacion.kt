package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderServiciosHabitacion (view: View): RecyclerView.ViewHolder(view) {
    val imgServicioHabitacion = view.findViewById<ImageView>(R.id.imgCardServicioHabitacion)
    val txtNombreServicioHabitacion = view.findViewById<TextView>(R.id.tvCardNombreServicioHabitacion)

}