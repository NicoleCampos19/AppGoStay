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

        // Mando a llamar los elementos de la vista
        val rcvOfertas = findViewById<RecyclerView>(R.id.rcvOfertas)
        rcvOfertas.layoutManager = LinearLayoutManager(this)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)

        // Para poder ir a Perfil
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }

        // Obtener la información de la oferta
        fun obtenerOfertas(): List<tbOfertas> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT tof.nombre_oferta, th.nombre, tof.descuentoTotal, th.id_hoteles \n" +
                    "FROM tbOfertas tof \n" +
                    "INNER JOIN tbHoteles th ON tof.id_hoteles = th.id_hoteles")!!

            // Se crea una lista mutable de tipo 'tbOfertas' para almacenar los resultados obtenidos de la base de datos
            val listaOfertas = mutableListOf<tbOfertas>()

            // Se crean las variables para almacenar los valores de cada columna del resultado.
            // Luego, se crea un objeto 'tbOfertas' con esos valores.
            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val nombre_oferta = resultSet.getString("nombre_oferta")
                val descuentoTotal = resultSet.getDouble("descuentoTotal")
                val id_hotel = resultSet.getInt("id_hoteles")

                val valoresJuntos = tbOfertas(nombre, nombre_oferta, descuentoTotal, id_hotel)
                listaOfertas.add(valoresJuntos)
            }
            return listaOfertas
        }
// se actualiza la interfaz principal en el hilo principal (Dispatchers.Main).
        // Se asigna el adaptador con las ofertas al RecyclerView.
        CoroutineScope(Dispatchers.IO).launch{
            val ofertasDB = obtenerOfertas()
            withContext(Dispatchers.Main){
                val miAdaptador = AdaptadorOfertas(ofertasDB)
                rcvOfertas.adapter = miAdaptador
            }
        }

    }
}