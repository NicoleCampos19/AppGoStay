package emily.jacobo.gostay

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.SQLException

class PerfilViewModel: ViewModel() {

    // LiveData para observar la URL de la imagen
    private val _urlImagen = MutableLiveData<String?>()
    val urlImagen: LiveData<String?> get() = _urlImagen

    // Función para cargar la imagen basada en el correo
    fun cargarImagen(correo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = obtenerImagenDeUsuario(correo)
            withContext(Dispatchers.Main) {
                _urlImagen.value = url
            }
        }
    }

    // Función para obtener la URL de la imagen desde la base de datos
    private suspend fun obtenerImagenDeUsuario(correo: String): String? {
        var urlImagen: String? = null
        withContext(Dispatchers.IO) {
            val connection = ClaseConexion().cadenaConexion()
            try {
                if (connection != null) { // Verifica si la conexión no es nula
                    val statement = connection.prepareStatement("SELECT imgFoto FROM tbUsuarios WHERE correo = ?")
                    statement.setString(1, correo)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        urlImagen = resultSet.getString("imgFoto")
                    }
                    resultSet.close()
                    statement.close()
                } else {
                    Log.e("PerfilViewModel", "La conexión a la base de datos es nula.")
                }
            } catch (e: SQLException) {
                Log.e("PerfilViewModel", "Error al obtener la imagen de usuario", e)
            } finally {
                connection?.close()
            }
        }
        return urlImagen
    }
}