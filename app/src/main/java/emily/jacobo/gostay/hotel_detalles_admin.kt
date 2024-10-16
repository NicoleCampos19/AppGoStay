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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import emily.jacobo.gostay.hotel_detalles.Companion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.ServicioInfo
import modelo.tbCarrusel
import modelo.tbComentarios
import modelo.tbHotel
import java.sql.SQLException

class hotel_detalles_admin : AppCompatActivity(), OnMapReadyCallback {
    // Vista del mapa
    private lateinit var mapView: MapView
    // Instancia del mapa de Google
    private lateinit var googleMap: GoogleMap
    // Cliente para ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // Nombre de la actividad anterior
    private lateinit var prevActivity: String
    // Adaptador para servicios
    private lateinit var servicioAdapter: ServicioAdapter

    // Latitud
    var latitud: Double = 0.0
    // Longitud
    var longitud: Double = 0.0
    // Descripción
    var desc: String = ""
    // Nombre del hotel
    var nombreHotel: String = ""

    override fun onBackPressed() {
        // Ejecuta el código antes de regresar
        AdaptadorOfertas.descuentoTotalGlobal = 0.0
        // Luego llama al comportamiento predeterminado de volver atrás
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_hotel_detalles_admin)
        val rcvCarrusels = findViewById<RecyclerView>(R.id.carrusel_recycler_views)
        rcvCarrusels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Inicializa el mapView y el cliente de ubicación
        mapView = findViewById(R.id.mapView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Verifica si hay un estado guardado y extrae el bundle para el mapa
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(hotel_detalles.MAP_VIEW_BUNDLE_KEY)
        }
        // Crea el mapView
        mapView.onCreate(mapViewBundle)
        // Inicia el mapa de manera asíncrona
        mapView.getMapAsync(this)

        // Select para obtener las imágenes de los hoteles
        fun obtenerImagenes(): List<tbCarrusel> {
            val objConexion = ClaseConexion().cadenaConexion() // Obtiene la conexión a la base de datos
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

        // Obtiene el ID del hotel pasado como extra en el intent
        val ID_Hotel = intent.getIntExtra("id_hoteles", 0)
        // Lanza una corrutina en el contexto de I/O para realizar una consulta en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            val conexion = ClaseConexion().cadenaConexion()
            // Establece la conexión a la base de datos
            // Prepara una consulta SQL con el ID del hotel como parámetro
            val statement = conexion?.prepareStatement("SELECT * FROM tbHoteles WHERE id_hoteles = ?")!!
            statement.setInt(1, ID_Hotel)
            val resultSet = statement.executeQuery()
            withContext(Dispatchers.Main) {
                if (resultSet.next()) {
                    latitud = resultSet.getDouble("latitudHotel")
                    longitud = resultSet.getDouble("longitudHotel")
                    desc = resultSet.getString("descripcion")
                    nombreHotel = resultSet.getString("nombre")
                    if (::googleMap.isInitialized) {
                        updateMapLocation()
                    }
                }
            }
        }

