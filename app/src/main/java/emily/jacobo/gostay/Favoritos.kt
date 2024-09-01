package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorFavoritos
import RecyclerViewHelpers.HotelAdapter
import android.annotation.SuppressLint
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
import modelo.tbFavoritos
import modelo.tbHotel

class Favoritos : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favoritos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rcvFavoritos = findViewById<RecyclerView>(R.id.rcvFavoritos)
        val imvBuscar = findViewById<ImageView>(R.id.imvBuscarb)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritoa)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)
        val correo = activity_iniciar_sesion.correoIngresado
        rcvFavoritos.layoutManager = LinearLayoutManager(this)

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
        suspend fun obtenerIdUsuario(correo: String): Int {
            return withContext(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val getId = objConexion?.prepareStatement("SELECT id_usuario FROM tbUsuarios WHERE correo = ?")
                getId?.setString(1, correo)
                val resultSet = getId?.executeQuery()
                if (resultSet != null && resultSet.next()) {
                    resultSet.getInt("id_usuario")
                } else {
                    0
                }
            }
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
                        val id_usuario = resultSet.getInt("id_usuario")

                        val hotel = tbHotel(
                            id_hoteles, nombre, descripcion, direccion, correo,
                            cantidad_habitaciones, img_url, id_usuario
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
            val id_usuario = obtenerIdUsuario(correo)
            val hotelDB = obtenerHotelesFavoritos(id_usuario)
            withContext(Dispatchers.Main){
                val adapter = HotelAdapter(hotelDB, true){ hotel ->
                    val intent = Intent(this@Favoritos, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                rcvFavoritos.adapter = adapter
            }
        }
    }
}