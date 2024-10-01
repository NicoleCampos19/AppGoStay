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

class ReservaAdapter(private val reservas: List<ReservaInfo>) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    // ViewHolder que enlaza los elementos del layout 'activity_item_card_mostrar_reservaciones' con las propiedades del ViewHolder.
    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val entradaView: TextView = view.findViewById(R.id.tvCardEntrada)  // TextView para mostrar la fecha de entrada.
        val salidaView: TextView = view.findViewById(R.id.tvCardSalida)  // TextView para mostrar la fecha de salida.
        val hotelView: TextView = view.findViewById(R.id.tvCardHotelMostrar)  // TextView para mostrar el nombre del hotel.
        val usuarioView: TextView = view.findViewById(R.id.tvCardNombreUsuarioMostrar)  // TextView para mostrar el nombre del usuario.
        val tipoHabitacionView: TextView = view.findViewById(R.id.tvCardTipoHabitacion)  // TextView para mostrar el tipo de habitación.
        val imagenView: ImageView = view.findViewById(R.id.imgCardHotel)  // ImageView para mostrar la imagen del hotel.
    }

    // Método que crea y devuelve un nuevo ViewHolder inflando el layout 'activity_item_card_mostrar_reservaciones'.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_mostrar_reservaciones, parent, false)
        return ReservaViewHolder(view)
    }

    // Método que vincula los datos de una reserva específica a un ViewHolder.
    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]

        // Asigna los datos de la reserva (entrada, salida, hotel, usuario, tipo de habitación) a los TextViews correspondientes.
        holder.entradaView.text = reserva.entrada
        holder.salidaView.text = reserva.salida
        holder.hotelView.text = reserva.hotel_nombre
        holder.usuarioView.text = reserva.usuario_nombre
        holder.tipoHabitacionView.text = reserva.nombre_tipo_habitacion

        // Carga la imagen del hotel usando Glide y la muestra en el ImageView.
        Glide.with(holder.imagenView.context)
            .load(reserva.img_url)
            .into(holder.imagenView)
    }

    // Retorna el tamaño de la lista 'reservas', que define cuántos elementos se mostrarán.
    override fun getItemCount(): Int {
        return reservas.size
    }
}
