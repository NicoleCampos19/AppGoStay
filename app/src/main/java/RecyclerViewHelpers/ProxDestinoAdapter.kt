package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos
import modelo.tbHotel

class ProxDestinoAdapter(
    var Datos: List<tbHotel>, // Lista de hoteles que se va a mostrar
    var DatosDos: List<tbFavoritos>, // Lista de favoritos vinculada a los hoteles
    val clickListener: (tbHotel) -> Unit // Función de clic para manejar interacciones en los hoteles
) : RecyclerView.Adapter<ViewHolderHotel>() {

    // Método que crea y devuelve un nuevo ViewHolder inflando el layout 'item_hotel'
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vistaHotel = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }

    // Retorna el tamaño de la lista 'Datos', que define cuántos elementos se mostrarán
    override fun getItemCount() = Datos.size

    // Método que vincula los datos de un hotel específico a un ViewHolder
    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {
        val item = Datos[position] // Obtiene el hotel en la posición actual.
        val correoIngresado = activity_iniciar_sesion.correoIngresado // Obtiene el correo del usuario que ha iniciado sesión

        // Verifica si 'DatosDos' tiene suficientes elementos para la posición actual
        val itemdos: tbFavoritos? = if (position < DatosDos.size) DatosDos[position] else null

        // Función suspendida para obtener el ID de un usuario a partir del correo
        suspend fun obtenerIdUsuario(correo: String): Int? {
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val getId = objConexion?.prepareStatement("SELECT id_usuario FROM tbUsuarios WHERE correo = ?")
                getId?.setString(1, correo)
                val resultSet = getId?.executeQuery()
                if (resultSet != null && resultSet.next()) {
                    resultSet.getInt("id_usuario") // Retorna el ID del usuario si lo encuentra
                } else {
                    null
                }
            }
        }

        // Controlador del cambio de estado del ToggleButton de favoritos
        holder.tbToogleFavoritos.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Agregar el hotel a favoritos si el botón está activado
                GlobalScope.launch(Dispatchers.Main) {
                    val idUsuario = obtenerIdUsuario(correoIngresado) // Obtiene el ID del usuario actual
                    if (idUsuario != null) {
                        withContext(Dispatchers.IO) {
                            val objConexion = ClaseConexion().cadenaConexion()
                            val agregarFavoritos = objConexion?.prepareStatement("INSERT INTO tbPreferenciales (id_hoteles, id_usuario) VALUES (?, ?)")!!
                            agregarFavoritos.setInt(1, item.id_hoteles)
                            agregarFavoritos.setInt(2, idUsuario)
                            agregarFavoritos.executeUpdate() // Inserta la relación de hotel favorito en la base de datos.
                        }
                    }
                }
            } else {
                // Eliminar el hotel de favoritos si el botón está desactivado
                if (itemdos != null) {
                    val listaFavoritos = DatosDos.toMutableList()
                    listaFavoritos.removeAt(position) // Remueve el hotel de la lista local de favoritos
                    GlobalScope.launch(Dispatchers.IO) {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val deleteFavorito = objConexion?.prepareStatement("DELETE FROM tbPreferenciales WHERE id_preferencial = ?")!!
                        deleteFavorito.setInt(1, itemdos.id_preferenciales) // Elimina el favorito en la base de datos
                        deleteFavorito.executeUpdate()

                        val commit = objConexion.prepareStatement("COMMIT") // Realiza commit en la base de datos
                        commit.executeUpdate()
                    }

                    DatosDos = listaFavoritos.toList() // Actualiza la lista de favoritos
                    GlobalScope.launch(Dispatchers.Main) {
                        notifyItemRemoved(position) // Notifica la eliminación del item en la lista
                        notifyDataSetChanged() // Refresca la lista
                    }
                }
            }
        }

        // Vincula los datos del hotel con la interfaz gráfica
        holder.bind(item, clickListener)

        // Carga la imagen del hotel usando Glide y la muestra en el ImageView
        Glide.with(holder.itemView)
            .load(item.img_url)
            .into(holder.imgHotelCard)
    }
}

