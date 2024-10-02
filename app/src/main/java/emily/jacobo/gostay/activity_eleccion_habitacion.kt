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

    // Variables que se inicializarán más tarde
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
        //Mando a llamar la imágen que está en la vista
        val imvRegresaralHotel = findViewById<ImageView>(R.id.imvRegresaralHotel)
        imvRegresaralHotel.setOnClickListener {
            finish()
        }

        //Mando a llamar el rvc de la vista
        val rcvTiposHabitaciones = findViewById<RecyclerView>(R.id.rcvTiposHabitaciones)
        rcvTiposHabitaciones.layoutManager = LinearLayoutManager(this)

        //El id del hotel recibido será igual al id del hotel al que se le de click en la página de inicio
        val idHotelRecivido = PaginaInicio.hotelIdGlobal

        //Hago un select para traer la información del tipo de habitación
        fun loadTipoHabitacionesFromDatabase(idHotelRecivido: Int): List<tbTipoHabitacion> {
            val tipoHabitacionList = mutableListOf<tbTipoHabitacion>()
            val query = """
        select id_tipo_habitacion, nombre_tipo_habitacion, precio_habitacion, img_tipo_habitacion from tbTiposHabitaciones where id_hoteles = ? AND estado = 'activo'

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
                                val img_url = rs.getString("img_tipo_habitacion")
                                tipoHabitacionList.add(tbTipoHabitacion(idTipoHabitacion, nombre, precio, img_url))
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
            // Uso el id_hoteles para cargar las habitaciones del hotel
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