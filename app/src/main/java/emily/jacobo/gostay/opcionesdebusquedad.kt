package emily.jacobo.gostay

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class opcionesdebusquedad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opciones_busquedad)

        //Mando a llamar al botón
        val btnHotelCerca = findViewById<Button>(R.id.btnHotelCerca)
        val btnProxDestino = findViewById<ImageButton>(R.id.btnProxDestino)

        //Para que al darle click cambie de color
        btnHotelCerca.setOnClickListener {
            btnHotelCerca.setBackgroundColor(ContextCompat.getColor(this, R.color.amarillo))
        }

        btnProxDestino.setOnClickListener {
            btnProxDestino.setImageResource(R.drawable.pastillaamarilla)
            btnProxDestino.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gris)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}