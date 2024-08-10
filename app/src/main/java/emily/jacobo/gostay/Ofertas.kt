package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorOfertas
import RecyclerViewHelpers.ComentarioAdapter
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
import modelo.tbHotel
import modelo.tbOfertas

class Ofertas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ofertas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rcvOfertas = findViewById<RecyclerView>(R.id.rcvOfertas)
        rcvOfertas.layoutManager = LinearLayoutManager(this)

        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)


        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }
        fun obtenerOfertas(): List<tbOfertas>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT tof.nombre_oferta, th.nombre\n" +
                    "FROM tbOfertas tof\n" +
                    "INNER JOIN tbHoteles th ON tof.id_hoteles = th.id_hoteles")!!

            val listaOfertas = mutableListOf<tbOfertas>()

            while (resultSet.next()){

                val nombre = resultSet.getString("nombre")
               val nombre_oferta = resultSet.getString("nombre_oferta")



                val valoresJuntos = tbOfertas(nombre, nombre_oferta)

                listaOfertas.add(valoresJuntos)
            }
            return listaOfertas

        }
        CoroutineScope(Dispatchers.IO).launch{
            val ofertasDB = obtenerOfertas()
            withContext(Dispatchers.Main){
                val miAdaptador = AdaptadorOfertas(ofertasDB)
                rcvOfertas.adapter = miAdaptador
            }
        }

    }
}