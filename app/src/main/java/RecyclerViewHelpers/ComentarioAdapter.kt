package RecyclerViewHelpers

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.PaginaInicio
import emily.jacobo.gostay.R
import kotlinx.coroutines.*

import modelo.ClaseConexion
import modelo.tbComentarios

class ComentarioAdapter(var Datos: List<tbComentarios>) : RecyclerView.Adapter<ViewHolderComentario>() {

    fun actualizarListado(nuevocomentario: List<tbComentarios>) {
        Datos = nuevocomentario
        notifyDataSetChanged()
    }

    fun eliminarDatos(idValoracion: Int, posicion: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                objConexion?.prepareStatement("DELETE FROM tbValoraciones WHERE id_valoracion = ?")?.apply {
                    setInt(1, idValoracion)
                    executeUpdate()
                }

                objConexion?.prepareStatement("commit")?.executeUpdate()

                withContext(Dispatchers.Main) {
                    val listaDatos = Datos.toMutableList()
                    listaDatos.removeAt(posicion)
                    Datos = listaDatos.toList()
                    notifyItemRemoved(posicion)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun actualizarDato(comentario: String, idValoracion: Int, position: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                objConexion?.prepareStatement("UPDATE tbValoraciones SET comentario = ? WHERE id_valoracion = ?")?.apply {
                    setString(1, comentario)
                    setInt(2, idValoracion)
                    executeUpdate()
                }

                objConexion?.prepareStatement("commit")?.executeUpdate()

                withContext(Dispatchers.Main) {
                    Datos = Datos.toMutableList().apply {
                        this[position].comentario = comentario
                    }
                    notifyItemChanged(position)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderComentario {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_comentario, parent, false)
        return ViewHolderComentario(vista)
    }







    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderComentario, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        holder.txtComentarioCard.text = item.comentario

        CoroutineScope(Dispatchers.Main).launch {
            val idHotel = PaginaInicio.hotelIdGlobal
           // holder.txtUsuarioCard.text = obtenerCorreoUsuario(idHotel)
            val idUsuarioActivo = PaginaInicio.idUsuarioGlobalL
            //val imagen = obtenerImagenUsuario(idHotel)

            holder.imvMas.visibility = if (idUsuarioActivo == item.id_usuario) View.VISIBLE else View.GONE

            /*if (!imagen.isNullOrEmpty()) {
                try {
                    Log.e("imagen", imagen)
                    Glide.with(context)
                        .load(imagen)
                        .fitCenter()
                        .into(holder.imageProfile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }*/
        }

        holder.imvMas.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu, context, item, position)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbComentarios, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_1 -> {
                    // Cuadro de diálogo para actualizar el comentario
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Actualizar")

                    val cuadroTexto = EditText(context)
                    cuadroTexto.setHint(item.comentario)
                    builder.setView(cuadroTexto)

                    builder.setPositiveButton("Actualizar") { dialog, _ ->
                        actualizarDato(cuadroTexto.text.toString(), item.id_valoracion, position, context)
                        dialog.dismiss()
                    }

                    builder.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val dialog = builder.create()
                    dialog.show()
                    true
                }
                R.id.option_2 -> {
                    eliminarDatos(item.id_valoracion, position, context)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}

