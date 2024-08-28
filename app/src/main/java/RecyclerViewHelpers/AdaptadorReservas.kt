package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.jacobo.gostay.R
import modelo.tbHotel
import modelo.tbReservas

class AdaptadorReservas (var datos: List<tbReservas>) : RecyclerView.Adapter<ViewHolderReservas>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderReservas {
        val vistaReseva = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card_mostrar_reservaciones, parent, false)
        return ViewHolderReservas(vistaReseva)
    }

    override fun onBindViewHolder(holder: ViewHolderReservas, position: Int) {
        val item = datos[position]


    }

    override fun getItemCount()= datos.size
}