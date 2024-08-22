package emily.jacobo.gostay

import RecyclerViewHelpers.ViewModelPerfil
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.SQLException

class Perfil : AppCompatActivity() {



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

        // Inicialización de vistas
        val imvPerfilUsu = findViewById<ImageView>(R.id.imvPerfilUsu)
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

        val idUsuario = intent.getIntExtra("id_usuario", -1)


        val correoUsuario = activity_iniciar_sesion.correoIngresado


        ViewModelPerfil.loadUserInfo(this)

        /*
        // Inicializa el ViewModelPerfil
        val viewModelPerfil = ViewModelProvider(this).get(ViewModelPerfil::class.java)


        // Observa los cambios en la imagen del perfil
        viewModelPerfil.profilePicture.observe(this) { imageUrl ->
            // Usa Glide para cargar la imagen
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions().circleCrop()) // Ajusta el RequestOptions según tus necesidades
                .into(imvPerfilUsu)
        }*/

        /*
        ViewModelPerfil.profilePicture.observe(viewLifecycleOwner, {
                profilePicture ->
            Glide.with(this).load(profilePicture).into(imvPerfilUsu)
        })*/

        /* Carga la información del usuario
        viewModelPerfil.loadUserInfo(this)*/


        // Configuración de click listeners
        setClickListener(imvComentario, TusComentarios::class.java, idUsuario)
        setClickListener(imvComentarios, TusComentarios::class.java, idUsuario)
        setClickListener(txtComentarios, TusComentarios::class.java, idUsuario)
        setClickListener(imvOfertas, Ofertas::class.java, idUsuario)
        setClickListener(imvOferta, Ofertas::class.java, idUsuario)
        setClickListener(txtOfertas, Ofertas::class.java, idUsuario)
        setClickListener(txtCerrarSesion, activity_iniciar_sesion::class.java, idUsuario)
        setClickListener(imvPoliticas, activity_politicas::class.java, idUsuario)
        setClickListener(txtPoliticas, activity_politicas::class.java, idUsuario)
        setClickListener(txtInformaciónPer, activity_editar_perfil::class.java, idUsuario)
        setClickListener(imvInformacionPer, activity_editar_perfil::class.java, idUsuario)
        setClickListener(imvBuscar, PaginaInicio::class.java, idUsuario)
        setClickListener(imvFavorito, Favoritos::class.java, idUsuario)
        setClickListener(imvReseva, Reservas::class.java, idUsuario)
        setClickListener(imvPerfil, Perfil::class.java, idUsuario)





    }




    private fun <T> setClickListener(view: View, clazz: Class<T>, idUsuario: Int) {
        view.setOnClickListener {
            val intent = Intent(this, clazz)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }


}






