package emily.jacobo.gostay

import RecyclerViewHelpers.ViewModelPerfil
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
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
        val txtHistorialReservas = findViewById<TextView>(R.id.txtHistorialReservas)
        val imvHistorialReservas = findViewById<ImageView>(R.id.imvHistorialReservas)
        val imvHistorialReserva = findViewById<ImageView>(R.id.imvHistorialReserva)
        //val correoIngresado = activity_iniciar_sesion.variableGloalLogin.correoIngresado

        // Obtener SharedPreferences
        val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)


        // Recuperar el correo almacenado
        val correoIngresado = userPreferences.getString("email", "") ?: ""

        println("correo $correoIngresado")

        // Select para mostrar la foto de perfil
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

                        withContext(Dispatchers.Main) {
                            println("dentro del withContext")
                            Log.d("Perfil", "URL de imagen: $imgFotoUrl")
                            println("url imagen $imgFotoUrl ")

                            Glide.with(this@Perfil)
                                .load(imgFotoUrl)
                                .apply(RequestOptions().circleCrop())
                                .into(imvPerfilUsu)
                        }
                        // Mostrar una toast si la img no pudo ser cargada
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Perfil, "No se encontró la imagen de perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Mostrar una toast si la img no pudo ser cargada
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

        // Cargar la img dependiendo del correo ingresado
        cargarImagenperfil(correoIngresado)

        // Configuración del click para cerrar sesión
        txtCerrarSesion.setOnClickListener {
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
        setClickListener(txtHistorialReservas, historial_reservas::class.java)
        setClickListener(imvHistorialReservas, historial_reservas::class.java)
        setClickListener(imvHistorialReserva, historial_reservas::class.java)
    }

    // Función para cerrar sesión
    private fun cerrarSesion() {
        // Mostrar un diálogo de confirmación para cerrar sesión
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = Dialog(this@Perfil)
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_card)
            dialog.setContentView(R.layout.dialog_cerrar_sesion)

            val btnClose = dialog.findViewById<Button>(R.id.btnNoCerrarSesion)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            val btnCerrarSesion = dialog.findViewById<Button>(R.id.btnCerrarSesion)
            btnCerrarSesion.setOnClickListener {
                // Cerrar sesión de Firebase
                FirebaseAuth.getInstance().signOut()

                // Cerrar sesión de Google
                val googleSignInClient = GoogleSignIn.getClient(this@Perfil, GoogleSignInOptions.DEFAULT_SIGN_IN)
                googleSignInClient.signOut().addOnCompleteListener {
                    // Limpiar SharedPreferences
                    val userPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
                    userPreferences.edit().clear().apply()

                    // Redirigir al login
                    val intent = Intent(this@Perfil, activity_iniciar_sesion::class.java)
                    startActivity(intent)
                    finish() // Finalizar la actividad actual para que no pueda volver atrás
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    // Recibe un `view` (el elemento de la interfaz) y `clazz` (la clase a la que se quiere navegar).
    private fun <T> setClickListener(view: View, clazz: Class<T>) {
        view.setOnClickListener {
            val intent = Intent(this, clazz)
            startActivity(intent)
            overridePendingTransition(0, 0)
            // Elimina la animación de transición entre actividades para que sea instantánea
        }
    }
}