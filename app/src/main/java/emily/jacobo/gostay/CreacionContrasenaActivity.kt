package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.security.MessageDigest
import java.sql.PreparedStatement

class CreacionContrasenaActivity : AppCompatActivity() {

    lateinit var txtNewContra: EditText
    lateinit var Correo: String

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_creacion_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        txtNewContra = findViewById<EditText>(R.id.txtNuevaContrasena1)
        val imvVerNewContra = findViewById<ImageView>(R.id.imvVerNewContra)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnCrearContrasena = findViewById<Button>(R.id.btnCrearcontrasena1)
        val Correo = RecuperacionCuentaActivity.variablesGobalesRecuperacion.Correo
        var isPasswordVisible = false

        val poppinsFont = ResourcesCompat.getFont(this, R.font.poppins)
        txtNewContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        txtNewContra.typeface = poppinsFont

        imvVerNewContra.setOnClickListener {
            if (isPasswordVisible) {
                // Si la contraseña es visible, la ocultamos y cambiamos la imagen
                txtNewContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imvVerNewContra.setImageResource(R.drawable.ojocerrado)
            } else {
                // Si la contraseña está oculta, la mostramos y cambiamos la imagen
                txtNewContra.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imvVerNewContra.setImageResource(R.drawable.ojo)
            }
            // Solicita que el EditText se vuelva a dibujar para aplicar los cambios

            txtNewContra.setSelection(txtNewContra.text.length) // Mantiene el cursor al final
            isPasswordVisible = !isPasswordVisible
        }

       btnCrearContrasena.setOnClickListener {
           val nuevaContra = txtNewContra.text.toString()
           // Validar la contraseña
           if (validatePassword(nuevaContra)) {
               actualizarContraseña(Correo, nuevaContra)
               val intent = Intent(this, activity_iniciar_sesion::class.java)
               startActivity(intent)
               Toast.makeText(this, "Contraseña actualizada Correctamente", Toast.LENGTH_SHORT).show()
           }
           else {
                }
       }

        imvAtrasc.setOnClickListener {
            val volverAtras = Intent(this, Confirmacion_Cuenta::class.java)
            startActivity(volverAtras)
            overridePendingTransition(0, 0)
        }
    }

    fun hashSHA256(contrasenaEscrita: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    //Validación para campos
    fun setErrorWithCustomFont(editText: TextView, errorMessage: String, fontResId: Int) {
        val typeface = ResourcesCompat.getFont(this, fontResId)
        val spannableString = android.text.SpannableString(errorMessage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            spannableString.setSpan(
                typeface?.let { android.text.style.TypefaceSpan(it) }, 0, spannableString.length, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        editText.error = spannableString
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            setErrorWithCustomFont(txtNewContra, "Llena este campo", R.font.poppins)
            false
        } else if (password.length < 12) {
            setErrorWithCustomFont(txtNewContra, "La contraseña debe contener  12 carácteres", R.font.poppins)
            false
        } else {
            true
        }
    }

    private fun actualizarContraseña(correo: String, contraseña: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val contrasenaEncriptada = hashSHA256(txtNewContra.text.toString())

                val objConexion = ClaseConexion().cadenaConexion()
                if (objConexion != null) {
                    val query =
                        "UPDATE tbUsuarios SET contraseña = ? WHERE correo = ?"
                    val preparedStatement: PreparedStatement = objConexion.prepareStatement(query)
                    preparedStatement.setString(1, contrasenaEncriptada)
                    preparedStatement.setString(2, correo)
                    preparedStatement.executeUpdate()
                    preparedStatement.close()

                    val commit = objConexion.prepareStatement("commit")
                    commit.executeUpdate()
                    objConexion.close()
                } else {
                    println("No se pudo actualizar la contraseña")
                }

            } catch (e: NumberFormatException) {
            }
        }

    }
}

