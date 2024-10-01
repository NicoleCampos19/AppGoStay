package RecyclerViewHelpers

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.tbComentarios
import modelo.tbFavoritos
import modelo.tbHotel


// Adaptador para un RecyclerView que muestra una lista de hoteles administrados por un administrador
class AdaptadorHotelAdmin(var Datos: List<tbHotel>, val clickListener: (tbHotel) -> Unit) : RecyclerView.Adapter<ViewHolderHotelAdmin>() {

    // Crea y devuelve un ViewHolderHotelAdmin cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotelAdmin {
        // Infla el layout de cada elemento en el RecyclerView (item de hotel administrado)
        val vistaHotelAdmin = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_hotel_admin, parent, false)
        return ViewHolderHotelAdmin(vistaHotelAdmin)  // Devuelve el ViewHolder con la vista inflada
    }

    // Función para eliminar un hotel de la lista de datos y de la base de datos
    fun eliminarDatos(id_hoteles: Int, posicion: Int) {
        val listaDatos = Datos.toMutableList()  // Convierte la lista en mutable para modificarla
        listaDatos.removeAt(posicion)  // Elimina el hotel de la posición indicada

        // Lanza una coroutine en un hilo de IO para eliminar el hotel de la base de datos
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            // Prepara y ejecuta la sentencia SQL para eliminar el hotel
            val deleteComentario = objConexion?.prepareStatement("DELETE FROM tbHoteles WHERE id_hoteles = ?")
            deleteComentario?.setInt(1, id_hoteles)
            deleteComentario?.executeUpdate()

            // Ejecuta un commit para confirmar los cambios en la base de datos
            val commit = objConexion?.prepareStatement("commit")
            commit?.executeUpdate()
        }

        Datos = listaDatos.toList()  // Actualiza la lista de datos en el adaptador
        notifyItemRemoved(posicion)  // Notifica la eliminación del elemento en la posición indicada
        notifyDataSetChanged()  // Notifica que la lista de datos ha cambiado
    }

    // Devuelve el número de elementos en la lista de hoteles
    override fun getItemCount() = Datos.size

    // Vincula los datos de un hotel a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderHotelAdmin, position: Int) {
        val item = Datos[position]  // Obtiene el hotel en la posición actual
        val context = holder.itemView.context

        // Establece un listener para mostrar un menú emergente cuando se hace clic en la imagen
        holder.ImageView.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menuu, context, item, position)
        }

        val itemHotelAdmin = Datos[position]
        holder.bind(itemHotelAdmin, clickListener)  // Vincula los datos del hotel con el clickListener

        // Usa Glide para cargar la imagen del hotel desde la URL y mostrarla en el ImageView
        Glide.with(holder.itemView)
            .load(itemHotelAdmin.img_url)
            .into(holder.imgHotelCard)
    }

    // Muestra un menú emergente con opciones para el hotel
    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbHotel, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)  // Infla el menú emergente con las opciones

        // Establece un listener para gestionar las acciones seleccionadas en el menú
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_2 -> {
                    eliminarDatos(item.id_hoteles, position)  // Elimina el hotel si se selecciona la opción correspondiente
                    true
                }
                else -> false
            }
        }

        popup.setOnDismissListener {
            // Acción al cerrar el menú emergente, si es necesario
        }

        // Muestra el menú emergente
        popup.show()
    }

}


