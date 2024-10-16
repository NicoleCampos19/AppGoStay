package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Configuraciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuraciones)

        // Ajuste de los márgenes para los system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mando a llamar los elementos de la vista
        val imvIdiomas = findViewById<ImageView>(R.id.imvIdiomas)
        val imvContacto = findViewById<ImageView>(R.id.imvContact)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)

        //Navegación para ir a la activity anterior
        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0,0)

        }

        // Configuración para que el botón imvIdiomas abra la sección de idiomas
        imvIdiomas.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }

        // Configuración para que el botón imvContact abra la sección de Contactános
        imvContacto.setOnClickListener {
            val siguientepantalla = Intent(this, contacto::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0,0)

        }
    }
}
