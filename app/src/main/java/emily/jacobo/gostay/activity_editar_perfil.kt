package emily.jacobo.gostay

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.UUID

class activity_editar_perfil : AppCompatActivity() {

    lateinit var correoActual: String
    lateinit var contrasenaActual: String
    lateinit var txtNewContraP: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //  variables del companion object de activity_iniciar_sesion
        correoActual = activity_iniciar_sesion.variableGloalLogin.txtCorreoInciarSesionV
        contrasenaActual = activity_iniciar_sesion.variableGloalLogin.txtContrasenaIniciarSesionV

        // Acceder al EditText y obtener el valor de la contraseña como String
        val editTextContra = findViewById<EditText>(R.id.txtContraPerfil)
        txtNewContraP = editTextContra.text.toString()

        val imvAtrasPerfil = findViewById<ImageView>(R.id.imvAtrasPerfil)
        val btnGuardarPerfil = findViewById<Button>(R.id.btnGuardarPerfil)


        btnGuardarPerfil.setOnClickListener{

            val nuevoCorreo = findViewById<EditText>(R.id.txtCorreoPerfil).text.toString()

            // Actualizar el correo y la contraseña utilizando las funciones ya definidas
            actualizarCorreo(nuevoCorreo, correoActual)
            actualizarContraseña(correoActual, txtNewContraP)




           /* val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(siguientepantalla)*/


        }


        imvAtrasPerfil.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }


        }

    }

fun hashSHA256(contrasenaEscrita: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(contrasenaEscrita.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

private fun actualizarContraseña(correo: String, contraseña: String) {

    CoroutineScope(Dispatchers.IO).launch {

        try {

            // Encripta la contraseña que se pasa como parámetro
            val contrasenaEncriptada = hashSHA256(contraseña)

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
            e.printStackTrace()
        }

    }
}



private fun actualizarCorreo(nuevoCorreo: String, correoActual: String) {

    CoroutineScope(Dispatchers.IO).launch {

        try {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {

                // Verificar si el correo actual existe en la base de datos
                val selectQuery = "SELECT id_usuario FROM tbUsuarios WHERE correo = ?"
                val selectStatement = objConexion.prepareStatement(selectQuery)
                selectStatement.setString(1, correoActual)
                val selectResult = selectStatement.executeQuery()

                if (selectResult.next()) {
                    println("Usuario encontrado con el correo: $correoActual")

                    // Si el correo existe, proceder con la actualización
                    val query =
                        "UPDATE tbUsuarios SET correo = ? WHERE id_usuario = (SELECT id_usuario FROM tbUsuarios WHERE correo = ?)"
                    val preparedStatement: PreparedStatement = objConexion.prepareStatement(query)
                    preparedStatement.setString(1, nuevoCorreo)
                    preparedStatement.setString(2, correoActual)
                    preparedStatement.executeUpdate()
                    preparedStatement.close()

                    val commit = objConexion.prepareStatement("commit")
                    commit.executeUpdate()
                    objConexion.close()

                } else {
                    println("No se encontró ningún usuario con el correo: $correoActual")
                }

                selectStatement.close()
                selectResult.close()

            } else {
                println("No se pudo conectar a la base de datos")
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }
}


