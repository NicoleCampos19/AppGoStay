package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorCarrusel
import RecyclerViewHelpers.AdaptadorOfertas
import RecyclerViewHelpers.ComentarioAdapter
import RecyclerViewHelpers.ServicioAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
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
import emily.jacobo.gostay.PaginaInicio.Companion.hotelIdGlobal
import emily.jacobo.gostay.PaginaInicio.Companion.idUsuarioGlobalL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.ServicioInfo
import modelo.tbCarrusel
import modelo.tbComentarios
import modelo.tbHotel

class hotel_detalles : AppCompatActivity(), OnMapReadyCallback {

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

    // Clave para el bundle del mapa
    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    // Latitud
    var latitud: Double = 0.0
    // Longitud
    var longitud: Double = 0.0
    // Descripción
    var desc: String = ""
    // Nombre del hotel
    var nombreHotel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_detalles)
        prevActivity = intent.getStringExtra("prev_activity") ?: "default_value"

        // Asigna la reseña global de la actividad a una variable
        val reseñaGlobal = activity_resenas.resenaGlobal
        // Mando a llamar el rcv
        val rcvCarrusels = findViewById<RecyclerView>(R.id.carrusel_recycler_views)
        rcvCarrusels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Inicializa el mapView y el cliente de ubicación
        mapView = findViewById(R.id.mapView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Verifica si hay un estado guardado y extrae el bundle para el mapa
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }


        // Crea el mapView
        mapView.onCreate(mapViewBundle)
        // Inicia el mapa de manera asíncrona
        mapView.getMapAsync(this)

        // Select para obtener las imágenes de los hoteles
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
                    lista.add(tbCarrusel(id_imagenes, id_hoteles, url_imagen))
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

        // Lanza otra corrutina para obtener las imágenes de los hoteles y configurar el carrusel
        CoroutineScope(Dispatchers.IO).launch {
            val imagenesBD = obtenerImagenes() // Obtiene las imágenes desde la base de datos
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorCarrusel(imagenesBD)
                rcvCarrusels.adapter = adapter
            }
        }

        // Obtiene el ID global del hotel desde la página de inicio
        val idHotelGlobal = PaginaInicio.hotelIdGlobal
        // Inicializa el RecyclerView para mostrar servicios
        val recyclerView: RecyclerView = findViewById(R.id.rcvServiciosHotel)

        // Función suspendida que carga los servicios de un hotel desde la base de datos
        suspend fun loadServiciosFromDatabase(idHotelGlobal: Int): List<ServicioInfo> {
            val serviciosList = mutableListOf<ServicioInfo>() // Lista para almacenar los servicios obtenidos
            val query = """
                SELECT sh.nombre_servicio, sh.img_icono_hotel
                FROM tbIntermedia_Hoteles_Servicios ish
                INNER JOIN tbServiciosHotel sh ON ish.id_servicio_hotel = sh.id_servicio_hotel
                WHERE id_hoteles = ?
            """.trimIndent() // Consulta SQL para obtener los servicios del hotel
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection -> // Obtiene la conexión a la base de datos
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idHotelGlobal) // Asigna el id del hotel a la consulta
                    }
                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery() // Ejecuta la consulta
                        resultSet.use { rs ->
                            while (rs.next()) {
                                val nombre_servicio = rs.getString("nombre_servicio")
                                val img_icono_hotel = rs.getString("img_icono_hotel")
                                serviciosList.add(ServicioInfo(nombre_servicio, img_icono_hotel))
                            }
                        }
                    }
                }
                // Manejo de errores en caso de que algo falle
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return serviciosList
        }

        // Lanza una corrutina para cargar los servicios y actualizar el RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val servicios = loadServiciosFromDatabase(idHotelGlobal!!)
            withContext(Dispatchers.Main) {
                servicioAdapter = ServicioAdapter(servicios)
                recyclerView.adapter = servicioAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@hotel_detalles, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        // Inicializa el ImageView para el botón de retroceso
        val imageViewBack = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        imageViewBack.setOnClickListener {
            AdaptadorOfertas.descuentoTotalGlobal = 0.0
            navigateBack()
        }

        // Obtiene el id del hotel y los detalles del hotel desde el intent
        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as tbHotel

        // Configura el botón para elegir tipo de habitación
        val btnTipoHabitacion: Button = findViewById(R.id.btnTipoHabitacion)
        btnTipoHabitacion.setOnClickListener {
            if (idHotel != -1) {
                val intent = Intent(this, activity_eleccion_habitacion::class.java)
                startActivity(intent)
            }
        }

        // Manda a llamar los elementos de la vista
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val txtComentario = findViewById<EditText>(R.id.txtComentario)
        val imvEnviar = findViewById<ImageView>(R.id.imvEnviar)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)
        val btnReportar = findViewById<Button>(R.id.btnReportar)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val txtCalificacion = findViewById<TextView>(R.id.txtCalificacion)



        btnReportar.setOnClickListener {
            val siguientepantalla = Intent(this, RealizarDenuncia::class.java)
            siguientepantalla.putExtra("id_hotel", idHotel)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        // Configura el RecyclerView para mostrar los comentarios en una disposición horizontal
        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Función para obtener los comentarios de un hotel específico desde la base de datos
        fun obtenerComentarios(idHotel: Int): List<tbComentarios> {
            val listaComentarios = mutableListOf<tbComentarios>() // Lista para almacenar los comentarios obtenidos
            val objConexion = ClaseConexion().cadenaConexion() // Obtiene la conexión a la base de datos
            val statement = objConexion?.prepareStatement(
                """
                    SELECT vl.id_valoracion, vl.comentario, vl.id_calificación ,us.id_usuario, us.nombre_usuario, us.imgfoto 
                    FROM tbValoraciones vl 
                    INNER JOIN tbUsuarios us ON vl.id_usuario = us.id_usuario 
                    WHERE vl.id_hoteles = ?
                """.trimIndent()
            )
            statement?.setInt(1, idHotel)
            val resultSet = statement?.executeQuery()
            // Recorre los resultados obtenidos y extrae los datos para añadirlos a la lista de comentarios
            if (resultSet != null) {
                while (resultSet.next()) {
                    val id_valoracion = resultSet.getInt("id_valoracion")
                    val comentario = resultSet.getString("comentario")
                    val id_calificación = resultSet.getFloat("id_calificación")
                    val id_usuario = resultSet.getInt("id_usuario")
                    val nombre_usuario = resultSet.getString("nombre_usuario")
                    val imgfoto = resultSet.getString("imgfoto")

                    listaComentarios.add(tbComentarios(id_valoracion, comentario, id_usuario, nombre_usuario, imgfoto, id_calificación))
                }
            }
            return listaComentarios // Retorna la lista
        }

        // Lanza una corrutina para obtener y cargar los comentarios en el RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val comentarios = obtenerComentarios(hotelIdGlobal!!)
            withContext(Dispatchers.Main) {
                val adapter = ComentarioAdapter(comentarios)
                rcvComentarios.adapter = adapter
                tvDescripcionDetalleHotel.text = desc
                tvNombreDetalleHotel.text = nombreHotel
            }
        }




        fun obtenerCalificacionHotel(idHotel: Int): String {
            val objConexion = ClaseConexion().cadenaConexion() // Obtener la conexión ya existente
            val statement = """
        SELECT id_hoteles, 
               COUNT(id_valoracion) AS cantidad_valoraciones, 
               AVG(id_calificación) AS calificacion_final 
        FROM tbValoraciones 
        WHERE id_hoteles = ?
        GROUP BY id_hoteles
    """.trimIndent()

            var resultado = "" // Valor por defecto

            objConexion?.let { connection ->
                try {
                    val preparedStatement = connection.prepareStatement(statement)
                    preparedStatement.setInt(1, idHotel) // Establece el ID del hotel
                    val resultSet = preparedStatement.executeQuery()

                    while (resultSet.next()) { // Solo un hotel, por eso se utiliza `next()`
                        val cantidadValoraciones = resultSet.getInt("cantidad_valoraciones")
                        val calificacionFinal = resultSet.getDouble("calificacion_final")

                        resultado = "%.1f".format(calificacionFinal) // Formato de dos decimales

                    }
                } catch (e: Exception) {
                    Log.e("CalificacionHotel", "Error: ${e.message}", e)
                }
            }

            return resultado.toString() // Devuelve el resultado
        }

        // Función para obtener la calificación y actualizar el TextView
        fun actualizarCalificacionHotel(idHotel: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val calificacion = obtenerCalificacionHotel(idHotel) // Llama a la función que obtiene la calificación
                withContext(Dispatchers.Main) {
                    txtCalificacion.text = calificacion // Actualiza el TextView en el hilo principal
                    Log.d("CalificacionHotel", calificacion) // Agregar log para verificar el resultado

                }
            }
        }

        // Llamada a la función para actualizar el TextView
        actualizarCalificacionHotel(hotelIdGlobal!!)

        var ratingValue = 0 // Variable para almacenar el valor de calificación


        ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            ratingValue = fl.toInt() // Convierte el valor flotante en entero (1 a 5)

        }

        // Configura el botón para enviar un comentario
        imvEnviar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Obtiene el comentario del campo de texto
                    val comentario = txtComentario.text.toString()
                    val idUsuario = idUsuarioGlobalL
                    val objConexion = ClaseConexion().cadenaConexion()
                    // Prepara la inserción de un nuevo comentario en la base de datos
                    val sentencia = objConexion?.prepareStatement("INSERT INTO tbValoraciones (comentario, id_usuario, id_hoteles, id_calificación) VALUES (?, ?, ?, ?)")
                    sentencia?.setString(1, comentario)
                    sentencia?.setInt(2, idUsuario!!)
                    sentencia?.setInt(3, hotelIdGlobal!!)
                    sentencia?.setInt(4, ratingValue)
                    sentencia?.executeUpdate()

                    val comentarios = obtenerComentarios(hotelIdGlobal!!)
                    withContext(Dispatchers.Main) {
                        val adapter = ComentarioAdapter(comentarios)
                        rcvComentarios.adapter = adapter
                        txtComentario.text.clear() // Limpia el campo de texto después de enviar el comentario
                    }
                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
            }
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

    // Función privada para navegar hacia atrás en función de la actividad previa
    private fun navigateBack() {
        when (prevActivity) {
            "PaginaInicio" -> startActivity(Intent(this, PaginaInicio::class.java))
            "ofertasUsuarios" -> startActivity(Intent(this, Ofertas::class.java))
            else -> finish()
        }
    }
}

