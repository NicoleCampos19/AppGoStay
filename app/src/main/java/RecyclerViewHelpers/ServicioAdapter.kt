package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.ReservaInfo
import modelo.ServicioInfo

class ServicioAdapter (private val servicios: List<ServicioInfo>) : RecyclerView.Adapter<ServicioAdapter.ServicioInfoViewHolder>() {

    class ServicioInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Aquí enlazas los elementos del layout con el ViewHolder
        val imagenView: ImageView = view.findViewById(R.id.imgServicioHotelCard)
        val nombreView: TextView = view.findViewById(R.id.txtServicioHotelCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_servicio_hotel, parent, false)
        return ServicioInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioInfoViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.nombreView.text = servicio.nombre_servicio
        Glide.with(holder.imagenView.context)
            .load(servicio.img_icono_hotel)
            .into(holder.imagenView)
    }

        override fun getItemCount(): Int {
            return servicios.size
        }

}
