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

class ReservaAdapter (private val reservas: List<ReservaInfo>) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Aquí enlazas los elementos del layout con el ViewHolder
        val entradaView: TextView = view.findViewById(R.id.tvCardEntrada)
        val salidaView: TextView = view.findViewById(R.id.tvCardSalida)
        val hotelView: TextView = view.findViewById(R.id.tvCardHotelMostrar)
        val usuarioView: TextView = view.findViewById(R.id.tvCardNombreUsuarioMostrar)
        val tipoHabitacionView: TextView = view.findViewById(R.id.tvCardTipoHabitacion)
        val imagenView: ImageView = view.findViewById(R.id.imgCardHotel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_mostrar_reservaciones, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]
        holder.entradaView.text = reserva.entrada
        holder.salidaView.text = reserva.salida
        holder.hotelView.text = reserva.hotel_nombre
        holder.usuarioView.text = reserva.usuario_nombre
        holder.tipoHabitacionView.text = reserva.nombre_tipo_habitacion
        Glide.with(holder.imagenView.context)
            .load(reserva.img_url)
            .into(holder.imagenView)
    }

    override fun getItemCount(): Int {
        return reservas.size
    }
}