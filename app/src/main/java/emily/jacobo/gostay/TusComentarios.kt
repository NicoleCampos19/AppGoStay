package emily.jacobo.gostay

import RecyclerViewHelpers.ComentarioAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.sql.SQLException

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
        rcvTusComentarios.layoutManager = LinearLayoutManager(this@TusComentarios)

        val idUsuario = intent.getIntExtra("id_usuario", -1)
        println("id recibido ${idUsuario}")

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)



        imvAtrasc.setOnClickListener {
            finish()
        }
        fun obtenerMisComentarios(idUsuario: Int): List<tbComentarios> {
            val listaComentarios = mutableListOf<tbComentarios>()

            try {
                // Crea un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()
                val query = "SELECT id_valoracion, comentario, id_usuario FROM tbValoraciones WHERE id_usuario = ?"
                val preparedStatement = objConexion?.prepareStatement(query)
                preparedStatement?.setInt(1, idUsuario)
                val resultSet = preparedStatement?.executeQuery()

                // Recorre el ResultSet
                while (resultSet?.next() == true) {
                    val id_valoracion = resultSet.getInt("id_valoracion")
                    val comentario = resultSet.getString("comentario")
                    val id_usuario = resultSet.getInt("id_usuario")

                    // Log cada comentario recuperado
                    Log.d("ComentariosDebug", "Comentario encontrado: id_valoracion=$id_valoracion, comentario=$comentario, id_usuario=$id_usuario")

                    val comentarios = tbComentarios(id_valoracion, comentario, id_usuario)
                    listaComentarios.add(comentarios)
                }

                // Log la cantidad de comentarios
                Log.d("ComentariosDebug", "Número total de comentarios obtenidos: ${listaComentarios.size}")

            } catch (e: SQLException) {
                // Manejo de excepciones SQL
                Log.e("DatabaseError", "SQL Error: ${e.message}")
            } catch (e: Exception) {
                // Manejo de otras excepciones
                Log.e("GeneralError", "Error: ${e.message}")
            }

            return listaComentarios
        }




        CoroutineScope(Dispatchers.IO).launch {
            val comentariosDB = obtenerMisComentarios(idUsuario)
            withContext(Dispatchers.Main) {
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvTusComentarios.adapter = miAdaptador
            }
        }
    }
}