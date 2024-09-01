package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptorTipoHabitacion
import android.os.Bundle
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
import modelo.tbTipoHabitacion

class activity_eleccion_habitacion : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptorTipoHabitacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eleccion_habitacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imvRegresaralHotel = findViewById<ImageView>(R.id.imvRegresaralHotel)
        imvRegresaralHotel.setOnClickListener {
            finish()
        }

        val rcvTiposHabitaciones = findViewById<RecyclerView>(R.id.rcvTiposHabitaciones)
        rcvTiposHabitaciones.layoutManager = LinearLayoutManager(this)

        val idHotelRecivido = PaginaInicio.hotelIdGlobal

        fun loadTipoHabitacionesFromDatabase(idHotelRecivido: Int): List<tbTipoHabitacion> {
            val tipoHabitacionList = mutableListOf<tbTipoHabitacion>()
            val query = """
        SELECT th.id_tipo_habitacion, th.nombre_tipo_habitacion, th.precio_habitacion 
        FROM tbIntermedia_Hoteles_TipoHabitacion thb  
        INNER JOIN tbTiposHabitaciones th 
        ON thb.id_tipo_habitacion = th.id_tipo_habitacion 
        WHERE thb.id_hoteles = ?
    """.trimIndent()

            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idHotelRecivido)
                    }

                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        resultSet.use { rs ->
                            while (rs.next()) {
                                val idTipoHabitacion = rs.getInt("id_tipo_habitacion")
                                val nombre = rs.getString("nombre_tipo_habitacion")
                                val precio = rs.getInt("precio_habitacion")
                                tipoHabitacionList.add(tbTipoHabitacion(idTipoHabitacion, nombre, precio))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }

            return tipoHabitacionList
        }
        if (idHotelRecivido != -1) {
            // Usar el id_hoteles para cargar las habitaciones del hotel
            CoroutineScope(Dispatchers.IO).launch {

                val idHotelporsiacaso = idHotelRecivido ?: -1
                val tipoHabitacionDB = loadTipoHabitacionesFromDatabase(idHotelporsiacaso)
                withContext(Dispatchers.Main) {
                    val miAdaptador = AdaptorTipoHabitacion(tipoHabitacionDB)
                    rcvTiposHabitaciones.adapter = miAdaptador
                }
            }
        }else{
            println("No se encontro el id del hotel")
        }

    }
}