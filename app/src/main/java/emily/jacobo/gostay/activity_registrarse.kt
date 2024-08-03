package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.app.Activity
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
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import emily.jacobo.gostay.activity_iniciar_sesion.variableGloalLogin
import org.checkerframework.checker.regex.qual.Regex
import java.io.ByteArrayOutputStream
import java.sql.SQLException
import java.util.UUID

class activity_registrarse : AppCompatActivity() {

    companion object variableGloalLogin{
        lateinit var txtCorreoI: EditText
        lateinit var txtContraI: EditText

    }
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
        txtCorreoI = findViewById(R.id.txtCorreoElectronico)
        val txtTelefono = findViewById<TextView>(R.id.txtTelefono)
        txtContraI = findViewById(R.id.txtContrasenaRegistrarse)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val imvIniciargoogle = findViewById<ImageView>(R.id.imvIniciarGoogle)
        val imvVerContra1 = findViewById<ImageView>(R.id.imvVerContra1)
        val imvVerContra2 = findViewById<ImageView>(R.id.imvVerContra2)
        val txtConfirmarContraRegis = findViewById<TextView>(R.id.txtConfirmarContraRegis)

        fun hashSHA256(contrasenaEscrita: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        txtCorreoI = findViewById(R.id.txtCorreoElectronico)
        txtContraI = findViewById(R.id.txtContrasenaRegistrarse)
        //Mostrar calendario en el txtFechaNacimiento

        txtFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            // Calcular la fecha máxima (hace 18 años a partir de hoy)
            val fechaMaxima = Calendar.getInstance()
            fechaMaxima.set(anio - 18, mes, dia)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaNacimiento.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            // Configurar la fecha máxima a hace 18 años a partir de hoy
            datePickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis
            datePickerDialog.show()
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
            //Para el campo de nombre
        btnRegistrarse.setOnClickListener {
            val nombre = txtNombre.text.toString()
            val apellido = txtApellido.text.toString()
            val telefono = txtTelefono.text.toString()
            val fechanacimiento = txtFechaNacimiento.text.toString()
            val correo = txtCorreoI.text.toString()
            val contrasena = txtContraI.text.toString()
            val password = txtContraI.text.toString()
            val confirmPassword = txtConfirmarContraRegis.text.toString()

                var hayVacios = false
                var hayErrores = false

                //Para el campo de nombre
            if(nombre.isEmpty()){
                setErrorWithCustomFont(txtNombre, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (!nombre.matches(Regex("^[a-zA-Z]+$"))) {
                setErrorWithCustomFont(txtNombre, "El nombre contiene solo letras", R.font.poppins)
                hayErrores = true
            }
            else if(apellido.isEmpty()){
                setErrorWithCustomFont(txtApellido, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (!apellido.matches(Regex("^[a-zA-Z]+$"))) {
                setErrorWithCustomFont(txtApellido, "El apellido solo debe contener letras", R.font.poppins)
                hayErrores = true
            }

            //Para el campo de fecha nacimiento
            else if(fechanacimiento.isEmpty()){
                setErrorWithCustomFont(txtFechaNacimiento, "Llena este campo", R.font.poppins)
                hayVacios = true
            }

                //Para el campo de correo
                else if(correo.isEmpty()){
                    setErrorWithCustomFont(txtCorreoI, "Llena este campo", R.font.poppins)
                    hayVacios = true

                }
            else if (!correo.matches (Regex("[a-zA-Z0-9._-]+@[a-z]+[.][a-z]+"))) {
                setErrorWithCustomFont(txtCorreoI, "El formato del correo no es válido", R.font.poppins)
                    hayErrores = true
                }

            //Para el campo de telefono
            else if(telefono.isEmpty()){
                setErrorWithCustomFont(txtTelefono, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (telefono.length != 8) {
                setErrorWithCustomFont(txtTelefono, "El teléfono solo debe contener 8 carácteres", R.font.poppins)
                hayErrores = true
            }

            //Para el campo de contraseña
            else if(contrasena.isEmpty()){
                setErrorWithCustomFont(txtContraI, "Llena este campo", R.font.poppins)
                hayVacios = true
            }
            else if (contrasena.length < 12) {
                setErrorWithCustomFont(txtContraI, "La contraseña debe contener más de 11 carácteres", R.font.poppins)
                hayErrores = true
            }


            // Validar que las contraseñas coinciden
            if (password != confirmPassword) {
                setErrorWithCustomFont(txtConfirmarContraRegis, "Las contraseñas no coinciden", R.font.poppins)
                hayErrores = true
            }


            // Si hay errores, no procede a guardar los datos
            if (hayVacios || hayErrores) {
                Toast.makeText(this, "Verificar todos los campos", Toast.LENGTH_LONG)
            } else {
                GlobalScope.launch(Dispatchers.IO) {

                    val objConexion = ClaseConexion().cadenaConexion()

                    val contrasenaEncriptada = hashSHA256(txtContraI.text.toString())

                    val crearUsuario =
                        objConexion?.prepareStatement("INSERT INTO tbUsuarios(nombre, apellido, fecha_nacimiento, correo, telefono, contraseña) VALUES (?, ?, ?, ?, ?, ?)")!!
                    crearUsuario.setString(1, txtNombre.text.toString())
                    crearUsuario.setString(2, txtApellido.text.toString())
                    crearUsuario.setString(3, txtFechaNacimiento.text.toString())
                    crearUsuario.setString(4, txtCorreoI.text.toString())
                    crearUsuario.setString(5, txtTelefono.text.toString())
                    crearUsuario.setString(6, contrasenaEncriptada)
                    crearUsuario.executeUpdate()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@activity_registrarse,
                            "Usuario creado",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        txtCorreoI.setText("")
                        txtContraI.setText("")
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

        imvVerContra1.setOnClickListener {
            if (txtContraI.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                txtContraI.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtContraI.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        imvVerContra2.setOnClickListener {
            if (txtConfirmarContraRegis.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                txtConfirmarContraRegis.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtConfirmarContraRegis.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == emily.jacobo.gostay.activity_iniciar_sesion.InicioSesionGoogle) {
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
                                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_LONG).show()
            }
        }
    }
}



