package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorServicioHotel
import RecyclerViewHelpers.ComentarioAdapter
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
import kotlinx.coroutines.*
import modelo.ClaseConexion
import modelo.tbComentarios
import modelo.tbHotel
import modelo.tbServiciosHotel

class hotel_detalles : AppCompatActivity() {

    private lateinit var rcvServicioHotel: RecyclerView
    private lateinit var rcvComentarios: RecyclerView
    private lateinit var prevActivity: String
    private lateinit var imvEnviar: ImageView
    private lateinit var txtComentario: EditText

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
        rcvServicioHotel.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rcvComentarios = findViewById(R.id.rcvComentarios)
        rcvComentarios.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        prevActivity = intent.getStringExtra("prev_activity") ?: "PaginaInicio"

        val imageViewBack = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        imageViewBack.setOnClickListener {
            navigateBack()
        }

        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as? tbHotel
        if (idHotel != -1) {
            obtenerServiciosHotel(idHotel)
        }

        val btnTipoHabitacion: Button = findViewById(R.id.btnTipoHabitacion)
        val idHotelRecivido = PaginaInicio.hotelIdGlobal
        btnTipoHabitacion.setOnClickListener {
            if (idHotelRecivido != -1) {
                val intent = Intent(this, activity_eleccion_habitacion::class.java)
                startActivity(intent)
            } else {
                println("No se encontró el id del hotel")
            }
        }

        imvEnviar = findViewById(R.id.imvEnviar)
        txtComentario = findViewById(R.id.txtComentario)

        val imvReportar = findViewById<ImageView>(R.id.imvReportar)
        val btnReportar = findViewById<Button>(R.id.btnReportar)

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

        CoroutineScope(Dispatchers.IO).launch {
            val comentariosDB = obtenerComentarios()
            withContext(Dispatchers.Main) {
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvComentarios.adapter = miAdaptador
            }
        }

        imvEnviar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()
                try {
                    val addComentario =
                        objConexion?.prepareStatement("INSERT INTO tbValoraciones(comentario, id_usuario, id_calificación) VALUES (?, ?, ?)")!!
                    addComentario.setString(1, txtComentario.text.toString())

                    val idUsuario = obtenerIdUsuario(activity_iniciar_sesion.correoIngresado)
                    if (idUsuario != null) {
                        addComentario.setInt(2, idUsuario)
                        addComentario.setInt(3, 3)
                        addComentario.executeUpdate()

                        val nuevoComentario = obtenerComentarios()
                        withContext(Dispatchers.Main) {
                            (rcvComentarios.adapter as? ComentarioAdapter)?.actualizarListado(
                                nuevoComentario
                            )
                            txtComentario.setText("")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            println("No se pudo obtener el id del usuario")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    objConexion?.close()
                }
            }
        }

        hotel?.let {
            val imageView = findViewById<ImageView>(R.id.imvImagenHotel)
            val imageUrl = it.img_url

            if (imageView == null) {
                println("ImageView no encontrado.")
                return@let
            }

            if (!imageUrl.isNullOrBlank()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(imageView)
            } else {
                println("URL de imagen no válida: $imageUrl")
            }
        } ?: run {
            println("Hotel es nulo.")
        }
        ?: run {
            println("El objeto hotel es nulo")
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
                val intent = Intent(this, PaginaInicio::class.java)
                startActivity(intent)
            }
        }
        finish()
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



    private suspend fun obtenerComentarios(): List<tbComentarios> {
        return withContext(Dispatchers.IO) {
            val listaComentarios = mutableListOf<tbComentarios>()
            val query = "SELECT * FROM tbValoraciones"

            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.createStatement()
                    val resultSet = statement.executeQuery(query)
                    resultSet.use { rs ->
                        while (rs.next()) {
                            val idValoracion = rs.getInt("id_valoracion")
                            val comentario = rs.getString("comentario")
                            val idUsuario = rs.getInt("id_usuario")
                            listaComentarios.add(tbComentarios(idValoracion, comentario, idUsuario))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            listaComentarios
        }
    }

    private suspend fun obtenerIdUsuario(correo: String): Int? {
        return withContext(Dispatchers.IO) {
            var idUsuario: Int? = null
            val query = "SELECT id_usuario FROM tbUsuarios WHERE correo = ?"

            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query)
                    statement.setString(1, correo)
                    val resultSet = statement.executeQuery()
                    resultSet.use { rs ->
                        if (rs.next()) {
                            idUsuario = rs.getInt("id_usuario")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            idUsuario
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnDismissListener {
            // Aquí puedes manejar el evento de cierre del menú si es necesario.
        }
        popup.show()
    }
}
