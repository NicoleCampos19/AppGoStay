package emily.jacobo.gostay

import RecyclerViewHelpers.ReservaAdapter
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class historial_reservas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_reservas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se asigna el valor del ID de usuario global de la página de inicio.
        val idUsuarioGLobal = PaginaInicio.idUsuarioGlobalL

        // Se crea una lista mutable para almacenar reservas inactivas
        fun loadHabitacionesFromDatabase(idUsuarioRecivido: Int): List<ReservaInfo> {
            val reservasInactivas = mutableListOf<ReservaInfo>()
            val query = """
        select hot.nombre as hotel_nombre, ha.entrada, ha.salida, us.nombre_usuario, hot.img_url, th.nombre_tipo_habitacion
        from tbHabitaciones ha
        INNER JOIN tbHoteles hot ON ha.id_hoteles = hot.id_hoteles
        INNER JOIN tbUsuarios us ON ha.id_usuario = us.id_usuario
        INNER JOIN tbTiposHabitaciones th ON ha.id_tipo_habitacion = th.id_tipo_habitacion
        where ha.id_usuario = ?
    """.trimIndent()
// Consulta SQL que une varias tablas para obtener información de las reservas y habitaciones
            // basándose en el ID del usuario.
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idUsuarioRecivido)
                    }

                    // Se crea una conexión y se prepara la consulta SQL, configurando el ID de usuario recibido
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

                                // Solo agregar a las reservas inactivas si la fecha de salida es anterior a la actual
                                if (fechaSalida != null && fechaSalida.before(fechaActual)) {
                                    reservasInactivas.add(reserva) // Reserva inactiva
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }

            return reservasInactivas // Retorna solo las reservas inactivas
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Obtener solo las reservas inactivas
            val reservasInactivas = idUsuarioGLobal?.let { loadHabitacionesFromDatabase(it) } ?: emptyList()

            withContext(Dispatchers.Main) {
                // Adaptador para reservas inactivas
                val reservaAdapterInactivas = ReservaAdapter(reservasInactivas)
                val recyclerViewInactivas: RecyclerView = findViewById(R.id.rcvMostrarReservaciones)
                recyclerViewInactivas.adapter = reservaAdapterInactivas
                recyclerViewInactivas.layoutManager = LinearLayoutManager(this@historial_reservas)
            }
        }
    }
}