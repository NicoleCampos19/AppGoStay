package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class activity_iniciar_sesion : AppCompatActivity() {

    private val InicioSesionGoogle = 100

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtOlvidasteContrasena = findViewById<TextView>(R.id.txtOlvidasteContrasena)
        val txtCorreoIniciarSesion = findViewById<TextView>(R.id.txtCorreoInciarSesion)
        val txtContrasenaIniciarSesion = findViewById<TextView>(R.id.txtContrasenaIniciarSesion)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvFoto = findViewById<ImageView>(R.id.imvFoto)
        val imvIniciarconGoogle = findViewById<ImageView>(R.id.imvIniciarconGoogle)
        val btnMientras = findViewById<Button>(R.id.btnmientrasxd)

        btnMientras.setOnClickListener {
            val siguientePantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientePantalla)
        }

        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnIniciar.setOnClickListener {



            //Validación de campos
            val correo = txtCorreoIniciarSesion.text.toString()
            val contrasena = txtContrasenaIniciarSesion.text.toString()
            var hayErrores = false


            if (!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoIniciarSesion.error = "El correo no tiene un formato válido"
                hayErrores = true
            } else {
                txtCorreoIniciarSesion.error = null
            }

            if (contrasena.length <= 4) {
                txtContrasenaIniciarSesion.error = "La contraseña debe tener al menos 12 caracteres"
                hayErrores = true
            } else {
                txtContrasenaIniciarSesion.error = null
            }

            // Si hay errores, no procede a guardar los datos
            if (hayErrores) {
                //Hacer algo si hay errores
            } else {


                GlobalScope.launch(Dispatchers.IO) {
                    val objConexion = ClaseConexion().cadenaConexion()

                    val contraseniaEncriptada = hashSHA256(txtContrasenaIniciarSesion.text.toString())

                    val comprobarUsuario =
                        objConexion?.prepareStatement("SELECT * FROM tbUsuarios WHERE correo = ? AND contraseña = ?")!!
                    comprobarUsuario.setString(1, txtCorreoIniciarSesion.text.toString())
                    comprobarUsuario.setString(2, contraseniaEncriptada)
                    val resultado = comprobarUsuario.executeQuery()
                    //Si encuentra un resultado
                    if (resultado?.next() == true) {
                        val esAdmin = correo == "admin@gmail.com"
                        val siguientePantalla = if (esAdmin) {
                            Intent(this@activity_iniciar_sesion, Favoritos::class.java)
                        } else {
                            Intent(this@activity_iniciar_sesion, PaginaInicio::class.java)
                        }
                        startActivity(siguientePantalla)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@activity_iniciar_sesion,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("contraseña $contraseniaEncriptada")
                        }

                    }


                }
            }



        }


        imvIniciarconGoogle.setOnClickListener {
            val configuracionGoogle =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                    .build()

            val ClienteGoogle = GoogleSignIn.getClient(this, configuracionGoogle)

            startActivityForResult(ClienteGoogle.signInIntent, InicioSesionGoogle)
        }

        txtOlvidasteContrasena.setOnClickListener {
            val siguientepantalla = Intent(this, RecuperacionCuentaActivity::class.java)
            startActivity(siguientepantalla)
        }

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, activity_registrarse::class.java)
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
                                overridePendingTransition(0, 0)
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