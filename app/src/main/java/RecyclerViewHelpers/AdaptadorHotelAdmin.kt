package RecyclerViewHelpers

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbHotel

// Adaptador para un RecyclerView que muestra una lista de hoteles administrados por un administrador
class AdaptadorHotelAdmin(var Datos: List<tbHotel>, val clickListener: (tbHotel) -> Unit) : RecyclerView.Adapter<ViewHolderHotelAdmin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotelAdmin {
        val vistaHotelAdmin = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_hotel_admin, parent, false)
        return ViewHolderHotelAdmin(vistaHotelAdmin)
    }

    // Método para eliminar el hotel de la base de datos y de la lista
    fun eliminarDatosHotel(id_hoteles: Int, posicion: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            try {
                // Eliminar el hotel de la base de datos
                val deleteHotel = objConexion?.prepareStatement("DELETE FROM tbHoteles WHERE id_hoteles = ?")
                deleteHotel?.setInt(1, id_hoteles)
                deleteHotel?.executeUpdate()

                // Actualizar la lista y la interfaz en el hilo principal
                withContext(Dispatchers.Main) {
                    val listaDatos = Datos.toMutableList()
                    listaDatos.removeAt(posicion)
                    Datos = listaDatos
                    notifyItemRemoved(posicion)
                    notifyDataSetChanged() // Refresca la lista
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Mostrar error en la interfaz en caso de fallo
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar el hotel", Toast.LENGTH_SHORT).show()
                }
            } finally {
                objConexion?.close()
            }
        }
    }

    // Método alternativo para eliminar el hotel (si lo necesitas)
    fun eliminarDatoHotel(idHotel: Int, position: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                // Ejecuta la consulta SQL para eliminar el hotel
                objConexion?.prepareStatement("DELETE FROM tbHoteles WHERE id_hoteles = ?")?.apply {
                    setInt(1, idHotel)
                    executeUpdate()
                }

                // Actualiza la interfaz en el hilo principal
                withContext(Dispatchers.Main) {
                    Datos = Datos.toMutableList().apply {
                        removeAt(position)
                    }
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar el hotel", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotelAdmin, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context

        // Establece un listener para mostrar un menú emergente cuando se hace clic en la imagen
        holder.ImageView.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menuu, context, item, position)
        }

        val itemHotelAdmin = Datos[position]
        holder.bind(itemHotelAdmin, clickListener)

        // Usa Glide para cargar la imagen del hotel desde la URL y mostrarla en el ImageView
        Glide.with(holder.itemView)
            .load(itemHotelAdmin.img_url)
            .into(holder.imgHotelCard)
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbHotel, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_2 -> {
                    eliminarDatosHotel(item.id_hoteles, position, context)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}



