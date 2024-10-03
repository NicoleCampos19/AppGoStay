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
import emily.jacobo.gostay.PaginaInicio.Companion.idUsuarioGlobalL
import emily.jacobo.gostay.R
import kotlinx.coroutines.*

import modelo.ClaseConexion
import modelo.tbComentarios

class ComentarioAdapter(var Datos: List<tbComentarios>) : RecyclerView.Adapter<ViewHolderComentario>() {

    // Actualiza la lista de comentarios con nuevos datos
    fun actualizarListado(nuevocomentario: List<tbComentarios>) {
        Datos = nuevocomentario
        notifyDataSetChanged() // Notifica que los datos han cambiado para actualizar la vista
    }

    // Elimina un comentario de la base de datos y actualiza la lista
    fun eliminarDatos(idValoracion: Int, posicion: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                // Ejecuta la consulta SQL para eliminar un comentario por su id
                objConexion?.prepareStatement("DELETE FROM tbValoraciones WHERE id_valoracion = ?")?.apply {
                    setInt(1, idValoracion)
                    executeUpdate()
                }

                // Ejecuta un commit para confirmar la operación
                objConexion?.prepareStatement("commit")?.executeUpdate()

                // Actualiza la lista y la interfaz en el hilo principal
                withContext(Dispatchers.Main) {
                    val listaDatos = Datos.toMutableList()
                    listaDatos.removeAt(posicion)
                    Datos = listaDatos.toList()
                    notifyItemRemoved(posicion) // Notifica que se eliminó un item de la lista
                }
            } catch (e: Exception) {
                // Manejo de errores en caso de fallo al eliminar
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Actualiza un comentario existente en la base de datos y en la lista
    fun actualizarDato(comentario: String, idValoracion: Int, position: Int, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                // Ejecuta la consulta SQL para actualizar el comentario
                objConexion?.prepareStatement("UPDATE tbValoraciones SET comentario = ? WHERE id_valoracion = ?")?.apply {
                    setString(1, comentario)
                    setInt(2, idValoracion)
                    executeUpdate()
                }

                // Realiza un commit de la actualización
                objConexion?.prepareStatement("commit")?.executeUpdate()

                // Actualiza la interfaz en el hilo principal
                withContext(Dispatchers.Main) {
                    Datos = Datos.toMutableList().apply {
                        this[position].comentario = comentario // Cambia el comentario en la lista local
                    }
                    notifyItemChanged(position) // Notifica el cambio en el item de la lista
                }
            } catch (e: Exception) {
                // Manejo de errores en caso de fallo al actualizar
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Crea el ViewHolder que contiene los elementos de cada comentario
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderComentario {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_comentario, parent, false)
        return ViewHolderComentario(vista)
    }

    // Retorna el tamaño de la lista de comentarios
    override fun getItemCount() = Datos.size

    // Vincula los datos del comentario a los elementos gráficos del ViewHolder
    override fun onBindViewHolder(holder: ViewHolderComentario, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        holder.txtComentarioCard.text = item.comentario // Muestra el texto del comentario
        holder.txtUsuarioCard.text = item.nombre_usuario // Muestra el nombre del usuario
        holder.ratingBar.rating = item.id_calificación

        // Carga la imagen de perfil del usuario con Glide
        Glide.with(context)
            .load(item.foto_usuario)
            .fitCenter()
            .into(holder.imageProfile)

        // Lanza una corrutina para verificar si el comentario pertenece al usuario activo
        CoroutineScope(Dispatchers.Main).launch {
            val idHotel = PaginaInicio.hotelIdGlobal
            val idUsuarioActivo = PaginaInicio.idUsuarioGlobalL

            // Muestra el botón de "Más opciones" solo si el comentario es del usuario actual
            holder.imvMas.visibility = if (idUsuarioActivo == item.id_usuario) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // Configura el listener para el botón de "Más opciones"
        holder.imvMas.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu, context, item, position) // Muestra el menú de opciones
        }
    }

    // Muestra un menú contextual con opciones para actualizar o eliminar el comentario
    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbComentarios, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        // Maneja las acciones seleccionadas en el menú
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_1 -> {
                    // Muestra un cuadro de diálogo para actualizar el comentario
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Actualizar")

                    val cuadroTexto = EditText(context)
                    cuadroTexto.setHint(item.comentario) // Muestra el comentario actual como sugerencia
                    builder.setView(cuadroTexto)

                    builder.setPositiveButton("Actualizar") { dialog, _ ->
                        actualizarDato(cuadroTexto.text.toString(), item.id_valoracion, position, context)
                        dialog.dismiss() // Actualiza el comentario y cierra el cuadro de diálogo
                    }

                    builder.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss() // Cierra el cuadro de diálogo sin hacer cambios
                    }

                    val dialog = builder.create()
                    dialog.show() // Muestra el cuadro de diálogo
                    true
                }
                R.id.option_2 -> {
                    eliminarDatos(item.id_valoracion, position, context) // Elimina el comentario
                    true
                }
                else -> false
            }
        }
        popup.show() // Muestra el menú de opciones
    }
}


