package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbHotel

class HotelAdapter(var Datos: List<tbHotel>): RecyclerView.Adapter<ViewHolderHotel>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHotel {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return ViewHolderHotel(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderHotel, position: Int) {
        val item = Datos[position]
        holder.txtNombreHotelCard.text = item.nombre
    }


}