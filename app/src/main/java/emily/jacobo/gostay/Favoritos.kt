package emily.jacobo.gostay

import RecyclerViewHelpers.HotelAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
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

        // Se mandan a llamar los elementos de la vsita
        val rcvFavoritos = findViewById<RecyclerView>(R.id.rcvFavoritos)
        val imvBuscar = findViewById<ImageView>(R.id.imvBuscarb)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritoa)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfil)
        val lottie2 = findViewById<LottieAnimationView>(R.id.lottie2)
        val txt1 = findViewById<TextView>(R.id.txtReservas)
        val txt2 = findViewById<TextView>(R.id.txt2)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        rcvFavoritos.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val correo = sharedPreferences.getString("email", null)

        // Navegación para ir a la página de inicio
        btnBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        // Navegación para ir a la página de inicio
        imvBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        // Navegación para ir a Favoritos
        imvFavorito.setOnClickListener {
            val siguientepantalla = Intent(this, Favoritos::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        // Navegación para ir a Reservas
        imvReseva.setOnClickListener {
            val siguientepantalla = Intent(this, Reservas::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        // Navegación para ir a Perfil
        imvPerfil.setOnClickListener {
            val siguientepantalla = Intent(this, Perfil::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        // Para obtener el id del usuario
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
        //Función para mostrar hoteles favoritos
        suspend fun obtenerHotelesFavoritos(idUsuario: Int): List<tbHotel> {
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

                if (resultSet != null && resultSet.isBeforeFirst) {
                    withContext(Dispatchers.Main) {
                        rcvFavoritos.visibility = View.VISIBLE
                        lottie2.visibility = View.GONE
                        txt1.visibility = View.GONE
                        txt2.visibility = View.GONE
                        btnBuscar.visibility = View.GONE
                    }
                    while (resultSet.next()) {
                        val id_hoteles = resultSet.getInt("id_hoteles")
                        val nombre = resultSet.getString("nombre")
                        val descripcion = resultSet.getString("descripcion")
                        val direccion = resultSet.getString("direccion")
                        val latitudHotel = resultSet.getDouble("latitudHotel")
                        val longitudHotel = resultSet.getDouble("longitudHotel")
                        val correo = resultSet.getString("correo")
                        val img_url = resultSet.getString("img_url")
                        val id_usuario = resultSet.getInt("id_usuario")
                        println("$id_usuario")

                        // Crea un objeto hotel y lo añade a la lista
                        val hotel = tbHotel(id_hoteles, nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, img_url, id_usuario)
                        listaHotelesFavoritos.add(hotel)
                    }
                } else {
                    // No se encuentran hoteles favoritos y muestra informacion alterna
                    withContext(Dispatchers.Main) {
                        rcvFavoritos.visibility = View.GONE
                        lottie2.visibility = View.VISIBLE
                        txt1.visibility = View.VISIBLE
                        txt2.visibility = View.VISIBLE
                        btnBuscar.visibility = View.VISIBLE
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
            // Obtiene el ID del usuario a partir del correo electrónico
            val id_usuario = obtenerIdUsuario(correo!!)
            val hotelDB = obtenerHotelesFavoritos(id_usuario)
            // Cambia el contexto de ejecución al hilo principal
            withContext(Dispatchers.Main){
                val adapter = HotelAdapter(hotelDB, false){ hotel ->
                    val intent = Intent(this@Favoritos, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                // Asigna el adaptador al RecyclerView para mostrar la lista de hoteles favoritos en la UI
                rcvFavoritos.adapter = adapter
            }
        }
    }
}