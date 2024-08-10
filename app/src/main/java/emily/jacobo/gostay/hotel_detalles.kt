package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorServicioHotel
import RecyclerViewHelpers.ComentarioAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.MenuRes
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
import modelo.tbComentarios
import modelo.tbHotel

import java.util.UUID

import modelo.tbServiciosHotel



class hotel_detalles : AppCompatActivity() {

    private lateinit var rcvServicioHotel: RecyclerView
    private lateinit var prevActivity: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotel_detalles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rcvServicioHotel = findViewById(R.id.rcvServiciosHotel)
        rcvServicioHotel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        prevActivity = intent.getStringExtra("prev_activity") ?: "PaginaInicio"

        val imageViewBack = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        imageViewBack.setOnClickListener {
            navigateBack()
        }



        val idHotel = intent.getIntExtra("id_hoteles", -1)
        val hotel = intent.getSerializableExtra("hotel") as tbHotel
        if (idHotel != -1) {
            obtenerServiciosHotel(idHotel)
        }



        val imvVolverDetallesHotel = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val txtComentario = findViewById<EditText>(R.id.txtComentario)
        val imvEnviar = findViewById<ImageView>(R.id.imvEnviar)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)
        val imvReportar = findViewById<ImageView>(R.id.imvReportar)
        val btnReportar = findViewById<Button>(R.id.btnReportar)


        imvReportar.setOnClickListener {
            val irADenuncias = Intent(this, RealizarDenuncia::class.java)
            irADenuncias.putExtra("idHotel", idHotel)
            startActivity(irADenuncias)
        }

        btnReportar.setOnClickListener {
            val irADenuncias = Intent(this, RealizarDenuncia::class.java)
            irADenuncias.putExtra("idHotel", idHotel)
            startActivity(irADenuncias)
        }

        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val btnTipoHabitacion: Button = findViewById(R.id.btnTipoHabitacion)
        btnTipoHabitacion.setOnClickListener {
            val intent = Intent(this, activity_eleccion_habitacion::class.java)
            startActivity(intent)
        }

        fun obtenerComentarios(): List<tbComentarios> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbValoraciones")!!

            val listaComentarios = mutableListOf<tbComentarios>()

            while (resultSet.next()){
                val id_valoracion = resultSet.getInt("id_valoracion")
                val comentario = resultSet.getString("comentario")



                val comentarios = tbComentarios(id_valoracion, comentario)

                listaComentarios.add(comentarios)
            }
            return listaComentarios
        }


        CoroutineScope(Dispatchers.IO).launch{
            val comentariosDB = obtenerComentarios()
            withContext(Dispatchers.Main){
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvComentarios.adapter = miAdaptador
            }
        }

        imvEnviar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()

                val addComentario = objConexion?.prepareStatement("insert into tbValoraciones(comentario) values(?)")!!
                addComentario.setString(1, txtComentario.text.toString())


                addComentario.executeUpdate()

                val nuevocomentario = obtenerComentarios()
                withContext(Dispatchers.Main){
                    (rcvComentarios.adapter as? ComentarioAdapter)?.actualizarListado(nuevocomentario)
                    txtComentario.setText("")

                }


            }


        }


        hotel?.let {
    Glide.with(this)
        .load(hotel.img_url)

        imvEnviar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //1- Crear un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //2- Crear una variable que contenga un PrepareStatement
                val addComentario = objConexion?.prepareStatement("insert into tbValoraciones(comentario) values(?)")!!
                addComentario.setString(1, txtComentario.text.toString())

                        addComentario.executeUpdate()

                val nuevocomentario = obtenerComentarios()
                withContext(Dispatchers.Main){
                    //Actualizo al adaptador con los datos nuevos
                    (rcvComentarios.adapter as? ComentarioAdapter)?.actualizarListado(nuevocomentario)
                    txtComentario.setText("")

                }


            }


        }

        Glide.with(this)
            .load(hotel.img_url)

        tvNombreDetalleHotel.text = hotel.nombreHotel
        tvDescripcionDetalleHotel.text = hotel.descripcion


    tvNombreDetalleHotel.text = hotel.nombreHotel
    tvDescripcionDetalleHotel.text = hotel.descripcion
}


        
    }

    private fun navigateBack() {
        when (prevActivity) {
            "PaginaInicio" -> {
                val intent = Intent(this, PaginaInicio::class.java)
                startActivity(intent)
            }
            "InicioAdmin" -> {
                val intent = Intent(this, InicioAdmin::class.java)
                startActivity(intent)
            }
            else -> {
                // En caso de que no se reconozca la Activity previa, regresar a una Activity por defecto
                val intent = Intent(this, PaginaInicio::class.java)
                startActivity(intent)
            }
        }
        finish()
    }

    private fun obtenerServiciosHotel(idHotel: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val servicios = cargarServiciosHotel(idHotel)
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorServicioHotel(servicios)
                rcvServicioHotel.adapter = adapter
            }
        }
    }
    private fun cargarServiciosHotel(idHotel: Int): List<tbServiciosHotel> {
        val listaServicios = mutableListOf<tbServiciosHotel>()
        val conexion = ClaseConexion().cadenaConexion()

        val query = """
            SELECT sh.id_servicio_hotel, sh.nombre_servicio, sh.img_icono_hotel 
            FROM tbServiciosHotel sh 
            JOIN tbHoteles h ON h.id_servicio_hotel = sh.id_servicio_hotel 
            WHERE h.id_hoteles = ?
        """
        val statement = conexion?.prepareStatement(query)
        statement?.setInt(1, idHotel)
        val resultSet = statement?.executeQuery()
        while (resultSet?.next() == true) {
            val idServicioHotel = resultSet.getInt("id_servicio_hotel")
            val nombreServicio = resultSet.getString("nombre_servicio")
            val imgIconoHotel = resultSet.getString("img_icono_hotel")
            listaServicios.add(tbServiciosHotel(idServicioHotel, nombreServicio, imgIconoHotel))
        }
        resultSet?.close()
        statement?.close()
        conexion?.close()
        return listaServicios
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