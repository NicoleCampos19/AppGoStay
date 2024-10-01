package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbCarrusel

// Definición de una propiedad personalizada para una ImageView, aunque aún no se ha implementado su funcionalidad
private var ImageView.image: Int
    get() {
        TODO("Not yet implemented") // Esta parte aún no se ha implementado, lo que provocará una excepción si se accede
    }
    set(value) {}
// No se hace nada en este setter, por lo que la asignación no tendrá efecto

// Adaptador para un RecyclerView que muestra imágenes en un carrusel
class AdaptadorCarrusel(var Datos: List<tbCarrusel>):RecyclerView.Adapter<ViewHolderCarrusel>(){

    // Crea y devuelve un ViewHolderCarrusel cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCarrusel {
    // Infla el layout del carrusel para cada elemento en el RecyclerView
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_carrusel, parent,false)
        return ViewHolderCarrusel(vista)
    }

    // Devuelve el número de elementos en la lista de datos
        override fun getItemCount() = Datos.size

    // Vincula los datos de un elemento a la vista correspondiente en el ViewHolder
    override fun onBindViewHolder(holder: ViewHolderCarrusel, position: Int) {

        val item = Datos[position]
        holder.imageView.image= item.id_hoteles
        Glide.with(holder.imageView.context).load(item.url_imagen).into(holder.imageView)
    }


}
