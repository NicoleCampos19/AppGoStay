package emily.jacobo.gostay

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import modelo.ClaseConexion
import java.lang.reflect.Array.setInt

class activity_confirmacionReserva : AppCompatActivity() {

    private lateinit var tvEntradaR: TextView
    private lateinit var tvSalidaR: TextView
    private lateinit var tvFechaCaducidadR: TextView
    private lateinit var txtCvv: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacion_reserva)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_confirmacion_reserva)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvEntradaR = findViewById(R.id.tvEntradaDate)
        tvSalidaR = findViewById(R.id.tvSalidaDate)
        val idHotelRecivido = PaginaInicio.hotelIdGlobal

        //trayendo los intents

        val idTipoHabitacion = intent.getIntExtra("id_tipo_habitacion", -1)
        val numeroTarjeta = intent.getStringExtra("numero_tarjeta")
        val nombreTitular = intent.getStringExtra("nombre_titular")
        val cvv = intent.getStringExtra("cvv")
        Log.d("activity_confirmacionReserva", "CVV recibido: $cvv")
        val entrada = intent.getStringExtra("entrada")
        val salida = intent.getStringExtra("salida")
        val idDepartamento = intent.getIntExtra("id_departamento", -1)
        val fechaCaducidad = intent.getStringExtra("fechaCaducidad")
        val idUsuario = intent.getIntExtra("id_usuario", -1)

        val nombreUsuario = intent.getStringExtra("nombre_usuario")

        fun buscarDireccionHotelPorId(idHotel: Int): String? {
            var direccionHotel: String? = null
            val query = "SELECT direccion FROM tbHoteles WHERE id_hoteles = ?"
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idHotel)
                    }
                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        if (resultSet.next()) {
                            direccionHotel = resultSet.getString("direccion")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }
            return direccionHotel
        }
        fun buscarNombreHotelPorId(idHotel: Int): String? {
            var nombreHotel: String? = null
            val query = "SELECT nombre FROM tbHoteles WHERE id_hoteles = ?"
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idHotel)
                    }
                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        if (resultSet.next()) {
                            nombreHotel = resultSet.getString("nombre")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }
            return nombreHotel
        }
        fun buscarNombreTipoHabitacionPorId(idTipoHabitacion: Int): String? {
            var nombreTipoHabitacion: String? = null
            val query = "SELECT nombre_tipo_habitacion FROM tbTiposHabitaciones WHERE id_tipo_habitacion = ?"
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idTipoHabitacion)
                    }
                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        if (resultSet.next()) {
                            nombreTipoHabitacion = resultSet.getString("nombre_tipo_habitacion")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }
            return nombreTipoHabitacion
        }


        val direccionHotel = buscarDireccionHotelPorId(idHotelRecivido ?: -1)
        val nombreHotel = buscarNombreHotelPorId(idHotelRecivido ?: -1)


        //mostrar
        findViewById<TextView>(R.id.tvHotelName).text = nombreHotel
        findViewById<TextView>(R.id.tvHotelAddress).text = direccionHotel
        tvEntradaR.text = entrada
        tvSalidaR.text = salida
        findViewById<TextView>(R.id.tvSeleccionDetails).text = buscarNombreTipoHabitacionPorId(idTipoHabitacion)
        findViewById<TextView>(R.id.tvReservaNombre).text = nombreUsuario




        //findViewById<TextView>(R.id.tvSeleccionDetails).text = tipoHabitacion
        //findViewById<TextView>(R.id.tvTotalAmount).text = "$$total"

    }
}