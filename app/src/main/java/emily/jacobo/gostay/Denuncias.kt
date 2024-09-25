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

        val imvHotelNavegacion = findViewById<ImageView>(R.id.imvHotelNavegacion)
        val imvDenunciasNavegacion = findViewById<ImageView>(R.id.imvDenunciasNavegacion)

        imvHotelNavegacion.setOnClickListener {
            val siguientePantalla = Intent(this, InicioAdmin::class.java)
            startActivity(siguientePantalla)
        }

        imvDenunciasNavegacion.setOnClickListener {
            val siguientePantalla = Intent(this, Denuncias::class.java)
            startActivity(siguientePantalla)
        }


        val rcvHotelesDenunciados = findViewById<RecyclerView>(R.id.rcvHotelesDenunciados)
        rcvHotelesDenunciados.layoutManager = LinearLayoutManager(this@Denuncias)

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

            val listaHotelesDenunciados = mutableListOf<tbHotelConDenuncias>()

            while (resultSet.next()) {
                val idHotel = resultSet.getInt("idHotel")
                val imgUrl = resultSet.getString("img_url")
                val nombre = resultSet.getString("nombre")
                val numeroDenuncias = resultSet.getInt("numero_denuncias")

                val hotelDenunciadoCompleto = tbHotelConDenuncias(idHotel, imgUrl, nombre, numeroDenuncias)
                listaHotelesDenunciados.add(hotelDenunciadoCompleto)
            }
            return listaHotelesDenunciados
        }

        CoroutineScope(Dispatchers.IO).launch {
            val hotelesdenunciadosDB = obtenerHotelesDenunciados()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorHotelConDenuncias(hotelesdenunciadosDB)
                rcvHotelesDenunciados.adapter = adapter
            }
        }

    }
}