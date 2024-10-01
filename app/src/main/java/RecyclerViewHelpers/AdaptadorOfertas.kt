package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.PaginaInicio
import emily.jacobo.gostay.PaginaInicio.Companion.idUsuarioGlobalL
import emily.jacobo.gostay.PaginaInicio.Companion.nombreUsuarioGlobalL
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import emily.jacobo.gostay.hotel_detalles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbHotel
import modelo.tbOfertas

class AdaptadorOfertas(var Datos: List<tbOfertas>): RecyclerView.Adapter<ViewHolderOfertas>() {

    private var expandedPosition = -1  // Controla la posición del item expandido
    companion object{
        var descuentoTotalGlobal = 0.0  // Variable estática que guarda el descuento total global
    }

    // Método que crea y devuelve un nuevo ViewHolder inflando el layout 'activity_item_ofertas'.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOfertas {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_ofertas, parent, false)
        return ViewHolderOfertas(vista)
    }

    // Retorna el tamaño de la lista 'Datos', que define cuántos elementos se mostrarán
    override fun getItemCount() = Datos.size

    // Método que vincula los datos de un elemento específico a un ViewHolder
    override fun onBindViewHolder(holder: ViewHolderOfertas, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context

        // Asigna los valores correspondientes a los TextViews y variables globales
        holder.txtNombreHotel.text = item.nombre
        holder.txtNombreOferta.text = item.nombre_oferta
        holder.txtDescuento.text = "Descuento: ${item.descuentoTotal}%"
        descuentoTotalGlobal = item.descuentoTotal
        PaginaInicio.hotelIdGlobal = item.id_hoteles

        val correUsuarioRecivido  = activity_iniciar_sesion.correoIngresado

        // Si hay un correo, obtiene el nombre y el id del usuario en segundo plano
        if (correUsuarioRecivido != null) {
            obtenerNombreUsuarioEnGl(correUsuarioRecivido)
            obteneridUsuarioEnGl(correUsuarioRecivido)
        }

        // Expande o colapsa el contenedor basado en si el item está expandido
        val isExpanded = position == expandedPosition
        holder.expandableContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Listener para expandir/colapsar el item y abrir detalles del hotel al hacer clic
        holder.itemView.setOnClickListener {
            val prevExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) {
                // Llama a un método para obtener los detalles del hotel desde la base de datos
                GlobalScope.launch(Dispatchers.Main) {
                    val hotelDetalles = withContext(Dispatchers.IO) { obtenerHotelPorId(item.id_hoteles) }
                    if (hotelDetalles != null) {
                        val intent = Intent(context, hotel_detalles::class.java).apply {
                            putExtra("id_hoteles", item.id_hoteles)
                            putExtra("prev_activity", "Ofertas")
                            putExtra("hotel", hotelDetalles)  // Pasa el objeto hotel con detalles
                        }
                        context.startActivity(intent)  // Inicia la actividad con detalles del hotel
                    }
                }
                -1  // Colapsa la oferta expandida
            } else {
                position  // Expande la oferta actual
            }
            notifyItemChanged(prevExpandedPosition)  // Actualiza el ítem anterior
            notifyItemChanged(expandedPosition)  // Actualiza el ítem expandido
        }
    }

    // Método para obtener el nombre del usuario desde la base de datos en segundo plano
    private fun obtenerNombreUsuarioEnGl(correoUsuario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val nombreUsuario = cargarNombreUsuario(correoUsuario)
            withContext(Dispatchers.Main) {
                nombreUsuarioGlobalL = nombreUsuario
            }
        }
    }

    // Consulta en la base de datos el nombre del usuario con el correo dado
    private fun cargarNombreUsuario(correoUsuario: String): String? {
        var nombreUsuario: String? = null
        val conexion = ClaseConexion().cadenaConexion()
        val query = """
        SELECT nombre_usuario FROM tbUsuarios WHERE correo = ?
        """
        val statement = conexion?.prepareStatement(query)
        statement?.setString(1, correoUsuario)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            nombreUsuario = resultSet.getString("nombre_usuario")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return nombreUsuario
    }

    // Método para obtener el ID del usuario desde la base de datos en segundo plano
    private fun obteneridUsuarioEnGl(correoUsuario: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val idUsuario = cargaridUsuario(correoUsuario)
            withContext(Dispatchers.Main) {
                idUsuarioGlobalL = idUsuario
            }
        }
    }

    // Consulta en la base de datos el ID del usuario con el correo dado
    private fun cargaridUsuario(correoUsuario: String): Int? {
        var idUsuario: Int? = null
        val conexion = ClaseConexion().cadenaConexion()
        val query = """
        SELECT id_usuario FROM tbUsuarios WHERE correo = ?
        """
        val statement = conexion?.prepareStatement(query)
        statement?.setString(1, correoUsuario)
        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            idUsuario = resultSet.getInt("id_usuario")
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return idUsuario
    }

    // Método para obtener detalles de un hotel por su ID desde la base de datos en segundo plano
    private suspend fun obtenerHotelPorId(id_hoteles: Int): tbHotel? {
        return try {
            val listaHoteles = obtenerHoteles()  // Obtiene la lista de hoteles
            listaHoteles.find { it.id_hoteles == id_hoteles }  // Busca el hotel con el ID dado
        } catch (e: Exception) {
            e.printStackTrace()  // Muestra el error en los logs
            null  // Retorna null en caso de error
        }
    }

    // Método que retorna una lista de hoteles consultada desde la base de datos
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

                // Crea un objeto hotel y lo añade a la lista
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
            e.printStackTrace()  // Muestra el error en los logs
        }
        return listaHoteles  // Retorna la lista de hoteles
    }
}
