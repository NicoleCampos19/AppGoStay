package emily.jacobo.gostay

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import emily.jacobo.gostay.activity_registrarse.variableGloalLogin.txtContraI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import oracle.ons.Connection
import java.security.MessageDigest
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement

class CreacionContrasenaActivity : AppCompatActivity() {

    lateinit var txtNewContra: EditText
    lateinit var Correo: String

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



        txtNewContra = findViewById<EditText>(R.id.txtNewContra)
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtrasc)
        val btnCrearContrasena = findViewById<Button>(R.id.btnCrearcontrasena)
        val Correo = RecuperacionCuentaActivity.variablesGobalesRecuperacion.Correo


       btnCrearContrasena.setOnClickListener {


               actualizarContraseña(Correo, txtNewContra.text.toString())
               val intent = Intent(this, activity_iniciar_sesion::class.java)
               startActivity(intent)


           val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
           startActivity(siguientepantalla)
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

