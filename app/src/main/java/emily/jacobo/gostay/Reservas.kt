package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorHabitaciones
import RecyclerViewHelpers.AdaptorTipoHabitacion
import RecyclerViewHelpers.ReservaAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import modelo.ReservaInfo
import modelo.tbHabitaciones
import modelo.tbTipoHabitacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Reservas : AppCompatActivity() {

    private lateinit var reservaAdapter: ReservaAdapter



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reservas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imvBuscar = findViewById<ImageView>(R.id.imvBuscarb)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritos)
        val imvReseva = findViewById<ImageView>(R.id.imvReservaa)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)


        imvBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvFavorito.setOnClickListener {
            val siguientepantalla = Intent(this, Favoritos::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvReseva.setOnClickListener {
            val siguientepantalla = Intent(this, Reservas::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvPerfil.setOnClickListener {
            val siguientepantalla = Intent(this, Perfil::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }


        val idUsuarioGLobal = PaginaInicio.idUsuarioGlobalL
        val recyclerView: RecyclerView = findViewById(R.id.rcvMostrarReservaciones)


        fun loadHabitacionesFromDatabase(idUsuarioRecivido: Int): List<ReservaInfo> {
            val reservasActivas = mutableListOf<ReservaInfo>()

            val query = """
        select hot.nombre as hotel_nombre, ha.entrada, ha.salida, us.nombre_usuario, hot.img_url, th.nombre_tipo_habitacion
        from tbHabitaciones ha
        INNER JOIN tbHoteles hot ON ha.id_hoteles = hot.id_hoteles
        INNER JOIN tbUsuarios us ON ha.id_usuario = us.id_usuario
        INNER JOIN tbTiposHabitaciones th ON ha.id_tipo_habitacion = th.id_tipo_habitacion
        where ha.id_usuario = ?
    """.trimIndent()

            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idUsuarioRecivido)
                    }

                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        resultSet.use { rs ->
                            while (rs.next()) {
                                val entrada = rs.getString("entrada")
                                val salida = rs.getString("salida")
                                val usuario_nombre = rs.getString("nombre_usuario")
                                val img_url = rs.getString("img_url")
                                val nombre_tipo_habitacion = rs.getString("nombre_tipo_habitacion")
                                val hotel_nombre = rs.getString("hotel_nombre")

                                val reserva = ReservaInfo(entrada, salida, usuario_nombre, img_url, nombre_tipo_habitacion, hotel_nombre)

                                // Cambiar el formato para que coincida con "yyyy-MM-dd"
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val fechaSalida = dateFormat.parse(salida)
                                val fechaActual = Date() // Fecha actual

                                // Solo agregar a las reservas activas si la fecha de salida es mayor o igual a la actual
                                if (fechaSalida != null && !fechaSalida.before(fechaActual)) {
                                    reservasActivas.add(reserva) // Reserva activa
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }

            return reservasActivas // Solo retorna las reservas activas
        }
        CoroutineScope(Dispatchers.IO).launch {
            // Obtener solo las reservas activas
            val reservasActivas = idUsuarioGLobal?.let { loadHabitacionesFromDatabase(it) } ?: emptyList()

            withContext(Dispatchers.Main) {
                // Adaptador para reservas activas
                val reservaAdapterActivas = ReservaAdapter(reservasActivas)
                val recyclerViewActivas: RecyclerView = findViewById(R.id.rcvMostrarReservaciones)
                recyclerViewActivas.adapter = reservaAdapterActivas
                recyclerViewActivas.layoutManager = LinearLayoutManager(this@Reservas)

            }
        }




    }
}