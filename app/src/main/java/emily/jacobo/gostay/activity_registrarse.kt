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
import java.util.Calendar
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat

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
        //Mando a llamar todos los elementos
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
                    val fechaSeleccionada =
                        "$díaSeleccionado/${mesSeleccionado + 1}/$añoSeleccionado"
                    txtFechaNacimiento.setText(fechaSeleccionada)
                },
                año, mes, día
            )
            datePickerDialog.show()
        }

        btnRegistrarse.setOnClickListener {

            val Nombre = txtNombre.text.toString()
            val Apellido = txtApellido.text.toString()
            val FechaNacimiento = txtFechaNacimiento.text.toString()
            val correo = txtCorreoElectronico.text.toString()
            val Telefono = txtTelefono.text.toString()
            val Contrasena = txtContrasena.text.toString()
            var hayErrores = false

            if (Nombre.isEmpty()) {
                txtNombre.error = "El nombre es obligatorio"
                hayErrores = true
            } else {
                txtNombre.error = null
            }

            if (Apellido.isEmpty()) {
                txtApellido.error = "El apellido es obligatorio"
                hayErrores = true
            } else {
                txtApellido.error = null
            }

            if (FechaNacimiento.isEmpty()) {
                txtFechaNacimiento.error = "La fecha de nacimiento es obligatoria"
                hayErrores = true
            } else {
                txtFechaNacimiento.error = null
            }

            if (!correo.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+"))) {
                txtCorreoElectronico.error = "El correo no tiene un formato válido"
                hayErrores = true
            } else {
                txtCorreoElectronico.error = null
            }

            if (Telefono.isEmpty()) {
                txtTelefono.error = "El teléfono es obligatorio"
                hayErrores = true
            } else {
                txtTelefono.error = null
            }

            if (Contrasena.length <= 12) {
                txtContrasena.error = "La contraseña debe tener al menos 12 caracteres"
                hayErrores = true
            } else {
                txtContrasena.error = null
            }


            // Si hay errores, no procede a guardar los datos
            if (hayErrores) {
                //Hacer algo si hay errores
            } else {


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
                val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
                startActivity(siguientepantalla)

            }



        }



        imvIniciargoogle.setOnClickListener {
            val configuracionGoogle =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
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