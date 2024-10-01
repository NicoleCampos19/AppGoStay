package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorDenuncias
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
import modelo.tbDenuncias

class VerMasDenuncias : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_mas_denuncias)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mando a llamar los elementos de la vista
        val rcvMasDenuncias = findViewById<RecyclerView>(R.id.rcvMasDenuncias)
        rcvMasDenuncias.layoutManager = LinearLayoutManager(this@VerMasDenuncias)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)

        // Navegación para ir a Denuncias
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Denuncias::class.java)
            startActivity(volverAtras)
        }

        // Obtiene el ID del hotel pasado en el Intent
        val id_hoteles = intent.getIntExtra("id_hoteles", -1)

        // Llama a la función para obtener más denuncias en segundo plano.
        CoroutineScope(Dispatchers.IO).launch {
            val masDenuncias = verMasDenuncias(id_hoteles)
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorDenuncias(masDenuncias)
                rcvMasDenuncias.adapter = adapter
            }
        }
    }

    // Select para poder ver los detalles de la denuncia
    fun verMasDenuncias(id_hoteles: Int): List<tbDenuncias> {
        val objConexion = ClaseConexion().cadenaConexion()
        val statement = objConexion?.prepareStatement(
            "SELECT d.id_denuncia, d.nombre_denuncia, h.nombre " +
                    "FROM tbDenuncias d " +
                    "INNER JOIN tbHoteles h ON d.id_hoteles = h.id_hoteles " +
                    "WHERE h.id_hoteles = ?"
        )

        // Establece el ID del hotel en la consulta
        statement?.setInt(1, id_hoteles)
        val resultSet = statement?.executeQuery()

        // Crea una lista mutable para almacenar las denuncias
        val listaDenuncias = mutableListOf<tbDenuncias>()

        // Obtiene los datos de cada denuncia del resultado
        while (resultSet?.next() == true) {
            val idDenuncia = resultSet.getInt("id_denuncia")
            val nombreDenuncia = resultSet.getString("nombre_denuncia")
            val nombreHotel = resultSet.getString("nombre")

            listaDenuncias.add(tbDenuncias(idDenuncia, nombreDenuncia, nombreHotel))
        }
        return listaDenuncias
    }
}
