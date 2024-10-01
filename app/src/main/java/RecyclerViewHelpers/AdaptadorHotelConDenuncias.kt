package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.VerMasDenuncias
import modelo.tbHotelConDenuncias

class AdaptadorHotelConDenuncias(var Datos: List<tbHotelConDenuncias>): RecyclerView.Adapter<ViewHolderHotelConDenuncias>() {

    // Método que crea y devuelve un nuevo ViewHolder inflando el layout 'activity_item_denuncias'
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotelConDenuncias {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_denuncias, parent, false)
        return ViewHolderHotelConDenuncias(vista)
    }

    // Retorna el tamaño de la lista 'Datos', que define cuántos elementos se mostrarán
    override fun getItemCount() = Datos.size

    // Método que vincula los datos de un elemento específico a un ViewHolder
    override fun onBindViewHolder(holder: ViewHolderHotelConDenuncias, position: Int) {
        val item = Datos[position]

        // Asigna el número de denuncias al TextView correspondiente
        holder.lblNumeroDenuncias.text = item.numeroDenuncias.toString()

        // Asigna el nombre del hotel denunciado al TextView correspondiente
        holder.lblNombreHotelDenunciado.text = item.nombreHotel

        // Utiliza Glide para cargar la imagen del hotel denunciado desde una URL
        Glide.with(holder.itemView.context).load(item.imgUrl).into(holder.imvHotelDenunciado)

        // Configura el click en el botón 'Ver Denuncias' para abrir una nueva actividad pasando el ID del hotel
        holder.btnVerDenunciasHotel.setOnClickListener{
            val context = holder.btnVerDenunciasHotel.context
            val intent = Intent(context, VerMasDenuncias::class.java)
            intent.putExtra("id_hoteles", item.id_hoteles)  // Pasa el ID del hotel como extra
            context.startActivity(intent)  // Inicia la nueva actividad
        }
    }
}
