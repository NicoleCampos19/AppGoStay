package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import emily.jacobo.gostay.R
import modelo.tbCarrusel


private var ImageView.image: Int
    get() {
        TODO("Not yet implemented")
    }
    set(value) {}


class AdaptadorCarrusel(var Datos: List<tbCarrusel>):RecyclerView.Adapter<ViewHolderCarrusel>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCarrusel {

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_carrusel, parent,false)
        return ViewHolderCarrusel(vista)
    }

        override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderCarrusel, position: Int) {

        val item = Datos[position]
        holder.imageView.image= item.id_imagenes
        Glide.with(holder.imageView.context).load(item.url_imagen).into(holder.imageView)
    }


}
