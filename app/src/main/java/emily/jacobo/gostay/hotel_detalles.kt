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

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var prevActivity: String
    private lateinit var servicioAdapter: ServicioAdapter

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    var latitud: Double = 0.0
    var longitud: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_detalles)

        val reseñaGlobal = activity_resenas.resenaGlobal
        val rcvCarrusels = findViewById<RecyclerView>(R.id.carrusel_recycler_views)
        rcvCarrusels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mapView = findViewById(R.id.mapView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

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

        val ID_Hotel = intent.getIntExtra("id_hoteles", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val conexion = ClaseConexion().cadenaConexion()
            val statement = conexion?.prepareStatement("SELECT * FROM tbHoteles WHERE id_hoteles = ?")!!
            statement.setInt(1, ID_Hotel)
            val resultSet = statement.executeQuery()
            withContext(Dispatchers.Main) {
                if (resultSet.next()) {
                    latitud = resultSet.getDouble("latitudHotel")
                    longitud = resultSet.getDouble("longitudHotel")
                    if (::googleMap.isInitialized) {
                        updateMapLocation()
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val imagenesBD = obtenerImagenes()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorCarrusel(imagenesBD)
                rcvCarrusels.adapter = adapter
            }
        }

        val idHotelGlobal = PaginaInicio.hotelIdGlobal
        val recyclerView: RecyclerView = findViewById(R.id.rcvServiciosHotel)

        suspend fun loadServiciosFromDatabase(idHotelGlobal: Int): List<ServicioInfo> {
            val serviciosList = mutableListOf<ServicioInfo>()
            val query = """
                SELECT sh.nombre_servicio, sh.img_icono_hotel
                FROM tbIntermedia_Hoteles_Servicios ish
                INNER JOIN tbServiciosHotel sh ON ish.id_servicio_hotel = sh.id_servicio_hotel
                WHERE id_hoteles = ?
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
                                serviciosList.add(ServicioInfo(nombre_servicio, img_icono_hotel))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return serviciosList
        }

        CoroutineScope(Dispatchers.IO).launch {
            val servicios = loadServiciosFromDatabase(idHotelGlobal!!)
            withContext(Dispatchers.Main) {
                servicioAdapter = ServicioAdapter(servicios)
                recyclerView.adapter = servicioAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@hotel_detalles, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        val imageViewBack = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        imageViewBack.setOnClickListener {
            AdaptadorOfertas.descuentoTotalGlobal = 0.0
            navigateBack()
        }

        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as tbHotel

        val btnTipoHabitacion: Button = findViewById(R.id.btnTipoHabitacion)
        btnTipoHabitacion.setOnClickListener {
            if (idHotel != -1) {
                val intent = Intent(this, activity_eleccion_habitacion::class.java)
                startActivity(intent)
            }
        }

        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val txtComentario = findViewById<EditText>(R.id.txtComentario)
        val imvEnviar = findViewById<ImageView>(R.id.imvEnviar)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)

        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        suspend fun obtenerComentarios(idHotel: Int): List<tbComentarios> {
            val listaComentarios = mutableListOf<tbComentarios>()
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.prepareStatement(
                """
                    SELECT vl.id_valoracion, vl.comentario, us.id_usuario, us.nombre_usuario, us.imgfoto 
                    FROM tbValoraciones vl 
                    INNER JOIN tbUsuarios us ON vl.id_usuario = us.id_usuario 
                    WHERE vl.id_hoteles = ?
                """.trimIndent()
            )
            statement?.setInt(1, idHotel)
            val resultSet = statement?.executeQuery()
            if (resultSet != null) {
                while (resultSet.next()) {
                    val id_valoracion = resultSet.getInt("id_valoracion")
                    val comentario = resultSet.getString("comentario")
                    val id_usuario = resultSet.getInt("id_usuario")
                    val nombre_usuario = resultSet.getString("nombre_usuario")
                    val imgfoto = resultSet.getString("imgfoto")
                    listaComentarios.add(tbComentarios(id_valoracion, comentario, id_usuario, nombre_usuario, imgfoto))
                }
            }
            return listaComentarios
        }

        CoroutineScope(Dispatchers.IO).launch {
            val comentarios = obtenerComentarios(hotelIdGlobal!!)
            withContext(Dispatchers.Main) {
                val adapter = ComentarioAdapter(comentarios)
                rcvComentarios.adapter = adapter
            }
        }

        imvEnviar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val comentario = txtComentario.text.toString()
                    val idUsuario = idUsuarioGlobalL
                    val objConexion = ClaseConexion().cadenaConexion()
                    val sentencia = objConexion?.prepareStatement("INSERT INTO tbValoraciones (comentario, id_usuario, id_hoteles) VALUES (?, ?, ?)")
                    sentencia?.setString(1, comentario)
                    sentencia?.setInt(2, idUsuario!!)
                    sentencia?.setInt(3, hotelIdGlobal!!)
                    sentencia?.executeUpdate()

                    val comentarios = obtenerComentarios(hotelIdGlobal!!)
                    withContext(Dispatchers.Main) {
                        val adapter = ComentarioAdapter(comentarios)
                        rcvComentarios.adapter = adapter
                        txtComentario.text.clear()
                    }
                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMapLocation()
    }

    private fun updateMapLocation() {
        val location = LatLng(latitud, longitud)
        googleMap.addMarker(MarkerOptions().position(location).title("Ubicación del Hotel"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun navigateBack() {
        when (prevActivity) {
            "PaginaInicio" -> startActivity(Intent(this, PaginaInicio::class.java))
            "ofertasUsuarios" -> startActivity(Intent(this, Ofertas::class.java))
            else -> finish()
        }
    }
}

