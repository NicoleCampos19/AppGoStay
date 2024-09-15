package emily.jacobo.gostay

import RecyclerViewHelpers.ViewModelPerfil
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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

        //val idUsuario = intent.getIntExtra("id_usuario", -1)



        val correoIngresado = activity_iniciar_sesion.variableGloalLogin.correoIngresado

        println("correo $correoIngresado")

        fun cargarImagenperfil(correoIngresado: String) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Realizar la consulta para obtener la imagen
                    println("conexion")
                    val conexion = ClaseConexion().cadenaConexion()
                    println("query")
                    val query = "SELECT imgFoto FROM tbUsuarios WHERE correo = ?"
                    println("antes preparedStatement")
                    val preparedStatement = conexion!!.prepareStatement(query)
                    println("despues preparedStatement")
                    preparedStatement.setString(1, correoIngresado)
                    println("despues del correo")

                    val resultSet = preparedStatement.executeQuery()
                    println("ANTES DEL IF")
                    if (resultSet.next()) {
                        println("DESPUES DEL IF")
                        val imgFotoUrl = resultSet.getString("imgFoto")
                        val imgFotoUrl2 = "https://fotografias.lasexta.com/clipping/cmsimages02/2020/09/21/86828440-B1FB-43AC-9E9C-A94AC6A4B8BD/default.jpg?crop=1300,731,x0,y0&width=1900&height=1069&optimize=low"
                        println(imgFotoUrl)

                        println("urlimg")

                        withContext(Dispatchers.Main) {
                            println("dentro del withContext")

                            Log.d("Perfil", "URL de imagen: $imgFotoUrl")

                            println("url imagen $imgFotoUrl ")

                            println(" antes Glide")
                            Glide.with(this@Perfil)
                                .load(imgFotoUrl)
                                .apply(RequestOptions().circleCrop())
                                .into(imvPerfilUsu)
                            println("Glide")
                        }
                    } else {

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Perfil, "No se encontró la imagen de perfil", Toast.LENGTH_SHORT).show()
                        }
                    }


                    resultSet.close()
                    preparedStatement.close()
                    conexion.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Perfil, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show()
                    }
                }
        }





        }
        cargarImagenperfil(correoIngresado)


        txtCerrarSesion.setOnClickListener{
            cerrarSesion()
        }




        // Configuración de click listeners
        setClickListener(imvComentario, TusComentarios::class.java)
        setClickListener(imvComentarios, TusComentarios::class.java)
        setClickListener(txtComentarios, TusComentarios::class.java)
        setClickListener(imvOfertas, Ofertas::class.java)
        setClickListener(imvOferta, Ofertas::class.java)
        setClickListener(txtOfertas, Ofertas::class.java)
        setClickListener(imvPoliticas, activity_politicas::class.java)
        setClickListener(txtPoliticas, activity_politicas::class.java)
        setClickListener(txtInformaciónPer, activity_editar_perfil::class.java)
        setClickListener(imvInformacionPer, activity_editar_perfil::class.java)
        setClickListener(imvBuscar, PaginaInicio::class.java)
        setClickListener(imvFavorito, Favoritos::class.java)
        setClickListener(imvReseva, Reservas::class.java)
        setClickListener(imvPerfil, Perfil::class.java)




    }


    private fun cerrarSesion() {
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@Perfil)
            dialog.setContentView(R.layout.dialog_cerrar_sesion)

            val btnClose = dialog.findViewById<Button>(R.id.btnNoCerrarSesion)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            val btnCerrarSesion = dialog.findViewById<Button>(R.id.btnCerrarSesion)
            btnCerrarSesion.setOnClickListener{
                val intent = Intent(this@Perfil, activity_iniciar_sesion::class.java)
                startActivity(intent)
            }

            dialog.show()
        }
    }

    private fun <T> setClickListener(view: View, clazz: Class<T>) {
        view.setOnClickListener {
            val intent = Intent(this, clazz)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

/*
    private fun cargarImagenPerfil(correo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Realizar la consulta para obtener la imagen
                val conexion = ClaseConexion().cadenaConexion()
                val query = "SELECT imgFoto FROM tbUsuarios WHERE correo = ?"
                val preparedStatement = conexion!!.prepareStatement(query)
                preparedStatement.setString(1, correo)

                val resultSet = preparedStatement.executeQuery()
                if (resultSet.next()) {
                    val imgFotoUrl = resultSet.getString("imgFoto")

                    withContext(Dispatchers.Main) {

                        // Log para verificar la URL de la imagen
                        Log.d("Perfil", "URL de imagen: $imgFotoUrl")

                        // Usar Glide para cargar la imagen en el ImageView
                        val imvPerfilUsu = findViewById<ImageView>(R.id.imvPerfilUsu)
                        Glide.with(this@Perfil)
                            .load(imgFotoUrl)
                            .apply(RequestOptions().circleCrop()) // Ajusta si quieres que la imagen sea circular
                            .into(imvPerfilUsu)
                    }
                } else {
                    // Si no se encuentra la imagen, maneja el caso aquí
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Perfil, "No se encontró la imagen de perfil", Toast.LENGTH_SHORT).show()
                    }
                }

                // Cerrar recursos
                resultSet.close()
                preparedStatement.close()
                conexion.close()
            } catch (e: SQLException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Perfil, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/


}






