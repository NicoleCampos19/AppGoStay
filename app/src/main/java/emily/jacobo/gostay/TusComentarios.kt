package emily.jacobo.gostay

import RecyclerViewHelpers.ComentarioAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbComentarios
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class TusComentarios : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tus_comentarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rcvTusComentarios = findViewById<RecyclerView>(R.id.rcvTusComentarios)
        rcvTusComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }

        fun obtenerComentarios(): List<tbComentarios> {
            val listaComentarios = mutableListOf<tbComentarios>()
            val correoUsuario = activity_iniciar_sesion.variableGloalLogin.txtCorreoInciarSesionV
            if (correoUsuario.isNullOrEmpty()) return listaComentarios

            val query = """
                SELECT v.id_valoracion, v.comentario, v.id_usuario 
                FROM tbValoraciones v 
                INNER JOIN tbUsuarios u ON v.id_usuario = u.id_usuario 
                WHERE u.correo = ?
            """

            var connection: Connection? = null
            var statement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                connection = ClaseConexion().cadenaConexion()
                statement = connection?.prepareStatement(query)
                statement?.setString(1, correoUsuario)
                resultSet = statement?.executeQuery()

                while (resultSet?.next() == true) {
                    val id_valoracion = resultSet.getInt("id_valoracion")
                    val comentario = resultSet.getString("comentario")
                    val id_usuario = resultSet.getInt("id_usuario")
                    listaComentarios.add(tbComentarios(id_valoracion, comentario, id_usuario))
                }
            } catch (e: Exception) {
                println("El error es este: $e")
            } finally {
                resultSet?.close()
                statement?.close()
                connection?.close()
            }

            return listaComentarios
        }

        CoroutineScope(Dispatchers.IO).launch {
            val comentariosDB = obtenerComentarios()
            withContext(Dispatchers.Main) {
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvTusComentarios.adapter = miAdaptador
            }
        }
    }
}