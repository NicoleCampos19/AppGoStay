package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_habitacion_economica : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_habitacion_economica)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val imageViewBack = findViewById<ImageView>(R.id.imgVolver)
        val btnReservar = findViewById<Button>(R.id.btnReservar)

        imageViewBack.setOnClickListener {
            finish()
        }

        btnReservar.setOnClickListener {
            val intent = Intent(this, activity_reserva::class.java)
            startActivity(intent)
        }
    }
}