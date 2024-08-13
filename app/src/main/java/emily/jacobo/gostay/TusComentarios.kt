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
    }
}