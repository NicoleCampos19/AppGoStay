package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorServicioHotel
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
import modelo.tbServiciosHotel

class hotel_detalles : AppCompatActivity() {

    private lateinit var rcvServicioHotel: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotel_detalles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rcvServicioHotel = findViewById(R.id.rcvServiciosHotel)
        rcvServicioHotel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as tbHotel
        if (idHotel != -1) {
            obtenerServiciosHotel(idHotel)
        }



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
hotel?.let {
    Glide.with(this)
        .load(hotel.img_url)
        .into(imvDetalleHotel)

    tvNombreDetalleHotel.text = hotel.nombreHotel
    tvDescripcionDetalleHotel.text = hotel.descripcion
}
        imvVolverDetallesHotel.setOnClickListener {
            val volverAtras = Intent(this, PaginaInicio::class.java)
            startActivity(volverAtras)
        }
        
    }

    private fun obtenerServiciosHotel(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val servicios = cargarServiciosHotel(idHotel)
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorServicioHotel(servicios)
                rcvServicioHotel.adapter = adapter
            }
        }
    }
    private fun cargarServiciosHotel(idHotel: Int): List<tbServiciosHotel> {
        val listaServicios = mutableListOf<tbServiciosHotel>()
        val conexion = ClaseConexion().cadenaConexion()

        val query = """
            SELECT sh.id_servicio_hotel, sh.nombre_servicio, sh.img_icono_hotel 
            FROM tbServiciosHotel sh 
            JOIN tbHoteles h ON h.id_servicio_hotel = sh.id_servicio_hotel 
            WHERE h.id_hoteles = ?
        """
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idHotel)
        val resultSet = statement?.executeQuery()
        while (resultSet?.next() == true) {
            val idServicioHotel = resultSet.getInt("id_servicio_hotel")
            val nombreServicio = resultSet.getString("nombre_servicio")
            val imgIconoHotel = resultSet.getString("img_icono_hotel")
            listaServicios.add(tbServiciosHotel(idServicioHotel, nombreServicio, imgIconoHotel))
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return listaServicios
    }
}