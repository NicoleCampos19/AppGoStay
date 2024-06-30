package emily.jacobo.gostay

import RecyclerViewHelpers.HotelAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Hotel
import java.sql.Connection
import java.sql.ResultSet

class PaginaInicio : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hotelAdapter: HotelAdapter
    private lateinit var hotelList: MutableList<Hotel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_inicio)

// Inicializar RecyclerView
        recyclerView = findViewById(R.id.rcvHotel)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Usar coroutine para obtener lista de hoteles desde la base de datos
        CoroutineScope(Dispatchers.Main).launch {
            hotelList = obtenerListaHotelesDesdeBaseDeDatos()

            // Verifica si la lista no está vacía antes de asignar el adaptador
            if (hotelList.isNotEmpty()) {
                hotelAdapter = HotelAdapter(hotelList)
                recyclerView.adapter = hotelAdapter
            } else {
                // Maneja el caso donde no hay datos
                println("La lista de hoteles está vacía.")
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        val imvBuscar = findViewById<ImageView>(R.id.imvBuscar)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritos)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)


        imvBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvFavorito.setOnClickListener {
            val siguientepantalla = Intent(this, Favoritos::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvReseva.setOnClickListener {
            val siguientepantalla = Intent(this, Reservas::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvPerfil.setOnClickListener {
            val siguientepantalla = Intent(this, Perfil::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }



    }

    private suspend fun obtenerListaHotelesDesdeBaseDeDatos(): MutableList<Hotel> {
        return withContext(Dispatchers.IO) {
            val hoteles = mutableListOf<Hotel>()
            val conexion: Connection? = ClaseConexion().cadenaConexion()

            if (conexion != null) {
                val statement = conexion.createStatement()
                val resultSet: ResultSet = statement.executeQuery("SELECT id_hoteles, nombre, img_url FROM tbHoteles")

                while (resultSet.next()) {
                    val hotel = Hotel(
                        resultSet.getInt("id_hoteles"),
                        resultSet.getString("nombre"),
                        "", // Descripción vacía
                        "", // Dirección vacía
                        "", // Correo vacío
                        0, // Cantidad de habitaciones 0
                        resultSet.getString("img_url"),
                        null, // ID habitación nulo
                        null  // ID servicio hotel nulo
                    )
                    hoteles.add(hotel)
                }

                resultSet.close()
                statement.close()
                conexion.close()
            }

            hoteles
        }
    }
}

