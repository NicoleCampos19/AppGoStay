package RecyclerViewHelpers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ViewModelPerfil {
companion object {
    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> get() = _nombre
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email
    private val _profilePicture = MutableLiveData<String>()

    val profilePicture: LiveData<String> get() = _profilePicture

    fun setUserInfo(nombre: String, email: String, profilePicture: String) {
        _nombre.value = nombre
        _email.value = email
        _profilePicture.value = profilePicture
    }

    fun loadUserInfo(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", "")
        val email = sharedPreferences.getString("email", "")
        val imgFoto = sharedPreferences.getString("imgFoto", "")
        if (!nombre.isNullOrEmpty() && !email.isNullOrEmpty() && !imgFoto.isNullOrEmpty()) {
            setUserInfo(nombre, email, imgFoto)
        }
    }

    fun saveUserInfo(context: Context, nombre: String, email: String, imgFoto: String) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("nombre", nombre)
        editor.putString("email", email)
        editor.putString("imgFoto", imgFoto)
        editor.apply()
    }
}
}