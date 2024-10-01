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

class ServicioAdapter(private val servicios: List<ServicioInfo>) : RecyclerView.Adapter<ServicioAdapter.ServicioInfoViewHolder>() {

    // ViewHolder que enlaza los elementos del layout 'item_card_servicio_hotel' con las propiedades del ViewHolder
    class ServicioInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagenView: ImageView = view.findViewById(R.id.imgServicioHotelCard)  // ImageView para mostrar el icono del servicio
        val nombreView: TextView = view.findViewById(R.id.txtServicioHotelCard)  // TextView para mostrar el nombre del servicio
    }

    // Método que crea y devuelve un nuevo ViewHolder inflando el layout 'item_card_servicio_hotel'
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_servicio_hotel, parent, false)
        return ServicioInfoViewHolder(view)
    }

    // Método que vincula los datos de un servicio específico a un ViewHolder
    override fun onBindViewHolder(holder: ServicioInfoViewHolder, position: Int) {
        val servicio = servicios[position]

        // Asigna el nombre del servicio al TextView correspondiente
        holder.nombreView.text = servicio.nombre_servicio

        // Carga la imagen del servicio usando Glide y la muestra en el ImageView
        Glide.with(holder.imagenView.context)
            .load(servicio.img_icono_hotel)
            .into(holder.imagenView)
    }

    // Retorna el tamaño de la lista 'servicios', que define cuántos elementos se mostrarán
    override fun getItemCount(): Int {
        return servicios.size
    }
}

