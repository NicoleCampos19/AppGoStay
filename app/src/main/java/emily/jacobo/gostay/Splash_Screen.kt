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
        val sharedPreferences: SharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val correoIngresado = sharedPreferences.getString("email", null) // Obtener el correo del usuario guardado
        val primerUso = sharedPreferences.getBoolean("firstUse", true) // Verificar si es el primer uso
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false) // Verificar si el usuario es ADMIN

        GlobalScope.launch(Dispatchers.Main) {
            delay(3000)

            if (primerUso) {
                // Si es el primer uso, redirigir a la pantalla de bienvenida
                startActivity(Intent(this@Splash_Screen, Bienvenida::class.java))

                // Actualizar el valor de primerUso a false para que no vuelva a mostrarse la bienvenida
                editor.putBoolean("firstUse", false)
                editor.apply()
            } else {
                if (correoIngresado != null) {
                    // Si ya hay un correo guardado, el usuario está logueado
                    if (isAdmin) {
                        // Redirigir a la pantalla de administración si es ADMIN
                        startActivity(Intent(this@Splash_Screen, InicioAdmin::class.java))
                    } else {
                        // Redirigir a la página de inicio normal si no es ADMIN
                        startActivity(Intent(this@Splash_Screen, PaginaInicio::class.java))
                    }
                }
                else {
                    // Si no hay un correo guardado, redirigir a la pantalla de inicio de sesión
                    startActivity(Intent(this@Splash_Screen, activity_iniciar_sesion::class.java))
                }
            }
            finish() // Cerrar la Splash Screen
        }
    }
}