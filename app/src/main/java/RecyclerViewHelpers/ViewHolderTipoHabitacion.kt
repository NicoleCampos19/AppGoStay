package RecyclerViewHelpers

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderTipoHabitacion (view: View) : RecyclerView.ViewHolder(view) {
    val txtNombreTipoHabitacion = view.findViewById<TextView>(R.id.txt_nombre_tipo_habitacion)
    val txtPrecioTipoHabitacion = view.findViewById<TextView>(R.id.txt_precio_tipo_habitacion)
    val imgTipoHabitacion = view.findViewById<ImageView>(R.id.img_tipo_habitacion)
    val btnVerMas = view.findViewById<Button>(R.id.btn_ver_mas)
}