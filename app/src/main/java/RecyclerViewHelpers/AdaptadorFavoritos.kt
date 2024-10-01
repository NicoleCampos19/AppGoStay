package RecyclerViewHelpers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import emily.jacobo.gostay.activity_iniciar_sesion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbFavoritos
import modelo.tbHotel

// Adaptador para un RecyclerView que muestra una lista de favoritos
class AdaptadorFavoritos(
    var Datos: List<tbHotel>,
    val clickListener: (tbHotel) -> Unit
) : RecyclerView.Adapter<ViewHolderHotel>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        // Infla el layout para cada elemento en el RecyclerView
        val vistaHotel = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount(): Int = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {

    }


}