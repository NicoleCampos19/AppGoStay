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

class AdaptadorFavoritos(
    var Datos: List<tbHotel>
    ,
    val clickListener: (tbHotel) -> Unit
) : RecyclerView.Adapter<ViewHolderHotel>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vistaHotel = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vistaHotel)
    }

    override fun getItemCount(): Int = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {

    }


}