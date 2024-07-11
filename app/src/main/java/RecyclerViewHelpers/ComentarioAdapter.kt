package RecyclerViewHelpers

import android.database.SQLException
import android.view.LayoutInflater
import android.view.ViewGroup
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

        val comentario = item.comentario
        holder.txtComentarioCard.text = comentario



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



}