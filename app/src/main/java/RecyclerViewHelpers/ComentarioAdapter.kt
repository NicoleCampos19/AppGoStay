package RecyclerViewHelpers

import android.app.AlertDialog
import android.content.Context
import android.database.SQLException
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    fun actualizarDato(comentario: String, idValoracion: Int){
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

        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderComentario {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_comentario, parent, false)
        return ViewHolderComentario(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderComentario, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        val comentario = item.comentario
        holder.txtComentarioCard.text = comentario

        holder.ImageView.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu, context, item, position)
        }
    }

    // Cuidado: probablemente sobrecargue la base de datos o el proyecto Kotlin. Cambiar luego.
    fun obtenerNombreUsuario(idUsuario: Int): String? {
        val conexion = ClaseConexion().cadenaConexion()
        var nombreUsuario: String? = null

        val query = "SELECT nombre FROM tbUsuarios WHERE id_usuario = ?"
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idUsuario)

        val resultSet = statement?.executeQuery()
        if (resultSet?.next() == true) {
            nombreUsuario = resultSet.getString("nombre")
        }

        resultSet?.close()
        statement?.close()
        conexion?.close()

        return nombreUsuario
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbComentarios, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_1 -> {
                    actualizarDato(item.comentario, item.id_valoracion)


                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Actualizar")


                    val cuadroTexto = EditText(context)
                    cuadroTexto.setHint(item.comentario)
                    builder.setView(cuadroTexto)

                    builder.setPositiveButton("Actualizar"){
                            dialog, wich ->
                        actualizarDato(cuadroTexto.text.toString(), item.id_valoracion)
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