package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    }
}