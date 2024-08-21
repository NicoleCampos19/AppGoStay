package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptorTipoHabitacion
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.tbHotel
import java.lang.reflect.Array.setInt
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class activity_confirmacionReserva : AppCompatActivity() {

    companion object {
        lateinit var direccionHotelGlobal: String
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmacion_reserva)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_confirmacion_reserva)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //recividos de verdad XD

        val idHotelRecivido = PaginaInicio.hotelIdGlobal
        val idTipoHabitacionRecivido = AdaptorTipoHabitacion.idTipoHabitacionGlobal
        val cvv = activity_reserva.cvv
        val fechaCaducidad = activity_reserva.fechaCaducidad
        val numeroTarjeta = activity_reserva.numeroTarjeta
        val nombreTitular = activity_reserva.nombreTitular
        val fechaEntrada = activity_reserva.fechaEntrada
        val fechaSalida = activity_reserva.fechaSalida
        val idUsuario = PaginaInicio.idUsuarioGlobalL
        val idDepartamento = obtenerIdDepartamentoPorNombre(activity_reserva.departamento ?: "")

        fun buscarDireccionHotelPorId(idHotel: Int): String? {
            var direccionHotel: String? = null

            // Lanza una corutina para realizar la operación en un hilo de IO
            GlobalScope.launch(Dispatchers.IO) {
                // 1- Crea un objeto de la clase conexión
                val objConexion = ClaseConexion().cadenaConexion()

                // 2- Prepara la sentencia SQL para buscar la dirección
                val query = "SELECT direccion FROM tbHoteles WHERE id_hoteles = ?"

                objConexion?.use { connection ->
                    try {
                        val statement = connection.prepareStatement(query)
                        statement.setInt(1, idHotel)

                        // 3- Ejecuta la consulta y obtiene la dirección
                        val resultSet = statement.executeQuery()
                        if (resultSet.next()) {
                            direccionHotel = resultSet.getString("direccion")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
           direccionHotelGlobal = direccionHotel?: ""
            return direccionHotelGlobal
        }

        if (idDepartamento != null) {
        } else {
            println("No se encontró un departamento con el nombre $idDepartamento")
        }
        val nombreUsuario = PaginaInicio.nombreUsuarioGlobalL
        val nombreTipoHabitacion = buscarNombreTipoHabitacionPorId(idTipoHabitacionRecivido ?: -1)
        val nombreHotel = buscarNombreHotelPorId(idHotelRecivido ?: -1)
        val direccionHotelRecivido = direccionHotelGlobal



        //mostrar
        findViewById<TextView>(R.id.tvHotelName).text = nombreHotel
        findViewById<TextView>(R.id.tvHotelAddress).text = direccionHotelRecivido
        findViewById<TextView>(R.id.tvEntradaDate).text = fechaEntrada
        findViewById<TextView>(R.id.tvSalidaDate).text = fechaSalida
        findViewById<TextView>(R.id.tvSeleccionDetails).text = nombreTipoHabitacion
        findViewById<TextView>(R.id.tvReservaNombre).text = nombreUsuario





        

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
    fun obtenerIdDepartamentoPorNombre(nombreDepartamento: String): Int? {
        var idDepartamento: Int? = null
        val conexion: Connection? = ClaseConexion().cadenaConexion()

        try {
            // Crear el PreparedStatement
            val query = "SELECT id_departamento FROM tbDepartamentos WHERE nombre_departamento = ?"
            val statement: PreparedStatement? = conexion?.prepareStatement(query)

            // Asignar el valor del nombre del departamento al parámetro
            statement?.setString(1, nombreDepartamento)

            // Ejecutar la consulta
            val resultSet: ResultSet? = statement?.executeQuery()

            // Obtener el resultado
            if (resultSet?.next() == true) {
                idDepartamento = resultSet.getInt("id_departamento")
            }

            // Cerrar ResultSet y PreparedStatement
            resultSet?.close()
            statement?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Cerrar la conexión a la base de datos
            conexion?.close()
        }

        return idDepartamento
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
}