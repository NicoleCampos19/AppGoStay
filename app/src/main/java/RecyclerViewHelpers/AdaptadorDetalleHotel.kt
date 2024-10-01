package RecyclerViewHelpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R

// Adaptador para un RecyclerView que muestra imágenes detalladas de un hotel
class AdaptadorDetalleHotel(private val imageUrls: List<String>, holder: Any) : RecyclerView.Adapter<AdaptadorDetalleHotel.HotelViewHolder>() {

    // ViewHolder que contiene la vista de la imagen para cada elemento
    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)  // Obtiene el ImageView desde el layout
    }
    // Crea y devuelve un HotelViewHolder cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        // Infla el layout para cada elemento del RecyclerView (imagen detallada del hotel)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detalles_hotel_img, parent, false)
        return HotelViewHolder(view)  // Devuelve el ViewHolder con la vista inflada
    }

    // Vincula los datos (en este caso la URL de la imagen) al ViewHolder
    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val imageUrl = imageUrls[position]  // Obtiene la URL de la imagen en la posición actual
    }

    // Devuelve el número de elementos (imágenes) en la lista
    override fun getItemCount(): Int {
        return imageUrls.size  // Devuelve el tamaño de la lista de URLs de imágenes
    }
}
