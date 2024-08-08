package emily.jacobo.gostay

import RecyclerViewHelpers.HotelAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.databinding.ActivityProximoDestinoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos
import modelo.tbHotel

class proximo_destino : AppCompatActivity() {
    private lateinit var rcvHoteles: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_proximo_destino)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAtras = findViewById<ImageView>(R.id.btnAtras)
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        rcvHoteles = findViewById(R.id.rcvHoteles)
        rcvHoteles.layoutManager = LinearLayoutManager(this)

        btnAtras.setOnClickListener {
            val volverAtras = Intent(this, opcionesdebusquedad::class.java)
            startActivity(volverAtras)
        }

        etBuscar.addTextChangedListener { text ->
            val textoEscrito = text.toString()
            obtenerHoteles(textoEscrito)
        }
    }

    private fun obtenerHoteles(textoEscrito: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val hotelDB = fetchHotelsFromDatabase(textoEscrito)
            val favDB = fetchFavoritesFromDatabase()
            withContext(Dispatchers.Main) {
                val adapter = HotelAdapter(hotelDB, favDB) { hotel ->
                    val intent = Intent(this@proximo_destino, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                rcvHoteles.adapter = adapter
            }
        }
    }

    private fun fetchHotelsFromDatabase(textoEscrito: String): List<tbHotel> {
        val objConexion = ClaseConexion().cadenaConexion()
        val query = "SELECT * FROM tbHoteles WHERE nombre LIKE ?"
        val buscar = objConexion?.prepareStatement(query)
        buscar?.setString(1, "%${textoEscrito}%")
        val resultSet = buscar?.executeQuery()
        val listaHoteles = mutableListOf<tbHotel>()

        println(textoEscrito)
        while (resultSet?.next() == true) {
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

    private fun fetchFavoritesFromDatabase(): List<tbFavoritos> {
        val objConexion = ClaseConexion().cadenaConexion()
        val statement = objConexion?.createStatement()
        val resultSet = statement?.executeQuery("SELECT * FROM tbPreferenciales")
        val listaFav2 = mutableListOf<tbFavoritos>()

        while (resultSet?.next() == true) {
            val id_preferenciales = resultSet.getInt("id_preferencial")
            val id_hoteles = resultSet.getInt("id_hoteles")
            val id_usuario = resultSet.getInt("id_usuario")

            val valoresJuntosFav = tbFavoritos(id_preferenciales, id_hoteles, id_usuario)
            listaFav2.add(valoresJuntosFav)
        }
        return listaFav2
    }
}
