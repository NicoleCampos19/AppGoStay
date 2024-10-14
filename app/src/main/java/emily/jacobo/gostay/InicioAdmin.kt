package emily.jacobo.gostay
import RecyclerViewHelpers.AdaptadorHotelAdmin
import RecyclerViewHelpers.HotelAdapter
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
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
        val imvSalir = findViewById<ImageView>(R.id.imvSalir)

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

        btnVoz.setOnClickListener {
            iniciarReconocimientoDeVoz()
        }

        imvSalir.setOnClickListener {
            cerrarSesion()
        }
    }

    // Función para cerrar sesión
    private fun cerrarSesion() {
        // Mostrar un diálogo de confirmación para cerrar sesión
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@InicioAdmin)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_cerrar_sesion)

            val btnClose = dialog.findViewById<Button>(R.id.btnNoCerrarSesion)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            val btnCerrarSesion = dialog.findViewById<Button>(R.id.btnCerrarSesion)
            btnCerrarSesion.setOnClickListener {
                // Cerrar sesión de Firebase
                FirebaseAuth.getInstance().signOut()

                val googleSignInClient = GoogleSignIn.getClient(this@InicioAdmin, GoogleSignInOptions.DEFAULT_SIGN_IN)
                googleSignInClient.signOut().addOnCompleteListener {
                    // Limpiar solo las credenciales específicas en SharedPreferences
                    val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
                    with(userPreferences.edit()) {
                        remove("email")     // Eliminar el correo ingresado
                        remove("isAdmin")   // Eliminar el estado de admin
                        apply()             // Aplicar los cambios
                    }

                    // Redirigir al login
                    val intent = Intent(this@InicioAdmin, activity_iniciar_sesion::class.java)
                    startActivity(intent)
                    finish() // Finalizar la actividad actual para que no pueda volver atrás
                }
                dialog.dismiss()
            }
            dialog.show()
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

    private fun realizarBusqueda(query: String) {
        val rcvHotelAdmin = findViewById<RecyclerView>(R.id.rcvHotelAdmin)
        rcvHotelAdmin.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            val hotelDB = obtenerHoteles(query, query)
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorHotelAdmin(hotelDB) { hotel ->
                    val intent = Intent(this@InicioAdmin, hotel_detalles_admin::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
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

            val valoresJuntos = tbHotel(
                id_hoteles,
                nombre,
                descripcion,
                direccion,
                latitudHotel,
                longitudHotel,
                correo,
                img_url,
                id_usuario
            )

            listaHoteles.add(valoresJuntos)
        }
        return listaHoteles
    }

    private fun cargarHoteles(sqlQuery: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val hotelesDB = obtenerHoteles(sqlQuery, "")  // Aquí sólo se pasa un parámetro de búsqueda
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorHotelAdmin(hotelesDB) { hotel ->
                    hotelIdGlobal = hotel.id_hoteles
                    Log.d("HotelAdapter", "ID del hotel seleccionado: $hotelIdGlobal")
                    val intent = Intent(this@InicioAdmin, hotel_detalles_admin::class.java).apply {
                        putExtra("hotel", hotel)
                        putExtra("id_hoteles", hotel.id_hoteles)
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