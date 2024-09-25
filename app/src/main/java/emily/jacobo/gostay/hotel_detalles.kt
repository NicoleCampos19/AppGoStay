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
import androidx.activity.enableEdgeToEdge
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.PaginaInicio.Companion.hotelIdGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.ServicioInfo
import modelo.tbCarrusel
import modelo.tbComentarios
import modelo.tbHotel
import java.sql.Statement

class hotel_detalles : AppCompatActivity() {


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

        setContentView(R.layout.activity_hotel_detalles)
       val rcvCarrusels = findViewById<RecyclerView>(R.id.carrusel_recycler_views)
        rcvCarrusels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fun obtenerImagenes(): List<tbCarrusel> {
            // Establece una conexión a la base de datos
            val objConexion = ClaseConexion().cadenaConexion()

            // Crea una lista mutable para almacenar las imágenes
            val lista = mutableListOf<tbCarrusel>()

            // Prepara la consulta SQL para seleccionar imágenes del hotel específico
            val statement = objConexion?.prepareStatement("SELECT * FROM tbImagenes_Hoteles WHERE id_Hoteles = ?")!!
            statement.setInt(1, PaginaInicio.hotelIdGlobal!!) // Asigna el ID del hotel a la consulta
            val resultSet = statement.executeQuery() // Ejecuta la consulta

            // Verifica si el resultado no es nulo
            if (resultSet != null) {
                // Itera a través de los resultados
                while (resultSet.next()) {
                    val id_imagenes = resultSet.getInt("id_imagenes")
                    val id_hoteles = resultSet.getInt("id_hoteles")
                    val url_imagen = resultSet.getString("url_imagen")

                    // Crea una instancia de tbCarrusel con los datos obtenidos
                    val valoresJuntos = tbCarrusel(id_imagenes, id_hoteles, url_imagen)
                    lista.add(valoresJuntos) // Agrega la imagen a la lista
                }
            }
            // Devuelve la lista de imágenes
            return lista
        }

// Asigna el adaptador al RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val ImagenesBD = obtenerImagenes() // Obtiene las imágenes de la base de datos

            withContext(Dispatchers.Main) {
                val adapter = AdaptadorCarrusel(ImagenesBD) // Crea un adaptador con las imágenes
                rcvCarrusels.adapter = adapter // Asigna el adaptador al RecyclerView
            }
        }

        val idHotelGlobal = PaginaInicio.hotelIdGlobal
        val recyclerView: RecyclerView = findViewById(R.id.rcvServiciosHotel)


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
                    recyclerView.layoutManager = LinearLayoutManager(this@hotel_detalles, LinearLayoutManager.HORIZONTAL, false)
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

        val btnTipoHabitacion: Button = findViewById(R.id.btnTipoHabitacion)
        val idHotelRecivido = PaginaInicio.hotelIdGlobal
        btnTipoHabitacion.setOnClickListener {
            if (idHotelRecivido != -1) {
                val intent = Intent(this, activity_eleccion_habitacion::class.java)
                startActivity(intent)
            }else{

                println("No se encontro el id del hotel")
            }
        }

        val imvVolverDetallesHotel = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val txtComentario = findViewById<EditText>(R.id.txtComentario)
        val imvEnviar = findViewById<ImageView>(R.id.imvEnviar)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)
        val imvReportar = findViewById<ImageView>(R.id.imvReportar)
        val btnReportar = findViewById<Button>(R.id.btnReportar)
        val txtCalificacion = findViewById<TextView>(R.id.txtCalificacion)

        //txtCalificacion.text = reseñaGlobal.promedio

        imvReportar.setOnClickListener {
            val irADenuncias = Intent(this, RealizarDenuncia::class.java)
            irADenuncias.putExtra("idHotel", idHotel)
            startActivity(irADenuncias)
        }

        btnReportar.setOnClickListener {
            val irADenuncias = Intent(this, RealizarDenuncia::class.java)
            irADenuncias.putExtra("idHotel", idHotel)
            startActivity(irADenuncias)
        }

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



        fun insertarValoracionComentario(idUsuario: Int, comentario: String, idHotel: Int) {
            val objConexion = ClaseConexion().cadenaConexion()

            // Insertar nueva valoración en la tabla tbValoraciones
            val insertarValoracionStmt = objConexion?.prepareStatement(
                "INSERT INTO tbValoraciones (id_usuario, comentario) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            )!!
            insertarValoracionStmt.setInt(1, idUsuario)
            insertarValoracionStmt.setString(2, comentario)
            insertarValoracionStmt.executeUpdate()

            // Obtener el ID de la valoración recién insertada
            val generatedKeys = insertarValoracionStmt.generatedKeys
            var idValoracion = 0
            if (generatedKeys.next()) {
                idValoracion = generatedKeys.getInt(1)
            }

            // Insertar en la tabla intermedia tbIntermedia_valoracion_hoteles
            val insertarIntermediaStmt = objConexion?.prepareStatement(
                "INSERT INTO tbIntermedia_valoracion_hoteles (id_intermedia_valoracion_hoteles, id_hoteles, id_valoracion) VALUES (seq_tbIntermedia_valoracion_hoteles.NEXTVAL, ?, ?)"
            )!!
            insertarIntermediaStmt.setInt(1, idHotel)
            insertarIntermediaStmt.setInt(2, idValoracion)
            insertarIntermediaStmt.executeUpdate()
        }





        //TODO: por si no funciona el filtro con datos nuevos!!

        fun obtenerComentarios(): List<tbComentarios> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            val comentarios_ = objConexion?.prepareStatement("SELECT *  FROM tbValoraciones")!!
            val resultSet = comentarios_.executeQuery()

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

        imvEnviar.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                // Verifica que el correo no sea nulo
                if (activity_iniciar_sesion.correoIngresado.isNotEmpty()) {
                    val idUsuario = obtenerIdUsuario(activity_iniciar_sesion.correoIngresado)

                    // Asegúrate de que idUsuario no sea nulo
                    if (idUsuario != null) {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val addComentario = objConexion?.prepareStatement("insert into tbValoraciones(comentario,id_usuario,id_calificacion) values(?,?,?)")

                        // Asegúrate de que addComentario no sea nulo
                        if (addComentario != null) {
                            addComentario.setString(1, txtComentario.text.toString())
                            addComentario.setInt(2, idUsuario)
                            addComentario.setInt(3, 3)
                            addComentario.executeUpdate()
                            objConexion.commit()

                            val nuevocomentario = obtenerComentarios()
                            withContext(Dispatchers.Main) {
                                (rcvComentarios.adapter as? ComentarioAdapter)?.actualizarListado(nuevocomentario)
                                txtComentario.setText("")

                                val intent = Intent(this@hotel_detalles, activity_resenas::class.java)
                                startActivity(intent)
                            }
                        } else {
                            Log.e("Error", "No se pudo preparar la declaración SQL")
                        }
                    } else {
                        Log.e("Error", "No se pudo obtener el ID del usuario")
                    }
                } else {
                    Log.e("Error", "El correo ingresado es vacío")
                }
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
