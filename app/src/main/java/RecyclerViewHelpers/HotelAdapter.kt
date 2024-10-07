package RecyclerViewHelpers

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos
import modelo.tbHotel

class HotelAdapter(
    private var datos: List<tbHotel>, // Lista de datos de hoteles
    private val esFavoritos: Boolean, // Indica si se está visualizando la lista de favoritos
    private val clickListener: (tbHotel) -> Unit // Listener para manejar clics en los elementos
) : RecyclerView.Adapter<ViewHolderHotel>() {

    // Método para inflar la vista de cada hotel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vistaHotel =
            LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }

    // Devuelve la cantidad de elementos en la lista
    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {
        val item = datos[position]
        val sharedPreferences = holder.itemView.context.getSharedPreferences(
            "userPreferences", android.content.Context.MODE_PRIVATE)
        val correoIngresado = sharedPreferences.getString("email", null)

        Log.e("Correo", "$correoIngresado")
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

        // Función para verificar si un hotel está en favoritos para el usuario
        suspend fun estaFavorito(id_hotel: Int, id_usuario: Int): Boolean{
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val query = objConexion?.prepareStatement("SELECT 1 FROM tbPreferenciales WHERE id_hoteles = ? AND id_usuario = ?")!!
                query.setInt(1, id_hotel)
                query.setInt(2, id_usuario)
                val resultSet = query.executeQuery()
                if(resultSet.next()){
                    true;
                } else{
                    false
                }
            }
        }


        // Lanza una coroutine en el hilo principal para manejar el cambio de estado del toggle de favoritos
        holder.tbToogleFavoritos.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.Main).launch {
                val id_usuario = obtenerIdUsuario(correoIngresado!!)
                if (id_usuario != null) {
                    withContext(Dispatchers.IO) {
                        val objConexion = ClaseConexion().cadenaConexion()
                        if (!esFavoritos && isChecked && !estaFavorito(item.id_hoteles,id_usuario)) {
                            val agregarFavoritos = objConexion?.prepareStatement("INSERT INTO tbPreferenciales (id_hoteles, id_usuario) VALUES (?, ?)")!!
                            agregarFavoritos.setInt(1, item.id_hoteles)
                            agregarFavoritos.setInt(2, id_usuario)
                            Log.e("hotel", "${item.id_hoteles}")
                            Log.e("user", "${item.id_usuario}")
                            agregarFavoritos.executeUpdate()
                            val commit = objConexion.prepareStatement("commit")
                            commit.executeUpdate()
                        } else {
                            if(!isChecked){
                                val deleteFavorito =
                                    objConexion?.prepareStatement("DELETE FROM tbPreferenciales WHERE id_hoteles = ? AND id_usuario = ?")!!
                                deleteFavorito.setInt(1, item.id_hoteles)
                                deleteFavorito.setInt(2, id_usuario)
                                deleteFavorito.executeUpdate()
                                val commit = objConexion.prepareStatement("commit")
                                commit.executeUpdate()
                            }
                            if(esFavoritos && !isChecked)
                                withContext(Dispatchers.Main) {
                                    datos = datos.toMutableList().also { it.removeAt(position) }
                                    notifyItemRemoved(position)
                                    notifyDataSetChanged()
                                }
                        }
                        val commit = objConexion?.prepareStatement("COMMIT")!!
                        commit.executeUpdate()
                    }
                }
            }
        }
        // Lanza otra coroutine para verificar si el hotel está en favoritos al cargar el ítem
        CoroutineScope(Dispatchers.Main).launch {
            val id_usuario = obtenerIdUsuario(correoIngresado!!)
            if(id_usuario!= null){
                holder.tbToogleFavoritos.isChecked = estaFavorito(item.id_hoteles, id_usuario)
            }
        }
        holder.bind(item, clickListener)
        Glide.with(holder.itemView)
            .load(item.img_url)
            .into(holder.imgHotelCard)
    }
}