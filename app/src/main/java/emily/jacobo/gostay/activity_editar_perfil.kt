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
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.UUID

class activity_editar_perfil : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

       val txtCorreoPerfil = findViewById<EditText>(R.id.txtCorreoPerfil)
       val txtContraPerfil = findViewById<EditText>(R.id.txtContraPerfil)
        val imvAtrasPerfil = findViewById<ImageView>(R.id.imvAtrasPerfil)
        val btnGuardarPerfil = findViewById<Button>(R.id.btnGuardarPerfil)
        val txtCorreoInciarSesionV = activity_iniciar_sesion.variableGloalLogin.txtCorreoInciarSesionV



        btnGuardarPerfil.setOnClickListener{

            actualizarContraseña(txtCorreoInciarSesionV.text.toString(), txtContraPerfil.text.toString())
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)


            val siguientepantalla = Intent(this, activity_iniciar_sesion::class.java)
            startActivity(siguientepantalla)

        }


        imvAtrasPerfil.setOnClickListener {
            val volverAtras = Intent(this, Perfil::class.java)
            startActivity(volverAtras)
        }






        }


    }




private fun actualizarContraseña(correo: String, contraseña: String) {


    CoroutineScope(Dispatchers.IO).launch {

        try {
            val objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
                val query =
                    "UPDATE tbUsuarios SET contraseña = ? WHERE correo = ?"
                val preparedStatement: PreparedStatement = objConexion.prepareStatement(query)
                preparedStatement.setString(1, contraseña)
                preparedStatement.setString(2, correo)
                preparedStatement.executeUpdate()
                preparedStatement.close()
                objConexion.close()

                val commit = objConexion.prepareStatement("commit")
                commit.executeUpdate()
            } else {
                println("No se pudo actualizar la contraseña")
            }

        } catch (e: NumberFormatException) {


        }


    }



}