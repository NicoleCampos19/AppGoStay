package emily.jacobo.gostay

import RecyclerViewHelpers.ComentarioAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbComentarios
import modelo.tbHotel

class hotel_detalles : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotel_detalles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val hotel = intent.getSerializableExtra("hotel") as tbHotel

        val imvVolverDetallesHotel = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        val imvDetalleHotel = findViewById<ImageView>(R.id.imvDetalleHotel)
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)


        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fun obtenerComentarios(): List<tbComentarios> {

            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbValoraciones")!!

            val listaComentarios = mutableListOf<tbComentarios>()

            while (resultSet.next()){
                val id_valoracion = resultSet.getInt("id_valoracion")
                val nombre_valoracion = resultSet.getString("nombre_valoracion")
                val comentario = resultSet.getString("comentario")
                val id_usuario = resultSet.getInt("id_usuario")


                val valoresJuntos = tbComentarios(id_valoracion, nombre_valoracion, comentario, id_usuario)

                listaComentarios.add(valoresJuntos)
            }
            return listaComentarios
        }

        CoroutineScope(Dispatchers.IO).launch {
            val comentariosDB = obtenerComentarios()
            withContext(Dispatchers.Main) {
                val adapter = ComentarioAdapter(comentariosDB)
                rcvComentarios.adapter = adapter
            }
        }

        Glide.with(this)
            .load(hotel.img_url)
            .into(imvDetalleHotel)

        tvNombreDetalleHotel.text = hotel.nombreHotel
        tvDescripcionDetalleHotel.text = hotel.descripcion

        imvVolverDetallesHotel.setOnClickListener {
            val volverAtras = Intent(this, PaginaInicio::class.java)
            startActivity(volverAtras)
        }
    }

}