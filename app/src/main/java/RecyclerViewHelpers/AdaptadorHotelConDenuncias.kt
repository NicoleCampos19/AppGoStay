package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbHotelConDenuncias

class AdaptadorHotelConDenuncias(var Datos: List<tbHotelConDenuncias>): RecyclerView.Adapter<ViewHolderHotelConDenuncias>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotelConDenuncias {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_denuncias, parent, false)
        return ViewHolderHotelConDenuncias(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotelConDenuncias, position: Int) {
        val item = Datos[position]
        holder.lblNumeroDenuncias.text = item.numeroDenuncias.toString()
        holder.lblNombreHotelDenunciado.text = item.nombreHotel
        Glide.with(holder.itemView.context).load(item.imgUrl).into(holder.imvHotelDenunciado)

    }
}