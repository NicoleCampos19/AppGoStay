package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorHotelConDenuncias
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
import modelo.tbHotel
import modelo.tbHotelConDenuncias

class Denuncias : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_denuncias)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se mandan a llamar los elementos de la vista
        val imvHotelNavegacion = findViewById<ImageView>(R.id.imvHotelNavegacion)
        val imvDenunciasNavegacion = findViewById<ImageView>(R.id.imvDenunciasNavegacion)

        // Navegación para ir a la activity de InicioAdmin
        imvHotelNavegacion.setOnClickListener {
            val siguientePantalla = Intent(this, InicioAdmin::class.java)
            startActivity(siguientePantalla)
        }

        // Navegación para ir a la activity de Denuncias
        imvDenunciasNavegacion.setOnClickListener {
            val siguientePantalla = Intent(this, Denuncias::class.java)
            startActivity(siguientePantalla)
        }

        // Se manda a llamar el rcv de laa denuncias
        val rcvHotelesDenunciados = findViewById<RecyclerView>(R.id.rcvHotelesDenunciados)
        rcvHotelesDenunciados.layoutManager = LinearLayoutManager(this@Denuncias)

        // Select para obtener los hoteles denunciados
        fun obtenerHotelesDenunciados(): List<tbHotelConDenuncias> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery(
                "SELECT h.id_hoteles AS idHotel, h.img_url, h.nombre, COUNT(d.id_denuncia) AS numero_denuncias " +
                        "FROM tbHoteles h " +
                        "INNER JOIN tbDenuncias d ON h.id_hoteles = d.id_hoteles " +
                        "GROUP BY h.id_hoteles, h.img_url, h.nombre " +
                        "HAVING COUNT(d.id_denuncia) >= 5"
            )!!

            // Se crea una lista mutable para almacenar objetos de tipo tbHotelConDenuncias
            val listaHotelesDenunciados = mutableListOf<tbHotelConDenuncias>()

            // Itera sobre cada fila del ResultSet (resultado de una consulta SQL)
            while (resultSet.next()) {
                val idHotel = resultSet.getInt("idHotel")
                val imgUrl = resultSet.getString("img_url")
                val nombre = resultSet.getString("nombre")
                val numeroDenuncias = resultSet.getInt("numero_denuncias")

                // Crea un objeto tbHotelConDenuncias con los valores obtenidos.
                val hotelDenunciadoCompleto = tbHotelConDenuncias(idHotel, imgUrl, nombre, numeroDenuncias)
                listaHotelesDenunciados.add(hotelDenunciadoCompleto)
            }
            // Se retorna la lista
            return listaHotelesDenunciados
        }

        // Inicia una nueva corutina en un hilo de entrada/salida (IO), lo que permite ejecutar código en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            // Llama a una función que obtiene la lista de hoteles denunciados desde la base de datos (operación que toma tiempo, por eso se ejecuta en el hilo de IO).
            val hotelesdenunciadosDB = obtenerHotelesDenunciados()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorHotelConDenuncias(hotelesdenunciadosDB)
                rcvHotelesDenunciados.adapter = adapter
            }
        }

    }
}