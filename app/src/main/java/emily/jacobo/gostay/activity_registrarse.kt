package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DatePickerDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import emily.jacobo.gostay.R.id.txtIniciaSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest
import java.util.Calendar
import android.app.DatePickerDialog
import java.util.UUID

class activity_registrarse : AppCompatActivity() {

    private val InicioSesionGoogle = 100

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrarse)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val txtIniciarsesion = findViewById<TextView>(R.id.txtIniciaSesion)
        val txtNombre = findViewById<TextView>(R.id.txtNombre)
        val txtApellido = findViewById<TextView>(R.id.txtApellido)
        val txtFechaNacimiento = findViewById<TextView>(R.id.txtFechaNacimiento)
        val txtCorreoElectronico = findViewById<TextView>(R.id.txtCorreoElectronico)
        val txtTelefono = findViewById<TextView>(R.id.txtTelefono)
        val txtContrasena = findViewById<TextView>(R.id.txtContrasenaRegistrarse)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val imvIniciargoogle = findViewById<ImageView>(R.id.imvIniciarGoogle)

        fun hashSHA256(contrasenaEscrita: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //Mostrar calendario en el txtFechaNacimiento
        txtFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val día = calendario.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { view, añoSeleccionado, mesSeleccionado, díaSeleccionado ->
                    val fechaSeleccionada = "$díaSeleccionado/${mesSeleccionado + 1}/$añoSeleccionado"
                    txtFechaNacimiento.setText(fechaSeleccionada)
                },
                año, mes, día
            )
            datePickerDialog.show()
        }

        btnRegistrarse.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {

                val objConexion = ClaseConexion().cadenaConexion()


                val contrasenaEncriptada = hashSHA256(txtContrasena.text.toString())


                val crearUsuario =
                    objConexion?.prepareStatement("INSERT INTO tbUsuarios(nombre, apellido, fecha_nacimiento, correo, telefono, contraseña) VALUES (?, ?, ?, ?, ?, ?)")!!
                crearUsuario.setString(1, txtNombre.text.toString())
                crearUsuario.setString(2, txtApellido.text.toString())
                crearUsuario.setString(3, txtFechaNacimiento.text.toString())
                crearUsuario.setString(4, txtCorreoElectronico.text.toString())
                crearUsuario.setString(5, txtTelefono.text.toString())
                crearUsuario.setString(6, contrasenaEncriptada)
                crearUsuario.executeUpdate()
                withContext(Dispatchers.Main) {

                    Toast.makeText(this@activity_registrarse, "Usuario creado", Toast.LENGTH_SHORT)
                        .show()
                    txtCorreoElectronico.setText("")
                    txtContrasena.setText("")


                }

            }
        }

        imvIniciargoogle.setOnClickListener {
            val configuracionGoogle =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("AIzaSyDlsAfeacUYjZRTkeFWoZ8_dxJEoUKy_zM").requestEmail()
                    .build()

            val ClienteGoogle = GoogleSignIn.getClient(this, configuracionGoogle)

            startActivityForResult(ClienteGoogle.signInIntent, InicioSesionGoogle)
        }



        txtIniciarsesion.setOnClickListener {
            val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(siguientepantalla)
        }

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Bienvenida::class.java)
            startActivity(volverAtras)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InicioSesionGoogle) {
            val tarea = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = tarea.getResult(ApiException::class.java)
                if (cuenta != null) {
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credenciales)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val paginaInicio = Intent(this, PaginaInicio::class.java)
                                startActivity(paginaInicio)
                            } else {
                                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_LONG).show()

            }
        }
    }
}