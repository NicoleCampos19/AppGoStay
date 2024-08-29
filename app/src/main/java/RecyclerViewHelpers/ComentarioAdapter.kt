package RecyclerViewHelpers

import android.app.AlertDialog
import android.content.Context
import android.database.SQLException
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import modelo.ClaseConexion
import modelo.tbComentarios

class ComentarioAdapter(var Datos: List<tbComentarios>): RecyclerView.Adapter<ViewHolderComentario>() {

    fun actualizarListado(nuevocomentario: List<tbComentarios>) {
        Datos = nuevocomentario
        notifyDataSetChanged()
    }

    fun eliminarDatos(idValoracion: Int, posicion: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteComentario = objConexion?.prepareStatement("DELETE FROM tbValoraciones WHERE id_valoracion = ?")
            deleteComentario?.setInt(1, idValoracion)
            deleteComentario?.executeUpdate()

            val commit = objConexion?.prepareStatement("commit")
            commit?.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun actualizarDato(comentario: String, idValoracion: Int, position: Int){
        GlobalScope.launch(Dispatchers.IO){
            //1- Creo un obj de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo una variable que contenga un PrepareStatement
            val updateComentario = objConexion?.prepareStatement("update tbValoraciones set comentario = ? where id_valoracion = ?")!!
            updateComentario.setString(1, comentario)
            updateComentario.setInt(2, idValoracion)
            updateComentario.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()

            Datos = Datos.toMutableList().apply {
                this[position].comentario = comentario
            }

            launch(Dispatchers.Main) {
                notifyItemChanged(position)
            }

        }

        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderComentario {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_comentario, parent, false)
        return ViewHolderComentario(vista)
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
    suspend fun obtenerImagenUsuario(correo: String): String? {
        return withContext(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val getId = objConexion?.prepareStatement("SELECT imgfoto FROM tbUsuarios WHERE correo = ?")
            getId?.setString(1, correo)
            val resultSet = getId?.executeQuery()
            if (resultSet != null && resultSet.next()) {
                resultSet.getString("imgfoto")
            } else {
                null
            }
        }

    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderComentario, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        val comentario = item.comentario
        holder.txtComentarioCard.text = comentario

        CoroutineScope(Dispatchers.Main).launch {
            var correo = activity_iniciar_sesion.correoIngresado
            holder.txtUsuarioCard.text = correo
            val idUsuarioActivo = obtenerIdUsuario(correo)
            val imagen = obtenerImagenUsuario(correo)
            if(idUsuarioActivo != item.id_usuario){
                holder.ImageView.visibility = View.GONE
            }
            if(!imagen.isNullOrEmpty()){
             try{
                 Log.e("imagen", imagen)
                 Glide.with(context)
                     .load(imagen)
                     .fitCenter()
                     .into(holder.imageProfile)
             } catch (e: Exception){
                 e.printStackTrace()
             }
            }
        }
        holder.ImageViewasd.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu, context, item, position)
        }
    }

    // Cuidado: probablemente sobrecargue la base de datos o el proyecto Kotlin. Cambiar luego.


    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbComentarios, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_1 -> {
                    actualizarDato(item.comentario, item.id_valoracion, position)


                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Actualizar")


                    val cuadroTexto = EditText(context)
                    cuadroTexto.setHint(item.comentario)
                    builder.setView(cuadroTexto)

                    builder.setPositiveButton("Actualizar"){
                            dialog, wich ->
                        actualizarDato(cuadroTexto.text.toString(), item.id_valoracion, position)
                    }
                    builder.setNegativeButton("Cancelar"){
                            dialog, wich ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()

                    true
                }
                R.id.option_2 -> {
                    eliminarDatos(item.id_valoracion, position)
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