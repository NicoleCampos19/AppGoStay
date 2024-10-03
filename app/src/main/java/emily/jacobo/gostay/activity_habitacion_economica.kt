package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorServiciosHabitacion
import RecyclerViewHelpers.AdaptorTipoHabitacion
import RecyclerViewHelpers.ServicioAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.ServicioInfo
import modelo.tbServiciosHabitacion

class activity_habitacion_economica : AppCompatActivity() {

    // Variable que se inicializará más tarde
    private lateinit var servicioAdapter: AdaptadorServiciosHabitacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_habitacion_economica)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

       //Mando a llamar todos los elementos que se encuentran en la vista
        val imageViewBack = findViewById<ImageView>(R.id.imgVolver)
        val btnReservar = findViewById<Button>(R.id.btnReservar)
        val idTipoHabitacion = AdaptorTipoHabitacion.idTipoHabitacionGlobal
        val recyclerView: RecyclerView = findViewById(R.id.rcvEspecificaciones)

        // Btn para finalizar la activity
        imageViewBack.setOnClickListener {
            finish()
        }

        //Navegación para ir a la activity de reservas
        btnReservar.setOnClickListener {
            val intent = Intent(this, activity_reserva::class.java)
            startActivity(intent)
        }


       // Variable para almacenar la URL de la imagen
        var imgTipoHabitacionUrl: String? = null
        var nombreTipoHabitacion: String? = null

       // Realiza la consulta de un select en un hilo separado (por ejemplo, usando una corrutina)
        CoroutineScope(Dispatchers.IO).launch {
            val query = "SELECT nombre_tipo_habitacion, img_tipo_habitacion FROM tbTiposHabitaciones WHERE id_tipo_habitacion = ?"
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                objConexion?.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idTipoHabitacion)
                    }
                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        resultSet.use { rs ->
                            if (rs.next()) {
                                nombreTipoHabitacion = rs.getString("nombre_tipo_habitacion")
                                imgTipoHabitacionUrl = rs.getString("img_tipo_habitacion")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
            }

            // Si obtuviste la URL de la imagen y el nombre, los muestra en los respectivos views en el hilo principal
            withContext(Dispatchers.Main) {
                // Mostrar el nombre del tipo de habitación en el TextView
                nombreTipoHabitacion?.let { nombre ->
                    val textView = findViewById<TextView>(R.id.tvNombreTipoHabitacion)
                    textView.text = nombre
                }

                // Mostrar la imagen del tipo de habitación en el ImageView
                imgTipoHabitacionUrl?.let { url ->
                    val imageView = findViewById<ImageView>(R.id.imgTipoHabitacion)
                    Glide.with(this@activity_habitacion_economica)
                        .load(url)
                        .into(imageView)
                }
            }
        }

        // Función para traer los servicios de habitación que hay que la base
        fun loadServiciosFromDatabase(idTipoHabitacion: Int): List<tbServiciosHabitacion> {
            val ServiciosList = mutableListOf<tbServiciosHabitacion>()
            val query = """
        SELECT nombre_servicio_habitacion, img_icono_habitacion 
        FROM tbServiciosHabitacion 
        WHERE id_tipo_habitacion = ?
    """.trimIndent()
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                println("ID Tipo Habitación: $idTipoHabitacion")
                if (objConexion == null) {
                    println("Error: No se pudo establecer conexión a la base de datos.")
                    return ServiciosList
                }

                objConexion.use { connection ->
                    val statement = connection.prepareStatement(query).apply {
                        setInt(1, idTipoHabitacion)
                    }

                    statement.use { preparedStatement ->
                        val resultSet = preparedStatement.executeQuery()
                        resultSet.use { rs ->
                            while (rs.next()) {
                                val nombre_servicio_habitacion = rs.getString("nombre_servicio_habitacion")
                                val img_icono_habitacion = rs.getString("img_icono_habitacion")
                                ServiciosList.add(tbServiciosHabitacion(nombre_servicio_habitacion, img_icono_habitacion))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to debug
                println("Error al cargar servicios: ${e.message}")
            }
            return ServiciosList // Retorno la lista
        }


        // Traer los servicios dependiendo del id_habitación
        CoroutineScope(Dispatchers.IO).launch {
            val servicios = idTipoHabitacion?.let { loadServiciosFromDatabase(it) }
            println("Servicios recuperados: ${servicios?.size}")
            withContext(Dispatchers.Main) {
                servicios?.let {
                    servicioAdapter = AdaptadorServiciosHabitacion(it)
                    recyclerView.adapter = servicioAdapter
                    recyclerView.layoutManager = LinearLayoutManager(this@activity_habitacion_economica)
                    val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
                    dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this@activity_habitacion_economica, R.drawable.divider)!!)
                    recyclerView.addItemDecoration(dividerItemDecoration)
                }?: run {
                    // Maneja el caso en que reservas sea null, quizás mostrando un mensaje de error o un mensaje de "No hay datos"
                    println("No se encontraron servicios para el hotel.")
                }
            }
        }
    }
}