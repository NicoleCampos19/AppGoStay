package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.storage
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.txtContraI
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.txtCorreoI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class activity_iniciar_sesion : AppCompatActivity() {

    companion object variableGloalLogin{
        private val InicioSesionGoogle = 100

        val correoIngresado = "admin@gmail.com"

    }
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
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnIniciar = findViewById<Button>(R.id.btnIniciar)
        val imvIniciarconGoogle = findViewById<ImageView>(R.id.imvIniciarconGoogle)
        val btnMientras = findViewById<Button>(R.id.btnmientrasxd)
        val txtCorreoInciarSesion = findViewById<EditText>(R.id.txtCorreoRecu)
        val txtContrasenaIniciarSesion = findViewById<EditText>(R.id.txtContrasenaIniciarSesion)
        val correoIngreado = txtCorreoInciarSesion.text.toString().trim()
        val clave = txtContrasenaIniciarSesion.text.toString().trim()

        btnMientras.setOnClickListener {
            val siguientePantalla = Intent(this, PaginaInicio::class.java)
            startActivity(siguientePantalla)
        }

        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //Validación para campos
        @RequiresApi(Build.VERSION_CODES.P)
        fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
            val typeface = ResourcesCompat.getFont(this, fontResId)
            val spannableString = android.text.SpannableString(errorMessage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                spannableString.setSpan(
                    typeface?.let { android.text.style.TypefaceSpan(it) }, 0, spannableString.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            editText.error = spannableString
        }
        btnIniciar.setOnClickListener {
            val correo = txtCorreoInciarSesion.text.toString()
            val contrasena = txtContrasenaIniciarSesion.text.toString()
            var hayErrores = false

            //Para el campo de correo electrónico
            if(correo.isEmpty()){
                setErrorWithCustomFont(txtCorreoInciarSesion, "Llena este campo", R.font.poppins)
            }
            // if (txtCorreoInciarSesion.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
            //setErrorWithCustomFont(txtCorreoInciarSesion, "El correo no tiene un formato válido", R.font.poppins)
            //}

            //Para el campo de contraseña
            if(contrasena.isEmpty()){
                setErrorWithCustomFont(txtContrasenaIniciarSesion, "Llena este campo", R.font.poppins)
            }

            // Si hay errores, no procede a guardar los datos
            if (hayErrores) {

                // Hacer algo si hay errores
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    val objConexion = ClaseConexion().cadenaConexion()

                    val contraseniaEncriptada =
                        hashSHA256(txtContrasenaIniciarSesion.text.toString())

                    val comprobarUsuario =
                        objConexion?.prepareStatement("SELECT * FROM tbUsuarios WHERE correo = ? AND contraseña = ?")!!
                    comprobarUsuario.setString(1, txtCorreoInciarSesion.text.toString())
                    comprobarUsuario.setString(2, contraseniaEncriptada)
                    val resultado = comprobarUsuario.executeQuery()
                    // Si encuentra un resultado
                    if (resultado?.next() == true) {
                        val esAdmin = correoIngresado == "admin@gmail.com"
                        val siguientePantalla = if (esAdmin) {
                            Intent(this@activity_iniciar_sesion, InicioAdmin::class.java)
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
    }





