package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorCarrusel
import RecyclerViewHelpers.AdaptadorOfertas
import RecyclerViewHelpers.ComentarioAdapter
import RecyclerViewHelpers.ServicioAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.MenuRes
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
import modelo.ServicioInfo
import modelo.tbCarrusel
import modelo.tbComentarios
import modelo.tbHotel

class hotel_detalles_admin : AppCompatActivity() {

    private lateinit var prevActivity: String
    private lateinit var servicioAdapter: ServicioAdapter

    override fun onBackPressed() {
        // Ejecuta el código antes de regresar
        AdaptadorOfertas.descuentoTotalGlobal = 0.0

        // Luego llama al comportamiento predeterminado de volver atrás
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reseñaGlobal = activity_resenas.resenaGlobal

        setContentView(R.layout.activity_hotel_detalles_admin)
        val rcvCarrusels = findViewById<RecyclerView>(R.id.carrusel_recycler_views)
        rcvCarrusels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fun obtenerImagenes(): List<tbCarrusel> {
            val objConexion = ClaseConexion().cadenaConexion()


            val lista = mutableListOf<tbCarrusel>()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbImagenes_Hoteles")

            if (resultSet != null) {
                while (resultSet.next()) {

                    val id_imagenes = resultSet.getInt("id_imagenes")
                    val id_hoteles = resultSet.getInt("id_hoteles")
                    val url_imagen = resultSet.getString("url_imagen")

                    val valoresJuntos = tbCarrusel(id_imagenes, id_hoteles, url_imagen)

                    lista.add(valoresJuntos)
                }
            }
            return lista
        }

        //Asignarle el adaptador al Recyclearview
        CoroutineScope(Dispatchers.IO).launch {
            val ImagenesBD = obtenerImagenes()

            withContext(Dispatchers.Main){
                val adapter = AdaptadorCarrusel(ImagenesBD)
                rcvCarrusels.adapter = adapter

            }

        }
        val idHotelGlobal = PaginaInicio.hotelIdGlobal
        val recyclerView: RecyclerView = findViewById(R.id.rcvServiciosHotel)

        //Para hacer select a los servicios
        fun loadServiciosFromDatabase(idHotelGlobal: Int): List<ServicioInfo> {
            val ServiciosList = mutableListOf<ServicioInfo>()
            val query = """
         SELECT sh.nombre_servicio, sh.img_icono_hotel
         from tbIntermedia_Hoteles_Servicios ish
         INNER JOIN tbServiciosHotel sh ON ish.id_servicio_hotel = sh.id_servicio_hotel
            where id_hoteles = ?
         """.trimIndent()

            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idHotelGlobal)
                    }

                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        resultSet.use { rs ->
                            while (rs.next()) {
                                val nombre_servicio = rs.getString("nombre_servicio")
                                val img_icono_hotel = rs.getString("img_icono_hotel")
                                ServiciosList.add(ServicioInfo(nombre_servicio, img_icono_hotel))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }

            return ServiciosList
        }
        CoroutineScope(Dispatchers.IO).launch {
            val servicios = idHotelGlobal?.let { loadServiciosFromDatabase(it) }
            withContext(Dispatchers.Main) {
                servicios?.let {
                    servicioAdapter = ServicioAdapter(it)
                    recyclerView.adapter = servicioAdapter
                    recyclerView.layoutManager = LinearLayoutManager(this@hotel_detalles_admin, LinearLayoutManager.HORIZONTAL, false)
                }?: run {
                    // Maneja el caso en que reservas sea null, quizás mostrando un mensaje de error o un mensaje de "No hay datos"
                    println("No se encontraron servicios para el hotel.")
                }
            }
        }

        prevActivity = intent.getStringExtra("prev_activity") ?: "PaginaInicio"

        val imageViewBack = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        imageViewBack.setOnClickListener {
            AdaptadorOfertas.descuentoTotalGlobal = 0.0
            navigateBack()
        }

        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as tbHotel


        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)
        val txtCalificacion = findViewById<TextView>(R.id.txtCalificacion)

        //txtCalificacion.text = reseñaGlobal.promedio

        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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

        fun obtenerComentarios(): List<tbComentarios> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbValoraciones")!!

            val listaComentarios = mutableListOf<tbComentarios>()

            while (resultSet.next()){
                val id_valoracion = resultSet.getInt("id_valoracion")
                val comentario = resultSet.getString("comentario")
                val id_usuario = resultSet.getInt("id_usuario")
                val comentarios = tbComentarios(id_valoracion, comentario,id_usuario)

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

        hotel?.let {
            Glide.with(this)
            tvNombreDetalleHotel.text = hotel.nombreHotel
            tvDescripcionDetalleHotel.text = hotel.descripcion
            tvNombreDetalleHotel.text = hotel.nombreHotel
            tvDescripcionDetalleHotel.text = hotel.descripcion
        }
    }

    private fun navigateBack() {
        when (prevActivity) {
            "PaginaInicio" -> {
                val intent = Intent(this, PaginaInicio::class.java)
                startActivity(intent)
            }
            "InicioAdmin" -> {
                val intent = Intent(this, InicioAdmin::class.java)
                startActivity(intent)
            }
            else -> {
                // En caso de que no se reconozca la Activity previa, regresar a una Activity por defecto
                val intent = Intent(this, PaginaInicio::class.java)
                startActivity(intent)
            }
        }
        finish()
    }
    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}