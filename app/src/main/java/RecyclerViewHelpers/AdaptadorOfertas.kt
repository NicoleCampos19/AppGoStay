package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import emily.jacobo.gostay.hotel_detalles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbHotel
import modelo.tbOfertas

class AdaptadorOfertas(var Datos: List<tbOfertas>): RecyclerView.Adapter<ViewHolderOfertas>() {

    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOfertas {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_ofertas, parent, false)
        return ViewHolderOfertas(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderOfertas, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        val nombreOferta = item.nombre_oferta
        val descuentoTotal = item.descuentoTotal
        val id_hoteles = item.id_hoteles
        holder.txtNombreHotel.text = item.nombre
        holder.txtNombreOferta.text = nombreOferta
        holder.txtDescuento.text = "Descuento: ${descuentoTotal.toString()}%"


        val isExpanded = position == expandedPosition
        holder.expandableContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val prevExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) {
                // Si ya está expandido, obtener detalles del hotel desde la base de datos en un hilo separado
                GlobalScope.launch(Dispatchers.Main) {
                    val hotelDetalles = withContext(Dispatchers.IO) { obtenerHotelPorId(id_hoteles) }
                    if (hotelDetalles != null) {
                        val intent = Intent(context, hotel_detalles::class.java).apply {
                            putExtra("id_hoteles", id_hoteles)
                            putExtra("prev_activity", "Ofertas")
                            putExtra("hotel", hotelDetalles) // Pasar el objeto hotel con detalles desde la BD
                        }
                        context.startActivity(intent)
                    } else {
                        // Manejar el caso en que no se encuentra el hotel
                    }
                }
                -1 // Recolapsar la oferta expandida al regresar
            } else {
                position
            }
            notifyItemChanged(prevExpandedPosition)
            notifyItemChanged(expandedPosition)
        }
    }

    private suspend fun obtenerHotelPorId(id_hoteles: Int): tbHotel? {
        return try {
            val listaHoteles = obtenerHoteles() // Usar tu método para obtener todos los hoteles
            listaHoteles.find { it.id_hoteles == id_hoteles } // Buscar el hotel con el ID correspondiente
        } catch (e: Exception) {
            e.printStackTrace() // Mostrar el error en el log
            null // Retornar null en caso de error
        }
    }

    private fun obtenerHoteles(): List<tbHotel> {
        val listaHoteles = mutableListOf<tbHotel>()
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbHoteles")

            while (resultSet?.next() == true) {
                val id_hoteles = resultSet.getInt("id_hoteles")
                val nombre = resultSet.getString("nombre")
                val descripcion = resultSet.getString("descripcion")
                val direccion = resultSet.getString("direccion")
                val correo = resultSet.getString("correo")
                val cantidad_habitaciones = resultSet.getInt("cantidad_habitaciones")
                val img_url = resultSet.getString("img_url")
                val id_usuario = resultSet.getInt("id_usuario")

                val hotel = tbHotel(
                    id_hoteles,
                    nombre,
                    descripcion,
                    direccion,
                    correo,
                    cantidad_habitaciones,
                    img_url,
                    id_usuario
                )

                listaHoteles.add(hotel)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Mostrar el error en el log
        }
        return listaHoteles
    }
}
