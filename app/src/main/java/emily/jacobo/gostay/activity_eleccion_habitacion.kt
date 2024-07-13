package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptorTipoHabitacion
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

       val rcvTiposHabitaciones = findViewById<RecyclerView>(R.id.rcvTiposHabitaciones)
        rcvTiposHabitaciones.layoutManager = LinearLayoutManager(this)




        fun loadTipoHabitacionesFromDatabase(): List<tbTipoHabitacion> {


            val objConexion = ClaseConexion().cadenaConexion()
            val tipoHabitacionList = mutableListOf<tbTipoHabitacion>()

            val statement = objConexion?.createStatement()
            val resultSet =
                statement?.executeQuery("SELECT nombre_tipo_habitacion, precio_habitacion FROM tbTiposHabitaciones")!!

            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre_tipo_habitacion")
                val precio = resultSet.getInt("precio_habitacion")

                val valoresJuntos = tbTipoHabitacion(nombre, precio)
                tipoHabitacionList.add(valoresJuntos)

            }

            return tipoHabitacionList
        }

        CoroutineScope(Dispatchers.IO).launch {

            val tipoHabitacionDB = loadTipoHabitacionesFromDatabase()
            withContext(Dispatchers.Main) {
                val miAdaptador = AdaptorTipoHabitacion(tipoHabitacionDB)
                rcvTiposHabitaciones.adapter = miAdaptador
            }

        }
    }

}