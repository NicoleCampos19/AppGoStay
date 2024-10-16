package emily.jacobo.gostay

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import emily.jacobo.gostay.PaginaInicio.Companion.hotelIdGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class verMas_servicios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_mas_servicios)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // ID del hotel
        val idHotelGlobal = PaginaInicio.hotelIdGlobal

        val lblValoracion = findViewById<TextView>(R.id.lblValoracion)


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
                    lblValoracion.text = calificacion // Actualiza el TextView en el hilo principal
                    Log.d("CalificacionHotel", calificacion) // Agregar log para verificar el resultado

                }
            }
        }

        // Llamada a la función para actualizar el TextView
        actualizarCalificacionHotel(hotelIdGlobal!!)




        // Botón de retroceso
        val imvAtras = findViewById<ImageView>(R.id.imvAtrasvms)
        imvAtras.setOnClickListener {
            finish()
            overridePendingTransition(0,0)

        }


        // Llamar a la función para obtener calificaciones
        obtenerCalificaciones(idHotelGlobal!!)
        obtenerEstrellas(idHotelGlobal!!)
    }

    // Función para obtener las calificaciones
    private fun obtenerCalificaciones(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val sentencia = objConexion?.prepareStatement(
                    """
                    SELECT id_servicio, AVG(id_calificación) * 20 AS calificacion_escala_100
                    FROM tbValoracionOtrosServicios
                    WHERE id_hoteles = ?
                    GROUP BY id_servicio
                    """.trimIndent()
                )
                sentencia?.setInt(1, idHotel)

                val resultados = sentencia?.executeQuery()

                // Inicialización de calificaciones
                var calificacionLimpieza = 0
                var calificacionUbicacion = 0
                var calificacionPersonal = 0
                var calificacionInstalaciones = 0

                // Iterar sobre los resultados
                while (resultados?.next() == true) {
                    val idServicio = resultados.getInt("id_servicio")
                    val calificacion = resultados.getInt("calificacion_escala_100") ?: 0

                    // Asignar calificaciones según el servicio
                    when (idServicio) {
                        1 -> calificacionLimpieza = calificacion // Limpieza
                        2 -> calificacionUbicacion = calificacion // Ubicación
                        3 -> calificacionPersonal = calificacion // Personal
                        4 -> calificacionInstalaciones = calificacion // Instalaciones
                    }
                }

                withContext(Dispatchers.Main) {
                    // Actualizar los ProgressBar en el hilo principal
                    findViewById<ProgressBar>(R.id.pbLimpieza).progress = calificacionLimpieza
                    findViewById<ProgressBar>(R.id.pbUbicacion).progress = calificacionUbicacion
                    findViewById<ProgressBar>(R.id.pbPersonal).progress = calificacionPersonal
                    findViewById<ProgressBar>(R.id.pbInstalaciones).progress = calificacionInstalaciones
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al obtener calificaciones: ${e.message}")
            }
        }
    }

    private fun obtenerEstrellas(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                val sentencia = objConexion?.prepareStatement(
                    """
                SELECT id_calificación, COUNT(*) AS total,
                (SELECT COUNT(*) FROM tbValoraciones WHERE id_hoteles = ?) AS total_calificaciones
                FROM tbValoraciones
                WHERE id_hoteles = ?
                GROUP BY id_calificación
                """.trimIndent()
                )
                sentencia?.setInt(1, idHotel)
                sentencia?.setInt(2, idHotel)

                val resultados = sentencia?.executeQuery()

                // Variables para almacenar el total de cada calificación
                var totalMuyMalo = 0
                var totalMalo = 0
                var totalRegular = 0
                var totalBueno = 0
                var totalExcelente = 0
                var totalCalificaciones = 0

                // Iterar sobre los resultados
                while (resultados?.next() == true) {
                    val idCalificacion = resultados.getInt("id_calificación")
                    val total = resultados.getInt("total")
                    totalCalificaciones = resultados.getInt("total_calificaciones")

                    // Asignar el total a la variable correspondiente
                    when (idCalificacion) {
                        1 -> totalMuyMalo = total
                        2 -> totalMalo = total
                        3 -> totalRegular = total
                        4 -> totalBueno = total
                        5 -> totalExcelente = total
                    }
                }

                // Calcular porcentajes
                val porcentajeMuyMalo = if (totalCalificaciones > 0) (totalMuyMalo.toFloat() / totalCalificaciones * 100).toInt() else 0
                val porcentajeMalo = if (totalCalificaciones > 0) (totalMalo.toFloat() / totalCalificaciones * 100).toInt() else 0
                val porcentajeRegular = if (totalCalificaciones > 0) (totalRegular.toFloat() / totalCalificaciones * 100).toInt() else 0
                val porcentajeBueno = if (totalCalificaciones > 0) (totalBueno.toFloat() / totalCalificaciones * 100).toInt() else 0
                val porcentajeExcelente = if (totalCalificaciones > 0) (totalExcelente.toFloat() / totalCalificaciones * 100).toInt() else 0

                // Actualizar los ProgressBar en el hilo principal
                withContext(Dispatchers.Main) {
                    findViewById<ProgressBar>(R.id.pbMuyMalo).progress = porcentajeMuyMalo
                    findViewById<ProgressBar>(R.id.pbMalo).progress = porcentajeMalo
                    findViewById<ProgressBar>(R.id.pbRegular).progress = porcentajeRegular
                    findViewById<ProgressBar>(R.id.pbBueno).progress = porcentajeBueno
                    findViewById<ProgressBar>(R.id.pbExcelente).progress = porcentajeExcelente
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al obtener calificaciones: ${e.message}")
            }
        }
    }

}
