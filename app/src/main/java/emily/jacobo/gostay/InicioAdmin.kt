package emily.jacobo.gostay
import RecyclerViewHelpers.AdaptadorHotelAdmin
import RecyclerViewHelpers.HotelAdapter
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.PaginaInicio.Companion.hotelIdGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos
import modelo.tbHotel
var sql: String = "SELECT * FROM tbHoteles"

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

        //Mando a llamar lo de la vista
        val imvHotelNavegacion = findViewById<ImageView>(R.id.imvHotelNavegacion)
        val imvDenunciasNavegacion = findViewById<ImageView>(R.id.imvDenunciasNavegacion)
        val etBuscarHotel = findViewById<EditText>(R.id.etBuscarHotel)
        val btnVoz = findViewById<ImageView>(R.id.btnVoz)
        val rcvHotelAdmin = findViewById<RecyclerView>(R.id.rcvHotelAdmin)
        rcvHotelAdmin.layoutManager = LinearLayoutManager(this)
        cargarHoteles(sql)

        //Navegación entre pantallas
        imvHotelNavegacion.setOnClickListener {
            val siguientePantalla = Intent(this, InicioAdmin::class.java)
            startActivity(siguientePantalla)
        }

        imvDenunciasNavegacion.setOnClickListener {
            val siguientePantalla = Intent(this, Denuncias::class.java)
            startActivity(siguientePantalla)
        }

        etBuscarHotel.setOnKeyListener { view, i, keyEvent ->
            realizarBusqueda(etBuscarHotel.text.toString())
            false
        }

        // Inicia el reconocimiento de voz
        btnVoz.setOnClickListener {
            iniciarReconocimientoDeVoz()
        }
    }

    // Método para iniciar el reconocimiento de voz
    private fun iniciarReconocimientoDeVoz() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora")

        try {
            startActivityForResult(intent, 1)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext, "No es posible el reconocimiento de voz en su dispositivo", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para el resultado del reconocimiento de voz
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val textoReconocido = result?.get(0) ?: ""

                    // Mostrar el texto reconocido en el EditText y realizar la búsqueda
                    findViewById<EditText>(R.id.etBuscar).setText(textoReconocido)
                    realizarBusqueda(textoReconocido)
                }
            }

        }
    }

    // Función para realizar las búsquedad
    private fun realizarBusqueda(query: String) {
        val rcvHotelAdmin = findViewById<RecyclerView>(R.id.rcvHotelAdmin)
        rcvHotelAdmin.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            val hotelDB = obtenerHoteles(query, query)
            val favDB = obtenerFavoritos()
            withContext(Dispatchers.Main) {
                val esFavoritos = favDB.any { it.id_hoteles in hotelDB.map { hotel -> hotel.id_hoteles } }
                val adapter = HotelAdapter(hotelDB, esFavoritos) { hotel ->
                    val intent = Intent(this@InicioAdmin, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                rcvHotelAdmin.adapter = adapter
            }
        }
    }

    // Método para obtener hoteles con un edit text
    private fun obtenerHoteles(textoNombre: String, textoDireccion: String): List<tbHotel> {
        val objConexion = ClaseConexion().cadenaConexion()
        val buscar =
            objConexion?.prepareStatement("SELECT * FROM tbHoteles WHERE LOWER(nombre) LIKE LOWER(?) OR LOWER(direccion) LIKE LOWER(?)")!!
        buscar.setString(1, "%${textoNombre}%")
        buscar.setString(2, "%${textoDireccion}%")
        val resultSet = buscar.executeQuery()
        val listaHoteles = mutableListOf<tbHotel>()

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

            // Crea un objeto hotel y lo añade a la lista
            val hotel = tbHotel(id_hoteles, nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, img_url, id_usuario)
            listaHoteles.add(hotel)
        }
        return listaHoteles
    }

    // Función para obtener los favoritos agregados
    private fun obtenerFavoritos(): List<tbFavoritos> {
        val objConexion = ClaseConexion().cadenaConexion()

        val statement = objConexion?.createStatement()
        val resultSet = statement?.executeQuery("select * from tbPreferenciales")!!

        val listaFav2 = mutableListOf<tbFavoritos>()

        while (resultSet.next()) {
            val id_preferenciales = resultSet.getInt("id_preferencial")
            val id_hoteles = resultSet.getInt("id_hoteles")
            val id_usuario = resultSet.getInt("id_usuario")

            val valoresJuntosFav = tbFavoritos(id_preferenciales, id_hoteles, id_usuario )

            listaFav2.add(valoresJuntosFav)
        }
        return listaFav2
    }

    // Select para cargar los hoteles
    private fun cargarHoteles(sqlQuery: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val hotelesDB = obtenerHoteles(sqlQuery, "")  // Aquí sólo se pasa un parámetro de búsqueda
            withContext(Dispatchers.Main) {
                val adapter = HotelAdapter(hotelesDB, false) { hotel ->
                    hotelIdGlobal = hotel.id_hoteles
                    val intent = Intent(this@InicioAdmin, hotel_detalles::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
                        putExtra("prev_activity", "PaginaInicio")
                    }
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                findViewById<RecyclerView>(R.id.rcvHotelAdmin).adapter = adapter // Asegúrate de usar el ID correcto
            }
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}


