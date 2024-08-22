package emily.jacobo.gostay

import RecyclerViewHelpers.HotelAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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

class PaginaInicio : AppCompatActivity() {


    companion object {
        var hotelIdGlobal: Int? = null
        var nombreUsuarioGlobalL: String? = null
        var idUsuarioGlobalL: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imvBuscar = findViewById<ImageView>(R.id.imvBuscar)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritos)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)
        val txtAggBusquedad = findViewById<TextView>(R.id.txtAggBusquedad)
        val imgFiltros = findViewById<ImageView>(R.id.imgFiltros)

        imgFiltros.setOnClickListener {
            val siguientepantalla = Intent(this, filtros::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        txtAggBusquedad.setOnClickListener {
            val siguientepantalla = Intent(this, opcionesdebusquedad::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

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

        val rcvHotel = findViewById<RecyclerView>(R.id.rcvHotel)
        rcvHotel.layoutManager = LinearLayoutManager(this)
        val idUsuario = buscarIdUsuarioPorCorreo(activity_iniciar_sesion.txtCorreoInciarSesionV)
        val nombreUsuario = buscarNombreUsuarioPorCorreo(activity_iniciar_sesion.txtCorreoInciarSesionV)
        idUsuarioGlobalL = idUsuario
        nombreUsuarioGlobalL = nombreUsuario

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

        fun obtenerHotelesFavoritos(idUsuario: Int): List<tbHotel> {
            val listaHotelesFavoritos = mutableListOf<tbHotel>()
            val objConexion = ClaseConexion().cadenaConexion()

            try {
                val statement = objConexion?.prepareStatement(
                    "SELECT h.* FROM tbPreferenciales p " +
                            "INNER JOIN tbHoteles h ON h.id_hoteles = p.id_hoteles " +
                            "WHERE p.id_usuario = ?"
                )
                statement?.setInt(1, idUsuario)
                val resultSet = statement?.executeQuery()

                if (resultSet != null) {
                    while (resultSet.next()) {
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

                        val hotel = tbHotel(
                            id_hoteles, nombre, descripcion, direccion, correo,
                            cantidad_habitaciones, img_url, id_tipo_habitacion,
                            id_servicio_hotel, id_valoracion
                        )

                        listaHotelesFavoritos.add(hotel)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                objConexion?.close()
            }

            return listaHotelesFavoritos
        }
        CoroutineScope(Dispatchers.IO).launch {
            val hotelDB = obtenerHoteles()
            withContext(Dispatchers.Main){
                val adapter = HotelAdapter(hotelDB, false){ hotel ->
                    hotelIdGlobal = hotel.id_hoteles
                    val intent = Intent(this@PaginaInicio, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                rcvHotel.adapter = adapter
            }
        }



    }
    private fun buscarNombreUsuarioPorCorreo(correo: String): String? {
        var nombreUsuario: String? = null
        val query = "SELECT nombre FROM tbUsuarios WHERE correo = ?"
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.prepareStatement(query).apply {
                    setString(1, correo)
                }
                statement.use { preparedStatement ->
                    val resultSet = preparedStatement.executeQuery()
                    if (resultSet.next()) {
                        nombreUsuario = resultSet.getString("nombre")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception to debug
        }
        return nombreUsuario
    }

    private fun buscarIdUsuarioPorCorreo(correo: String): Int {
        var idUsuario = -1
        val query = "SELECT id_usuario FROM tbUsuarios WHERE correo = ?"
        try {
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { connection ->
                val statement = connection.prepareStatement(query).apply {
                    setString(1, correo)
                }
                statement.use { preparedStatement ->
                    val resultSet = preparedStatement.executeQuery()
                    if (resultSet.next()) {
                        idUsuario = resultSet.getInt("id_usuario")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception to debug
        }
        return idUsuario
    }

    }


