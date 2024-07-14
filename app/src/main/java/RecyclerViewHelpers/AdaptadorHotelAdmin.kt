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


class AdaptadorHotelAdmin(var Datos: List<tbHotel>, val clickListener: (tbHotel) -> Unit): RecyclerView.Adapter<ViewHolderHotelAdmin>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotelAdmin {
val vistaHotelAdmin = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_hotel_admin, parent, false)
    return ViewHolderHotelAdmin(vistaHotelAdmin)
    }

    fun eliminarDatos(id_hoteles: Int, posicion: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteComentario = objConexion?.prepareStatement("DELETE FROM tbHoteles WHERE id_hoteles = ?")
            deleteComentario?.setInt(1, id_hoteles)
            deleteComentario?.executeUpdate()

            val commit = objConexion?.prepareStatement("commit")
            commit?.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun getItemCount() = Datos.size


    override fun onBindViewHolder(holder: ViewHolderHotelAdmin, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        holder.ImageView.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu, context, item, position)
        }


        val itemHotelAdmin = Datos[position]
        holder.bind(itemHotelAdmin, clickListener)

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
                    eliminarDatos(item.id_hoteles, position)
                    true
                }
                else -> false
            }
        }

        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

}

