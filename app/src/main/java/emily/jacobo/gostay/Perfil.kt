package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Perfil : AppCompatActivity() {

    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103

    lateinit var imageView: ImageView
    lateinit var miPath:String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imvBuscar = findViewById<ImageView>(R.id.imvBuscarb)
        val imvFavorito = findViewById<ImageView>(R.id.imvFavoritos)
        val imvReseva = findViewById<ImageView>(R.id.imvReservas)
        val imvPerfil = findViewById<ImageView>(R.id.imvPerfila)
        val imvInformacionPer = findViewById<ImageView>(R.id.imvInformacionPer)
        val imvPoliticas = findViewById<ImageView>(R.id.imvPoliticas)
        val txtInformaciónPer = findViewById<TextView>(R.id.txtInformaciónPer)
        val txtPoliticas = findViewById<TextView>(R.id.txtPoliticas)
        val txtCerrarSesion = findViewById<TextView>(R.id.txtCerrarSesion)
        val imvOfertas = findViewById<ImageView>(R.id.imvOfertas)
        val imvOferta = findViewById<ImageView>(R.id.imvOferta)
        val txtOfertas = findViewById<TextView>(R.id.txtOfertas)
        val imvComentario = findViewById<ImageView>(R.id.imvComentario)
        val txtComentarios = findViewById<TextView>(R.id.txtComentarios)
        val imvComentarios = findViewById<ImageView>(R.id.imvComentarios)



        imvComentario.setOnClickListener {
                val siguientepantalla = Intent(this, TusComentarios::class.java)
            startActivity(siguientepantalla)
        }

        imvComentarios.setOnClickListener {
            val siguientepantalla = Intent(this, TusComentarios::class.java)
            startActivity(siguientepantalla)
        }
        txtComentarios.setOnClickListener {
            val siguientepantalla = Intent(this, TusComentarios::class.java)
            startActivity(siguientepantalla)
        }


        imvOfertas.setOnClickListener {
            val siguientepantalla = Intent(this, Ofertas::class.java)
            startActivity(siguientepantalla)
        }

        imvOferta.setOnClickListener {
            val siguientepantalla = Intent(this, Ofertas::class.java)
            startActivity(siguientepantalla)
        }
        txtOfertas.setOnClickListener {
            val siguientepantalla = Intent(this, Ofertas::class.java)
            startActivity(siguientepantalla)
        }

        imvOfertas.setOnClickListener {
            val siguientepantalla = Intent(this, Ofertas::class.java)
            startActivity(siguientepantalla)
        }

        imvOferta.setOnClickListener {
            val siguientepantalla = Intent(this, Ofertas::class.java)
            startActivity(siguientepantalla)
        }
        txtOfertas.setOnClickListener {
            val siguientepantalla = Intent(this, Ofertas::class.java)
            startActivity(siguientepantalla)
        }



        txtCerrarSesion.setOnClickListener{
            val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(siguientepantalla)
        }

        imvPoliticas.setOnClickListener{
            val siguientepantalla = Intent(this, activity_politicas::class.java)
            startActivity(siguientepantalla)
        }

        txtPoliticas.setOnClickListener{
            val siguientepantalla = Intent(this, activity_politicas::class.java)
            startActivity(siguientepantalla)
        }

        txtInformaciónPer.setOnClickListener{
            val siguientepantalla = Intent(this, activity_editar_perfil::class.java)
            startActivity(siguientepantalla)
        }


        imvInformacionPer.setOnClickListener{
            val siguientepantalla = Intent(this, activity_editar_perfil::class.java)
            startActivity(siguientepantalla)
        }


        imvBuscar.setOnClickListener {
            val siguientepantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvFavorito.setOnClickListener {
            val siguientepantalla = Intent(this, Favoritos::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvReseva.setOnClickListener {
            val siguientepantalla = Intent(this, Reservas::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }

        imvPerfil.setOnClickListener {
            val siguientepantalla = Intent(this, Perfil::class.java)
            startActivity(siguientepantalla)
            overridePendingTransition(0, 0)
        }
    }
}