package emily.jacobo.gostay

import RecyclerViewHelpers.AdaptadorDenuncias
import android.os.Bundle
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

        val rcvMasDenuncias = findViewById<RecyclerView>(R.id.rcvMasDenuncias)
        rcvMasDenuncias.layoutManager = LinearLayoutManager(this@VerMasDenuncias)

        val id_hoteles = intent.getIntExtra("id_hoteles", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val masDenuncias = verMasDenuncias(id_hoteles)
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorDenuncias(masDenuncias)
                rcvMasDenuncias.adapter = adapter
            }
        }
    }

    fun verMasDenuncias(id_hoteles: Int): List<tbDenuncias> {
        val objConexion = ClaseConexion().cadenaConexion()
        val statement = objConexion?.prepareStatement(
            "SELECT d.id_denuncia, d.nombre_denuncia, h.nombre " +
                    "FROM tbDenuncias d " +
                    "INNER JOIN tbHoteles h ON d.id_hoteles = h.id_hoteles " +
                    "WHERE h.id_hoteles = ?"
        )

        statement?.setInt(1, id_hoteles)
        val resultSet = statement?.executeQuery()

        val listaDenuncias = mutableListOf<tbDenuncias>()

        while (resultSet?.next() == true) {
            val idDenuncia = resultSet.getInt("id_denuncia")
            val nombreDenuncia = resultSet.getString("nombre_denuncia")
            val nombreHotel = resultSet.getString("nombre")

            listaDenuncias.add(tbDenuncias(idDenuncia, nombreDenuncia, nombreHotel))
        }
        return listaDenuncias
    }
}
