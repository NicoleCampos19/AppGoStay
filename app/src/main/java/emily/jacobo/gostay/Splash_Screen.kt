package emily.jacobo.gostay

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash_Screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener SharedPreferences
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("userPreferences", MODE_PRIVATE)
        val correoIngresado = sharedPreferences.getString("email", null)
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false) // Valor del tipo de usuario

        GlobalScope.launch(Dispatchers.Main) {
            delay(3000)

            if (correoIngresado != null) {
                // Si ya hay un correo guardado, el usuario está logueado
                if (isAdmin) {
                    // Redirigir a la pantalla de administración si es ADMIN
                    startActivity(Intent(this@Splash_Screen, InicioAdmin::class.java))
                } else {
                    // Redirigir a la página de inicio normal si no es ADMIN
                    startActivity(Intent(this@Splash_Screen, PaginaInicio::class.java))
                }
            } else {
                // Si el usuario no está logueado, dirigir a la pantalla de inicio de sesión
                startActivity(Intent(this@Splash_Screen, activity_iniciar_sesion::class.java))
            }

            finish()
        }
    }
}