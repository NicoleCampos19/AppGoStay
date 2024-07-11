package emily.jacobo.gostay

import RecyclerViewHelpers.ComentarioAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import modelo.tbComentarios
import modelo.tbHotel
import java.util.UUID

class hotel_detalles : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotel_detalles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val hotel = intent.getSerializableExtra("hotel") as tbHotel

        val imvVolverDetallesHotel = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        val imvDetalleHotel = findViewById<ImageView>(R.id.imvDetalleHotel)
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)
        val txtComentario = findViewById<EditText>(R.id.txtComentario)
        val imvEnviar = findViewById<ImageView>(R.id.imvEnviar)
        val rcvComentarios = findViewById<RecyclerView>(R.id.rcvComentarios)


        rcvComentarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)



        fun obtenerComentarios(): List<tbComentarios> {
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM tbValoraciones")!!

            //Voy a guardar all lo que me traiga el Select
            val listaComentarios = mutableListOf<tbComentarios>()

            while (resultSet.next()){

                val comentario = resultSet.getString("comentario")

                val comentarios = tbComentarios(comentario)

                listaComentarios.add(comentarios)
            }
            return listaComentarios
        }

        //Asignar el adapter al RecyclerView
        //Ejecutar la funcion para mostrar datos
        CoroutineScope(Dispatchers.IO).launch{
            //Creo una variable que ejecute la funcion de mostrar datos
            val comentariosDB = obtenerComentarios()
            withContext(Dispatchers.Main){
                val miAdaptador = ComentarioAdapter(comentariosDB)
                rcvComentarios.adapter = miAdaptador
            }
        }

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
            .into(imvDetalleHotel)

        tvNombreDetalleHotel.text = hotel.nombreHotel
        tvDescripcionDetalleHotel.text = hotel.descripcion

        imvVolverDetallesHotel.setOnClickListener {
            val volverAtras = Intent(this, PaginaInicio::class.java)
            startActivity(volverAtras)
        }
    }

}