        //Asignarle el adaptador al Recyclearview
        CoroutineScope(Dispatchers.IO).launch {
            val ImagenesBD = obtenerImagenes()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorCarrusel(ImagenesBD)
                rcvCarrusels.adapter = adapter

            }

        }
        // Obtiene el ID global del hotel desde la página de inicio
        val idHotelGlobal = PaginaInicio.hotelIdGlobal
        // Inicializa el RecyclerView para mostrar servicios
        val recyclerView: RecyclerView = findViewById(R.id.rcvServiciosHotel)

        //Para hacer select a los servicios
        fun loadServiciosFromDatabase(idHotelGlobal: Int): List<ServicioInfo> {
            val ServiciosList = mutableListOf<ServicioInfo>() // Lista para almacenar los servicios obtenidos
            val query = """
        SELECT nombre_servicio, img_icono_hotel
                FROM tbServiciosHotel           
                WHERE id_hoteles = ?
         """.trimIndent() // Consulta SQL para obtener los servicios del hotel
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idHotelGlobal) // Asigna el id del hotel a la consulta
                    }

                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery() // Ejecuta la consulta
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
        // Lanza una corrutina para cargar los servicios y actualizar el RecyclerView
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

        // Inicializa el ImageView para el botón de retroceso
        val imageViewBack = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        imageViewBack.setOnClickListener {
            finish()
            overridePendingTransition(0,0)

        }

        // Obtiene el id del hotel y los detalles del hotel desde el intent
        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as tbHotel

        // Manda a llamar los elementos de la vista
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)
        val txtCalificacion = findViewById<TextView>(R.id.txtCalificacion)

        // Configura el RecyclerView para mostrar los comentarios en una disposición horizontal
        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Select para obtener el id del usuario a través del campo de correo
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

        // Función para obtener el comentario a través de un select
        fun obtenerComentarios(idHotel: Int): List<tbComentarios> {
            val listaComentarios = mutableListOf<tbComentarios>()

            try {
                // 1- Creo un objeto de la clase conexión
                val objConexion = ClaseConexion().cadenaConexion()

                // Verifico si la conexión es válida
                if (objConexion != null) {
                    // 2- Creo el PreparedStatement y añado el parámetro `idHotel`
                    val comentarios_ = objConexion.prepareStatement(
                        """
                SELECT vl.id_valoracion, vl.comentario,vl.id_calificación, us.id_usuario, us.nombre_usuario, us.imgfoto 
                FROM tbValoraciones vl 
                INNER JOIN tbUsuarios us ON vl.id_usuario = us.id_usuario 
                WHERE vl.id_hoteles = ?
                """
                    )
                    comentarios_.setInt(1, idHotel) // Añadimos el valor de id_hoteles a la consulta

                    val resultSet = comentarios_.executeQuery()

                    // 3- Recorro el resultSet y obtengo los resultados
                    while (resultSet.next()) {
                        val id_valoracion = resultSet.getInt("id_valoracion")
                        val comentario = resultSet.getString("comentario")
                        val id_usuario = resultSet.getInt("id_usuario")
                        val nombre_usuario = resultSet.getString("nombre_usuario")
                        val imgfoto = resultSet.getString("imgfoto")
                        val id_calificación = resultSet.getFloat("id_calificación")


                        // 4- Creo el objeto tbComentarios y lo añado a la lista
                        val comentarios = tbComentarios(id_valoracion, comentario, id_usuario, nombre_usuario, imgfoto, id_calificación)
                        listaComentarios.add(comentarios)
                    }

                    // 5- Cierro los recursos para evitar fugas de memoria
                    resultSet.close()
                    comentarios_.close()
                    objConexion.close()
                } else {
                    // Manejar el caso en que no se obtiene conexión
                    throw SQLException("No se pudo establecer la conexión a la base de datos")
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }

            return listaComentarios
        }

        // Lanza una corrutina para obtener y cargar los comentarios en el RecyclerView
        CoroutineScope(Dispatchers.IO).launch{
            val idHotel = PaginaInicio.hotelIdGlobal
            val comentariosDB = obtenerComentarios(idHotel!!)
            withContext(Dispatchers.Main){
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvComentarios.adapter = miAdaptador
            }
        }

        // Si el objeto hotel no es nulo, ejecuta el siguiente bloque de código
        hotel?.let {
            // Usa la biblioteca Glide para cargar imágenes u otros recursos (aunque no se muestra la imagen aquí)
            Glide.with(this)
            tvNombreDetalleHotel.text = hotel.nombreHotel
            tvDescripcionDetalleHotel.text = hotel.descripcion
            tvNombreDetalleHotel.text = hotel.nombreHotel
            tvDescripcionDetalleHotel.text = hotel.descripcion
        }
    }

    // Se ejecuta cuando el mapa está listo para ser usado
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMapLocation()
    }

    // Función privada para actualizar la ubicación en el mapa
    private fun updateMapLocation() {
        val location = LatLng(latitud, longitud)
        googleMap.addMarker(MarkerOptions().position(location).title("Ubicación del Hotel"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    // Llama al método onResume del mapView cuando la actividad se reanuda
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    // Llama al método onStart del mapView cuando la actividad empieza
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    // Llama al método onStop del mapView cuando la actividad se detiene
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    // Llama al método onPause del mapView cuando la actividad se pausa
    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    // Llama al método onDestroy del mapView cuando la actividad se destruye
    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    // Llama al método onLowMemory del mapView cuando el sistema está bajo memoria
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    /*
    // Función privada para gestionar la navegación hacia atrás en función de la actividad previa
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
    */

    // Función privada para mostrar un menú emergente (popup menu) al hacer clic en una vista
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