package emily.jacobo.gostay

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import modelo.tbHotel

class hotel_detalles : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotel_detalles)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val hotel = intent.getSerializableExtra("hotel") as tbHotel

        val imvVolverDetallesHotel = findViewById<ImageView>(R.id.imvVolverDetallesHotel)
        val imvDetalleHotel = findViewById<ImageView>(R.id.imvDetalleHotel)
        val tvNombreDetalleHotel = findViewById<TextView>(R.id.tvNombreDetalleHotel)
        val tvDescripcionDetalleHotel = findViewById<TextView>(R.id.tvDescripcionDetalleHotel)



        Glide.with(this)
            .load(hotel.img_url)
            .into(imvDetalleHotel)

        tvNombreDetalleHotel.text = hotel.nombreHotel
        tvDescripcionDetalleHotel.text = hotel.descripcion

        imvVolverDetallesHotel.setOnClickListener {
            val volverAtras = Intent(this, PaginaInicio::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }
    }

}