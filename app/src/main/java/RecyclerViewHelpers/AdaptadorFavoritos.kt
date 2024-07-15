package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos

class AdaptadorFavoritos(
    var Datos: List<tbFavoritos>,
    val clickListener: (tbFavoritos) -> Unit
) : RecyclerView.Adapter<AdaptadorFavoritos.ViewHolderFavoritos>() {

    class ViewHolderFavoritos(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rcvFavoritos: RecyclerView = itemView.findViewById(R.id.rcvFavoritos)

        fun bind(item: tbFavoritos, clickListener: (tbFavoritos) -> Unit) {
            // Aquí enlazas los datos del item a los elementos de la vista
            itemView.setOnClickListener { clickListener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFavoritos {
        val vistaFav = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_fav, parent, false)
        return ViewHolderFavoritos(vistaFav)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderFavoritos, position: Int) {
        val item = Datos[position]
        holder.bind(item, clickListener)

        val correoIngresado = activity_iniciar_sesion.correoIngresado

        //Función para que el recycleview se me actualice automáticamente
        fun actualizarRecyclerView(nuevaLista: List<tbFavoritos>) {
            Datos = nuevaLista
            notifyDataSetChanged() //Notifica que hay datos nuevos
        }

        suspend fun obtenerIdUsuario(correo: String): Int? {
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val getId = objConexion?.prepareStatement("SELECT id_usuario FROM tbUsuarios WHERE correo = ?")
                getId?.setString(1, correo)
                val resultSet = getId?.executeQuery()
                if (resultSet != null && resultSet.next()) {
                    resultSet.getInt("id_usuario")
                } else {
                    null
                }
            }
        }

        suspend fun obtenerFavoritos(): List<tbFavoritos> {
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val statement = objConexion?.createStatement()
                val resultSet = statement?.executeQuery("SELECT * FROM tbPreferenciales")
                val tbFavoritos = mutableListOf<tbFavoritos>()

                //Recorrer todos los datos que me trajo el select
                if (resultSet != null) {
                    while (resultSet.next()) {
                        val id_preferenciales = resultSet.getInt("id_preferenciales")
                        val id_hoteles = resultSet.getInt("id_hoteles")
                        val id_usuario = resultSet.getInt("id_usuario")
                        val favorito = tbFavoritos(id_preferenciales, id_hoteles, id_usuario)
                        tbFavoritos.add(favorito)
                    }
                }
                tbFavoritos
            }
        }

        //Ejecutamos la función
        CoroutineScope(Dispatchers.Main).launch {
            val ejecutarFuncion = obtenerFavoritos()

            // Actualizar la lista de datos y el RecyclerView en el hilo principal
            actualizarRecyclerView(ejecutarFuncion)
        }
    }
}