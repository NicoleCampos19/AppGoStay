package emily.jacobo.gostay

import RecyclerViewHelpers.ComentarioAdapter
import android.os.Bundle
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

class TusComentarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tus_comentarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)


        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        fun obtenerComentarios(): List<tbComentarios> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbValoraciones where correoUsuario = txtCorreoInciarSesionV")!!

            val listaComentarios = mutableListOf<tbComentarios>()

            while (resultSet.next()){
                val id_valoracion = resultSet.getInt("id_valoracion")
                val comentario = resultSet.getString("comentario")
                val id_usuario = resultSet.getInt("id_usuario")


                val comentarios = tbComentarios(id_valoracion, comentario, id_usuario)

                listaComentarios.add(comentarios)
            }
            return listaComentarios
        }


        CoroutineScope(Dispatchers.IO).launch{
            val comentariosDB = obtenerComentarios()
            withContext(Dispatchers.Main){
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvComentarios.adapter = miAdaptador
            }
        }

    }
}