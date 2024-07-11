package RecyclerViewHelpers

import android.content.Context
import android.database.SQLException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R

import modelo.ClaseConexion
import modelo.tbComentarios

class ComentarioAdapter(var Datos: List<tbComentarios>): RecyclerView.Adapter<ViewHolderComentario>() {

    fun actualizarListado(nuevocomentario: List<tbComentarios>) {
            Datos = nuevocomentario
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
            showMenu(v, R.menu.popup_menu,context )
        }


    }
    //cuidado probablemente sobre carge la base de datos o el proyecto kotlin cambiar luego
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

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context) {

        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }



}