package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbDenuncias

class AdaptadorDenuncias(var Datos: List<tbDenuncias>): RecyclerView.Adapter<ViewHolderDenuncias>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDenuncias {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_ver_mas_denuncias, parent, false)
        return ViewHolderDenuncias(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderDenuncias, position: Int) {
        val item = Datos[position]

        holder.lblDenunciaHotel.text = item.nombre_denuncia
        holder.lblNombreHotel.text = item.nombre
    }
}