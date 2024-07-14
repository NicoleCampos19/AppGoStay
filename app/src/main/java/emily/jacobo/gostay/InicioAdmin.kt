package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorHotelAdmin
import RecyclerViewHelpers.HotelAdapter
import android.content.Intent
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
import modelo.tbFavoritos
import modelo.tbHotel

class InicioAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rcvHotel = findViewById<RecyclerView>(R.id.rcvHotelAdmin)
        rcvHotel.layoutManager = LinearLayoutManager(this)

        fun obtenerHoteles(): List<tbHotel>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbHoteles")!!

            val listaHoteles = mutableListOf<tbHotel>()

            while (resultSet.next()){
                val id_hoteles = resultSet.getInt("id_hoteles")
                val nombre = resultSet.getString("nombre")
                val descripcion = resultSet.getString("descripcion")
                val direccion = resultSet.getString("direccion")
                val correo = resultSet.getString("correo")
                val cantidad_habitaciones = resultSet.getInt("cantidad_habitaciones")
                val img_url = resultSet.getString("img_url")
                val id_tipo_habitacion = resultSet.getInt("id_tipo_habitacion")
                val id_servicio_hotel = resultSet.getInt("id_servicio_hotel")
                val id_valoracion = resultSet.getInt("id_valoracion")

                val valoresJuntos = tbHotel(id_hoteles, nombre, descripcion, direccion, correo, cantidad_habitaciones, img_url, id_tipo_habitacion, id_servicio_hotel, id_valoracion)

                listaHoteles.add(valoresJuntos)
            }
            return listaHoteles
        }


        CoroutineScope(Dispatchers.IO).launch {
            val hotelDB = obtenerHoteles()

            withContext(Dispatchers.Main){
                val adapter = AdaptadorHotelAdmin(hotelDB){ hotel ->
                    val intent = Intent(this@InicioAdmin, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                rcvHotel.adapter = adapter
            }
        }

    }
}