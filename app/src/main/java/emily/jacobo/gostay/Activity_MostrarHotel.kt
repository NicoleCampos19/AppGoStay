package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorServicioHotel
import android.content.Intent
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
import modelo.tbServiciosHotel

class Activity_MostrarHotel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mostrar_hotel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rcvServiciosHotel = findViewById<RecyclerView>(R.id.rcvServiciosHotel)

        rcvServiciosHotel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fun obtenerServiciosHotel(): List<tbServiciosHotel>{
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbServiciosHotel")!!

            val listaServiciosHotel = mutableListOf<tbServiciosHotel>()

            while (resultSet.next()){
                val id_servicio_hotel = resultSet.getInt("id_servicio_hotel")
                val nombre_servicio = resultSet.getString("nombre_servicio")

                val valoresJuntos = tbServiciosHotel(id_servicio_hotel, nombre_servicio)

                listaServiciosHotel.add(valoresJuntos)
            }
            return  listaServiciosHotel
        }
        CoroutineScope(Dispatchers.IO).launch {
            val serviciosHotelDB = obtenerServiciosHotel()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorServicioHotel(serviciosHotelDB)
                rcvServiciosHotel.adapter = adapter
            }
        }
        val imgRegresarServiciosHotel: ImageView = findViewById(R.id.imgRegresarServiciosHotel)
        imgRegresarServiciosHotel.setOnClickListener {
            val intent = Intent(this, PaginaInicio::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}