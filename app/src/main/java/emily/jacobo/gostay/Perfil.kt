package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.SQLException

class Perfil : AppCompatActivity() {

    lateinit var imvFotoPerfil: ImageView

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
//git        imvFotoPerfil = findViewById(R.id.imvFotoPerfil)
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



        val correoUsuario = activity_registrarse.variableGloalLogin.txtCorreoI.text.toString()
        //cargarImagenPerfil(correoUsuario)


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

/*
private fun cargarImagenPerfil(correoUsuario: String) {
    CoroutineScope(Dispatchers.IO).launch {
        var pathImagen: String? = null

        try {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
                // Consulta SQL para seleccionar la imagen
                val query = "SELECT imgFoto FROM tbUsuarios WHERE correo = ?"
                val statement = objConexion.prepareStatement(query)
                statement.setString(1, correoUsuario)
                val resultSet = statement.executeQuery()

                // Obtiene el path de la imagen si existe
                if (resultSet.next()) {
                    pathImagen = resultSet.getString("imgFoto")
                }

                // Cierra los recursos
                resultSet.close()
                statement.close()
                objConexion.close()
            } else {
                println("No se pudo conectar a la base de datos")
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }

        // Actualiza la interfaz de usuario en el hilo principal
        withContext(Dispatchers.Main) {
            if (pathImagen != null) {
                try {
                    // Suponiendo que pathImagen es una ruta válida
                    val uriImagen = Uri.parse(pathImagen)
                    imvFotoPerfil.setImageURI(uriImagen)
                } catch (e: Exception) {
                    Toast.makeText(
                        /* context = */ this@withContext,
                        /* text = */ "Error al cargar la imagen",
                        /* duration = */ Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@withContext,
                    "No se encontró imagen para el usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

*/