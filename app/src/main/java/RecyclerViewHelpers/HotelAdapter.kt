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

class HotelAdapter(var Datos: List<tbHotel>, var DatosDos: List<tbFavoritos>, val clickListener: (tbHotel) -> Unit): RecyclerView.Adapter<ViewHolderHotel>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vistaHotel =
            LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {
        val item = Datos[position]
        val itemdos = DatosDos[position]
        val correoIngresado = activity_iniciar_sesion.correoIngresado

        suspend fun obtenerIdUsuario(correo: String): Int? {
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val getId =
                    objConexion?.prepareStatement("SELECT id_usuario FROM tbUsuarios WHERE correo = ?")
                getId?.setString(1, correo)
                val resultSet = getId?.executeQuery()
                if (resultSet != null && resultSet.next()) {
                    resultSet.getInt("id_usuario")
                } else {
                    null
                }
            }
        }
        holder.tbToogleFavoritos.setOnCheckedChangeListener { buttonView, isChecked ->
            //Si el corazón esta lleno
            if (isChecked) {
                GlobalScope.launch(Dispatchers.Main) {
                    val idUsuario = obtenerIdUsuario(correoIngresado)
                    if (idUsuario != null) {
                        withContext(Dispatchers.IO) {
                            val objConexion = ClaseConexion().cadenaConexion()
                            val agregarFavoritos =
                                objConexion?.prepareStatement("INSERT INTO tbPreferenciales (id_hoteles, id_usuario) VALUES (?, ?)")!!
                            agregarFavoritos?.setInt(1, item.id_hoteles)
                            agregarFavoritos?.setInt(2, idUsuario)

                            agregarFavoritos?.executeUpdate()
                        }
                    }
                }
                //Si el corazón esta vacío
            } else {
                    val listaFavoritos = DatosDos.toMutableList()
                    listaFavoritos.removeAt(position)

                    GlobalScope.launch(Dispatchers.IO) {
                        val objConexion = ClaseConexion().cadenaConexion()

                        val deleteFavorito =
                            objConexion?.prepareStatement("delete tbPreferenciales where id_preferencial = ?")!!
                        deleteFavorito.setInt(1, itemdos.id_preferenciales)
                        deleteFavorito.executeUpdate()

                        val commit = objConexion.prepareStatement("commit")
                        commit.executeUpdate()
                    }
                    //Notificamos el cambio para que refresque la lista
                    DatosDos = listaFavoritos.toList()
                    //Quito los datos de la lista
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                }
            }
            val itemHotel = Datos[position]
            holder.bind(itemHotel, clickListener)

            Glide.with(holder.itemView)
                .load(itemHotel.img_url)
                .into(holder.imgHotelCard)
        }
    }
