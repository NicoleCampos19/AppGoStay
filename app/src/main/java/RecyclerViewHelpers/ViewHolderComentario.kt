package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

class ViewHolderComentario(view: View) : RecyclerView.ViewHolder(view) {

    val txtUsuarioCard = view.findViewById<TextView>(R.id.txtUsuarioCard)
    val txtComentarioCard = view.findViewById<TextView>(R.id.txtComentarioCard)
    val imvMas = view.findViewById<ImageView>(R.id.btnMas)
    val imageProfile = view.findViewById<ImageView>(R.id.imageProfile)
}